package com.obra.studio.data

import java.io.Serializable
import java.time.LocalDate
import java.time.YearMonth
import java.math.BigDecimal

data class Task(val id: Long, val label: String, var done: Boolean = false): Serializable
data class Commission(
    val id: Long, val title: String, val username: String, val platform: String,
    val type: String, val genre: String, val status: String, val stage: String,
    val totalMinor: Long, val commissionOn: String, val dueOn: String,
    val plannedStart: String = "", val startedOn: String = "", val finishedOn: String = "",
    val brief: String = "",
    val tasks: List<Task> = emptyList(),
    val displayName: String = "", val tags: String = "", val images: List<String> = emptyList(),
    val background: Boolean = false, val clientId: Long = 0, val typeId: Long = 0,
    val genreId: Long? = null, val revision: String = "", val creationToken: String = ""
): Serializable
data class Payment(val id: Long, val commissionId: Long, val amountMinor: Long, val receivedOn: String, val method: String, val note: String = "", val submissionToken: String = ""): Serializable
data class Recommendation(val clientName: String, val previousCommissionId: Long, val reason: String)

/** Backend team replaces this read provider. IDs, not visible titles, identify records. */
interface StudioRepository {
    fun commissions(): List<Commission>
    fun payments(): List<Payment>
    fun recommendations(selectedCommissionId: Long): List<Recommendation>
}
/** Called on the application database worker. */
interface StudioWriter {
    fun saveCommission(draft: CommissionDraft, submissionToken: String): Long
    fun updateCommission(id: Long, draft: CommissionDraft)
    fun recordPayment(commissionId: Long, amountMinor: Long, receivedOn: String, method: String, note: String, submissionToken: String)
    fun startCommission(id: Long, startedOn: String)
    fun updateProgress(id: Long, stage: String, finishedOn: String?)
    fun setTaskDone(commissionId: Long, taskId: Long, done: Boolean)
}
data class CommissionDraft(
    var platform: String = "X", var username: String = "", var displayName: String = "",
    var title: String = "", var type: String = "Portrait", var genre: String = "", var tags: String = "", var brief: String = "",
    var total: String = "1800.00", var commissionOn: String = LocalDate.now().toString(),
    var dueOn: String = LocalDate.now().plusDays(14).toString(), var plannedStart: String = "",
    var initialEnabled: Boolean = false, var initialAmount: String = "", var initialDate: String = LocalDate.now().toString(),
    var initialMethod: String = "GCash", var initialNote: String = "", var background: Boolean = false,
    var images: ArrayList<String> = arrayListOf(), var expectedRevision: String? = null
): Serializable
object Validation {
    fun minor(input: String): Long? = try { BigDecimal(input.trim()).movePointRight(2).longValueExact().takeIf { it in 1..100000000000L } } catch (_: Exception) { null }
    fun date(input: String): LocalDate? = try { LocalDate.parse(input) } catch (_: Exception) { null }
    fun schedule(d: CommissionDraft, original: Commission?): String? {
        val created = date(d.commissionOn) ?: return "Choose a valid commission date."
        val due = date(d.dueOn) ?: return "Choose a valid due date."
        if (created > LocalDate.now()) return "Commission date cannot be in the future."
        if (due < created) return "Due date cannot precede the commission date."
        if (d.plannedStart.isNotBlank()) { val start = date(d.plannedStart) ?: return "Choose a valid planned start."; if (start < created || start > due) return "Planned start must fall between commission and due dates." }
        if (original != null && original.startedOn.isNotEmpty() && created > date(original.startedOn)) return "Commission date cannot be after the actual start."
        return null
    }
}
object Fixtures: StudioRepository {
    private val today = LocalDate.now()
    private fun day(offset: Long) = today.plusDays(offset).toString()
    private val work = listOf(
        Commission(1,"Half-body illustration","kai.inks","X","Half body","Fantasy","In progress","Lineart",150000,day(-13),day(1),day(-12),day(-12)),
        Commission(2,"Fantasy portrait","mika.draws","Discord","Portrait","Fantasy","In progress","Lineart",180000,day(-13),day(4),day(-11),day(-11)),
        Commission(3,"Moonlit portrait","luna.art","Discord","Half body","Fantasy","Queued","",200000,day(-1),day(10),day(2)),
        Commission(4,"Forest chibi","ren.art","Discord","Chibi","Fantasy","Queued","",120000,day(-8),day(14),day(3)),
        Commission(5,"Cozy café illustration","bea.sketch","X","Full illustration","Slice of life","Completed","Finished",450000,day(-22),day(-3),day(-20),day(-20),day(-2)),
        Commission(6,"Autumn portrait","@tala.arts","X","Portrait","Fantasy","Completed","Finished",180000,day(-40),day(-16),day(-38),day(-38),day(-15))
    )
    override fun commissions() = work
    override fun payments() = listOf(Payment(1,1,50000,day(-10),"GCash","Initial payment"), Payment(2,2,180000,day(-8),"Bank transfer","Paid in full"),Payment(3,4,60000,day(-7),"GCash","Initial payment"),Payment(4,5,250000,day(-20),"Bank transfer","Initial payment"),Payment(5,6,180000,day(-15),"GCash","Paid in full"))
    override fun recommendations(selectedCommissionId: Long) = if (selectedCommissionId == 2L) listOf(Recommendation("@tala.arts",6,"Same type & genre")) else emptyList()
}
object Studio {
    // UI reads a complete in-memory snapshot published after a background database read.
    var repository: StudioRepository = StudioSnapshot(emptyList(), emptyList())
    fun work(id: Long) = repository.commissions().find { it.id == id }
    fun received(id: Long) = repository.payments().filter { it.commissionId == id }.sumOf { it.amountMinor }
    fun balance(c: Commission) = c.totalMinor - received(c.id)
    fun monthly(month: YearMonth) = repository.payments().filter { it.receivedOn.startsWith(month.toString()) }
    fun money(minor: Long) = "₱%,.2f".format(java.util.Locale.US, minor / 100.0)
}

class StudioSnapshot(
    private val work: List<Commission>, private val receipts: List<Payment>,
    val activeTypes: List<String> = emptyList(), val activeGenres: List<String> = emptyList()
) : StudioRepository {
    override fun commissions() = work
    override fun payments() = receipts
    override fun recommendations(selectedCommissionId: Long): List<Recommendation> {
        val selected = work.find { it.id == selectedCommissionId && it.status == "In progress" }
            ?: return emptyList()
        val genreId = selected.genreId ?: return emptyList()
        return work.asSequence()
            .filter { it.status == "Completed" && it.finishedOn.isNotBlank() &&
                it.clientId != selected.clientId && it.typeId == selected.typeId && it.genreId == genreId }
            .sortedWith(compareByDescending<Commission> { it.finishedOn }.thenByDescending { it.id })
            .distinctBy { it.clientId }.take(5)
            .map { Recommendation(it.username, it.id, "Same type & genre") }.toList()
    }
}
