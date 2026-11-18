package com.obra.studio.ui

import android.os.Bundle
import android.content.Intent
import android.widget.CheckBox
import com.obra.studio.data.*

class DetailActivity:BaseActivity() {
    override fun onStudioReady() { val position=scroll.scrollY;val c=Studio.work(intent.getLongExtra("commissionId",0));if(c==null){reset("Commission unavailable");empty("Record unavailable","Return to the list and choose another commission.");return};reset("Commission details")
        card(lavender) { text(c.type.uppercase(),13,false,it);text(c.title,25,true,it);text("${c.username} · ${c.platform}",15,false,it);text("${c.status}  ·  ${c.stage}",14,false,it) }
        button("Edit commission") { form("edit",c.id) }
        if(c.status=="Queued")button("Start commission",primary=true) { confirm("Start this commission?","The actual start will be today; payment records stay independent.") { write({it.startCommission(c.id,java.time.LocalDate.now().toString())}){onStudioReady()} } }
        if(c.status=="In progress")button("Update progress",primary=true) { form("progress",c.id) }
        section("Artwork brief");if(c.displayName.isNotBlank())text(c.displayName);if(c.tags.isNotBlank())text(c.tags);if(c.background)text("Detailed background included");text("${c.type} · ${c.genre}");text(c.brief)
        section("Reference images");button("Open reference preview") { startActivity(Intent(this,ViewerActivity::class.java).putExtra("commissionId",c.id).putStringArrayListExtra("images",ArrayList(c.images))) }
        section("Checklist");c.tasks.forEach { task->val check=CheckBox(this);check.text=task.label;check.typeface=heading;check.isChecked=task.done;check.minHeight=dp(48);body.addView(check);check.setOnClickListener { check.isChecked=task.done;write({it.setTaskDone(c.id,task.id,!task.done)}){onStudioReady()} } }
        section("Dates");text("Commission date: ${c.commissionOn}\nPlanned start: ${c.plannedStart.ifEmpty { "Not set" }}\nActual start: ${c.startedOn.ifEmpty { "Not started" }}\nDue date: ${c.dueOn}\nFinished: ${c.finishedOn.ifEmpty { "Not finished" }}")
        card(sage) { text("Price & payment",20,true,it);text("Agreed total  ${Studio.money(c.totalMinor)}\nReceived  ${Studio.money(Studio.received(c.id))}\nRemaining  ${Studio.money(Studio.balance(c))}",17,false,it);text(if(Studio.balance(c)==0L)"Fully paid" else if(Studio.received(c.id)>0)"Partially paid" else "Unpaid",14,false,it) }
        button("Record payment",primary=true) { form("payment",c.id) }.isEnabled=Studio.balance(c)>0
        button("Payment history") { form("payments",c.id) };scroll.post { scroll.scrollTo(0,position) }
    }
}
