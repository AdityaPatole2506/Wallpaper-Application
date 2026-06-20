# 📱 Wallpaper Application

A modern Android wallpaper manager built using **Kotlin** and **Jetpack Compose** that allows users to preview, manage, and automatically rotate wallpapers with a clean Material 3 interface.

## ✨ Features

### 🖼️ Wallpaper Management

* Select multiple wallpapers using Android Photo Picker
* Preview wallpapers before applying
* Apply wallpapers instantly
* Set random wallpaper
* Shuffle wallpapers instantly using **Shuffle Now**
* Remove individual wallpapers with long press
* Clear all selected wallpapers
* Display total selected wallpaper count

### 🎯 Wallpaper Targets

Apply wallpapers to:

* Home Screen
* Lock Screen
* Both Screens

### 🔄 Auto Wallpaper System

* Automatic wallpaper rotation
* Custom interval selection
* Minimum interval validation
* Foreground service support
* Smart Start/Stop controls
* Running/Stopped status indicator
* Auto wallpaper state persistence
* Auto wallpaper recovery after device reboot
* Interval persistence across app restarts

### 💾 Storage & Persistence

* Save selected wallpapers locally
* Save auto wallpaper interval
* Save auto wallpaper enabled state
* Restore selected wallpapers on app restart
* Restore wallpaper rotation settings after reboot

### 🛡️ Default Wallpaper Protection

* Set default Home wallpaper
* Set default Lock wallpaper
* Remove default wallpapers individually
* Automatically restore default wallpapers when Auto Wallpaper is stopped
* Null-safe wallpaper restoration to prevent crashes

### 🎨 User Experience

* Material 3 Design
* Responsive wallpaper grid
* Wallpaper count indicator
* Improved empty state screen
* Confirmation dialog for wallpaper deletion
* Confirmation dialog for clearing all wallpapers
* Smart button enable/disable states
* Toast feedback for important actions

## 🚀 Current Capabilities

### Manual Wallpaper Actions

* Preview wallpaper before applying
* Apply selected wallpaper
* Apply random wallpaper
* Shuffle wallpaper instantly

### Automated Wallpaper Actions

* Scheduled wallpaper changes
* Background wallpaper rotation
* Reboot recovery
* Persistent configuration

### Wallpaper Safety

* Default wallpaper backup
* Wallpaper restoration
* Crash-safe wallpaper handling

## 🛠️ Tech Stack

* Kotlin
* Jetpack Compose
* Material 3
* WorkManager
* Android Foreground Services
* SharedPreferences
* Android Photo Picker
* Coil
* Coroutines

## 📱 Application Workflow

1. Select wallpapers from device storage
2. Preview wallpapers before applying
3. Choose target (Home / Lock / Both)
4. Apply wallpaper manually or randomly
5. Use Shuffle Now for instant wallpaper changes
6. Configure Auto Wallpaper interval
7. Start automatic wallpaper rotation
8. Enjoy automatic wallpaper management

## 📂 Project Structure

* `MainActivity.kt` – Main UI and wallpaper controls
* `WallpaperWorker.kt` – Scheduled wallpaper rotation
* `WallpaperForegroundService.kt` – Background service management
* `PhotoStorage.kt` – Wallpaper persistence
* `DefaultWallpaperStorage.kt` – Default wallpaper backup and restore
* `IntervalStorage.kt` – Interval persistence
* `AutoWallpaperPreferences.kt` – Auto wallpaper state persistence
* `BootReceiver.kt` – Reboot recovery logic

## 🔮 Planned Features

* Wallpaper Categories
* Favorites System
* Enhanced Preview Experience
* UI Animations
* Backup & Restore
* Wallpaper History
* Statistics Dashboard

## 👨‍💻 Author

**Aditya Patole**

Built using Kotlin, Jetpack Compose, and Android modern development practices.
