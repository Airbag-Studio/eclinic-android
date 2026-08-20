package it.airbagstudio.ticare.pages.carePlans.selectActivity

import ch.ticare.eclinic.library.entity.BillingMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TS1-4 — raggruppamento delle prestazioni a domicilio per Modalità di fatturazione.
 */
class SectionsByBillingModeTest {

    private val consigli = BillingMode(
        code = "A", id = 10, isAssistance = 0,
        label = "A - Consigli e Istruzioni", name = "Consigli e Istruzioni"
    )
    private val esami = BillingMode(
        code = "B", id = 11, isAssistance = 0,
        label = "B - Esami e Cure", name = "Esami e Cure"
    )
    private val cureDiBase = BillingMode(
        code = "C", id = 12, isAssistance = 0,
        label = "C - Cure di Base", name = "Cure di Base"
    )
    private val billingModes = listOf(consigli, esami, cureDiBase)

    private fun item(id: Int, title: String = "Prestazione $id") =
        SelectCareActivityPopupUIState.ActivityListItem(
            title = title,
            code = null,
            id = id,
            carePlanId = null,
            isPlanned = false,
            isTransferActivity = false,
            isSelected = false
        )

    @Test
    fun `i blocchi seguono l'ordine delle modalita' di configurazione`() {
        val sections = sectionsByBillingMode(
            billingModes,
            listOf(
                cureDiBase.id to item(1),
                consigli.id to item(2),
                esami.id to item(3)
            )
        )

        assertEquals(
            listOf("A - Consigli e Istruzioni", "B - Esami e Cure", "C - Cure di Base"),
            sections.map { it.title }
        )
        assertEquals(listOf(2), sections[0].items.map { it.id })
        assertEquals(listOf(3), sections[1].items.map { it.id })
        assertEquals(listOf(1), sections[2].items.map { it.id })
    }

    @Test
    fun `le modalita' senza prestazioni non producono un blocco vuoto`() {
        val sections = sectionsByBillingMode(
            billingModes,
            listOf(esami.id to item(1))
        )

        assertEquals(listOf("B - Esami e Cure"), sections.map { it.title })
    }

    @Test
    fun `le prestazioni senza modalita' finiscono nel blocco di coda`() {
        val sections = sectionsByBillingMode(
            billingModes,
            listOf(
                consigli.id to item(1),
                null to item(2),
                99 to item(3)
            )
        )

        assertEquals(2, sections.size)
        assertEquals("A - Consigli e Istruzioni", sections[0].title)

        val other = sections[1]
        assertNull(other.title)
        assertNotNull(other.titleRes)
        // modalità assente e modalità sconosciuta finiscono entrambe qui
        assertEquals(listOf(2, 3), other.items.map { it.id })
    }

    @Test
    fun `senza modalita' configurate tutte le prestazioni restano visibili in coda`() {
        val sections = sectionsByBillingMode(
            billingModes = emptyList(),
            items = listOf(consigli.id to item(1), null to item(2))
        )

        assertEquals(1, sections.size)
        assertEquals(listOf(1, 2), sections[0].items.map { it.id })
    }

    @Test
    fun `nessuna prestazione produce nessun blocco`() {
        assertTrue(sectionsByBillingMode(billingModes, emptyList()).isEmpty())
    }
}
