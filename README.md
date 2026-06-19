# 📱 Wallpaper Application

A modern Android wallpaper manager built using **Kotlin** and **Jetpack Compose** that allows users to manage, preview, and automatically rotate wallpapers with a clean Material 3 interface.

## ✨ Features

### 🖼️ Wallpaper Management

* Select multiple wallpapers using Android Photo Picker
* Preview wallpapers before applying
* Apply wallpapers instantly
* Random wallpaper selection
* Shuffle wallpapers instantly with **Shuffle Now**
* Remove individual wallpapers
* Clear all selected wallpapers

### 🎯 Wallpaper Targets

* Set wallpaper for:

  * Home Screen
  * Lock Screen
  * Both Screens

### 🔄 Auto Wallpaper

* Automatic wallpaper rotation
* Custom interval support
* Foreground service support for reliability
* Smart Start/Stop controls
* Running/Stopped status indicator
* Interval persistence across app restarts

### 💾 Persistence & Recovery

* Save selected wallpapers locally
* Save auto wallpaper interval
* Save auto wallpaper enabled state
* Restore auto wallpaper after device reboot
* Restore saved interval after reboot

### 🛡️ Default Wallpaper Protection

* Set default Home wallpaper
* Set default Lock wallpaper
* Automatically restore default wallpapers when auto wallpaper stops
* Remove default wallpapers individually
* Null-safe wallpaper restoration to prevent crashes

### 🎨 User Experience Improvements

* Material 3 UI
* Responsive wallpaper grid
* Wallpaper counter display
* Improved empty state screen
* Confirmation dialogs for destructive actions
* Long-press wallpaper removal
* Smart button states to prevent invalid actions

## 📂 Tech Stack

* Kotlin
* Jetpack Compose
* Material 3
* WorkManager
* Foreground Services
* SharedPreferences
* Android Photo Picker
* Coil

## 🚀 Current Capabilities

* Manual wallpaper application
* Random wallpaper application
* Instant wallpaper shuffling
* Scheduled wallpaper rotation
* Device reboot recovery
* Home/Lock/Both wallpaper targeting
* Default wallpaper restoration

## 📸 Application Flow

1. Select wallpapers from device storage
2. Preview wallpapers before applying
3. Choose target (Home / Lock / Both)
4. Apply wallpaper manually or randomly
5. Enable Auto Wallpaper for scheduled rotation
6. Use Shuffle Now for instant wallpaper changes

## 🔮 Future Improvements

* Wallpaper Categories
* Favorites System
* Enhanced Preview Screen
* UI Animations
* Backup & Restore
* Wallpaper Statistics

## 👨‍💻 Author

**Aditya Patole**
