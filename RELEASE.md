# Release Guide

This document explains how to create and publish releases for RetroCam.

## 📦 Release Strategy

Each development phase gets its own release with a universal APK that works on all devices.

### Phase Releases

- **Phase 1**: Foundation (Camera + Basic UI)
- **Phase 2**: Camera Modes (Normal + Pro)
- **Phase 3**: Filters & Presets
- **Phase 4**: Polish & Optimization

## 🚀 Creating a Release

### Option 1: Automated Release (GitHub Actions)

1. **Tag your commit:**
   ```bash
   git tag -a phase1 -m "Phase 1: Foundation Complete"
   git push origin phase1
   ```

2. **GitHub Actions will automatically:**
   - Build the universal APK
   - Create a GitHub Release
   - Attach the APK file
   - Generate release notes

### Option 2: Manual Release

1. **Build the release APK:**
   ```bash
   ./gradlew assembleRelease
   ```

2. **Find the APK:**
   ```
   app/build/outputs/apk/release/RetroCam-1.0.0-release.apk
   ```

3. **Create GitHub Release:**
   - Go to GitHub → Releases → "Draft a new release"
   - Tag: `phase1` (or appropriate phase)
   - Title: `RetroCam Phase 1 - Foundation`
   - Upload the APK file
   - Add release notes

## 📱 Testing the Release

```bash
# Install on connected device
adb install -r RetroCam-1.0.0-release.apk

# Check logs
adb logcat | grep RetroCam

# Uninstall
adb uninstall com.retrocam.app
```

## 📋 Release Checklist

Before creating a release:

- [ ] All tests pass
- [ ] No critical bugs
- [ ] Tested on real device
- [ ] Camera permissions work
- [ ] Capture saves photos correctly
- [ ] UI is responsive
- [ ] No crashes on startup
- [ ] Version code incremented
- [ ] Version name updated
- [ ] Release notes prepared

## 🔄 Version Naming

- **Phase releases**: `phase1`, `phase2`, `phase3`, `phase4`
- **Patches**: `phase1.1`, `phase1.2`
- **Feature releases**: `v1.0.0`, `v1.1.0`, `v2.0.0`

## 📝 Release Notes Template

```markdown
## RetroCam Phase X - [Name]

### ✨ New Features
- Feature 1
- Feature 2

### 🐛 Bug Fixes
- Fix 1
- Fix 2

### 🎨 UI Improvements
- Improvement 1
- Improvement 2

### 📱 Installation
Download the universal APK and install on Android 12+

### 🧪 Known Issues
- Issue 1 (if any)
```

## 🔐 Signing (Production)

For Play Store releases, use a proper keystore:

1. **Generate keystore:**
   ```bash
   keytool -genkey -v -keystore retrocam-release.keystore \
     -alias retrocam -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Update `app/build.gradle.kts`:**
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file("path/to/retrocam-release.keystore")
           storePassword = System.getenv("KEYSTORE_PASSWORD")
           keyAlias = System.getenv("KEY_ALIAS")
           keyPassword = System.getenv("KEY_PASSWORD")
       }
   }
   ```

3. **Add secrets to GitHub:**
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`

## 🎯 Current Phase: Phase 1 ✅

Phase 1 foundation is complete and ready for release!

To release:
```bash
git tag -a phase1 -m "Phase 1: Foundation - Camera + Glass UI"
git push origin phase1
```

The GitHub Action will automatically build and create the release.
