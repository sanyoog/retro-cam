# RetroCam - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Prerequisites Check
```bash
# Verify Java 17
java -version
# Should show: openjdk version "17.x.x"

# Verify Android SDK
echo $ANDROID_HOME
# Should show path to Android SDK

# Verify ADB
adb version
# Should show Android Debug Bridge version
```

### Build & Run
```bash
# 1. Clone repository
git clone https://github.com/sanyoog/retro-cam.git
cd retro-cam

# 2. Make gradlew executable (Mac/Linux)
chmod +x gradlew

# 3. Connect Android device (enable USB debugging)
adb devices

# 4. Build and install
./gradlew installDebug

# 5. Launch app
adb shell am start -n com.retrocam/.MainActivity

# 6. View logs
adb logcat | grep RetroCam
```

## 📱 Using the App

### Normal Mode (Default)
1. **Grant Camera Permission** when prompted
2. **Tap Filter Button** (bottom left) to select a filter
3. **Tap Shutter Button** (center) to take a photo
4. **Tap Preset Button** (bottom right) to save/load presets

### Available Filters
- **None**: No filter, natural camera
- **Vintage**: Classic film aesthetic with grain
- **Warm**: Golden hour glow
- **Cool**: Crisp and fresh tones
- **B&W**: High contrast monochrome
- **Sepia**: Nostalgic warmth
- **Fade**: Soft and dreamy

### Pro Mode (Coming Soon)
- Switch using top mode selector
- Manual ISO, shutter speed, white balance
- Manual focus and exposure

## 🎨 UI Overview

```
┌─────────────────────────────────┐
│  [Normal Mode] [Pro Mode]       │  ← Mode Selector
│                                  │
│                                  │
│       CAMERA PREVIEW             │
│                                  │
│                                  │
│         [Filter Name]            │  ← Active Filter
│   [Filter] [Shutter] [Preset]   │  ← Controls
└─────────────────────────────────┘
```

## 🛠️ VS Code Tasks

Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac) and type "Tasks":

- **Build Debug APK**: Compile the app
- **Install Debug on Device**: Build and install
- **Launch App on Device**: Install and launch
- **View Logs (Filtered)**: Watch app logs
- **Clean Build**: Clean and rebuild
- **Run Unit Tests**: Execute tests

## 🐛 Troubleshooting

### "No devices found"
```bash
# Enable USB debugging on your Android device:
# Settings → About Phone → Tap "Build Number" 7 times
# Settings → Developer Options → Enable "USB Debugging"

# Verify device connection
adb devices
```

### "Permission Denied" on gradlew
```bash
chmod +x gradlew
```

### "Android SDK not found"
```bash
# Set ANDROID_HOME
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

### Build fails with dependency errors
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug --refresh-dependencies
```

### App crashes on launch
```bash
# View crash logs
adb logcat | grep -E "RetroCam|AndroidRuntime"

# Common causes:
# 1. Camera permission not granted (app will show permission screen)
# 2. Android version < 12 (app requires API 31+)
```

## 📂 Project Tour

### Key Files to Know

**Entry Point:**
- `app/src/main/java/com/retrocam/MainActivity.kt`

**Camera UI:**
- `app/src/main/java/com/retrocam/presentation/camera/CameraScreen.kt`
- `app/src/main/java/com/retrocam/presentation/camera/CameraViewModel.kt`

**Filters:**
- `app/src/main/java/com/retrocam/domain/model/Filter.kt`

**Theme:**
- `app/src/main/java/com/retrocam/presentation/theme/Theme.kt`
- `app/src/main/java/com/retrocam/presentation/theme/Color.kt`

**Components:**
- `app/src/main/java/com/retrocam/presentation/components/GlassComponents.kt`

### File Structure
```
app/src/main/java/com/retrocam/
├── MainActivity.kt                    # App entry
├── RetroCamApplication.kt            # Hilt setup
├── data/                             # Database & repositories
├── domain/                           # Business models
├── presentation/                     # UI (Compose)
│   ├── camera/                       # Camera screen
│   ├── components/                   # Reusable UI
│   └── theme/                        # Design system
├── di/                               # Dependency injection
└── util/                             # Utilities
```

## 🎯 Next Steps

### For Users
1. Test all filters
2. Save favorite presets
3. Provide feedback on UI/UX
4. Report bugs

### For Developers
1. Read `ARCHITECTURE.md`
2. Review `Filter.kt` to understand filter system
3. Check `CameraScreen.kt` for UI implementation
4. See `IMPLEMENTATION_STATUS.md` for roadmap

## 📚 Additional Resources

- **Full Documentation**: `README.md`
- **Build Guide**: `BUILD_INSTRUCTIONS.md`
- **Architecture**: `ARCHITECTURE.md`
- **Implementation Status**: `IMPLEMENTATION_STATUS.md`

## 💡 Tips

### Development
- Use `--no-daemon` with gradlew to save memory
- Keep logs running in a separate terminal
- Test on real device, not emulator
- Use `adb logcat -c` to clear old logs

### Testing Filters
- Try in different lighting conditions
- Test with portraits and landscapes
- Check performance on lower-end devices
- Verify filter names are correct

### UI Customization
- Colors: `presentation/theme/Color.kt`
- Text sizes: `presentation/theme/Type.kt`
- Spacing: Modify padding values
- Animations: Adjust `tween()` durations

## ❓ FAQ

**Q: Can I use this on an emulator?**
A: Camera features work best on real devices. Emulator support is limited.

**Q: What Android version is required?**
A: Android 12 (API 31) or higher.

**Q: How do I add a new filter?**
A: Add a new `Filter` object in `Filter.kt` companion object, then add it to `getAllFilters()`.

**Q: Where are photos saved?**
A: In the app's private storage. Future update will add gallery integration.

**Q: Can I change the UI colors?**
A: Yes! Edit `presentation/theme/Color.kt` and `presentation/theme/Theme.kt`.

## 🤝 Get Help

- Open an issue on GitHub
- Check existing issues for solutions
- Read the full documentation
- Review code comments

---

**Happy Coding! 📸**
