# Changelog

Tutte le modifiche rilevanti dell'app EClinic Android sono documentate qui.
Formato [Keep a Changelog](https://keepachangelog.com/it/1.1.0/), versioni [SemVer](https://semver.org/lang/it/).
Le versioni precedenti alla 1.2.68 si ricostruiscono dai tag git (`1.2.67` e precedenti).

## [Unreleased]

## [1.2.68] - 2026-10-28

### Changed
- AFAG-164 Dolore e Intensità del dolore facoltativi nel controllo ferita, come sul desktop; voce "Non specificato" nei due menu per annullare una scelta. Libreria `shared-android` 1.2.0 → 1.3.0 (campi del dolore nullable).
- AFAG-163 Sentry (`sentry-android` 8.59.0) al posto di Firebase Crashlytics, in configurazione MedTech: niente dati personali, screenshot, replay né tracing; testi redatti prima dell'invio. Firebase rimosso.

### Fixed
- AFAG-164 Una ferita con un controllo creato da desktop senza dolore ora si apre in app: prima la decodifica della lista ferite falliva.

[Unreleased]: https://github.com/Airbag-Studio/eclinic-android/compare/1.2.68...HEAD
[1.2.68]: https://github.com/Airbag-Studio/eclinic-android/compare/1.2.67...1.2.68
