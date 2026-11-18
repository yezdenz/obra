package com.obra.studio.ui

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import com.obra.studio.data.*

/** Keep the everyday workflow simple; retain optional stages/tasks without duplicating it. */
class DetailActivity : BaseActivity() {
    private var moreDetails = false

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        moreDetails = state?.getBoolean("moreDetails") ?: false
    }

    override fun onSaveInstanceState(out: Bundle) {
        out.putBoolean("moreDetails", moreDetails)
        super.onSaveInstanceState(out)
    }

    override fun onStudioReady() {
        val position = scroll.scrollY
        val c = Studio.work(intent.getLongExtra("commissionId", 0))
        if (c == null) {
            reset("Commission unavailable")
            empty("Record unavailable", "Return to the list and choose another commission.")
            return
        }
        reset("Commission details")
        headerAction("Edit") { form("edit", c.id) }
        card(lavender) {
            text(c.status, 14, false, it)
            text(c.title, 25, true, it)
            text("${c.username} · ${c.platform}", 15, false, it)
            text(deadline(c), 15, false, it)
            if (c.status == "In progress") {
                text("Keep working at your own pace. Mark completed when the artwork is ready.", 13, false, it)
            }
        }
        val received = Studio.received(c.id)
        val remaining = Studio.balance(c)
        card(sage) { panel ->
            text(if (remaining == 0L) "Fully paid" else "Still to collect", 18, true, panel)
            text(Studio.money(remaining), 28, false, panel)
            text("${Studio.money(received)} received of ${Studio.money(c.totalMinor)}", 14, false, panel)
            if (remaining > 0L && c.status != "Completed") {
                button("Record payment", panel) { form("payment", c.id) }
            }
            button("Payment history", panel) { form("payments", c.id) }
        }
        section("Artwork brief")
        text(listOf(c.type, c.genre).filter { it.isNotBlank() }.joinToString(" · "))
        text(c.brief.ifBlank { "No extra instructions." })
        if (c.images.isEmpty()) {
            note("No reference images added.")
        } else {
            button("View references (${c.images.size})") {
                startActivity(Intent(this, ViewerActivity::class.java).putExtra("commissionId", c.id)
                    .putStringArrayListExtra("images", ArrayList(c.images)))
            }
        }
        button(if (moreDetails) "Hide extra details" else "More details & optional checklist") {
            moreDetails = !moreDetails
            onStudioReady()
        }
        if (moreDetails) {
            section("Schedule")
            text("Commission date: ${readableDate(c.commissionOn)}\n" +
                "Planned start: ${c.plannedStart.ifBlank { "Not set" }.let(::readableDate)}\n" +
                "Actual start: ${c.startedOn.ifBlank { "Not started" }.let(::readableDate)}\n" +
                "Due date: ${readableDate(c.dueOn)}\n" +
                "Finished: ${c.finishedOn.ifBlank { "Not finished" }.let(::readableDate)}")
            if (c.displayName.isNotBlank()) text("Client name: ${c.displayName}")
            if (c.tags.isNotBlank()) text("Style: ${c.tags}")
            if (c.background) note("Detailed background included")
            if (c.status == "In progress") {
                section("Art stage (optional)")
                note("Current: ${c.stage}. Use this only if you want to track drawing stages.")
                button("Change art stage") { form("progress", c.id) }
            }
            if (c.tasks.isNotEmpty()) {
                section("Optional checklist")
                note("Personal reminders only. These do not change the artwork's status or payments.")
                c.tasks.forEach { task ->
                    CheckBox(this).apply {
                        text = task.label; typeface = supporting; isChecked = task.done
                        minHeight = dp(48); setTextColor(ink)
                        body.addView(this)
                        setOnClickListener {
                            isChecked = task.done
                            write({ it.setTaskDone(c.id, task.id, !task.done) }) { onStudioReady() }
                        }
                    }
                }
            }
        }
        // One fixed primary action, selected for the current work status.
        when (c.status) {
            "Queued" -> button("Start commission", footer, true) {
                confirm("Start this commission?", "Move it to In progress. Payments stay unchanged.") {
                    write({ it.startCommission(c.id, java.time.LocalDate.now().toString()) }) { onStudioReady() }
                }
            }
            "In progress" -> button("Mark completed", footer, true) { completeArtwork(c.id) }
            "Completed" -> if (remaining > 0L) {
                button("Record payment", footer, true) { form("payment", c.id) }
            } else {
                text("Artwork complete · Fully paid", 14, false, footer)
            }
        }
        scroll.post { scroll.scrollTo(0, position) }
    }
}
