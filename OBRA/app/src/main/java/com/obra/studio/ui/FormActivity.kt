package com.obra.studio.ui

import android.os.Bundle
import android.content.Intent
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.obra.studio.data.*
import java.time.LocalDate

class FormActivity:BaseActivity() {
    private var mode="add";private var workId=0L;private var step=1
    private var draft=CommissionDraft(total="");private var baseline=CommissionDraft(total="")
    private var submissionToken=java.util.UUID.randomUUID().toString()
    private var attemptedSave=false
    private var initialized=false
    private var restoredState:Bundle?=null
    private var pickedImages:List<android.net.Uri> = emptyList()
    private val fields=linkedMapOf<String,EditText>()
    private var amount="";private var paidOn=LocalDate.now().toString();private var method="GCash";private var paymentNote="";private var stage="";private var finished=LocalDate.now().toString();private var completing=false
    private var originalStage=""
    private var originalPaidOn=paidOn
    private var originalMethod=method
    private var originalFinished=finished
    private val picker=registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        pickedImages=uris
        if(initialized){applyPickedImages();render()}
    }
    private fun applyPickedImages() {
        pickedImages.take((6-draft.images.size).coerceAtLeast(0)).forEach { uri ->
            try {
                contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if(uri.toString() !in draft.images)draft.images.add(uri.toString())
            } catch (_:SecurityException) {
                error("This image provider could not grant lasting access. Choose the image from another provider.")
            }
        }
        pickedImages=emptyList()
    }
    override fun onCreate(state:Bundle?) { super.onCreate(state);restoredState=state;submissionToken=state?.getString("submissionToken")?:submissionToken;attemptedSave=state?.getBoolean("attemptedSave")?:false;mode=intent.getStringExtra("mode")?:"add";workId=intent.getLongExtra("commissionId",0)
    }
    override fun onStudioReady() {
        if (attemptedSave && mode=="add") {
            Studio.repository.commissions().find { it.creationToken==submissionToken }?.let { detail(it.id);finish();return }
        }
        if (attemptedSave && mode=="payment" && Studio.repository.payments().any { it.submissionToken==submissionToken }) {
            finish();return
        }
        if(initialized){capture();applyPickedImages();render();return}
        val state=restoredState
        val c=Studio.work(workId)
        if(mode!="add" && c==null){reset("Record unavailable");empty("Record unavailable","Return to the previous screen.");return}
        if(c!=null) {draft=CommissionDraft(platform=c.platform,username=c.username,title=c.title,type=c.type,genre=c.genre,brief=c.brief,displayName=c.displayName,tags=c.tags,images=ArrayList(c.images),background=c.background,expectedRevision=c.revision,total=java.math.BigDecimal(c.totalMinor).movePointLeft(2).toPlainString(),commissionOn=c.commissionOn,dueOn=c.dueOn,plannedStart=c.plannedStart);stage=c.stage}
        baseline=draft.copy(images=ArrayList(draft.images));originalStage=stage
        if(mode=="progress" && intent.getBooleanExtra("completeArtwork",false))completing=true
        if(state!=null){ val saved=androidx.core.os.BundleCompat.getSerializable(state,"draft",CommissionDraft::class.java);if(saved!=null)draft=saved;val savedBaseline=androidx.core.os.BundleCompat.getSerializable(state,"baseline",CommissionDraft::class.java);if(savedBaseline!=null)baseline=savedBaseline;step=state.getInt("step",1);amount=state.getString("amount")?:"";paidOn=state.getString("paidOn")?:paidOn;method=state.getString("method")?:method;paymentNote=state.getString("note")?:"";stage=state.getString("stage")?:stage;finished=state.getString("finished")?:finished;completing=state.getBoolean("completing");originalStage=state.getString("originalStage")?:originalStage;originalPaidOn=state.getString("originalPaidOn")?:originalPaidOn;originalMethod=state.getString("originalMethod")?:originalMethod;originalFinished=state.getString("originalFinished")?:originalFinished }
        onBackPressedDispatcher.addCallback(this,object:OnBackPressedCallback(true){override fun handleOnBackPressed(){capture();if((mode=="add"||mode=="edit")&&step>1){step--;render()}else if(completing && intent.getBooleanExtra("completeArtwork",false)){leave()}else if(completing){completing=false;render()}else leave()} });initialized=true;applyPickedImages();render()
    }
    override fun onSaveInstanceState(out:Bundle){if(!initialized){super.onSaveInstanceState(out);restoredState?.let{out.putAll(it)};return};capture();super.onSaveInstanceState(out);out.putString("submissionToken",submissionToken);out.putBoolean("attemptedSave",attemptedSave);out.putSerializable("baseline",baseline);out.putSerializable("draft",draft);out.putInt("step",step);out.putString("amount",amount);out.putString("paidOn",paidOn);out.putString("method",method);out.putString("note",paymentNote);out.putString("stage",stage);out.putString("finished",finished);out.putBoolean("completing",completing);out.putString("originalStage",originalStage);out.putString("originalPaidOn",originalPaidOn);out.putString("originalMethod",originalMethod);out.putString("originalFinished",originalFinished)}
    private fun leave(){val dirty=if(mode=="add"||mode=="edit")draft!=baseline else amount.isNotBlank()||paymentNote.isNotBlank()||paidOn!=originalPaidOn||method!=originalMethod||stage!=originalStage||finished!=originalFinished;if(dirty)confirm("Discard changes?","Your unsaved input will be discarded."){finish()}else finish()}
    private fun field(key:String,label:String,value:String,multi:Boolean=false){fields[key]=input(label,value,multiline=multi,suggestions=if(key=="genre")(Studio.repository as? StudioSnapshot)?.activeGenres else null);if(key in listOf("total","initialAmount","amount"))fields[key]!!.inputType=android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL}
    private fun capture(){fields.forEach { (k,e)->val v=e.text.toString();when(k){"username"->draft.username=v;"name"->draft.displayName=v;"title"->draft.title=v;"genre"->draft.genre=v;"tags"->draft.tags=v;"brief"->draft.brief=v;"total"->draft.total=v;"initialAmount"->draft.initialAmount=v;"initialNote"->draft.initialNote=v;"amount"->amount=v;"note"->paymentNote=v} }}
    private fun render(){fields.clear();
        if(mode!="add" && Studio.work(workId)==null){reset("Record unavailable");empty("Record unavailable","Return to the previous screen.");return}
        reset(when(mode){"edit"->"Edit commission";"payment"->"Record payment";"payments"->"Payment history";"progress"->if(completing)"Review completion" else "Update progress";else->"New commission"})
        if(mode=="payments"){payments();return};if(mode=="payment"){payment();return};if(mode=="progress"){progress();return}
        note("Step $step of 4");headerAction("Close") { capture();leave() }
        when(step){1->{section("Client details");note("Who are we creating for?");choose("Contact platform *",listOf("X","Discord"),draft.platform){draft.platform=it};field("username","Client username *",draft.username);field("name","Display name · Optional",draft.displayName)}
            2->{section("Artwork brief");note("Give the request a little shape.");field("title","Project title *",draft.title);val types=((Studio.repository as? StudioSnapshot)?.activeTypes.orEmpty()+if(mode=="edit")listOf(baseline.type) else emptyList()).distinct();if(draft.type !in types)draft.type=types.firstOrNull().orEmpty();if(types.isEmpty())note("No active commission types are available.")else choose("Commission type *",types,draft.type){draft.type=it};field("genre","Primary genre · Optional",draft.genre);field("tags","Style tags · Optional",draft.tags);field("brief","Pose & details",draft.brief,true);referenceGallery()}
            3->{section("Price & dates");note("Set the price you agreed with your client.")
                field("total","Agreed total (PHP) *",draft.total)
                if(mode=="edit")note("Already received: ${Studio.money(Studio.received(workId))}. Existing receipts remain unchanged.")
                dateField("Commission date",draft.commissionOn){capture();draft.commissionOn=it;render()};dateField("Due date",draft.dueOn){capture();draft.dueOn=it;render()};dateField("Planned start · Optional",draft.plannedStart){capture();draft.plannedStart=it;render()};if(draft.plannedStart.isNotBlank())button("Clear planned start"){capture();draft.plannedStart="";render()}
                if(mode=="add"){val cb=CheckBox(this);cb.text="Record initial payment";cb.typeface=heading;cb.isChecked=draft.initialEnabled;body.addView(cb);cb.setOnClickListener{capture();draft.initialEnabled=cb.isChecked;render()};if(draft.initialEnabled){field("initialAmount","Amount received (PHP) *",draft.initialAmount);dateField("Date received",draft.initialDate){capture();draft.initialDate=it;render()};choose("Payment method",methods,draft.initialMethod){draft.initialMethod=it};field("initialNote","Payment note · Optional",draft.initialNote)}}}
            4->{section(if(mode=="edit")"Review changes" else "One last look");if(mode=="edit"){val changes=mutableListOf<String>();if(draft.username!=baseline.username)changes.add("Username: ${baseline.username} → ${draft.username}");if(draft.title!=baseline.title)changes.add("Title: ${baseline.title} → ${draft.title}");if(draft.total!=baseline.total)changes.add("Price: ${baseline.total} → ${draft.total}");if(draft.dueOn!=baseline.dueOn)changes.add("Due: ${baseline.dueOn} → ${draft.dueOn}");text(changes.joinToString("\n").ifEmpty { if(draft==baseline)"No changes to save." else "Review the updated details below." })}
                review("Client","${draft.username} · ${draft.platform}\n${draft.displayName}",1);review("Artwork","${draft.title}\n${draft.type} · ${draft.genre}\n${draft.tags}\n${draft.brief}\n${draft.images.size} references",2);review("Schedule","Commission: ${readableDate(draft.commissionOn)}\nPlanned start: ${draft.plannedStart.ifEmpty { "Not set" }.let(::readableDate)}\nDue: ${readableDate(draft.dueOn)}",3);review("Price & payment","Agreed total: ₱${draft.total}\nInitial payment: ${if(draft.initialEnabled)"₱${draft.initialAmount}" else "None"}",3)} }
        val label=when(step){1->"Next: Artwork";2->"Next: Price & dates";3->if(mode=="edit")"Review changes" else "Review commission";else->if(mode=="edit")"Save changes" else "Save commission"}
        button(label,footer,true){capture();val issue=validate(step);if(issue!=null)error(issue)else if(step<4){step++;render();scroll.scrollTo(0,0)}else saveRequest()}.isEnabled=!(step==4&&mode=="edit"&&draft==baseline)
    }
    private fun saveRequest() {
        val submitted=draft.copy(images=ArrayList(draft.images))
        if(mode=="edit") {
            write({it.updateCommission(workId,submitted)}){finish()}
        } else {
            attemptedSave=true
            write({it.saveCommission(submitted,submissionToken)}){id->detail(id);finish()}
        }
    }
    private fun referenceGallery() {
        section("Reference images")
        note("${draft.images.size} of 6 images · Tap a preview to enlarge")
        draft.images.toList().withIndex().chunked(3).forEach { references ->
            val row=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL;weightSum=3f }
            body.addView(row,LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(4) })
            references.forEach { (index,uri) ->
                val tile=object : FrameLayout(this@FormActivity) {
                    override fun onMeasure(widthMeasureSpec:Int,heightMeasureSpec:Int) {
                        val side=android.view.View.MeasureSpec.getSize(widthMeasureSpec)
                        super.onMeasure(widthMeasureSpec,android.view.View.MeasureSpec.makeMeasureSpec(side,android.view.View.MeasureSpec.EXACTLY))
                    }
                }
                row.addView(tile,LinearLayout.LayoutParams(0,-2,1f))
                val preview=thumbnail(uri,"Preview reference ${index+1}",112).apply {
                    isClickable=true;isFocusable=true
                    setOnClickListener {
                        capture()
                        startActivity(Intent(this@FormActivity,ViewerActivity::class.java)
                            .putStringArrayListExtra("images",ArrayList(draft.images)).putExtra("index",index))
                    }
                }
                tile.addView(preview,FrameLayout.LayoutParams(-1,-1).apply { setMargins(dp(8),dp(8),dp(8),dp(8)) })
                val remove=FrameLayout(this).apply {
                    contentDescription="Remove reference ${index+1}";tag="remove-reference:$index"
                    isClickable=true;isFocusable=true
                    setOnClickListener { capture();draft.images.removeAt(index);render() }
                }
                val badge=FrameLayout(this).apply {
                    background=shape(0xffff3b30.toInt(),12).apply { setStroke(dp(2),0xff791f1a.toInt()) }
                    importantForAccessibility=android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
                badge.addView(ImageView(this).apply { setImageResource(com.obra.studio.R.drawable.ic_remove_reference) },
                    FrameLayout.LayoutParams(dp(18),dp(18),android.view.Gravity.CENTER))
                remove.addView(badge,FrameLayout.LayoutParams(dp(24),dp(24),android.view.Gravity.TOP or android.view.Gravity.END))
                tile.addView(remove,FrameLayout.LayoutParams(dp(48),dp(48),android.view.Gravity.TOP or android.view.Gravity.END))
            }
        }
        uploadButton("Add references") { capture();picker.launch(arrayOf("image/*")) }.isEnabled=draft.images.size<6
    }
    private fun review(label:String,value:String,target:Int) {
        card { panel ->
            val row=LinearLayout(this).apply { gravity=android.view.Gravity.CENTER_VERTICAL }
            panel.addView(row)
            text(label,20,true,row).layoutParams=LinearLayout.LayoutParams(0,-2,1f)
            text("Edit",14,false,row).apply {
                gravity=android.view.Gravity.CENTER;minHeight=dp(48);minWidth=dp(48)
                layoutParams=LinearLayout.LayoutParams(-2,dp(48))
                isClickable=true;isFocusable=true;contentDescription="Edit $label"
                setOnClickListener { step=target;render();scroll.scrollTo(0,0) }
            }
            text(value.lineSequence().filter { it.isNotBlank() }.joinToString("\n"),15,false,panel)
        }
    }
    private fun validate(until:Int):String? {
        if(draft.username.isBlank())return "Enter the client's username."
        if(until>=2&&draft.title.isBlank())return "Enter the project title."
        if(until>=2&&draft.type.isBlank())return "Choose an active commission type."
        if(until>=3){val total=Validation.minor(draft.total)?:return "Enter a positive price with at most two decimal places (maximum PHP 1 billion).";Validation.schedule(draft,Studio.work(workId))?.let{return it}
            if(mode=="edit"){if(total<Studio.received(workId))return "Price cannot be below payments already received.";if(Studio.repository.payments().any{it.commissionId==workId&&it.receivedOn<draft.commissionOn})return "Commission date cannot be after an existing receipt."}
            if(draft.initialEnabled){val initial=Validation.minor(draft.initialAmount)?:return "Enter a valid initial payment.";if(initial>total)return "Initial payment exceeds the agreed price.";val date=Validation.date(draft.initialDate)?:return "Choose a valid received date.";if(date<Validation.date(draft.commissionOn)||date>LocalDate.now())return "Received date must be between commission date and today."}}
        return null
    }
    private val methods=listOf("GCash","Bank transfer","PayPal","Cash","Other")
    private fun payment(){val c=Studio.work(workId)!!;card(sage){text(c.title,20,true,it);text("Remaining: ${Studio.money(Studio.balance(c))}",20,false,it)};field("amount","Amount received (PHP) *",amount);dateField("Received date",paidOn){capture();paidOn=it;render()};choose("Method",methods,method){method=it};field("note","Note · Optional",paymentNote,true);button("Save payment",footer,true){capture();val n=Validation.minor(amount);val d=Validation.date(paidOn);when{n==null->error("Enter a positive amount with at most two decimal places.");n>Studio.balance(c)->error("Payment cannot exceed the remaining balance.");d==null||d<Validation.date(c.commissionOn)||d>LocalDate.now()->error("Received date must be between commission date and today.");else->{attemptedSave=true;write({it.recordPayment(workId,n,paidOn,method,paymentNote,submissionToken)}){finish()}}}} }
    private fun payments() {
        val c=Studio.work(workId)!!
        headerAction("Delete") { deleteCommission(c) }
        section(c.title)
        text(listOf(c.displayName,c.username,c.platform).filter { it.isNotBlank() }.joinToString(" · "),14)
        card(sage) {
            text("Received ${Studio.money(Studio.received(workId))}",20,false,it)
            text("Remaining ${Studio.money(Studio.balance(c))}",16,false,it)
        }
        val ps=Studio.repository.payments().filter { it.commissionId==workId }
        if(ps.isEmpty())empty("No payments yet","Record money only after it has been received.")
        ps.sortedWith(compareByDescending<Payment> { it.receivedOn }.thenByDescending { it.id })
            .forEach { receipt(it,c,false) }
        if(Studio.balance(c)>0)button("Record payment",footer,true){form("payment",workId)}
        if(intent.getBooleanExtra("fromHistory",false))button("View commission",footer){detail(workId);finish()}
        else button("Back to commission",footer){finish()}
    }
    private fun progress() {
        val c=Studio.work(workId)!!
        section(c.title)
        if(c.status!="In progress") {
            empty("This artwork is not in progress","Go back to the commission to see its current status.")
            return
        }
        if(completing) {
            note("Ready to call it done? Check the finish date, then confirm below.")
            dateField("Actual finish date",finished){finished=it;render()}
            card(sage) {
                text("${Studio.money(Studio.balance(c))} still to collect",19,true,it)
                text("You can finish the artwork before the client pays the balance. Payments stay unchanged.",14,false,it)
            }
            button("Mark completed",footer,true) {
                val date=Validation.date(finished);val start=Validation.date(c.startedOn)
                if(date==null||start==null||date<start||date>LocalDate.now())error("Finish date must be between the actual start and today.")
                else write({it.updateProgress(workId,"Finished",finished)}){finish()}
            }
            button("Keep working") {
                if(intent.getBooleanExtra("completeArtwork",false))finish()
                else {completing=false;render()}
            }
        } else {
            var save: TextView?=null
            choose("Art stage (optional)",listOf("Sketch","Lineart","Colors","Finished"),stage) {
                stage=it
                save?.text=if(stage=="Finished")"Review completion" else "Save stage"
            }
            note("Use this extra detail only if it helps your workflow. You can mark the artwork completed without updating each stage.")
            save=button(if(stage=="Finished")"Review completion" else "Save stage",footer,true) {
                if(stage=="Finished"){completing=true;render()}
                else if(stage==c.stage)error("Choose a different stage to update progress.")
                else write({it.updateProgress(workId,stage,null)}){finish()}
            }
        }
    }
}
