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

- **Language**: Kotlin 1.8.20
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Dagger Hilt
- **Database**: SQLDelight with offline-first architecture
- **Networking**: Ktor Client with authentication
- **Navigation**: Jetpack Navigation Compose

### Key Libraries

```gradle
// UI & Compose
androidx.compose:compose-bom:2024.05.00
androidx.compose.material3:material3:1.2.1
androidx.navigation:navigation-compose:2.7.7

// Dependency Injection
com.google.dagger:hilt-android:2.50

// Networking
io.ktor:ktor-client-core:2.3.4
io.ktor:ktor-client-content-negotiation:2.3.4

// Database
app.cash.sqldelight:android-driver:2.0.0
app.cash.sqldelight:coroutines-extensions:2.0.0

// Image Loading
io.coil-kt:coil-compose:2.4.0

// Firebase
com.google.firebase:firebase-crashlytics
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Minimum SDK 28

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

- **Version**: 1.2.18 (Build 83)
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 34 (Android 14)
- **Build Tool**: Gradle 8.4.2
- **Status**: Active Development

---

Built with ❤️ for healthcare professionals