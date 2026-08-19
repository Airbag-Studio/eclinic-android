# EClinic Android

A comprehensive healthcare management Android application built for healthcare professionals to manage patients, track treatments, and fill out medical assessment forms.

## 📱 Overview

EClinic is a modern Android healthcare application that enables healthcare professionals to:

- **Manage Patients**: Comprehensive patient information and medical history
- **Track Treatments**: Drug administration, vital parameters, and wound management
- **Healthcare Forms**: Specialized assessment forms (CBI, COMID, IPOS, Senior Sitting)
- **Care Plans**: Create and manage detailed patient care plans
- **Offline Support**: Continue working without internet connectivity
- **Data Synchronization**: Automatic sync when connectivity is restored

## 🏗️ Architecture

### Technology Stack

- **Language**: Kotlin 2.2.10
- **Build**: Android Gradle Plugin 9.0.1, Gradle 9.1.0 (wrapper), KSP 2.2.10-2.0.2
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Dagger Hilt
- **Database**: SQLDelight with offline-first architecture
- **Networking**: Ktor Client with authentication
- **Navigation**: Jetpack Navigation Compose

### Key Libraries

```gradle
// UI & Compose (material3 and ui versions come from the BOM)
androidx.compose:compose-bom:2026.01.01
androidx.navigation:navigation-compose:2.8.5

// Dependency Injection
com.google.dagger:hilt-android:2.59.2

// Networking
io.ktor:ktor-client-core:2.3.12
io.ktor:ktor-client-content-negotiation:2.3.12

// Database
app.cash.sqldelight:android-driver:2.0.2
app.cash.sqldelight:coroutines-extensions:2.0.2

// Image Loading
io.coil-kt:coil-compose:2.7.0

// Charts
com.patrykandpatrick.vico:compose-m3:3.0.0

// Firebase
com.google.firebase:firebase-bom:33.7.0
com.google.firebase:firebase-crashlytics
```

## 🚀 Getting Started

### Prerequisites

- Android Studio version compatible with Android Gradle Plugin 9.0.1
- JDK 17 or later (the JDK bundled with Android Studio is fine)
- Android SDK 36 (compileSdk/targetSdk)
- Minimum SDK 28

### JDK setup (`JAVA_HOME`)

The project does **not** pin a JDK path: `gradle.properties` deliberately contains no
`org.gradle.java.home`, because an absolute path to a JDK only works on the machine it was
written on and makes `./gradlew` fail everywhere else.

Gradle therefore picks the JDK from `JAVA_HOME`. Android Studio uses its own setting
(*Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK*), so the IDE
works out of the box; from the command line, point `JAVA_HOME` at any JDK 17+, for example
the one bundled with Android Studio:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDevelopDebug
```

To avoid repeating it, export it from your shell profile (`~/.zshrc`):

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

The JDK that *runs* Gradle does not affect the produced bytecode: source/target
compatibility and the Kotlin JVM target are fixed at 17 by `compileOptions` in
[app/build.gradle](app/build.gradle), which is filesystem-independent. No
`jvmToolchain`/`java.toolchain` block is declared on purpose: it would make the build
require a JDK of that exact version to be installed (or downloadable) on every machine,
rather than accepting the JDK already shipped with Android Studio.

### Shared library

The app depends on `ch.ticare.eclinic:shared-android:1.1.0`, resolved from `mavenLocal()`.
Publish it from the [eclinic-library](../eclinic-library) checkout before building:

```bash
./gradlew publishToMavenLocal
```

### Build Configuration

The app supports three build flavors:

- **develop**: Development environment (`ch.ticare.eclinic.app.dev`)
- **preProd**: Pre-production environment (`ch.ticare.eclinic.app`)
- **mdm**: Enterprise MDM environment (`ch.ticare.eclinic.app.mdm`)

### Build Commands

```bash
# Clean and build
./gradlew clean build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📚 Core Features

