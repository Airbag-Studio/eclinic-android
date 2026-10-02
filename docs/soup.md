# SOUP — Software of Unknown Provenance

Dipendenze di terze parti di eClinic Android, come chiede la convenzione Airbag
§10 per i progetti MedTech: nome, versione, scopo, rischi noti, requisiti.

> **Lista avviata il 2026-10-02 con AFAG-163, e incompleta.** Contiene solo le
> dipendenze introdotte da quella data. Le precedenti (Compose, Hilt, Ktor,
> SQLDelight, Coil, Vico, logback-android, la libreria `shared-android` di
> Ti-Care…) sono da censire: è debito dichiarato, non un elenco chiuso.

| Nome | Versione | Scopo | Rischi noti e mitigazioni | Requisiti |
|---|---|---|---|---|
| `io.sentry:sentry-android` (senza `sentry-android-replay`) | 8.59.0 | Crash reporting: eccezioni non gestite, ANR e crash nativi inviati al progetto Sentry `eclinic-android` (org `airbag-studio-srl`), da cui il crash-agent di studio apre issue e bug | **Dati personali o clinici in uscita.** Mitigazioni: `sendDefaultPii` spento; niente screenshot, gerarchia delle viste, session replay (modulo escluso dal pacchetto) né tracce; nessuna strumentazione automatica di rete, database o log (plugin Gradle con `tracingInstrumentation` e `autoInstallation` spenti); messaggi e breadcrumb redatti da `crash/Redactor.kt` (email, AVS, codice fiscale, P.IVA, token). Ciò che una regex non riconosce — un nome, una diagnosi in un messaggio d'errore — resta un rischio residuo: i messaggi d'errore non devono interpolare dati clinici. **Dati negli USA** (regione dell'org): Sentry è sub-responsabile del trattamento, da comunicare a Ti-Care. | Android minSdk 28; rete in uscita verso `*.ingest.us.sentry.io` |
| `io.sentry.android.gradle` (plugin di build) | 6.23.0 | Caricamento del mapping R8 quando la minificazione sarà accesa; non entra nell'app | Strumentazione automatica del bytecode: spenta (vedi sopra). Telemetria del plugin spenta. | Solo in build |
