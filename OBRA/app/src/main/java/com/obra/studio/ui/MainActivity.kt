package com.obra.studio.ui

import android.os.Bundle
import android.widget.*
import android.text.TextWatcher
import android.text.Editable
import android.view.View
import android.view.Gravity
import com.obra.studio.R
import com.google.android.material.button.MaterialButton
import com.obra.studio.data.*
import java.time.YearMonth

class MainActivity: BaseActivity() {
    private var tab="Home"; private var filter="In progress"; private var query=""; private var month=YearMonth.now(); private var selected=0L
    private val positions=mutableMapOf<String,Int>()
    private var pendingScroll:Int?=null
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        tab=state?.getString("tab")?:"Home"
        if(intent.getBooleanExtra("showHome",false)) {tab="Home";intent.removeExtra("showHome")}
        if(tab !in listOf("Home","Commissions","History","Insights","Reconnect"))tab="Home"
        filter=state?.getString("filter")?:filter
        query=state?.getString("query")?:""
        month=YearMonth.parse(state?.getString("month")?:month.toString())
        selected=state?.getLong("selected")?:0
        state?.getBundle("positions")?.let { saved ->
            saved.keySet().forEach { positions[it]=saved.getInt(it) }
        }
        pendingScroll=state?.getInt("scroll")
    }
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        if(intent.getBooleanExtra("showHome",false)) {tab="Home";pendingScroll=0}
    }
    override fun onSaveInstanceState(out: Bundle) {
        positions[tab]=pendingScroll?:scroll.scrollY
        super.onSaveInstanceState(out)
        out.putString("tab",tab)
        out.putString("filter",filter)
        out.putString("query",query)
        out.putString("month",month.toString())
        out.putLong("selected",selected)
        out.putInt("scroll",positions[tab]?:0)
        out.putBundle("positions",Bundle().apply { positions.forEach { (tab,y)->putInt(tab,y) } })
    }
    override fun onStudioReady() {
        val position=pendingScroll?:scroll.scrollY
        pendingScroll=null
        render()
        scroll.post { scroll.scrollTo(0,position) }
    }
    private fun switch(next: String) { positions[tab]=scroll.scrollY;tab=next;render();scroll.post { scroll.scrollTo(0,positions[tab]?:0) } }
    private fun render() {
        reset(if(tab=="Home")"obra" else tab,false)
        when(tab) { "Home"->home(); "Commissions"->commissions(); "History"->history(); "Insights"->insights(); "Reconnect"->reconnect() }
        val nav=LinearLayout(this); footer.addView(nav)
        val destinations=listOf("Home" to R.drawable.ic_home,"Commissions" to R.drawable.ic_work,
            "History" to R.drawable.ic_history,"Insights" to R.drawable.ic_insights,
            "Reconnect" to R.drawable.ic_reconnect)
        destinations.forEach { (name,icon) ->
            val item=LinearLayout(this).apply {
                orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER
                minimumHeight=dp(64);setPadding(dp(2),dp(7),dp(2),dp(7))
                contentDescription=name;isSelected=name==tab;isFocusable=true
                background=shape(if(isSelected)selectedTone else cream,14)
                setOnClickListener { switch(name) }
            }
            item.addView(ImageView(this).apply {
                setImageResource(icon)
                importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_NO
            },LinearLayout.LayoutParams(dp(24),dp(24)))
            item.addView(TextView(this).apply {
                text=if(name=="Commissions")"Work" else name
                textSize=11f;typeface=supporting;setTextColor(ink);gravity=Gravity.CENTER
                importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_NO
            },LinearLayout.LayoutParams(-1,-2).apply { topMargin=dp(3) })
            nav.addView(item,LinearLayout.LayoutParams(0,-2,1f))
        }
    }
    private fun home() {
        note(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d")))
        val works=Studio.repository.commissions()
        val outstanding=works.filter { it.status != "Cancelled" }.sumOf { Studio.balance(it) }
        card(summaryTone) {
            val headingRow=LinearLayout(this).apply { gravity=android.view.Gravity.CENTER_VERTICAL }
            it.addView(headingRow)
            val monthPattern=if(month.year==YearMonth.now().year)"MMMM" else "MMMM yyyy"
            val monthTitle=month.format(java.time.format.DateTimeFormatter.ofPattern(monthPattern,java.util.Locale.ENGLISH))
            text("$monthTitle earnings",18,true,headingRow).layoutParams=LinearLayout.LayoutParams(0,-2,1f)
            val insights=ImageButton(this).apply {
                setImageResource(com.obra.studio.R.drawable.ic_insights)
                background=shape(yellow,14);setPadding(dp(12),dp(12),dp(12),dp(12))
                contentDescription="View insights";tooltipText="View insights"
                setOnClickListener { switch("Insights") }
            }
            headingRow.addView(insights,LinearLayout.LayoutParams(dp(48),dp(48)).apply { leftMargin=dp(8) })
            text(Studio.money(Studio.monthly(month).sumOf { p -> p.amountMinor }),30,false,it)
            text("Still to collect: ${Studio.money(outstanding)} across all artwork",13,false,it)
        }.apply {
            tag="home-earnings"
            background=shape(summaryTone,22).apply { setStroke(dp(2),yellow) }
        }
        val counts=LinearLayout(this);body.addView(counts,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(18)})
        listOf("${works.count{it.status=="In progress"}}\nIn progress","${works.count{it.status=="Queued"}}\nIn queue").forEachIndexed { i,label ->
            val count=TextView(this)
            count.text=label;count.typeface=supporting;count.textSize=16f;count.setTextColor(ink)
            count.gravity=17;count.minHeight=dp(66);count.isClickable=true;count.isFocusable=true
            counts.addView(count,LinearLayout.LayoutParams(0,-2,1f))
            count.setOnClickListener{filter=if(i==0)"In progress" else "Queued";query="";switch("Commissions")}
        }
        section("Current work")
        val current=works.filter { it.status=="In progress" }.sortedBy { it.dueOn }
        if(current.isEmpty())empty("No work in progress","Start a queued commission when you are ready.")
        else current.take(2).forEach { artwork(it) }
        section("Next in queue");works.filter { it.status=="Queued" }.sortedWith(compareBy<Commission> { it.plannedStart.isBlank() }.thenBy { it.plannedStart }.thenBy { it.id }).firstOrNull()?.let { artwork(it) } ?: empty("No queued work","New requests will appear here.")
        button("+ Add commission",footer,true) { form() }
    }
    private fun commissions() {
        note("A little order for every artwork.");button("+ New commission",primary=true) { form() }
        val filters=LinearLayout(this).apply { gravity=Gravity.CENTER;clipChildren=false }
        body.addView(filters,LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(8) })
        val filterButtons=mutableMapOf<String,MaterialButton>()
        var filterChanged: ()->Unit = {}
        listOf("Current" to "In progress","Queued" to "Queued","Completed" to "Completed").forEach { (label,status) ->
            filterButtons[status]=button(label,filters,primary=status==filter) {
                if(filter!=status) {
                    filter=status
                    filterButtons.forEach { (value,control) ->
                        control.isSelected=value==filter
                        control.backgroundTintList=android.content.res.ColorStateList.valueOf(if(control.isSelected)yellow else surface)
                    }
                    filterChanged()
                }
            }.apply {
                textSize=12f;gravity=Gravity.CENTER;textAlignment=View.TEXT_ALIGNMENT_CENTER
                setPadding(dp(5),dp(5),dp(5),dp(5))
                minimumWidth=0;insetLeft=0;insetRight=0
                strokeWidth=dp(1);strokeColor=android.content.res.ColorStateList.valueOf(outlineTone)
                contentDescription="$label commissions"
                isSelected=status==filter
                layoutParams=LinearLayout.LayoutParams(0,-2,1f).apply {
                    marginEnd=if(status=="Completed")0 else dp(8)
                }
            }
        }
        val search=input("Search artwork or client",query)
        val results=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL };body.addView(results)
        fun populate() { results.removeAllViews();val found=Studio.repository.commissions().filter { it.status==filter && (it.title+it.username).contains(query,true) };if(found.isEmpty())text("No commissions found. Try a different search or add a request.",16,false,results) else found.forEach { artwork(it,results) } }
        filterChanged={ populate() }
        search.addTextChangedListener(object:TextWatcher { override fun beforeTextChanged(s:CharSequence?,a:Int,c:Int,f:Int){};override fun onTextChanged(s:CharSequence?,a:Int,b:Int,c:Int){query=s.toString();populate()};override fun afterTextChanged(e:Editable?){} });populate()
    }
    private fun period() {
        val formatter=java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy",java.util.Locale.ENGLISH)
        button("${month.format(formatter)} ▾") {
            val months=(-12L..1L).map { YearMonth.now().plusMonths(it) }
            androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Reporting month")
                .setItems(months.map { it.format(formatter) }.toTypedArray()) { _,i -> month=months[i];render() }.show()
        }
    }
    private fun history() {
        note("Payments grouped by commission. Tap an entry for its full history.");period();val payments=Studio.monthly(month).sortedByDescending { it.receivedOn }
        card(summaryTone) { text(Studio.money(payments.sumOf { p->p.amountMinor }),30,false,it);text("${payments.size} payments received",14,false,it) }
        if(payments.isEmpty())empty("No payments this month","Received payments will appear here once recorded.")
        groupPaymentsForMonth(Studio.repository.payments(),month).forEach { group ->
            commissionPayments(group,Studio.work(group.commissionId),month)
        }
    }
    private fun insights() {
        note("A clearer view of your creative income.");period()
        card(summaryTone) { text("Monthly earnings",18,true,it);text(Studio.money(Studio.monthly(month).sumOf { p->p.amountMinor }),30,false,it);button("View payments",it) { switch("History") } }
        card { text("All-time received",17,true,it);text(Studio.money(Studio.repository.payments().sumOf { p->p.amountMinor }),26,false,it) }
        section("Six-month earnings")
        val series=(5L downTo 0L).map { month.minusMonths(it) }.map { it to Studio.monthly(it).sumOf { p->p.amountMinor } };val max=series.maxOf { it.second }.coerceAtLeast(1)
        series.forEach { (m,amount)->text("$m   ${Studio.money(amount)}",13);val bar=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.max=100;bar.progress=(amount*100/max).toInt();bar.progressTintList=android.content.res.ColorStateList.valueOf(accentTone);bar.contentDescription="$m received ${Studio.money(amount)}";body.addView(bar,LinearLayout.LayoutParams(-1,dp(12))) };note("Received income (PHP) by month; six months ending $month.")
        section("By commission type");val grouped=Studio.monthly(month).groupBy { Studio.work(it.commissionId)?.type?:"Other" };if(grouped.isEmpty())note("No received payments for this period.");grouped.forEach { (type,ps)->text("$type  ·  ${Studio.money(ps.sumOf { it.amountMinor })}") }
        section("Current balances");note("Across all dates, including completed artwork.");Studio.repository.commissions().filter { it.status != "Cancelled" && Studio.balance(it)>0 }.forEach { c->button("${c.title}\n${Studio.money(Studio.balance(c))} remaining") { detail(c.id) } }
    }
    private fun reconnect() {
        note("Familiar clients. New possibilities.");section("Find a familiar connection")
        val current=Studio.repository.commissions().filter { it.status=="In progress" };if(current.isEmpty()){empty("No current work","Start a queued commission to choose a matching context.");return}
        if(selected!=0L && current.none { it.id==selected })selected=-1L
        val labels=current.mapIndexed { index,c -> "${index+1}. ${c.title} · ${c.username}" }
        val selectedIndex=current.indexOfFirst { it.id==selected }
        val prompt="Choose current commission"
        choose("Current commission",listOf(prompt)+labels,labels.getOrNull(selectedIndex)?:prompt) { label ->
            val index=labels.indexOf(label)
            if(index>=0 && current[index].id!=selected){selected=current[index].id;render()}
        }
        if(selectedIndex<0){
            empty(if(selected==-1L)"Previous selection is no longer current" else "Choose current work",
                "Select a current commission above to find previous clients with matching type and genre.")
            return
        }
        val c=Studio.work(selected)!!;card(detailTone) { text("${c.type}  ·  ${c.genre}",17,true,it);text("Suggestions use commission type and genre.",14,false,it) }
        val results=Studio.repository.recommendations(selected)
        if(results.isEmpty())empty(if(c.genreId==null)"Choose a primary genre" else "No matching clients yet",if(c.genreId==null)"Edit this commission and add a primary genre to find similar completed work." else "Try another current commission. Matching completed artwork will appear here.")
        results.forEach { r->val previous=Studio.work(r.previousCommissionId)!!;card { text(r.clientName,22,true,it);text(previous.title,18,true,it);text("${r.reason}\n${previous.type} · ${previous.genre}\nFinished ${previous.finishedOn}",14,false,it);button("View previous commission",it) { detail(previous.id) } } }
    }
}
