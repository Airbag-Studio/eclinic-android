package it.airbagstudio.ticare.crash

import android.content.Context
import io.sentry.Breadcrumb
import io.sentry.SentryEvent
import io.sentry.android.core.SentryAndroid
import it.airbagstudio.ticare.BuildConfig
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Invio dei crash a Sentry (AFAG-163): da Sentry il crash-agent dello studio
 * apre una issue GitHub con causa e file, e un bug in Plan collegato. Ha preso
 * il posto di Firebase Crashlytics, che il crash-agent non riusciva a leggere.
 *
 * eClinic è un'app clinica, quindi la configurazione è quella stretta delle
 * convenzioni Airbag (§5) per il MedTech: nessun dato personale di default,
 * niente screenshot, gerarchia delle viste né replay, niente tracce, e ogni
 * testo passa da [Redactor] prima di partire.
 *
 * **Senza DSN non parte nulla**: con `SENTRY_DSN` vuoto in app/build.gradle
 * l'SDK non viene inizializzato.
 */
object ErrorReporting {

    /** Da chiamare in `Application.onCreate`, prima di qualunque altra cosa. */
    fun init(context: Context) {
        if (BuildConfig.SENTRY_DSN.isEmpty()) return

        SentryAndroid.init(context) { options ->
            options.dsn = BuildConfig.SENTRY_DSN
            // `debug` nelle build di debug, escluse dal crash-agent. Le release dei
            // tre flavour sono `production` e si distinguono dal bundle nella
            // release, che mette l'SDK: `<applicationId>@<versionName>+<versionCode>`.
            options.environment = if (BuildConfig.DEBUG) "debug" else "production"
            // Niente IP, utente né dispositivo con nome: un crash report è un log.
            options.isSendDefaultPii = false
            options.isAttachScreenshot = false
            options.isAttachViewHierarchy = false
            // Il replay è escluso dal pacchetto (app/build.gradle); a zero per scritto.
            options.sessionReplay.sessionSampleRate = 0.0
            options.sessionReplay.onErrorSampleRate = 0.0
            // Solo errori: `tracesSampleRate` resta null.
            options.setBeforeSend { event, _ -> filter(event) }
            options.setBeforeBreadcrumb { breadcrumb, _ -> scrub(breadcrumb) }
        }
    }

    /**
     * Decide se un evento va inviato e, se sì, lo ripulisce.
     *
     * - **scarta** gli errori di rete attesi ([isExpectedTransportError]): un
     *   tablet in reparto senza segnale non è un difetto dell'app;
     * - **ripulisce** messaggio e testi delle eccezioni con [Redactor].
     */
    fun filter(event: SentryEvent): SentryEvent? {
        if (isExpectedTransportError(event.throwable)) return null

        event.message?.let { message ->
            message.formatted = message.formatted?.let(Redactor::apply)
            message.message = message.message?.let(Redactor::apply)
        }
        event.exceptions?.forEach { exception ->
            exception.value = exception.value?.let(Redactor::apply)
        }
        return event
    }

    private fun scrub(breadcrumb: Breadcrumb): Breadcrumb {
        breadcrumb.message = breadcrumb.message?.let(Redactor::apply)
        return breadcrumb
    }

    /**
     * Vero per gli errori che dicono «la rete non c'è», non «l'app è rotta»:
     * host irraggiungibile, connessione rifiutata o caduta, timeout, handshake
     * fallito. Guarda anche le cause, perché Ktor le avvolge nelle sue.
     */
    fun isExpectedTransportError(throwable: Throwable?): Boolean =
        generateSequence(throwable) { it.cause }.take(8).any { cause ->
            cause is UnknownHostException ||
                cause is ConnectException ||
                cause is SocketTimeoutException ||
                cause is SSLException ||
                cause.javaClass.simpleName == "HttpRequestTimeoutException" ||
                (cause is IOException && cause.message?.contains("Connection reset") == true)
        }
}
