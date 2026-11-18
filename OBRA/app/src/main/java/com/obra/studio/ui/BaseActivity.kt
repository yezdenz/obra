package com.obra.studio.ui

import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.obra.studio.R
import com.obra.studio.data.*
import com.obra.studio.ObraApplication
import com.obra.studio.data.local.SqliteStudioRepository

open class BaseActivity: AppCompatActivity() {
    private val artworkImages by lazy { ArtworkImages(this) }
    private var requestNumber = 0
    private var loaded = false
    private var refreshError: View? = null
    private val controlStates = java.util.IdentityHashMap<View, Boolean>()
    protected var saving = false
        private set
    protected open val usesStudioData = true
    protected open fun onStudioReady() {}
    override fun onResume() {
        super.onResume()
        if (usesStudioData && !saving) refreshData()
    }
    private fun refreshData() {
        val request = ++requestNumber
        refreshError?.let { body.removeView(it) }
        refreshError = null
        if (!loaded) { reset("Loading studio", false); note("Reading saved records…") }
        setControlsEnabled(false)
        (application as ObraApplication).refresh { result ->
            if (isDestroyed || isFinishing || request != requestNumber) return@refresh
            setControlsEnabled(true)
            result.fold(onSuccess = { loaded = true; onStudioReady() }, onFailure = {
                if (!loaded) {
                    reset("Studio unavailable", false)
                    empty("Could not read saved records", "Your saved records could not be loaded.")
                    button("Retry", primary = true) { refreshData() }
                } else showRefreshError(false)
            })
        }
    }

    protected fun <T> write(operation: (SqliteStudioRepository) -> T, completed: (T) -> Unit) {
        if (saving) return
        saving = true
        setControlsEnabled(false)
        val backGuard = object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        onBackPressedDispatcher.addCallback(this, backGuard)
        (application as ObraApplication).execute(operation) { result ->
            if (isDestroyed || isFinishing) return@execute
            result.fold(onSuccess = { value ->
                // Refresh separately so a failed read can never be presented as a failed write.
                (application as ObraApplication).refresh { refreshed ->
                    if (isDestroyed || isFinishing) return@refresh
                    saving = false
                    backGuard.remove()
                    setControlsEnabled(true)
                    completed(value)
                    if (refreshed.isFailure && !isFinishing) {
                        showRefreshError(true)
                    }
                }
            }, onFailure = { failure ->
                saving = false
                backGuard.remove()
                setControlsEnabled(true)
                error(if (failure is IllegalArgumentException) failure.message ?: "Check the entered details."
                    else "Could not save. Your input is still available. Retry the same submission.")
            })
        }
    }

    private fun showRefreshError(saved: Boolean) {
        refreshError?.let { body.removeView(it) }
        val panel = card(lavender) {
            text(if (saved) "Saved successfully" else "Could not refresh saved records", 18, true, it)
            text("Showing previously loaded information. Retry to read the latest saved records.", 14, false, it)
            button("Retry refresh", it, true) { refreshData() }
        }
        body.removeView(panel)
        body.addView(panel, 0)
        refreshError = panel
    }

