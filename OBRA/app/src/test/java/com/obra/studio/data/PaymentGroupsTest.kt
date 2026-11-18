package com.obra.studio.data

import java.time.YearMonth
import org.junit.Assert.*
import org.junit.Test

class PaymentGroupsTest {
    private val november=YearMonth.of(2026,11)
    private fun payment(id:Long,work:Long,amount:Long,date:String)=Payment(id,work,amount,date,"GCash")

    @Test fun initialAndBalanceAppearInOneCommissionGroup() {
        val receipts=listOf(payment(2,7,150000,"2026-11-18"),payment(1,7,50000,"2026-11-01"))
        val group=groupPaymentsForMonth(receipts,november).single()
        assertEquals(7L,group.commissionId);assertEquals(200000L,group.periodReceived)
        assertEquals(listOf(1L,2L),group.allPayments.map { it.id })
        assertEquals(2,group.periodPayments.size)
    }

    @Test fun separateCommissionsStaySeparateEvenForTheSameReceiptDates() {
        val groups=groupPaymentsForMonth(listOf(payment(1,7,50000,"2026-11-18"),payment(2,8,50000,"2026-11-18")),november)
        assertEquals(setOf(7L,8L),groups.map { it.commissionId }.toSet())
    }

    @Test fun earlierDepositIsVisibleWithoutAddingItToThisMonthsEarnings() {
        val receipts=listOf(payment(1,7,50000,"2026-10-30"),payment(2,7,150000,"2026-11-18"))
        val group=groupPaymentsForMonth(receipts,november).single()
        assertEquals(150000L,group.periodReceived);assertEquals(200000L,group.totalReceived)
        assertEquals(1,group.periodPayments.size);assertEquals(2,group.allPayments.size)
    }

    @Test fun artworkWithoutReceiptsInTheSelectedMonthIsNotIncluded() {
        val groups=groupPaymentsForMonth(listOf(payment(1,7,50000,"2026-10-30"),payment(2,8,60000,"2026-11-18")),november)
        assertEquals(listOf(8L),groups.map { it.commissionId })
    }

    @Test fun groupsUseTheLatestInPeriodReceiptWithAnIdTieBreak() {
        val receipts=listOf(payment(1,7,10000,"2026-11-18"),payment(2,8,10000,"2026-11-18"),
            payment(3,9,10000,"2026-11-19"),payment(4,7,10000,"2026-12-01"))
        assertEquals(listOf(9L,8L,7L),groupPaymentsForMonth(receipts,november).map { it.commissionId })
    }

    @Test fun decemberBoundariesDoNotIncludeJanuaryReceipts() {
        val groups=groupPaymentsForMonth(listOf(payment(1,7,10000,"2026-12-31"),payment(2,7,20000,"2027-01-01")),YearMonth.of(2026,12))
        assertEquals(10000L,groups.single().periodReceived);assertEquals(30000L,groups.single().totalReceived)
    }

    @Test fun groupingPreservesTheExactMonthlyTotalAndOriginalReceipts() {
        val receipts=listOf(payment(1,7,12345,"2026-11-01"),payment(2,7,67890,"2026-11-18"),
            payment(3,8,80000,"2026-11-19"),payment(4,9,90000,"2026-10-30"))
        val original=receipts.toList()
        val groups=groupPaymentsForMonth(receipts,november)
        assertEquals(160235L,groups.sumOf { it.periodReceived })
        assertEquals(3,groups.sumOf { it.periodPayments.size })
        assertEquals(original,receipts)
    }

    @Test fun emptyMonthsReturnNoGroups() {
        assertTrue(groupPaymentsForMonth(emptyList(),november).isEmpty())
        assertTrue(groupPaymentsForMonth(listOf(payment(1,7,10000,"2026-10-31")),november).isEmpty())
    }
}
