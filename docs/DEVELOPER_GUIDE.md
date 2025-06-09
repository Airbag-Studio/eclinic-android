# Developer Setup and Contribution Guide

## Prerequisites

### Required Software

| Tool | Version | Purpose |
|------|---------|---------|
| **Android Studio** | Hedgehog (2023.1.1) or later | Primary IDE |
| **JDK** | 17 | Java compilation |
| **Android SDK** | API 34 | Target platform |
| **Git** | Latest | Version control |
| **Gradle** | 8.4+ | Build system |

### System Requirements

- **RAM**: Minimum 8GB, recommended 16GB
- **Storage**: 4GB free space for Android SDK
- **OS**: Windows 10+, macOS 10.14+, or Linux

## Initial Setup

### 1. Clone Repository

```bash
git clone https://github.com/your-org/eclinic-android.git
cd eclinic-android
```

### 2. Android Studio Configuration

#### Install Android Studio
1. Download from [Android Developer website](https://developer.android.com/studio)
2. Install with default settings
3. Open Android Studio and complete initial setup

#### Configure SDK
```bash
# Required SDK components
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0
- Android Emulator
- Android SDK Platform-Tools
- Intel x86 Emulator Accelerator (HAXM installer)
```

#### Set up Build Tools
1. Open `Tools` → `SDK Manager`
2. Install required SDK platforms and tools
3. Configure JDK path: `File` → `Project Structure` → `SDK Location`

### 3. Project Configuration

#### Import Project
1. Open Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to cloned repository folder
4. Click "OK"

#### Gradle Sync
```bash
# Automatic sync should trigger, or manually:
./gradlew --refresh-dependencies
```

#### Environment Variables
```bash
# Add to ~/.bashrc or ~/.zshrc (Linux/macOS) or Environment Variables (Windows)
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

## Development Environment

### Build Flavors

The project supports three build flavors:

```gradle
productFlavors {
    develop {
        applicationId "ch.ticare.eclinic.app.dev"
        // Development server configuration
    }
    preProd {
        applicationId "ch.ticare.eclinic.app"
        // Pre-production server configuration
    }
    mdm {
        applicationId "ch.ticare.eclinic.app.mdm"
        // Enterprise MDM configuration
    }
}
```

### Running the Application

#### Debug Build
```bash
# Command line
./gradlew assembleDevelopDebug
./gradlew installDevelopDebug

# Or use Android Studio Run button (Shift+F10)
```

#### Testing on Device
1. Enable Developer Options on Android device
2. Enable USB Debugging
3. Connect device via USB
4. Select device in Android Studio and run

#### Using Emulator
1. Open AVD Manager in Android Studio
2. Create new virtual device (Pixel 4, API 34)
3. Start emulator and run application

### Code Organization

#### Package Structure
```
it.airbagstudio.ticare/
├── data/                    # Data models and DTOs
├── di/                      # Dependency injection modules
├── navigation/              # Navigation setup
├── pages/                   # Feature screens
│   ├── carePlans/          # Care plan management
│   ├── diary/              # Patient diary
│   ├── drugsAdministration/ # Medication tracking
│   ├── login/              # Authentication
│   ├── patientDetails/     # Patient information
│   │   └── form/           # Assessment forms system
│   ├── patientsList/       # Patient list management
│   ├── wounds/             # Wound management
│   └── ...
├── ui/                     # Shared UI components
│   ├── components/         # Reusable Compose components
│   └── theme/              # App theming
└── utils/                  # Utility functions
```

#### Naming Conventions

**Files:**
- **Screens**: `FeatureScreen.kt` (e.g., `PatientDetailsScreen.kt`)
- **ViewModels**: `FeatureViewModel.kt` (e.g., `PatientDetailsViewModel.kt`)
- **Components**: `ComponentName.kt` (e.g., `PatientListItem.kt`)
- **Repositories**: `FeatureRepository.kt` (e.g., `UserRepository.kt`)

**Classes:**
- **Compose Functions**: PascalCase (e.g., `PatientListItem`)
- **Variables**: camelCase (e.g., `patientList`)
- **Constants**: SCREAMING_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)

**Resources:**
- **Strings**: snake_case (e.g., `patient_details_title`)
- **Drawables**: snake_case (e.g., `ic_patient_selected`)
- **Colors**: snake_case (e.g., `primary_color`)

## Development Workflow

### Git Workflow

#### Branch Naming
```bash
# Feature branches
feature/patient-search-improvement
feature/form-validation-enhancement

# Bug fixes
bugfix/login-crash-fix
bugfix/sync-data-issue

# Hotfixes
hotfix/critical-security-patch
```

#### Commit Messages
```bash
# Format: type(scope): description

# Examples:
feat(patient): add patient search functionality
fix(forms): resolve CBI form validation error
docs(api): update authentication documentation
refactor(ui): simplify navigation structure
test(repo): add unit tests for UserRepository
```

#### Pull Request Process
1. **Create Feature Branch**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/new-feature-name
   ```

2. **Development**
   - Make changes
   - Add tests
   - Update documentation

3. **Pre-submission Checks**
   ```bash
   # Run tests
   ./gradlew test
   ./gradlew connectedAndroidTest
   
   # Check code style
   ./gradlew ktlintCheck
   
   # Build all flavors
   ./gradlew build
   ```

4. **Submit Pull Request**
   - Push to remote branch
   - Create PR against `develop` branch
   - Fill out PR template
   - Request code review

### Code Quality

#### Linting and Formatting
```bash
# Run ktlint
./gradlew ktlintCheck

# Auto-format code
./gradlew ktlintFormat

# Android Lint
./gradlew lint
```

#### Code Review Checklist
- [ ] Code follows project conventions
- [ ] New features have tests
- [ ] Documentation updated
- [ ] No hardcoded strings (use resources)
- [ ] Proper error handling
- [ ] Performance considerations addressed
- [ ] Accessibility features considered

## Testing

### Unit Testing

#### Writing Tests
```kotlin
// Example unit test
@Test
fun `when patient data is valid, form validation passes`() {
    // Given
    val patientData = PatientData(
        firstName = "John",
        lastName = "Doe",
        dateOfBirth = "1990-01-01"
    )
    
    // When
    val result = patientData.validate()
    
    // Then
    assertTrue(result.isValid)
    assertTrue(result.errors.isEmpty())
}
```

#### Running Tests
```bash
# All unit tests
./gradlew test

# Specific test class
./gradlew test --tests "PatientRepositoryTest"

# Test with coverage
./gradlew testDebugUnitTestCoverage
```

### Integration Testing

#### Writing Integration Tests
```kotlin
@Test
fun `when syncing patient data, offline changes are uploaded`() = runTest {
    // Given
    val repository = TestRepositoryModule.providePatientRepository()
    val patientData = createTestPatientData()
    
    // When
    repository.savePatientOffline(patientData)
    val syncResult = repository.syncPendingData()
    
    // Then
    assertTrue(syncResult.isSuccess)
    verify(mockApiClient).uploadPatientData(patientData)
}
```

### UI Testing

#### Compose Testing
```kotlin
@Test
fun `when patient list loads, items are displayed correctly`() {
    composeTestRule.setContent {
        PatientListScreen(
            patients = testPatients,
            onPatientSelected = {}
        )
    }
    
    // Verify patients are displayed
    composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    composeTestRule.onNodeWithText("Jane Smith").assertIsDisplayed()
}
```

## Architecture Guidelines

### MVVM Pattern

#### ViewModel Best Practices
```kotlin
class PatientViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PatientUiState())
    val uiState: StateFlow<PatientUiState> = _uiState.asStateFlow()
    
    // Use coroutine exception handler
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = throwable.localizedMessage
        )
    }
    
    fun loadPatient(patientId: String) {
        viewModelScope.launch(exceptionHandler) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val patient = patientRepository.getPatient(patientId)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                patient = patient
            )
        }
    }
}
```

#### UI State Management
```kotlin
data class PatientUiState(
    val isLoading: Boolean = false,
    val patient: Patient? = null,
    val error: String? = null,
    val isOffline: Boolean = false
)
```

### Repository Pattern

#### Repository Implementation
```kotlin
class PatientRepositoryImpl @Inject constructor(
    private val apiClient: APIClient,
    private val database: Database,
    private val offlineRepository: OfflineOnlineRepository
) : PatientRepository {
    
    override suspend fun getPatient(patientId: String): Patient? {
        return try {
            // Try online first
            val onlinePatient = apiClient.getPatient(patientId)
            database.savePatient(onlinePatient)
            onlinePatient
        } catch (e: Exception) {
            // Fall back to offline data
            database.getPatient(patientId)
        }
    }
    
    override fun getPatientFlow(patientId: String): Flow<Patient?> {
        return combine(
            database.getPatientFlow(patientId),
            offlineRepository.isOnlineFlow()
        ) { patient, isOnline ->
            patient?.copy(isOfflineData = !isOnline)
        }
    }
}
```

### Dependency Injection

#### Hilt Setup
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object FeatureModule {
    
    @Provides
    @Singleton
    fun provideFeatureRepository(
        apiClient: APIClient,
        database: Database
    ): FeatureRepository {
        return FeatureRepositoryImpl(apiClient, database)
    }
}
```

#### ViewModel Injection
```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val repository: FeatureRepository
) : ViewModel() {
    // ViewModel implementation
}
```

## UI Development

### Compose Guidelines

#### Stateless Components
```kotlin
@Composable
fun PatientCard(
    patient: Patient,
    onPatientClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onPatientClick(patient.id) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${patient.firstName} ${patient.lastName}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = patient.dateOfBirth,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
```

#### State Hoisting
```kotlin
@Composable
fun PatientScreen(
    viewModel: PatientViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    PatientContent(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onPatientSelected = viewModel::selectPatient
    )
}

@Composable
private fun PatientContent(
    uiState: PatientUiState,
    onRefresh: () -> Unit,
    onPatientSelected: (String) -> Unit
) {
    // UI implementation
}
```

#### Material 3 Theming
```kotlin
@Composable
fun EclinicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

### Accessibility

#### Content Descriptions
```kotlin
@Composable
fun PatientImage(patient: Patient) {
    Image(
        painter = rememberAsyncImagePainter(patient.photoUrl),
        contentDescription = "Photo of ${patient.firstName} ${patient.lastName}",
        modifier = Modifier.semantics {
            role = Role.Image
        }
    )
}
```

#### Semantic Properties
```kotlin
@Composable
fun PatientCard(patient: Patient) {
    Card(
        modifier = Modifier.semantics {
            contentDescription = "Patient: ${patient.firstName} ${patient.lastName}"
            role = Role.Button
        }
    ) {
        // Card content
    }
}
```

## Build Configuration

### Gradle Configuration

#### Module-level build.gradle
```gradle
android {
    compileSdk 34
    
    defaultConfig {
        minSdk 28
        targetSdk 34
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildFeatures {
        compose true
        buildConfig true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
}
```

#### Dependency Management
```gradle
dependencies {
    // Use BOM for Compose
    implementation platform('androidx.compose:compose-bom:2024.05.00')
    implementation 'androidx.compose.material3:material3'
    
    // Hilt for DI
    implementation "com.google.dagger:hilt-android:2.50"
    kapt 'com.google.dagger:hilt-compiler:2.50'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
}
```

### Build Scripts

#### Common Tasks
```bash
# Clean build
./gradlew clean build

# Run specific flavor
./gradlew assembleDevelopDebug
./gradlew installDevelopDebug

# Generate test reports
./gradlew testDebugUnitTestCoverage
./gradlew createDebugCoverageReport
```

## Debugging

### Common Issues

#### Build Failures
```bash
# Clear Gradle cache
./gradlew clean
./gradlew --refresh-dependencies

# Invalidate caches (Android Studio)
File → Invalidate Caches and Restart
```

#### Runtime Issues
```kotlin
// Add logging for debugging
private val logger = ECLogger()

try {
    // Risky operation
    val result = repository.getData()
} catch (e: Exception) {
    logger.log(LogLevel.ERROR, "Failed to load data", e)
}
```

### Debugging Tools

#### Database Inspection
```bash
# Use Android Studio Database Inspector
View → Tool Windows → App Inspection → Database Inspector
```

#### Network Debugging
```kotlin
// Enable network logging in debug builds
if (BuildConfig.DEBUG) {
    HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
```

## Performance Optimization

### Memory Management

#### Avoiding Memory Leaks
```kotlin
class ViewModel : ViewModel() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    
    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
```

#### Efficient Lists
```kotlin
@Composable
fun PatientList(patients: List<Patient>) {
    LazyColumn {
        items(
            items = patients,
            key = { patient -> patient.id }  // Stable keys for efficiency
        ) { patient ->
            PatientListItem(patient = patient)
        }
    }
}
```

### Network Optimization

#### Caching Strategy
```kotlin
class PatientRepository {
    private val cache = LruCache<String, Patient>(50)
    
    suspend fun getPatient(patientId: String): Patient? {
        // Check cache first
        cache.get(patientId)?.let { return it }
        
        // Fetch from network/database
        val patient = loadPatientFromSource(patientId)
        patient?.let { cache.put(patientId, it) }
        
        return patient
    }
}
```

## Security Best Practices

### Data Protection

#### Secure Storage
```kotlin
// Use EncryptedSharedPreferences for sensitive data
val encryptedPrefs = EncryptedSharedPreferences.create(
    "auth_prefs",
    masterKeyAlias,
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

#### Network Security
```kotlin
// Enforce HTTPS
class ApiClient {
    private val client = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
        .build()
}
```

### Authentication

#### Token Handling
```kotlin
class AuthRepository {
    fun saveToken(token: String) {
        // Store securely
        encryptedPrefs.edit()
            .putString("access_token", token)
            .apply()
    }
    
    fun getToken(): String? {
        return encryptedPrefs.getString("access_token", null)
    }
}
```

## Contributing Guidelines

### Code Contribution Process

1. **Issue Creation**
   - Check existing issues first
   - Use issue templates
   - Provide detailed description
   - Add appropriate labels

2. **Development**
   - Follow coding standards
   - Write comprehensive tests
   - Update documentation
   - Add appropriate comments

3. **Testing**
   - All tests must pass
   - Add new tests for new features
   - Test on multiple devices/screen sizes
   - Verify accessibility

4. **Documentation**
   - Update relevant documentation
   - Add KDoc for public APIs
   - Update CHANGELOG.md
   - Include screenshots for UI changes

5. **Review Process**
   - Self-review before submission
   - Address reviewer feedback
   - Maintain clean commit history
   - Squash commits if needed

### Code Style Guidelines

#### Kotlin Style
```kotlin
// Use explicit types for public APIs
fun getPatientData(patientId: String): Flow<Patient?>

// Use trailing commas in lists
val patients = listOf(
    patient1,
    patient2,
    patient3,
)

// Prefer sealed classes for state
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<Patient>) : UiState()
    data class Error(val message: String) : UiState()
}
```

#### Compose Style
```kotlin
// Group modifier parameters logically
@Composable
fun PatientCard(
    patient: Patient,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { /* handle click */ }
    ) {
        // Content
    }
}
```

This guide provides a comprehensive foundation for developing and contributing to the EClinic Android project. Follow these guidelines to ensure consistent, high-quality code that integrates well with the existing architecture.