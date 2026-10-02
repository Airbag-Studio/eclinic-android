package it.airbagstudio.ticare.crash

import io.sentry.SentryEvent
import io.sentry.protocol.Message
import io.sentry.protocol.SentryException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException

/**
 * AFAG-163 — cosa esce dal tablet verso Sentry: gli errori di rete attesi non
 * partono, e i testi che partono sono ripuliti dai dati personali.
 */
class ErrorReportingTest {

    @Test
    fun `redige email, AVS, codice fiscale e token`() {
        val text = "Paziente mario.rossi@example.com AVS 756.1234.5678.97 " +
            "CF RSSMRA80A01H501U Authorization: Bearer abcdefgh12345678"

        assertEquals(
            "Paziente <email> AVS <avs> CF <cf> Authorization: Bearer <token>",
            Redactor.apply(text),
        )
    }

    @Test
    fun `redige i valori delle chiavi sensibili e lascia il resto leggibile`() {
        assertEquals(
            "login fallito: password=<redacted> per utente 42",
            Redactor.apply("login fallito: password=s3gr3t0 per utente 42"),
        )
    }

    @Test
    fun `scarta gli errori di rete attesi, anche avvolti`() {
        val wrapped = IOException("request failed", UnknownHostException("api.ticare.ch"))

        assertNull(ErrorReporting.filter(SentryEvent(wrapped)))
    }

    @Test
    fun `lascia passare un errore dell'app e ne ripulisce i testi`() {
        val event = SentryEvent(IllegalStateException("x")).apply {
            message = Message().apply { formatted = "Salvataggio fallito per anna@clinica.ch" }
            exceptions = listOf(
                SentryException().apply { value = "Paziente 756.1234.5678.97 non trovato" },
            )
        }

        val out = ErrorReporting.filter(event)

        assertNotNull(out)
        assertEquals("Salvataggio fallito per <email>", out!!.message!!.formatted)
        assertEquals("Paziente <avs> non trovato", out.exceptions!!.single().value)
    }
}
