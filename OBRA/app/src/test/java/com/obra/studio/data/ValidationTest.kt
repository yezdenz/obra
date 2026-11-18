package com.obra.studio.data

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ValidationTest {
    @org.junit.Before fun useExplicitFixtures() { Studio.repository = Fixtures }
    @org.junit.After fun resetRepository() { Studio.repository = StudioSnapshot(emptyList(), emptyList()) }
    @Test fun exactCentavos() { assertEquals(150001L,Validation.minor("1500.01")) }
    @Test fun rejectsFractionalCentavo() { assertNull(Validation.minor("12.345")) }
    @Test fun rejectsNonPositiveMoney() { assertNull(Validation.minor("0"));assertNull(Validation.minor("-1")) }
    @Test fun rejectsOverflow() { assertNull(Validation.minor("999999999999999999999")) }
    @Test fun rejectsMalformedMoney() { assertNull(Validation.minor("abc")) }
    @Test fun rejectsImpossibleDate() { assertNull(Validation.date("2026-02-30")) }
    @Test fun acceptsLeapDate() { assertNotNull(Validation.date("2028-02-29")) }
    @Test fun rejectsDeadlineBeforeCreation() { val d=CommissionDraft(commissionOn="2026-01-02",dueOn="2026-01-01");assertNotNull(Validation.schedule(d,null)) }
    @Test fun rejectsPlannedStartOutsideRange() { val d=CommissionDraft(commissionOn="2026-01-01",dueOn="2026-01-03",plannedStart="2026-01-04");assertNotNull(Validation.schedule(d,null)) }
    @Test fun acceptsOptionalStart() { val d=CommissionDraft(commissionOn="2026-01-01",dueOn="2026-01-03");assertNull(Validation.schedule(d,null)) }
    @Test fun rejectsCreationAfterActualStart() { val c=Fixtures.commissions().first();val d=CommissionDraft(commissionOn=LocalDate.now().toString(),dueOn=LocalDate.now().plusDays(3).toString());assertNotNull(Validation.schedule(d,c)) }
    @Test fun completionDoesNotImplyPaid() { val c=Fixtures.commissions().first{it.id==5L};assertEquals("Completed",c.status);assertTrue(Studio.balance(c)>0) }
    @Test fun fullyPaidCanStillBeInProgress() { val c=Fixtures.commissions().first{it.id==2L};assertEquals("In progress",c.status);assertEquals(0L,Studio.balance(c)) }
}
