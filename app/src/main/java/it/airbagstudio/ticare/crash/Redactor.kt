package it.airbagstudio.ticare.crash

/**
 * Toglie da un testo ciò che non deve uscire dal dispositivo, prima che un
 * crash report parta per Sentry (AFAG-163).
 *
 * Stesse regole della redazione di studio (`airbag_log`, convenzioni Airbag
 * §5), più il numero AVS svizzero: eClinic è un'app clinica di Ti-Care, e un
 * messaggio d'errore può portarsi dietro l'identità di un paziente.
 *
 * Il criterio è «meglio un falso positivo che un dato in chiaro»: un numero di
 * 11 cifre viene mascherato anche se non era una partita IVA. Le regole girano
 * in ordine, le strutturate (JWT, AVS) prima delle generiche che ne
 * mangerebbero un pezzo. Quello che una regex non riconosce — un nome, una
 * diagnosi — non lo toglie: per questo i log dell'app non diventano breadcrumb
 * (vedi il blocco `sentry` in app/build.gradle).
 */
object Redactor {

    private class Rule(pattern: String, val replacement: String, ignoreCase: Boolean = false) {
        val regex = if (ignoreCase) Regex(pattern, RegexOption.IGNORE_CASE) else Regex(pattern)
    }

    private val rules = listOf(
        // JWT: tre segmenti base64url separati da punto.
        Rule("""eyJ[A-Za-z0-9_-]{8,}\.[A-Za-z0-9_-]{8,}\.[A-Za-z0-9_-]{8,}""", "<jwt>"),
        // Bearer / Basic nelle intestazioni.
        Rule("""((?:Bearer|Basic)\s+)[A-Za-z0-9._~+/=-]{8,}""", "$1<token>"),
        // Chiave=valore con nomi sensibili (password=…, "token": "…", api_key: …),
        // tranne i valori già redatti e i «Bearer …», che hanno la loro regola.
        Rule(
            """((?:password|passwd|pwd|secret|token|api[_-]?key|authorization)\s*["']?\s*[:=]\s*["']?)""" +
                """(?!<|(?:Bearer|Basic)\b)[^\s"',;&<]+""",
            "$1<redacted>",
            ignoreCase = true,
        ),
        // Email.
        Rule("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""", "<email>"),
        // Numero AVS svizzero: 756.XXXX.XXXX.XX, con o senza punti.
        Rule("""\b756\.?\d{4}\.?\d{4}\.?\d{2}\b""", "<avs>"),
        // Codice fiscale italiano (16 caratteri, struttura fissa).
        Rule("""\b[A-Z]{6}\d{2}[A-EHLMPRST]\d{2}[A-Z]\d{3}[A-Z]\b""", "<cf>", ignoreCase = true),
        // Partita IVA: 11 cifre di fila (con o senza `IT`).
        Rule("""\b(?:IT\s?)?\d{11}\b""", "<piva>"),
        // Segreti lunghi senza struttura (API key, session id): 40+ caratteri.
        Rule("""\b[A-Za-z0-9_-]{40,}\b""", "<token>"),
    )

    fun apply(text: String): String =
        rules.fold(text) { acc, rule -> rule.regex.replace(acc, rule.replacement) }
}
