# RetroCam - Premium Android Camera App

A modern Android camera application with a glassy, premium UI and subtle retro aesthetic. Built with Jetpack Compose, CameraX, and modern Android architecture.

## Features

### 🎯 Core Features
- **Dual Mode System**
  - **Normal Mode**: Simple, auto camera for everyday users
  - **Pro Mode**: Manual controls (ISO, shutter, WB, focus, exposure)

- **Premium Filters**
  - Vintage, Warm, Cool, B&W, Sepia, Fade
  - Live preview with GPU acceleration
  - Adjustable intensity and parameters

- **Custom Presets**
  - Save favorite filter + camera settings
  - Name, edit, and duplicate presets
  - Quick access during capture

- **Glassy UI Design**
  - Frosted glass panels
  - Smooth animations
  - Minimal, premium aesthetic
  - Edge-to-edge experience

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Camera**: CameraX with Camera2 interop
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt/Dagger
- **Database**: Room
- **Storage**: DataStore
- **Async**: Coroutines + Flow

## Project Structure

```
app/src/main/java/com/retrocam/
├── data/
│   ├── local/              # Room database entities and DAOs
│   └── repository/         # Repository implementations
├── domain/
│   ├── model/              # Domain models (Filter, Preset, CameraSettings)
│   └── repository/         # Repository interfaces
├── presentation/
│   ├── camera/             # Camera screen and ViewModel
│   ├── filter/             # Filter selection UI
│   ├── preset/             # Preset management UI
│   ├── settings/           # Settings screen
│   ├── theme/              # Compose theme (glassy design)
│   └── components/         # Reusable UI components
├── di/                     # Dependency injection modules
└── util/                   # Utility classes
```

## Building the App

### Prerequisites
- Android SDK (API 31+)
- JDK 17
- Gradle 7.5+

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run tests
./gradlew test

# Build release
./gradlew assembleRelease
```

### VS Code Development

This project is designed for VS Code development:

1. Open folder in VS Code
2. Install recommended extensions:
   - Kotlin Language Support
   - Gradle for Java
3. Build using terminal: `./gradlew assembleDebug`
4. Install on device: `adb install app/build/outputs/apk/debug/app-debug.apk`
5. Debug with logcat: `adb logcat | grep RetroCam`

## Architecture Overview

### MVVM + Clean Architecture

- **Presentation Layer**: Compose UI + ViewModels
- **Domain Layer**: Business logic, models, use cases
- **Data Layer**: Repository pattern, Room database, CameraX integration

### Key Components

1. **CameraManager**: Handles CameraX operations and photo capture
2. **FilterEngine**: Applies filters with GPU acceleration
3. **PresetRepository**: Manages saved presets in Room database
4. **CameraViewModel**: Orchestrates camera state and user actions

## Design Philosophy

### UI/UX Principles
- **Glassy aesthetic**: Frosted blur, subtle gradients, soft glow
- **Minimal**: No clutter, thumb-friendly controls
- **Premium**: Smooth animations, intentional transitions
- **Accessible**: Clear hierarchy, readable typography

### Camera Modes
- **Normal Mode** (Default): Auto everything, minimal UI
- **Pro Mode**: Manual controls for enthusiasts

### Filter System
Retro-inspired but modern:
- Color temperature & tint
- Contrast adjustments
- Grain (animated)
- Vignette
- Subtle distortion
- Chromatic aberration

## Permissions

- `CAMERA`: Required for photo capture
- `WRITE_EXTERNAL_STORAGE`: Required on Android ≤28
- `READ_MEDIA_IMAGES`: Required on Android ≥33

## Development Notes

### Testing on Device
- App requires a real Android device (API 31+)
- Camera features don't work in emulator without proper setup
- Test all features on physical hardware

### Performance Considerations
- Filters use GPU acceleration (RenderEffect)
- Room database for efficient preset storage
- CameraX for optimized camera operations
- Compose for efficient UI rendering

### Future Enhancements
- Camera2 interop for advanced manual controls
- More filter presets
- Photo gallery integration
- Export/import presets
- Video recording support

## License

Copyright © 2024 RetroCam. All rights reserved.

## Contributing

This is a production app. Development follows:
- Clean Architecture principles
- Material Design 3 guidelines
- Android best practices
- Kotlin coding conventions