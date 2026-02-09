# RetroCam

A premium Android camera app with a glassy, modern UI and retro-inspired output.

## 🏗️ Architecture

- **Platform**: Android (API 31+)
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Camera**: CameraX + Camera2 interop
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Async**: Coroutines + Flow

## 📁 Project Structure

```
app/src/main/kotlin/com/retrocam/app/
├── data/
│   └── repository/          # Repository implementations
├── di/                      # Hilt dependency injection modules
├── domain/
│   ├── model/              # Domain models
│   └── repository/         # Repository interfaces
├── presentation/
│   └── camera/             # ViewModels
└── ui/
    ├── camera/             # Camera screen
    ├── components/         # Reusable UI components
    └── theme/              # Material 3 theme
```

## 🚀 Getting Started

### Prerequisites

- JDK 17 or higher
- Android SDK (API 31+)
- Gradle 8.2+

### Build & Run

```bash
# Build the project
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build and install
./gradlew build
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Development Workflow

This project is developed in **VS Code only** - no Android Studio dependencies.

1. Edit code in VS Code
2. Build using Gradle in terminal
3. Install via ADB
4. Test on real device
5. Debug with logcat

```bash
# Watch logs
adb logcat | grep RetroCam
```

## 📸 Features

### Phase 1: Foundation ✅
- [x] Project structure + Gradle setup
- [x] Hilt DI scaffolding
- [x] Basic CameraX integration
- [x] MVVM architecture
- [x] Camera preview + capture
- [x] Glassy Material 3 UI

### Phase 2: Camera Modes (Next)
- [ ] Normal mode (auto everything)
- [ ] Pro mode (manual controls)
- [ ] ISO, shutter speed, WB, focus controls
- [ ] Camera2 interop for manual settings

### Phase 3: Filters & Presets
- [ ] Filter engine (GPU-based)
- [ ] Live preview pipeline
- [ ] Preset system (save/load/edit)
- [ ] Retro-inspired filters

### Phase 4: Premium Polish
- [ ] Glassy overlay animations
- [ ] Slide-up panels
- [ ] Satisfying interactions
- [ ] Performance optimization

## 🎨 Design Principles

- **Modern glass UI**, not retro UI cosplay
- **Retro output** through filters and color science
- **Two modes**: Normal (simple) and Pro (manual controls)
- **Premium feel**: Smooth, confident, clean
- **GPU-accelerated** filters and effects

## 📦 Dependencies

- **Compose**: UI framework
- **CameraX**: Camera API
- **Hilt**: Dependency injection
- **Room**: Local database (for presets)
- **DataStore**: Settings persistence
- **Accompanist**: Permissions handling
- **Coroutines**: Async operations

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumentation tests (requires connected device)
./gradlew connectedAndroidTest
```

## 📱 Deployment

```bash
# Build release APK
./gradlew assembleRelease

# Build release bundle (for Play Store)
./gradlew bundleRelease
```

## 📄 License

TBD

---

**Built with ❤️ for photography enthusiasts**