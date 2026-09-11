package it.airbagstudio.ticare.pages.carePlans.selectActivity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * AEMF-3 — la selezione viene consumata al tocco su "Esegui selezionate", così un secondo
 * tocco non trova nulla da inviare e alla riapertura del popup non resta nulla di spuntato.
 */
class ActivitySelectionTest {

    @Test
    fun `consumare la selezione la restituisce e la azzera nello stesso gesto`() {
        val selection = ActivitySelection()
        selection.change(id = 1, isPlanned = true, isSelected = true)
        selection.change(id = 1, isPlanned = false, isSelected = true)
        selection.change(id = 7, isPlanned = false, isSelected = true)

        val snapshot = selection.consume()

        assertEquals(listOf(1), snapshot.plannedIds)
        assertEquals(listOf(1, 7), snapshot.unplannedIds)
        assertTrue(selection.plannedIds.value.isEmpty())
        assertTrue(selection.unplannedIds.value.isEmpty())
    }

    @Test
    fun `un secondo consumo non trova nulla da inviare`() {
        val selection = ActivitySelection()
        selection.change(id = 3, isPlanned = true, isSelected = true)
        selection.consume()

        val second = selection.consume()

        assertTrue(second.plannedIds.isEmpty())
        assertTrue(second.unplannedIds.isEmpty())
    }

    @Test
    fun `pianificate e non pianificate con lo stesso id restano separate`() {
        val selection = ActivitySelection()
        selection.change(id = 1, isPlanned = true, isSelected = true)

        selection.change(id = 1, isPlanned = false, isSelected = false)

        assertEquals(listOf(1), selection.plannedIds.value)
        assertTrue(selection.unplannedIds.value.isEmpty())
    }

    @Test
    fun `selezionare due volte lo stesso id non lo duplica`() {
        val selection = ActivitySelection()
        selection.change(id = 5, isPlanned = false, isSelected = true)
        selection.change(id = 5, isPlanned = false, isSelected = true)

        assertEquals(listOf(5), selection.unplannedIds.value)
    }

    @Test
    fun `seleziona tutte le pianificate lascia intatte le non pianificate`() {
        val selection = ActivitySelection()
        selection.change(id = 9, isPlanned = false, isSelected = true)

        selection.togglePlanned(allPlannedIds = listOf(1, 2, 3))

        assertEquals(listOf(1, 2, 3), selection.plannedIds.value)
        assertEquals(listOf(9), selection.unplannedIds.value)
    }

    @Test
    fun `seleziona tutte con tutte le pianificate spuntate le deseleziona`() {
        val selection = ActivitySelection()
        selection.togglePlanned(allPlannedIds = listOf(1, 2))

        selection.togglePlanned(allPlannedIds = listOf(1, 2))

        assertTrue(selection.plannedIds.value.isEmpty())
    }
}
