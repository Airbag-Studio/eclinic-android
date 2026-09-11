package it.airbagstudio.ticare.pages.carePlans.selectActivity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Selezione corrente delle prestazioni da eseguire.
 *
 * Gli id delle pianificate (id planning) e delle non pianificate (id tipo prestazione)
 * vengono da due spazi diversi e si sovrappongono: negli esempi delle API entrambi
 * partono da 1. Con le due liste su una sola schermata una selezione condivisa
 * marcherebbe la riga sbagliata, quindi i due insiemi restano separati (TS1-5).
 *
 * L'esecuzione "consuma" la selezione: [consume] la restituisce e la azzera nello stesso
 * gesto, prima di qualsiasi chiamata di rete. Così il bottone "Esegui selezionate" sparisce
 * al primo tocco e un secondo tocco non trova nulla da inviare; e alla riapertura del popup,
 * il cui ViewModel sopravvive alla chiusura, non resta nulla di spuntato (AEMF-3).
 */
internal class ActivitySelection {

    /** Istantanea della selezione nel momento in cui viene consumata. */
    data class Snapshot(val plannedIds: List<Int>, val unplannedIds: List<Int>)

    private val _plannedIds = MutableStateFlow<List<Int>>(listOf())
    private val _unplannedIds = MutableStateFlow<List<Int>>(listOf())

    val plannedIds: StateFlow<List<Int>> = _plannedIds.asStateFlow()
    val unplannedIds: StateFlow<List<Int>> = _unplannedIds.asStateFlow()

    fun change(id: Int, isPlanned: Boolean, isSelected: Boolean) {
        val target = if (isPlanned) _plannedIds else _unplannedIds
        target.update { selected ->
            when {
                isSelected && !selected.contains(id) -> selected + id
                !isSelected -> selected - id
                else -> selected
            }
        }
    }

    /**
     * Il "seleziona tutte" vale solo per le pianificate ed è additivo: le non pianificate
     * già selezionate restano tali e vanno comunque spuntate una a una, per evitare la
     * spunta massiva che il cliente ha chiesto esplicitamente di impedire.
     */
    fun togglePlanned(allPlannedIds: List<Int>) {
        _plannedIds.value = if (_plannedIds.value.size >= allPlannedIds.size) {
            listOf()
        } else {
            allPlannedIds
        }
    }

    /** Restituisce la selezione corrente e la azzera nello stesso gesto. */
    fun consume(): Snapshot {
        val snapshot = Snapshot(_plannedIds.value, _unplannedIds.value)
        _plannedIds.value = listOf()
        _unplannedIds.value = listOf()
        return snapshot
    }
}
