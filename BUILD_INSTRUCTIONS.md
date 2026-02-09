# Build Instructions for RetroCam

## Quick Start

### 1. Clone and Setup
```bash
git clone https://github.com/sanyoog/retro-cam.git
cd retro-cam
```

### 2. Verify Prerequisites
```bash
# Check Java version (needs JDK 17)
java -version

# Check Android SDK
echo $ANDROID_HOME
# Should point to Android SDK directory

# Verify Android SDK has required components:
# - API 34 (compileSdk)
# - Build Tools 34.0.0+
# - Platform Tools
```

### 3. Build the Project
```bash
# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug --no-daemon

# Output will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

### 4. Install on Device
```bash
# Connect Android device via USB with USB debugging enabled
adb devices

# Install the APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Or use Gradle
./gradlew installDebug
```

## Troubleshooting

### Gradle Wrapper Issues
If `./gradlew` fails, regenerate the wrapper:
```bash
# Download Gradle 7.5 manually
curl -L https://services.gradle.org/distributions/gradle-7.5-bin.zip -o /tmp/gradle.zip
unzip /tmp/gradle.zip -d /tmp/
/tmp/gradle-7.5/bin/gradle wrapper --gradle-version 7.5
```

### Missing Android SDK
```bash
# Set ANDROID_HOME environment variable
export ANDROID_HOME=/path/to/android/sdk

# Or add to ~/.bashrc or ~/.zshrc:
echo 'export ANDROID_HOME=/path/to/android/sdk' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools' >> ~/.bashrc
source ~/.bashrc
```

### Dependency Resolution Issues
If build fails due to missing dependencies:
```bash
# Clean build
./gradlew clean

# Rebuild
./gradlew assembleDebug --refresh-dependencies
```

### Kotlin Compiler Issues
Ensure Kotlin version matches Compose compiler:
- Kotlin: 1.8.22
- Compose Compiler: 1.4.8

## Build Variants

### Debug Build
```bash
./gradlew assembleDebug
```
- Includes debugging information
- Not optimized
- Use for development

### Release Build
```bash
# Generate keystore first
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias retrocam

# Build release
./gradlew assembleRelease

# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests
```bash
# Requires connected device or emulator
./gradlew connectedAndroidTest
```

## Debugging

### View Logs
```bash
# Filter by app package
adb logcat | grep "com.retrocam"

# Clear logs first
adb logcat -c
adb logcat | grep "RetroCam"
```

### Debug APK
```bash
# Install debug build
./gradlew installDebug

# Launch app
adb shell am start -n com.retrocam/.MainActivity

# View logs
adb logcat | grep "RetroCam\|AndroidRuntime"
```

## VS Code Setup

### Required Extensions
- Kotlin Language Support
- Gradle for Java
- Android iOS Emulator (optional)

### Build Tasks
Add to `.vscode/tasks.json`:
```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "Build Debug",
      "type": "shell",
      "command": "./gradlew assembleDebug",
      "group": {
        "kind": "build",
        "isDefault": true
      }
    },
    {
      "label": "Install Debug",
      "type": "shell",
      "command": "./gradlew installDebug",
      "dependsOn": "Build Debug"
    },
    {
      "label": "Clean Build",
      "type": "shell",
      "command": "./gradlew clean assembleDebug"
    }
  ]
}
```

## Performance Notes

- First build will download dependencies (~500MB)
- Subsequent builds use Gradle cache
- Use `--no-daemon` to avoid background Gradle processes
- Use `--parallel` for faster multi-module builds

## Minimum Requirements

- **OS**: Windows 10+, macOS 11+, or Linux
- **RAM**: 8GB minimum, 16GB recommended
- **Disk Space**: 10GB free space
- **Android Device**: API 31+ (Android 12+)
- **JDK**: Version 17
- **Gradle**: 7.5+
- **Android Gradle Plugin**: 7.4.2