    private fun setControlsEnabled(enabled: Boolean) {
        fun update(view: View) {
            if (enabled) view.isEnabled = controlStates.remove(view) ?: view.isEnabled
            else {
                if (!controlStates.containsKey(view)) controlStates[view] = view.isEnabled
                view.isEnabled = false
            }
            if (view is ViewGroup) for (index in 0 until view.childCount) update(view.getChildAt(index))
        }
        update(body); update(footer); update(header)
        if (enabled) controlStates.clear()
    }
    lateinit var body: LinearLayout; lateinit var header: LinearLayout; lateinit var footer: LinearLayout; lateinit var scroll: ScrollView
    val cream = Color.rgb(255,248,239); val ink = Color.rgb(48,44,55); val muted = Color.rgb(107,98,112)
    val yellow = Color.rgb(255,209,91); val sage = Color.rgb(236,235,220); val lavender = Color.rgb(237,228,244)
    val heading: Typeface by lazy { try { resources.getFont(R.font.dynapuff) } catch (_: Exception) { Typeface.DEFAULT_BOLD } }
    val supporting: Typeface by lazy { try { resources.getFont(R.font.itim) } catch (_: Exception) { Typeface.DEFAULT } }
    override fun onCreate(state: Bundle?) { super.onCreate(state); setContentView(R.layout.activity_shell)
        body=findViewById(R.id.content); header=findViewById(R.id.header); footer=findViewById(R.id.footer); scroll=findViewById(R.id.scroll)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shell)) { v, i -> val bars=i.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()); v.setPadding(bars.left,bars.top,bars.right,bars.bottom); i }
    }
    fun dp(n: Int) = (n*resources.displayMetrics.density).toInt()
    fun shape(color: Int, radius: Int=22, outline: Boolean=false) = GradientDrawable().apply { setColor(color); cornerRadius=dp(radius).toFloat(); if(outline)setStroke(dp(1),Color.rgb(216,206,196)) }
    fun text(value: String, size: Int=15, title: Boolean=false, parent: LinearLayout=body): TextView = TextView(this).apply { text=value; textSize=size.toFloat(); setTextColor(ink); typeface=if(title)heading else supporting; setLineSpacing(dp(3).toFloat(),1f); parent.addView(this, LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(8) }) }
    fun button(label: String, parent: LinearLayout=body, primary: Boolean=false, action: ()->Unit): MaterialButton = MaterialButton(this).apply { text=label; isAllCaps=false; typeface=heading; textSize=15f; setTextColor(ink); backgroundTintList=android.content.res.ColorStateList.valueOf(if(primary)yellow else Color.rgb(255,253,249)); cornerRadius=dp(18); minHeight=dp(50); setPadding(dp(12),dp(5),dp(12),dp(5)); parent.addView(this,LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(8) }); setOnClickListener { action() } }
    fun card(color: Int=Color.rgb(255,253,249), parent: LinearLayout=body, content: (LinearLayout)->Unit): LinearLayout = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(16),dp(16),dp(16),dp(12)); background=shape(color,22,true); parent.addView(this,LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(16) }); content(this) }
    fun title(name: String, back: Boolean=true) { header.removeAllViews(); if(back) { val b=TextView(this); b.text="‹"; b.textSize=32f; b.contentDescription="Back"; b.gravity=Gravity.CENTER; header.addView(b,LinearLayout.LayoutParams(dp(48),dp(48))); b.setOnClickListener { onBackPressedDispatcher.onBackPressed() } }; text(name,if(back)20 else 32,true,header).layoutParams=LinearLayout.LayoutParams(0,-2,1f) }
    fun headerAction(label: String, action: () -> Unit) {
        TextView(this).apply {
            text = label; typeface = supporting; textSize = 14f; setTextColor(ink)
            gravity = Gravity.CENTER; minWidth = dp(48); minHeight = dp(48)
            isClickable = true; isFocusable = true; contentDescription = label
            header.addView(this, LinearLayout.LayoutParams(-2, -2))
            setOnClickListener { action() }
        }
    }
    fun readableDate(value: String): String {
        val date = Validation.date(value) ?: return value
        val pattern = if (date.year == java.time.LocalDate.now().year) "MMM d" else "MMM d, yyyy"
        return date.format(java.time.format.DateTimeFormatter.ofPattern(pattern, java.util.Locale.ENGLISH))
    }
    fun deadline(c: Commission): String {
        if (c.status == "Completed") return "Finished ${readableDate(c.finishedOn)}"
        val date = Validation.date(c.dueOn) ?: return c.dueOn
        val days = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), date)
        return when {
            days < 0 -> "Overdue by ${-days} ${if (days == -1L) "day" else "days"}"
            days == 0L -> "Due today"
            days == 1L -> "Due tomorrow"
            else -> "Due ${readableDate(c.dueOn)}"
        }
    }
    override fun onDestroy() { artworkImages.close();super.onDestroy() }
    fun reset(name: String, back: Boolean=true) { artworkImages.clear();body.removeAllViews(); footer.removeAllViews(); title(name,back) }
    fun section(label: String) { text(label,21,true) }
    fun note(value: String) { text(value,13).setTextColor(muted) }
    /** Shared OBRA dialog, independent of manufacturer alert styling. */
    private fun showObraDialog(title: String, message: String, positive: String,
        negative: String? = null, destructive: Boolean = false, action: () -> Unit = {}) {
        val dialog = android.app.Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(24), dp(24), dp(16))
            background = shape(cream, 24, true)
        }
        text(title, 23, true, panel).apply { ViewCompat.setAccessibilityHeading(this, true) }
        text(message, 16, false, panel).apply { setTextColor(muted) }
        // Stacked actions remain readable on narrow screens and at larger text sizes.
        if (negative != null) button(negative, panel, true) { dialog.dismiss() }
        button(positive, panel, negative == null) { dialog.dismiss(); action() }.apply {
            if (destructive) {
                backgroundTintList = android.content.res.ColorStateList.valueOf(0xfffae1de.toInt())
                setTextColor(0xffa23d38.toInt())
            }
        }
        val viewport = ScrollView(this).apply { addView(panel); isFillViewport = true }
        dialog.setContentView(viewport)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            val metrics = resources.displayMetrics
            setLayout(minOf(metrics.widthPixels - dp(40), dp(420)), WindowManager.LayoutParams.WRAP_CONTENT)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.45f)
        }
    }
    fun error(message: String) { showObraDialog("Check these details", message, "OK") }
    fun confirm(title: String, message: String, action: () -> Unit) {
        val discard = title.startsWith("Discard")
        showObraDialog(title, message, if (discard) "Discard changes" else "Confirm",
            if (discard) "Keep editing" else "Cancel", discard, action)
    }
    fun detail(id: Long) { startActivity(Intent(this,DetailActivity::class.java).putExtra("commissionId",id)) }
    fun form(mode: String="add", id: Long=0) { startActivity(Intent(this,FormActivity::class.java).putExtra("mode",mode).putExtra("commissionId",id)) }
    fun completeArtwork(id: Long) {
        showObraDialog("Finish this artwork?", "Mark it completed today, ${readableDate(java.time.LocalDate.now().toString())}? You can still add artwork and record payments later.",
            "Mark completed", "Keep working") {
            write({ it.updateProgress(id,"Finished",java.time.LocalDate.now().toString()) }) { onStudioReady() }
        }
    }
    fun goHome() {
        startActivity(Intent(this,MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra("showHome",true))
        finish()
    }
    fun loadArtwork(view: ImageView, image: String, edge: Int=480) { artworkImages.load(view,image,edge) }
    fun thumbnail(image: String?, label: String, size: Int=76): ImageView = ImageView(this).apply {
        scaleType=ImageView.ScaleType.CENTER_CROP;contentDescription=label
        background=shape(0xffede5dc.toInt(),14);clipToOutline=true
        foreground=shape(Color.TRANSPARENT,14).apply { setStroke(dp(2),Color.BLACK) }
        setImageResource(android.R.drawable.ic_menu_gallery)
        imageTintList=android.content.res.ColorStateList.valueOf(muted)
        setPadding(dp(18),dp(18),dp(18),dp(18))
        if(image!=null)loadArtwork(this,image)
        layoutParams=LinearLayout.LayoutParams(dp(size),dp(size))
    }
    fun artworkGallery(c: Commission, finalFirst: Boolean=false) {
        val ordered=c.artworkUpdates.sortedWith(compareByDescending<ArtworkUpdate> { finalFirst && it.stage=="Finished" }.thenByDescending { it.id })
        val images=ordered.map { it.image } + c.finalImages.asReversed()
        startActivity(Intent(this,ViewerActivity::class.java).putExtra("commissionId",c.id)
            .putExtra("imageLabel","Artwork").putStringArrayListExtra("images",ArrayList(images)))
    }
    fun receipt(p: Payment, c: Commission?, openCommission: Boolean=true) {
        card { panel ->
            panel.setPadding(dp(14),dp(10),dp(14),dp(10))
            val row=LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL };panel.addView(row)
            val copy=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
            row.addView(copy,LinearLayout.LayoutParams(0,-2,1f).apply { rightMargin=dp(12) })
            fun line(value: String,size: Int,heading: Boolean=false) = text(value,size,heading,copy).apply {
                (layoutParams as LinearLayout.LayoutParams).bottomMargin=dp(3)
                maxLines=if(heading)2 else 1;ellipsize=android.text.TextUtils.TruncateAt.END
            }
            line("${readableDate(p.receivedOn)} · ${p.method}",12)
            line(Studio.money(p.amountMinor),20)
            if(openCommission)line(c?.title ?: "Commission unavailable",14,true)
            if(p.note.isNotBlank())line(p.note,12)
            row.addView(thumbnail(c?.historyImage,"${c?.title ?: "Commission"} artwork",76))
            if(openCommission && c!=null) {
                panel.isClickable=true;panel.isFocusable=true;panel.contentDescription="Open payment for ${c.title}"
                panel.setOnClickListener { detail(c.id) }
            }
        }
    }
    fun commissionPayments(group: CommissionPaymentGroup, c: Commission?, month: java.time.YearMonth) {
        card { panel ->
            panel.tag="commission-history:${group.commissionId}"
            panel.setPadding(dp(14),dp(12),dp(14),dp(12))
            val row=LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL };panel.addView(row)
            val copy=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
            row.addView(copy,LinearLayout.LayoutParams(0,-2,1f).apply { rightMargin=dp(12) })
            fun line(value:String,size:Int,heading:Boolean=false) = text(value,size,heading,copy).apply {
                (layoutParams as LinearLayout.LayoutParams).bottomMargin=dp(3)
                maxLines=2;ellipsize=android.text.TextUtils.TruncateAt.END
            }
            val client=if(c==null)"Client unavailable" else listOf(c.displayName,c.username,c.platform)
                .filter { it.isNotBlank() }.joinToString(" · ")
            line(client,13)
            line(c?.title ?: "Commission unavailable",16,true)
            line(Studio.money(group.periodReceived),21)
            if(group.allPayments.size==1) {
                val payment=group.allPayments.single()
                line("${readableDate(payment.receivedOn)} · ${payment.method}",12)
            } else {
                line("${month.format(java.time.format.DateTimeFormatter.ofPattern("MMMM",java.util.Locale.ENGLISH))} received · ${group.periodPayments.size} payments",12)
            }
            if(group.allPayments.size!=group.periodPayments.size)line("All-time paid ${Studio.money(group.totalReceived)}",12)
            row.addView(thumbnail(c?.historyImage,"${c?.title ?: "Commission"} artwork",76))
            val shown=if(group.allPayments.size>1)group.allPayments.takeLast(2) else emptyList()
            val breakdown=when {
                group.allPayments.size>2 -> "Latest payments · 2 of ${group.allPayments.size}"
                group.allPayments.size!=group.periodPayments.size -> "Payments · all dates"
                else -> null
            }
            if(breakdown!=null)text(breakdown,12,false,panel).setTextColor(muted)
            shown.forEach { payment ->
                val paymentRow=LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL };panel.addView(paymentRow)
                text("${readableDate(payment.receivedOn)} · ${payment.method}",12,false,paymentRow)
                    .layoutParams=LinearLayout.LayoutParams(0,-2,1f)
                text(Studio.money(payment.amountMinor),13,false,paymentRow).layoutParams=LinearLayout.LayoutParams(-2,-2)
            }
            if(c!=null) {
                panel.isClickable=true;panel.isFocusable=true
                panel.contentDescription="Open payments for ${c.title}, ${c.username}"
                panel.setOnClickListener {
                    startActivity(Intent(this,FormActivity::class.java).putExtra("mode","payments")
                        .putExtra("commissionId",c.id).putExtra("fromHistory",true))
                }
            }
        }
    }
    fun artwork(c: Commission, parent: LinearLayout=body) { card(parent=parent) { box ->
        val row=LinearLayout(this);row.gravity=Gravity.CENTER_VERTICAL;box.addView(row)
        val thumbnail=thumbnail(c.previewImage,"Latest artwork for ${c.title}");row.addView(thumbnail,LinearLayout.LayoutParams(dp(76),dp(84)).apply{rightMargin=dp(12)})
        val copy=LinearLayout(this);copy.orientation=LinearLayout.VERTICAL;row.addView(copy,LinearLayout.LayoutParams(0,-2,1f));text(c.title,16,true,copy);text("${c.username} · ${c.platform}",12,false,copy);text("${c.status} · ${deadline(c)}",12,false,copy);box.isClickable=true;box.isFocusable=true;box.contentDescription="Open ${c.title}";box.setOnClickListener{detail(c.id)}
    } }
    fun empty(title: String, message: String) { card { text(title,21,true,it); text(message,15,false,it) } }
    fun input(label: String, value: String, parent: LinearLayout=body, multiline: Boolean=false, suggestions: List<String>?=null): EditText { text(label,14,false,parent); return (if(suggestions==null) EditText(this) else AutoCompleteTextView(this).apply { setAdapter(ArrayAdapter(context,android.R.layout.simple_dropdown_item_1line,suggestions));threshold=1 }).apply { setText(value); textSize=16f; typeface=supporting; setTextColor(ink); background=shape(Color.rgb(255,253,249),14,true); setPadding(dp(12),dp(10),dp(12),dp(10)); minHeight=dp(50); inputType=if(multiline) android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE else android.text.InputType.TYPE_CLASS_TEXT; if(multiline)minLines=3; parent.addView(this,LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(16) }); contentDescription=label } }
    fun choose(label: String, values: List<String>, current: String, changed: (String)->Unit) { text(label,14); val spinner=Spinner(this); spinner.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,values); spinner.setSelection(values.indexOf(current).coerceAtLeast(0)); body.addView(spinner,LinearLayout.LayoutParams(-1,dp(52))); spinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener { override fun onNothingSelected(p: AdapterView<*>?) {} ; override fun onItemSelected(p: AdapterView<*>?,v: View?,position: Int,id: Long) { changed(values[position]) } } }
    fun dateField(label: String, value: String, changed: (String)->Unit) {
        text(label,13)
        val parsed=Validation.date(value)
        val display=parsed?.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy",java.util.Locale.ENGLISH)) ?: "Choose a date"
        val row=LinearLayout(this).apply {
            gravity=Gravity.CENTER_VERTICAL;minimumHeight=dp(60);setPadding(dp(14),dp(8),dp(14),dp(8))
            background=shape(0xfffffdf9.toInt(),16,true);isClickable=true;isFocusable=true
            contentDescription="$label, $display";tag="date-field:$label"
        }
        body.addView(row,LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(16) })
        text(display,18,false,row).layoutParams=LinearLayout.LayoutParams(0,-2,1f)
        row.addView(ImageView(this).apply {
            setImageResource(R.drawable.ic_calendar)
            imageTintList=android.content.res.ColorStateList.valueOf(muted)
            importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_NO
        },LinearLayout.LayoutParams(dp(24),dp(24)))
        val tag="obra-date:$label"
        fun bind(picker: com.google.android.material.datepicker.MaterialDatePicker<Long>) {
            picker.clearOnPositiveButtonClickListeners()
            picker.addOnPositiveButtonClickListener { millis ->
                changed(java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneOffset.UTC).toLocalDate().toString())
            }
        }
        @Suppress("UNCHECKED_CAST")
        val restored=supportFragmentManager.findFragmentByTag(tag) as? com.google.android.material.datepicker.MaterialDatePicker<Long>
        restored?.let(::bind)
        row.setOnClickListener {
            if(supportFragmentManager.findFragmentByTag(tag)==null) {
                val selected=(parsed ?: java.time.LocalDate.now()).atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
                val picker=com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                    .setTheme(R.style.ThemeOverlay_Obra_Calendar).setTitleText(label).setSelection(selected)
                    .setPositiveButtonText("Use date").setNegativeButtonText("Cancel").build()
                bind(picker);picker.show(supportFragmentManager,tag)
            }
        }
    }
}
