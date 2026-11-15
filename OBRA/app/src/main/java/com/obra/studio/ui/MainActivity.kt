package com.obra.studio.ui

import android.os.Bundle
import android.widget.*
import android.text.TextWatcher
import android.text.Editable
import com.obra.studio.data.*
import java.time.YearMonth

class MainActivity: BaseActivity() {
    private var tab="Home"; private var filter="In progress"; private var query=""; private var month=YearMonth.now(); private var selected=2L
    private val positions=mutableMapOf<String,Int>()
    override fun onCreate(state: Bundle?) { super.onCreate(state); tab=state?.getString("tab")?:"Home"; filter=state?.getString("filter")?:filter; query=state?.getString("query")?:""; month=YearMonth.parse(state?.getString("month")?:month.toString()); selected=state?.getLong("selected")?:2; render() }
    override fun onSaveInstanceState(out: Bundle) { super.onSaveInstanceState(out); out.putString("tab",tab);out.putString("filter",filter);out.putString("query",query);out.putString("month",month.toString());out.putLong("selected",selected) }
    private fun switch(next: String) { positions[tab]=scroll.scrollY;tab=next;render();scroll.post { scroll.scrollTo(0,positions[tab]?:0) } }
    private fun render() {
        reset(if(tab=="Home")"obra" else tab,false)
        when(tab) { "Home"->home(); "Commissions"->commissions(); "History"->history(); "Insights"->insights(); "Reconnect"->reconnect(); "All tasks"->tasks() }
        val nav=LinearLayout(this); footer.addView(nav)
        listOf("Home","Commissions","History","Insights","Reconnect").forEach { name -> val t=TextView(this); t.text=if(name=="Commissions")"Work" else name; t.textSize=11f;t.typeface=supporting;t.setTextColor(ink);t.gravity=17;t.minHeight=dp(56);t.contentDescription=name;t.isSelected=name==tab;t.background=shape(if(name==tab)0xfff0e7dc.toInt() else cream,14);nav.addView(t,LinearLayout.LayoutParams(0,-2,1f));t.setOnClickListener { switch(name) } }
    }
    private fun home() {
        note(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d")))
        card(sage) { text("Monthly earnings",18,true,it); text(Studio.money(Studio.monthly(month).sumOf { p->p.amountMinor }),30,false,it);text("Payments received  ·  $month",12,false,it);val link=text("View insights ↗",13,false,it);link.minHeight=dp(48);link.gravity=android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.END;link.setOnClickListener{switch("Insights")} }
        val works=Studio.repository.commissions()
        val counts=LinearLayout(this);body.addView(counts,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(18)})
        listOf("${works.count{it.status=="In progress"}}\nCurrent work","${works.sumOf{c->c.tasks.count{t->!t.done}}}\nUnfinished tasks","${works.count{it.status=="Queued"}}\nIn queue").forEachIndexed { i,label->val t=TextView(this);t.text=label;t.typeface=supporting;t.textSize=15f;t.setTextColor(ink);t.gravity=17;t.minHeight=dp(66);counts.addView(t,LinearLayout.LayoutParams(0,-2,1f));t.setOnClickListener{if(i==1)switch("All tasks")else{filter=if(i==0)"In progress" else "Queued";switch("Commissions")}} }
        section("Current work");works.filter { it.status=="In progress" }.take(2).forEach { artwork(it) }
        section("Coming up");works.flatMap { c->c.tasks.filter { !it.done }.map { c to it } }.take(2).forEach { (c,t)->task(c,t) };val all=text("All tasks ↗",14);all.minHeight=dp(48);all.setOnClickListener{switch("All tasks")}
        section("Next in queue");works.firstOrNull { it.status=="Queued" }?.let { artwork(it) } ?: empty("No queued work","New requests will appear here.")
        button("+ Add commission",primary=true) { form() }
    }
    private fun commissions() {
        note("A little order for every artwork.");button("+ New commission",primary=true) { form() }
        choose("Show",listOf("In progress","Queued","Completed"),filter) { if(it!=filter) {filter=it;render()} }
        val search=input("Search artwork or client",query)
        val results=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL };body.addView(results)
        fun populate() { results.removeAllViews();val found=Studio.repository.commissions().filter { it.status==filter && (it.title+it.username).contains(query,true) };if(found.isEmpty())text("No commissions found. Try a different search or add a request.",16,false,results) else found.forEach { artwork(it,results) } }
        search.addTextChangedListener(object:TextWatcher { override fun beforeTextChanged(s:CharSequence?,a:Int,c:Int,f:Int){};override fun onTextChanged(s:CharSequence?,a:Int,b:Int,c:Int){query=s.toString();populate()};override fun afterTextChanged(e:Editable?){} });populate()
    }
    private fun period() { button("‹    $month    ›") { val options=(-12L..1L).map { YearMonth.now().plusMonths(it).toString() }.toTypedArray(); androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Reporting month").setItems(options) { _,i->month=YearMonth.parse(options[i]);render() }.show() } }
    private fun history() {
        note("Received payments across your artwork.");period();val payments=Studio.monthly(month).sortedByDescending { it.receivedOn }
        card(sage) { text(Studio.money(payments.sumOf { p->p.amountMinor }),30,false,it);text("${payments.size} payments received",14,false,it) }
        if(payments.isEmpty())empty("No payments this month","Received payments will appear here once recorded.")
        payments.forEach { p->card { box->text(p.receivedOn,13,false,box);text(Studio.money(p.amountMinor),24,false,box);text("${Studio.work(p.commissionId)?.title}\n${p.method} · ${p.note}",14,false,box);button("View commission",box) { detail(p.commissionId) } } }
    }
    private fun insights() {
        note("A clearer view of your creative income.");period()
        card(sage) { text("Monthly earnings",18,true,it);text(Studio.money(Studio.monthly(month).sumOf { p->p.amountMinor }),30,false,it);button("View payments",it) { switch("History") } }
        card { text("All-time received",17,true,it);text(Studio.money(Studio.repository.payments().sumOf { p->p.amountMinor }),26,false,it) }
        section("Six-month earnings")
        val series=(5L downTo 0L).map { month.minusMonths(it) }.map { it to Studio.monthly(it).sumOf { p->p.amountMinor } };val max=series.maxOf { it.second }.coerceAtLeast(1)
        series.forEach { (m,amount)->text("$m   ${Studio.money(amount)}",13);val bar=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.max=100;bar.progress=(amount*100/max).toInt();bar.progressTintList=android.content.res.ColorStateList.valueOf(0xff6a507d.toInt());bar.contentDescription="$m received ${Studio.money(amount)}";body.addView(bar,LinearLayout.LayoutParams(-1,dp(12))) };note("Received income (PHP) by month; six months ending $month.")
        section("By commission type");val grouped=Studio.monthly(month).groupBy { Studio.work(it.commissionId)?.type?:"Other" };if(grouped.isEmpty())note("No received payments for this period.");grouped.forEach { (type,ps)->text("$type  ·  ${Studio.money(ps.sumOf { it.amountMinor })}") }
        section("Current balances");note("Across all dates, including completed artwork.");Studio.repository.commissions().filter { Studio.balance(it)>0 }.forEach { c->button("${c.title}\n${Studio.money(Studio.balance(c))} remaining") { detail(c.id) } }
    }
    private fun reconnect() {
        note("Familiar clients. New possibilities.");section("Find a familiar connection")
        val current=Studio.repository.commissions().filter { it.status=="In progress" };if(current.isEmpty()){empty("No current work","Start a queued commission to choose a matching context.");return}
        if(current.none { it.id==selected })selected=current.first().id
        choose("Current commission",current.map { it.title },current.first { it.id==selected }.title) { name->val next=current.first { it.title==name }.id;if(next!=selected){selected=next;render()} }
        val c=Studio.work(selected)!!;card(lavender) { text("${c.type}  ·  ${c.genre}",17,true,it);text("Suggestions use commission type and genre.",14,false,it) }
        val results=Studio.repository.recommendations(selected)
        if(results.isEmpty())empty("No matching clients yet","Try another current commission. Matching completed artwork will appear here.")
        results.forEach { r->val previous=Studio.work(r.previousCommissionId)!!;card { text(r.clientName,22,true,it);text(previous.title,18,true,it);text("${r.reason}\n${previous.type} · ${previous.genre}\nFinished ${previous.finishedOn}",14,false,it);button("View previous commission",it) { detail(previous.id) } } }
    }
    private fun task(c:Commission,t:Task) { val row=LinearLayout(this);row.gravity=android.view.Gravity.CENTER_VERTICAL;body.addView(row);val check=CheckBox(this);check.contentDescription="Mark ${t.label} done";row.addView(check,LinearLayout.LayoutParams(dp(48),dp(56)));check.setOnClickListener{check.isChecked=false;pending()};val labels=LinearLayout(this);labels.orientation=LinearLayout.VERTICAL;row.addView(labels,LinearLayout.LayoutParams(0,-2,1f));text(t.label,16,true,labels);text("${c.username} · ${c.type}",12,false,labels);labels.setOnClickListener{detail(c.id)} }
    private fun tasks() { val pending=Studio.repository.commissions().flatMap { c->c.tasks.filter { !it.done }.map { c to it } };if(pending.isEmpty())empty("All caught up","Your studio has a little breathing room.");pending.forEach { (c,t)->task(c,t) };button("Back to Home") { switch("Home") } }
}
