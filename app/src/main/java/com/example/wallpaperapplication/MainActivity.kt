package com.example.wallpaperapplication

// =========================
// IMPORT SECTION
// =========================

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random


// Compose Imports
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

import coil.compose.AsyncImage
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.combinedClickable

// =========================
// MAIN ACTIVITY
// =========================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WallpaperScreen()
        }
    }
}

// =========================
// WALLPAPER SCREEN
// =========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // =========================
    // VARIABLE SECTION
    // =========================

    var selectedPhotos by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    var photoToDelete by remember {
        mutableStateOf<Uri?>(null)
    }

    var intervalMinutes by remember {
        mutableStateOf(
            IntervalStorage.getInterval(
                context
            )
        )
    }

    var isAutoWallpaperEnabled by remember {
        mutableStateOf(
            AutoWallpaperPreferences.isEnabled(
                context
            )
        )
    }

    var previewPhoto by remember {
        mutableStateOf<Uri?>(null)
    }

    var selectedTarget by remember {
        mutableStateOf("Both")
    }

    var defaultHomeWallpaper by remember {
        mutableStateOf<Uri?>(null)
    }

    var defaultLockWallpaper by remember {
        mutableStateOf<Uri?>(null)
    }

    val scrollState =
        rememberScrollState()

    var showClearAllDialog by remember {
        mutableStateOf(false)
    }

    // =========================
    // BATTERY OPTIMIZATION
    // =========================

    fun requestBatteryOptimizationExemption() {

        val powerManager =
            context.getSystemService(
                Context.POWER_SERVICE
            ) as PowerManager

        if (
            !powerManager.isIgnoringBatteryOptimizations(
                context.packageName
            )
        ) {

            val intent =
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                ).apply {

                    data =
                        Uri.parse(
                            "package:${context.packageName}"
                        )
                }

            context.startActivity(intent)
        }
    }

    // =========================
    // WORKMANAGER FUNCTIONS
    // =========================

    fun scheduleWallpaperWorker(
        context: Context,
        intervalMinutes: Long
    ) {

        val workRequest =
            PeriodicWorkRequestBuilder<WallpaperWorker>(
                repeatInterval = intervalMinutes,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "wallpaper_worker",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    fun restoreDefaultWallpapers(
        context: Context
    ) {

        try {

            val wm =
                WallpaperManager.getInstance(
                    context
                )

            DefaultWallpaperStorage
                .getHomeWallpaper(context)
                ?.let { uri ->

                    val inputStream =
                        context.contentResolver
                            .openInputStream(
                                Uri.parse(uri)
                            )

                    val bitmap =
                        inputStream?.let {
                            BitmapFactory.decodeStream(it)
                        }

                    if (bitmap != null) {

                        wm.setBitmap(
                            bitmap,
                            null,
                            true,
                            WallpaperManager.FLAG_SYSTEM
                        )
                    }
                }

            DefaultWallpaperStorage
                .getLockWallpaper(context)
                ?.let { uri ->

                    val inputStream =
                        context.contentResolver
                            .openInputStream(
                                Uri.parse(uri)
                            )

                    val bitmap =
                        inputStream?.let {
                            BitmapFactory.decodeStream(it)
                        }

                    if (bitmap != null) {

                        wm.setBitmap(
                            bitmap,
                            null,
                            true,
                            WallpaperManager.FLAG_LOCK
                        )
                    }
                }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    fun stopWallpaperWorker(
        context: Context
    ) {

        WorkManager.getInstance(context)
            .cancelUniqueWork(
                "wallpaper_worker"
            )
        restoreDefaultWallpapers(
            context
        )
    }
    // =========================
    // FOREGROUND SERVICE
    // =========================

    fun startForegroundWallpaperService() {

        val intent =
            Intent(
                context,
                WallpaperForegroundService::class.java
            )

        ContextCompat.startForegroundService(
            context,
            intent
        )
    }

    fun stopForegroundWallpaperService() {

        val intent =
            Intent(
                context,
                WallpaperForegroundService::class.java
            )

        context.stopService(intent)
    }

    // =========================
    // WALLPAPER APPLY FUNCTION
    // =========================

    fun applyWallpaper(
        bitmap: android.graphics.Bitmap
    ) {

        val wm =
            WallpaperManager.getInstance(
                context
            )

        when (selectedTarget) {

            "Home" -> {

                wm.setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_SYSTEM
                )
            }

            "Lock" -> {

                wm.setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_LOCK
                )
            }

            else -> {

                wm.setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_SYSTEM
                )

                wm.setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_LOCK
                )
            }
        }
    }
    // =========================
    // WALLPAPER HELPER FUNCTION
    // =========================

    // =========================
    // LOAD SAVED DATA
    // =========================

    LaunchedEffect(Unit) {

        selectedPhotos =
            PhotoStorage
                .loadPhotos(context)
                .map {
                    Uri.parse(it)
                }

        defaultHomeWallpaper =
            DefaultWallpaperStorage
                .getHomeWallpaper(context)
                ?.let {
                    Uri.parse(it)
                }

        defaultLockWallpaper =
            DefaultWallpaperStorage
                .getLockWallpaper(context)
                ?.let {
                    Uri.parse(it)
                }
    }

    // =========================
    // PHOTO PICKER
    // =========================

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .PickMultipleVisualMedia()
        ) { uris ->

            selectedPhotos = uris

            uris.forEach { uri ->

                try {

                    context.contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                } catch (_: Exception) {
                }
            }

            scope.launch {

                PhotoStorage.savePhotos(
                    context,
                    uris.map {
                        it.toString()
                    }
                )
            }
        }

    // =========================
    // DEFAULT HOME PICKER
    // =========================

    val homeWallpaperPicker =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .PickVisualMedia()
        ) { uri ->

            uri?.let {

                defaultHomeWallpaper = it

                DefaultWallpaperStorage
                    .saveHomeWallpaper(
                        context,
                        it.toString()
                    )
            }
        }

    // =========================
    // DEFAULT LOCK PICKER
    // =========================

    val lockWallpaperPicker =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .PickVisualMedia()
        ) { uri ->

            uri?.let {

                defaultLockWallpaper = it

                DefaultWallpaperStorage
                    .saveLockWallpaper(
                        context,
                        it.toString()
                    )
            }
        }
    // =========================
