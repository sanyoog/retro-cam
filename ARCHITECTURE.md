# RetroCam Architecture

## Overview

RetroCam follows **Clean Architecture** principles with **MVVM** pattern, ensuring:
- Separation of concerns
- Testability
- Maintainability
- Scalability

## Layer Architecture

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (UI, ViewModels, Compose Screens)     │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│          Domain Layer                    │
│  (Business Logic, Models, Use Cases)    │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│           Data Layer                     │
│  (Repository, Database, CameraX)        │
└─────────────────────────────────────────┘
```

## 1. Presentation Layer

### Responsibilities
- Display UI with Jetpack Compose
- Handle user interactions
- Observe and react to state changes
- No business logic

### Components

#### **CameraScreen**
```kotlin
@Composable
fun CameraScreen(viewModel: CameraViewModel)
```
- Main camera interface
- Camera preview
- Shutter button
- Mode selector (Normal/Pro)
- Filter sheet
- Preset sheet

#### **CameraViewModel**
```kotlin
@HiltViewModel
class CameraViewModel(
    private val cameraManager: CameraManager,
    private val presetRepository: PresetRepository
)
```
- Manages camera state
- Handles user events
- Orchestrates photo capture
- Applies filters

#### **UI State**
```kotlin
data class CameraUiState(
    val mode: CameraMode,
    val currentFilter: Filter,
    val cameraSettings: CameraSettings,
    val isCapturing: Boolean,
    ...
)
```
- Immutable state representation
- Single source of truth
- Predictable state updates

### Theme System

#### Glassy Design
```kotlin
@Composable
fun GlassPanel(
    backgroundColor: Color,
    cornerRadius: Dp,
    content: @Composable BoxScope.() -> Unit
)
```
- Frosted blur effect
- Soft gradients
- Semi-transparent surfaces
- Premium aesthetic

## 2. Domain Layer

### Responsibilities
- Define business models
- Define repository interfaces
- Contain business logic (use cases)
- Platform-independent

### Models

#### **Filter**
```kotlin
data class Filter(
    val id: String,
    val name: String,
    val temperature: Float,    // 0.0 - 1.0
    val tint: Float,           // 0.0 - 1.0
    val contrast: Float,       // 0.0 - 1.0
    val highlights: Float,     // 0.0 - 1.0
    val shadows: Float,        // 0.0 - 1.0
    val grain: Float,          // 0.0 - 1.0
    val vignette: Float,       // 0.0 - 1.0
    val intensity: Float       // 0.0 - 1.0
)
```

**Design Decisions:**
- All values normalized to 0.0-1.0 for consistency
- Immutable data class
- Predefined filters as companion objects
- Easy to serialize and store

#### **Preset**
```kotlin
data class Preset(
    val id: Long,
    val name: String,
    val filter: Filter,
    val cameraSettings: CameraSettings?
)
```

#### **CameraSettings**
```kotlin
data class CameraSettings(
    val iso: Int?,
    val shutterSpeed: Long?,
    val whiteBalance: Int?,
    val focusDistance: Float?,
    val exposureCompensation: Int?
)
```
- Nullable values = auto mode
- Non-null = manual mode
- Used in Pro Mode only

### Repository Interfaces

```kotlin
interface PresetRepository {
    fun getAllPresets(): Flow<List<Preset>>
    suspend fun savePreset(preset: Preset): Long
    suspend fun deletePreset(preset: Preset)
}
```

## 3. Data Layer

### Responsibilities
- Implement repositories
- Manage data sources
- Handle camera operations
- Persist data

### Components

#### **CameraManager**
```kotlin
@Singleton
class CameraManager(context: Context)
```

**Operations:**
- Initialize CameraX
- Start/stop camera preview
- Capture photos
- Apply camera settings (Pro Mode)

**Design Decisions:**
- Uses CameraX for simplicity
- Camera2 interop for advanced controls
- Suspending functions for async operations
- Result<T> for error handling

#### **Room Database**

```kotlin
@Database(entities = [PresetEntity::class])
abstract class RetroCamDatabase : RoomDatabase()
```

**Entities:**
- `PresetEntity`: Stores saved presets

**DAOs:**
- `PresetDao`: CRUD operations for presets

**Design Decisions:**
- Single database for simplicity
- Flow for reactive queries
- Type converters for complex types
- Migration strategy for future updates

#### **Repository Implementation**

```kotlin
@Singleton
class PresetRepositoryImpl(
    private val presetDao: PresetDao
) : PresetRepository
```

- Implements domain repository interface
- Maps between entity and domain models
- Provides reactive data streams

## Dependency Injection (Hilt)

### Modules

#### **DatabaseModule**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): RetroCamDatabase
    
    @Provides
    fun providePresetDao(db: RetroCamDatabase): PresetDao
}
```

