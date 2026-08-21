package it.airbagstudio.ticare.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Dialog che occupa davvero tutto lo schermo, barre di sistema comprese.
 *
 * Un `Dialog` di Compose, anche con `usePlatformDefaultWidth = false`, non si estende sotto
 * la barra di stato e quella di navigazione: nelle zone scoperte resta visibile lo scrim di
 * oscuramento, che appare come una banda scura sopra e sotto. Con `decorFitsSystemWindows`
 * a false la finestra copre l'intero schermo e la gestione degli inset torna al contenuto —
 * `Scaffold` e le top bar di Material 3 se ne occupano da soli.
 *
 * Da usare al posto di `Dialog` per le schermate a tutto schermo (AEMF-2).
 */
@Composable
fun FullScreenDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        content()
    }
}