// DELETE WALLPAPER DIALOG
// =========================

    if (photoToDelete != null) {

        AlertDialog(
            onDismissRequest = {
                photoToDelete = null
            },

            title = {
                Text("Remove Wallpaper")
            },

            text = {
                Text(
                    "Do you want to remove this wallpaper from your collection?"
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        selectedPhotos =
                            selectedPhotos.filter {
                                it != photoToDelete
                            }

                        scope.launch {

                            PhotoStorage.savePhotos(
                                context,
                                selectedPhotos.map {
                                    it.toString()
                                }
                            )
                        }

                        photoToDelete = null
                    }
                ) {
                    Text("Remove")
                }
            },

            dismissButton = {

                Button(
                    onClick = {
                        photoToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }


    // =========================
    // MAIN UI
    // =========================

    if (showClearAllDialog) {

        AlertDialog(
            onDismissRequest = {
                showClearAllDialog = false
            },

            title = {
                Text("Clear All Wallpapers")
            },

            text = {
                Text(
                    "Are you sure you want to remove all selected wallpapers?"
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        selectedPhotos = emptyList()

                        scope.launch {

                            PhotoStorage.savePhotos(
                                context,
                                emptyList()
                            )
                        }

                        showClearAllDialog = false
                    }
                ) {
                    Text("Clear All")
                }
            },

            dismissButton = {

                Button(
                    onClick = {
                        showClearAllDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // =========================
            // TOP BAR
            // =========================

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Wallpaper Application"
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =========================
            // SELECT PHOTOS
            // =========================

            Button(
                onClick = {

                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts
                                .PickVisualMedia
                                .ImageOnly
                        )
                    )
                }
            ) {
                Text("Select Photos")
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =========================
            // RANDOM WALLPAPER
            // =========================

            Button(
                onClick = {

                    if (
                        selectedPhotos.isEmpty()
                    ) {

                        Toast.makeText(
                            context,
                            "Please select photos first",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    try {

                        val randomUri =
                            selectedPhotos[
                                Random.nextInt(
                                    selectedPhotos.size
                                )
                            ]

                        val bitmap =
                            BitmapFactory.decodeStream(
                                context.contentResolver
                                    .openInputStream(
                                        randomUri
                                    )
                            )

                        applyWallpaper(
                            bitmap
                        )

                        Toast.makeText(
                            context,
                            "Wallpaper Updated!",
                            Toast.LENGTH_SHORT
                        ).show()

                    } catch (e: Exception) {

                        Toast.makeText(
                            context,
                            e.message
                                ?: "Unknown Error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                Text(
                    "Set Random Wallpaper"
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =========================
            // INTERVAL INPUT
            // =========================

            OutlinedTextField(
                value = intervalMinutes,

                onValueChange = {

                    intervalMinutes = it

                    IntervalStorage.saveInterval(
                        context,
                        it
                    )
                },

                label = {
                    Text(
                        "Interval (minutes)"
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =========================
            // WALLPAPER TARGET
            // =========================

            Text(
                text = "Wallpaper Target",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )
            ) {

                listOf(
                    "Home",
                    "Lock",
                    "Both"
                ).forEach { target ->

                    FilterChip(
                        selected =
                            selectedTarget == target,

                        onClick = {
                            selectedTarget = target
                        },

                        label = {
                            Text(target)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // =========================
            // DEFAULT WALLPAPERS
            // =========================

            Card(
                modifier = Modifier
                    .fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        20.dp
                    )
            ) {

                Column(
                    modifier = Modifier
                        .padding(16.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Default Wallpapers",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        // =========================
                        // HOME WALLPAPER
                        // =========================

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            defaultHomeWallpaper?.let {

                                AsyncImage(
                                    model = it,
                                    contentDescription = null,

                                    modifier = Modifier
                                        .size(
                                            width = 90.dp,
                                            height = 160.dp
                                        )
                                        .clip(
                                            RoundedCornerShape(
                                                16.dp
                                            )
                                        ),

                                    contentScale =
                                        ContentScale.Crop
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text("Home")

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Button(
                                onClick = {

                                    homeWallpaperPicker.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts
                                                .PickVisualMedia
                                                .ImageOnly
                                        )
                                    )
                                }
                            ) {

                                Text("Select")
                            }
                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            if (defaultHomeWallpaper != null) {

                                Button(
                                    onClick = {

                                        DefaultWallpaperStorage
                                            .removeHomeWallpaper(
                                                context
                                            )

                                        defaultHomeWallpaper = null
                                    }
                                ) {
                                    Text("Remove")
                                }
                            }

                        }

                        // =========================
                        // LOCK WALLPAPER
                        // =========================

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            defaultLockWallpaper?.let {

                                AsyncImage(
                                    model = it,
                                    contentDescription = null,

                                    modifier = Modifier
                                        .size(
                                            width = 90.dp,
                                            height = 160.dp
                                        )
                                        .clip(
                                            RoundedCornerShape(
                                                16.dp
                                            )
                                        ),

                                    contentScale =
                                        ContentScale.Crop
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text("Lock")

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Button(
                                onClick = {

                                    lockWallpaperPicker.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts
                                                .PickVisualMedia
                                                .ImageOnly
                                        )
                                    )
                                }
                            ) {

                                Text("Select")
                            }
                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            if (defaultLockWallpaper != null) {

                                Button(
                                    onClick = {

                                        DefaultWallpaperStorage
                                            .removeLockWallpaper(
                                                context
                                            )

                                        defaultLockWallpaper = null
                                    }
                                ) {
                                    Text("Remove")
                                }
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    // =========================
                    // AUTO WALLPAPER
                    // =========================

                    Text(
                        text = "Auto Wallpaper",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Text(
                        text =
                            if (isAutoWallpaperEnabled)
                                "Status: Running 🟢"
                            else
                                "Status: Stopped 🔴",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Button(
                        enabled = !isAutoWallpaperEnabled,

                        onClick = {

                            val minutes =
                                intervalMinutes
                                    .toLongOrNull()
                                    ?: 15

                            if (
                                minutes < 15
                            ) {

                                Toast.makeText(
                                    context,
                                    "Minimum interval is 15 minutes",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }

                            requestBatteryOptimizationExemption()

                            scheduleWallpaperWorker(
                                context,
                                minutes
                            )

                            startForegroundWallpaperService()

                            AutoWallpaperPreferences.setEnabled(
                                context,
                                true
                            )

                            isAutoWallpaperEnabled = true

                            Toast.makeText(
                                context,
                                "Auto Wallpaper Started",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {

                        Text(
                            "Start Auto Wallpaper"
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Button(
                        enabled = isAutoWallpaperEnabled,

                        onClick = {

                            try {

                                stopWallpaperWorker(
                                    context
                                )

                                stopForegroundWallpaperService()

                                AutoWallpaperPreferences.setEnabled(
                                    context,
                                    false
                                )

                                isAutoWallpaperEnabled = false

                                Toast.makeText(
                                    context,
                                    "Auto Wallpaper Stopped",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    e.message ?: "Error",
                                    Toast.LENGTH_LONG
                                ).show()

                                e.printStackTrace()
                            }
                        }
                    ) {

                        Text(
                            "Stop Auto Wallpaper"
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Button(
                        enabled =
                            selectedPhotos.isNotEmpty(),

                        onClick = {

                            try {

                                val randomPhoto =
                                    selectedPhotos.random()

                                val bitmap =
                                    BitmapFactory.decodeStream(
                                        context.contentResolver
                                            .openInputStream(
                                                randomPhoto
                                            )
                                    )

                                if (bitmap != null) {

                                    applyWallpaper(
                                        bitmap
                                    )

                                    Toast.makeText(
                                        context,
                                        "Wallpaper Shuffled!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    e.message
                                        ?: "Failed to shuffle wallpaper",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    ) {

                        Text(
                            "Shuffle Now"
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                    // =========================
                    // PHOTO GRID SECTION
                    // =========================

                    if (
                        selectedPhotos.isNotEmpty()
                    ) {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text =
                                    "Selected Wallpapers: ${selectedPhotos.size}",

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                            )

                            Button(
                                onClick = {
                                    showClearAllDialog = true
                                }
                            ) {
                                Text("Clear All")
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        LazyVerticalGrid(
                            columns =
                                GridCells.Fixed(2),

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 500.dp),

                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    8.dp
                                ),

                            verticalArrangement =
                                Arrangement.spacedBy(
                                    8.dp
                                )
                        ) {

                            items(
                                selectedPhotos
                            ) { photo ->

                                AsyncImage(
                                    model = photo,

                                    contentDescription =
                                        null,

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(
                                                9f / 16f
                                            )
                                            .clip(
                                                RoundedCornerShape(
                                                    16.dp
                                                )
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = Color.Gray,
                                                shape =
                                                    RoundedCornerShape(
                                                        16.dp
                                                    )
                                            )
                                            .combinedClickable(

                                                onClick = {
                                                    previewPhoto = photo
                                                },

                                                onLongClick = {
                                                    photoToDelete = photo
                                                }
                                            ),

                                    contentScale =
                                        ContentScale.Fit
                                )
                            }
                        }

                    } else {

                        Text(
                            text =
                                "No wallpapers selected",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(
                                24.dp
                            )
                    )

                }
            }
        }
    }

    if (previewPhoto != null) {

        AlertDialog(
            onDismissRequest = {
                previewPhoto = null
            },

            confirmButton = {

                Button(
                    onClick = {

                        try {

                            val bitmap =
                                BitmapFactory.decodeStream(
                                    context.contentResolver
                                        .openInputStream(
                                            previewPhoto!!
                                        )
                                )

                            applyWallpaper(bitmap)

                            Toast.makeText(
                                context,
                                "Wallpaper Set!",
                                Toast.LENGTH_SHORT
                            ).show()

                            previewPhoto = null

                        } catch (e: Exception) {

                            Toast.makeText(
                                context,
                                e.message
                                    ?: "Failed to set wallpaper",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {

                Button(
                    onClick = {
                        previewPhoto = null
                    }
                ) {
                    Text("Close")
                }
            },

            text = {

                AsyncImage(
                    model = previewPhoto,
                    contentDescription = null,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(500.dp),

                    contentScale =
                    ContentScale.Fit
                )
            }
        )
    }
}