#### **RepositoryModule**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPresetRepository(
        impl: PresetRepositoryImpl
    ): PresetRepository
}
```

### Benefits
- Compile-time validation
- Easy testing with test doubles
- Singleton lifecycle management
- Automatic dependency provision

## Data Flow

### Photo Capture Flow

```
User Taps Shutter
       ↓
CameraScreen (UI)
       ↓
CameraViewModel.onEvent(CapturePhoto)
       ↓
CameraManager.capturePhoto()
       ↓
CameraX ImageCapture
       ↓
Result<Uri> returned
       ↓
Update CameraUiState
       ↓
UI recomposes with result
```

### Filter Selection Flow

```
User Selects Filter
       ↓
FilterSheet (UI)
       ↓
CameraViewModel.onEvent(SelectFilter(filter))
       ↓
Update currentFilter in state
       ↓
UI recomposes with new filter
       ↓
FilterEngine applies filter to preview
```

### Preset Save Flow

```
User Saves Preset
       ↓
PresetDialog (UI)
       ↓
PresetViewModel.savePreset(name)
       ↓
Create Preset from current state
       ↓
PresetRepository.savePreset()
       ↓
PresetDao.insertPreset()
       ↓
Room Database
       ↓
Flow emits updated preset list
       ↓
UI updates automatically
```

## State Management

### Single Source of Truth
- `CameraUiState` is the single source of truth
- All UI renders from this state
- State updates are unidirectional

### State Updates
```kotlin
// ViewModel
private val _uiState = MutableStateFlow(CameraUiState())
val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

fun onEvent(event: CameraEvent) {
    when (event) {
        is CameraEvent.SelectFilter -> {
            _uiState.update { it.copy(currentFilter = event.filter) }
        }
    }
}
```

### Benefits
- Predictable state changes
- Easy debugging
- Time-travel debugging possible
- Testable

## Performance Considerations

### Camera Preview
- CameraX handles optimization
- Preview resolution independent of capture
- GPU-accelerated rendering

### Filter Application
- RenderEffect for GPU acceleration
- Applied on GPU, not CPU
- Minimal performance impact
- Real-time preview

### Database Operations
- Room uses coroutines for async operations
- Flow for reactive updates
- Efficient indexing
- Background threading

### UI Rendering
- Compose recomposes only changed parts
- Lazy evaluation
- Skips unchanged compositions
- Efficient state observation

## Testing Strategy

### Unit Tests
- ViewModels: Test state transitions
- Use cases: Test business logic
- Repositories: Test data operations
- Mock dependencies with Hilt

### Integration Tests
- Room database tests
- Repository tests with real database
- CameraX integration tests

### UI Tests
- Compose UI tests
- Screenshot tests
- User flow tests
- Accessibility tests

## Future Enhancements

### Potential Additions
1. **Use Cases Layer**: Extract complex logic
2. **Filter Engine**: Dedicated filter processing
3. **Gallery Integration**: View captured photos
4. **Cloud Sync**: Sync presets across devices
5. **Video Support**: Extend to video recording
6. **Camera2 Full Integration**: Advanced manual controls

### Scalability
- Architecture supports easy addition of features
- Clear separation allows parallel development
- Testability ensures quality at scale
- Modularization possible if needed

## Design Patterns Used

1. **MVVM**: ViewModel + UI State
2. **Repository Pattern**: Abstract data sources
3. **Observer Pattern**: Flow-based reactive streams
4. **Singleton Pattern**: Database, CameraManager
5. **Factory Pattern**: ViewModelFactory (Hilt)
6. **Strategy Pattern**: Filter application
7. **State Pattern**: Camera modes

## Conclusion

RetroCam's architecture prioritizes:
- **Clarity**: Easy to understand and navigate
- **Maintainability**: Changes are localized
- **Testability**: Each layer independently testable
- **Performance**: Optimized for real-time camera operations
- **Scalability**: Ready for future enhancements
