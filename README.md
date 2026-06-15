# 📱 Wallpaper Application

A modern Android wallpaper manager built with **Kotlin**, **Jetpack Compose**, and **Material 3**.

The app allows users to select multiple wallpapers, preview them before applying, automatically rotate wallpapers at custom intervals, and configure default Home and Lock screen wallpapers for fallback restoration.

---

## ✨ Features

### 🖼️ Wallpaper Selection

* Select multiple photos using Android Photo Picker
* Persistent photo storage using DataStore
* View all selected wallpapers in a responsive grid

### 👀 Wallpaper Preview

* Tap any wallpaper to open a preview dialog
* Apply wallpapers only after confirmation
* Prevents accidental wallpaper changes

### 🎲 Random Wallpaper

* Instantly apply a random wallpaper from the selected collection
* Supports Home Screen, Lock Screen, or Both

### ⚙️ Auto Wallpaper

* Automatic wallpaper rotation using WorkManager
* Custom interval selection (minutes)
* Start and stop auto wallpaper service
* Foreground service support

### 🏠 Default Wallpapers

* Select a default Home wallpaper
* Select a default Lock wallpaper
* Persistent storage using SharedPreferences

### 🔄 Fallback Wallpaper Restoration

* Automatically restores default wallpapers when:

  * No wallpapers are available
  * Auto wallpaper is stopped
  * Wallpaper worker cannot load wallpapers

### 🎨 Modern UI

* Built entirely with Jetpack Compose
* Material 3 design system
* Wallpaper previews displayed side-by-side
* Clean and responsive layout

---

## 🛠️ Tech Stack

* Kotlin
* Jetpack Compose
* Material 3
* WorkManager
* Android Photo Picker
* DataStore Preferences
* SharedPreferences
* Foreground Services
* Coil AsyncImage

---

## 🚀 Upcoming Features

* Wallpaper categories
* Favorites system
* Remove selected wallpapers
* Backup & restore settings
* Scheduled wallpaper changes
* Wallpaper statistics
* Cloud sync support

---

## 👨‍💻 Author

**Aditya Patole**

Android Wallpaper Application built for learning Android development with Kotlin and Jetpack Compose.
