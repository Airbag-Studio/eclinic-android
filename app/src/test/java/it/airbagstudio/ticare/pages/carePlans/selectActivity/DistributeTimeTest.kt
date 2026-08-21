package it.airbagstudio.ticare.pages.carePlans.selectActivity

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * TS1-5 — ripartizione dei minuti sull'intera selezione, pianificate e non pianificate insieme.
 */
class DistributeTimeTest {

    @Test
    fun `lo scenario del cliente non lascia a zero la prestazione non pianificata`() {
        // 3 pianificate da 10 minuti + 1 non pianificata da 10, su 40 minuti trascorsi:
        // eseguendo prima le pianificate e poi le non pianificate in due passaggi separati,
        // le prime consumavano tutti i minuti e l'ultima restava a zero
        val distributed = distributeTime(listOf(10, 10, 10, 10), elapsedTime = 40)

        assertEquals(listOf(10, 10, 10, 10), distributed)
        assertEquals(40, distributed.sum())
    }

    @Test
    fun `i minuti sono ripartiti in proporzione alla durata prevista`() {
        val distributed = distributeTime(listOf(10, 20, 30), elapsedTime = 60)

        assertEquals(listOf(10, 20, 30), distributed)
    }

    @Test
    fun `il totale ripartito coincide sempre con i minuti trascorsi`() {
        val distributed = distributeTime(listOf(7, 13, 5), elapsedTime = 31)

        assertEquals(31, distributed.sum())
    }

    @Test
    fun `durate tutte a zero non producono minuti negativi o sbilanciati`() {
        val distributed = distributeTime(listOf(0, 0), elapsedTime = 20)

        assertEquals(listOf(0, 0), distributed)
    }

    @Test
    fun `una sola prestazione selezionata prende tutti i minuti`() {
        val distributed = distributeTime(listOf(15), elapsedTime = 42)

        assertEquals(listOf(42), distributed)
    }
}
