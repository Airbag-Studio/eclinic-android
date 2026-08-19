# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

EClinic is an Android healthcare application built with Kotlin and Jetpack Compose. It provides functionality for healthcare professionals to manage patients, track treatments, administer medications, manage care plans, and fill out various healthcare forms.

## Build System

- **Build Tool**: Gradle 9.1.0 (wrapper) with Android Gradle Plugin 9.0.1
- **Kotlin Version**: 2.2.10 (KSP 2.2.10-2.0.2)
- **Min SDK**: 28, Target SDK: 36, Compile SDK: 36
- **Java Version**: 17

### Build Commands

```bash
# Build the app
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Build specific flavor
./gradlew assembleDevelopDebug
./gradlew assemblePreProdRelease
./gradlew assembleMdmRelease
```

### Build Flavors

The app has three product flavors for different environments:
- `develop` - Development environment (ch.ticare.eclinic.app.dev)
- `preProd` - Pre-production environment (ch.ticare.eclinic.app)
- `mdm` - MDM environment (ch.ticare.eclinic.app.mdm)

## Architecture

### Dependency Injection
Uses Dagger Hilt for dependency injection. The main module is `AppModule.kt` which provides singleton repositories and services.

### Navigation
- **Navigation Framework**: Jetpack Navigation Compose
- **Main Navigation**: `EclinicNavGraph.kt` handles all screen navigation
- **Route Definitions**: `Destinations.kt` and `AppDestinations.kt` contain route constants

### Data Layer
- **Database**: SQLDelight for local data persistence
- **Network**: Ktor client for API communication with custom authentication
- **Repository Pattern**: Each feature has its own repository (e.g., `UserRepository`, `DiaryRepository`)
- **Offline Support**: `OfflineOnlineRepository` manages sync between local and remote data
- **Multi-tenant Support**: Company-based routing with authorization headers

### UI Architecture
- **UI Framework**: Jetpack Compose with Material 3
- **State Management**: ViewModels with StateFlow/LiveData
- **Screen Structure**: Each feature has its own package under `pages/`

### Key Libraries
- **Compose BOM**: 2026.01.01 (material3 and ui versions come from the BOM)
- **Navigation Compose**: 2.8.5
- **Hilt**: 2.59.2 for dependency injection
- **Ktor**: 2.3.12 for networking
- **SQLDelight**: 2.0.2 for database
- **Coil**: 2.7.0 for image loading
- **Vico**: 3.0.0 for charts
- **Firebase**: BOM 33.7.0, Crashlytics for error reporting

### API Architecture
- **Authentication**: Bearer token with automatic refresh
- **Offline-First**: All data cached locally, synced when network available
- **Conflict Resolution**: Last-write-wins strategy with server timestamps
- **Error Handling**: Centralized error interceptor with retry logic

## Form System

The app includes a comprehensive form system for healthcare assessments:

### Form Types
- **CBI Form**: Cognitive behavioral assessment
- **COMID Form**: Communication assessment
- **IPOS Form**: Unified palliative care outcome scale (3-day/7-day periods with time selector)
- **Senior Sitting Form**: Unified elderly care assessment (Adesione/Non-Adesione types with type selector)

### Form Architecture
- **Domain Layer**: Form models and business logic in `form/domain/`
- **Data Layer**: JSON-based data sources and repositories in `form/data/`
- **UI Layer**: Compose screens and ViewModels in `form/ui/`
- **Factory Pattern**: Each form has its own ViewModel factory
- **Unified Forms**: IPOS and Senior Sitting forms use selectors for dynamic content
- **Floating Legends**: Scale reference components that stay fixed during scroll

### Recent Form Improvements
- **Form Unification**: IPOS and Senior Sitting forms now use unified screens with selectors
- **Floating Legends**: Added fixed-position scale references ("Per nulla 0 1 2 3 4 Opprimente" for IPOS, "Poco 0 1 2 3 4 5 Molto" for Senior Sitting)
- **Dynamic Content**: Forms adapt their questions and layout based on user selections
- **Enhanced UX**: Improved form completion with always-visible scale references

## Shared Library

The app depends on the shared library `ch.ticare.eclinic:shared-android:1.1.0`, declared in `app/build.gradle` and resolved from `mavenLocal()` (see the repositories in `settings.gradle`). It contains core business logic, network layer, and data models, and provides:
- API client and authentication
- Database entities and repositories
- Core domain models

## Key Features

### Patient Management
- Patient list with search and filtering
- Patient details with medical information
- Offline data synchronization

### Healthcare Tasks
- Drug administration tracking
- Vital parameters recording
- Wound management with photo capture
- Care plan management
- Nursing courses and treatments

### Data Synchronization
- Automatic sync with backend when online
- Offline mode with local data persistence
- Conflict resolution for concurrent edits

## Testing

- **Unit Tests**: Located in `src/test/`
- **Instrumented Tests**: Located in `src/androidTest/`
- **Test Runner**: AndroidJUnitRunner for instrumented tests
- **Run Single Test**: `./gradlew test --tests "TestClassName.testMethodName"`
- **Run Test Class**: `./gradlew test --tests "TestClassName"`

## CI/CD

- **Platform**: Bitrise CI/CD
- **Build Triggers**: Automatic builds on merge to develop/master branches
- **Release Process**: Pre-production builds promoted to production after testing
- **Emergency Procedures**: Hotfix branches for critical production issues

## Security

- Uses enterprise feedback for MDM environments
- File provider configuration for secure file sharing
- Authentication token management through shared library
- APK signing with separate keystores per environment

## Code Quality

Note: The project does not currently have automated code formatting tools (ktlint, detekt) configured. Manual code review ensures consistency with Kotlin coding conventions.

## Additional Documentation

For more detailed information, see:
- `docs/API_ARCHITECTURE.md` - Detailed API and networking documentation
- `docs/DATABASE_SCHEMA.md` - Database structure and migrations
- `docs/DEPLOYMENT.md` - Comprehensive deployment procedures
- `docs/DEVELOPER_GUIDE.md` - Development setup and best practices
- `docs/FORM_SYSTEM.md` - Detailed form architecture documentation
- `docs/MEMORY_BANK.md` - Recent changes and improvements tracking