### Patient Management
- **Patient Lists**: Search, filter, and manage patient information
- **Patient Details**: Comprehensive medical information and history
- **Offline Access**: Download patient data for offline use

### Healthcare Tasks
- **Drug Administration**: Track medication schedules and administration
- **Vital Parameters**: Record blood pressure, temperature, weight, etc.
- **Wound Management**: Photo-based wound tracking with assessments
- **Care Plans**: Structured care planning with activities and goals

### Assessment Forms
- **CBI Form**: Caregiver Burden Inventory
- **COMID Form**: Multidimensional assessment for home care
- **IPOS Forms**: Palliative care outcome scales (3-day and 7-day)
- **Senior Sitting**: Elderly care assessment tools

### Administrative Features
- **Time Tracking**: Work hours and travel time logging
- **Diary System**: Activity timeline and progress tracking
- **Data Sync**: Automatic synchronization with backend systems

## 🏛️ Project Structure

```
app/src/main/java/it/airbagstudio/ticare/
├── data/                           # Data models
├── di/                            # Dependency injection modules
├── navigation/                    # Navigation setup
├── pages/                         # Feature screens
│   ├── carePlans/                # Care plan management
│   ├── consumptions/             # Expense tracking
│   ├── diary/                    # Activity diary
│   ├── drugsAdministration/      # Medication management
│   ├── login/                    # Authentication
│   ├── patientDetails/           # Patient information
│   │   └── form/                 # Healthcare forms system
│   ├── patientsList/             # Patient management
│   ├── vitalParameters/          # Vital signs
│   ├── wounds/                   # Wound management
│   └── ...
├── ui/                           # Shared UI components
│   ├── components/               # Reusable Compose components
│   └── theme/                    # App theming
└── utils/                        # Utility functions
```

## 🔧 Development Guidelines

### Code Style
- Follow Kotlin official coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused

### Compose Best Practices
- Use stateless composables when possible
- Hoist state to appropriate levels
- Use remember for expensive calculations
- Follow Material 3 design guidelines

### Architecture Patterns
- **ViewModels**: Handle UI state and business logic
- **Repositories**: Abstract data sources and handle offline/online logic
- **Use Cases**: Encapsulate complex business operations
- **Dependency Injection**: Use Hilt for all dependencies

## 🧪 Testing

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run tests with coverage
./gradlew testDebugUnitTestCoverage
```

### Instrumented Tests
```bash
# Run all instrumented tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.TestClass
```

## 📦 Build & Deployment

### Debug Build
```bash
./gradlew assembleDevelopDebug
```

### Release Build
```bash
./gradlew assembleDevelopRelease
```

### Bundle (for Play Store)
```bash
./gradlew bundleDevelopRelease
```

## 🔐 Security

- **Authentication**: Secure token-based authentication
- **Data Encryption**: Sensitive data encrypted at rest
- **Network Security**: HTTPS-only communication
- **File Security**: Secure file provider for document sharing
- **Enterprise Support**: MDM compatibility for institutional deployment

## 📱 Permissions

Required permissions:
- **INTERNET**: Network communication
- **CAMERA**: Photo capture for wound documentation
- **POST_NOTIFICATIONS**: Local notifications
- **SCHEDULE_EXACT_ALARM**: Medication reminders

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Code Review Guidelines
- Ensure all tests pass
- Follow coding standards
- Update documentation as needed
- Test on multiple devices/screen sizes

## 📄 License

This project is proprietary software developed for healthcare institutions. All rights reserved.

## 🆘 Support

For technical support or questions:
- Create an issue in the project repository
- Contact the development team
- Refer to the [CLAUDE.md](CLAUDE.md) file for AI assistant guidance

## 📊 Status

- **Version**: 1.2.63 (Build 2026063001)
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36 (Android 16)
- **Compile SDK**: 36
- **Build Tool**: Gradle 9.1.0 with Android Gradle Plugin 9.0.1
- **Status**: Active Development

---

Built with ❤️ for healthcare professionals