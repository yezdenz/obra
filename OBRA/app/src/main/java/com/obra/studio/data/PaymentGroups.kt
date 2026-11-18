package com.obra.studio.data

import java.time.YearMonth

/** Presentation grouping only: receipts remain separate, immutable saved payments. */
data class CommissionPaymentGroup(
    val commissionId: Long,
    val periodPayments: List<Payment>,
    val allPayments: List<Payment>
) {
    val periodReceived: Long get() = periodPayments.sumOf { it.amountMinor }
    val totalReceived: Long get() = allPayments.sumOf { it.amountMinor }
}

fun groupPaymentsForMonth(payments: List<Payment>, month: YearMonth): List<CommissionPaymentGroup> {
    val first=month.atDay(1).toString()
    val next=month.plusMonths(1).atDay(1).toString()
    val receiptOrder=compareBy<Payment> { it.receivedOn }.thenBy { it.id }
    return payments.groupBy { it.commissionId }.mapNotNull { (id, receipts) ->
        val inPeriod=receipts.filter { it.receivedOn>=first && it.receivedOn<next }.sortedWith(receiptOrder)
        if(inPeriod.isEmpty())null else CommissionPaymentGroup(id,inPeriod,receipts.sortedWith(receiptOrder))
    }.sortedWith(compareByDescending<CommissionPaymentGroup> { it.periodPayments.last().receivedOn }
        .thenByDescending { it.periodPayments.last().id })
}
