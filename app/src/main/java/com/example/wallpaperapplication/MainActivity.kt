package com.example.wallpaperapplication

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import androidx.core.content.ContextCompat
import android.os.PowerManager
import android.provider.Settings
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WallpaperScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedPhotos by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    var wallpaperHistory by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var intervalMinutes by remember {
        mutableStateOf("15")
    }

    var selectedTarget by remember {
        mutableStateOf("Both")
    }
    fun requestBatteryOptimizationExemption() {

        val powerManager =
            context.getSystemService(
                Context.POWER_SERVICE
            ) as PowerManager

        if (!powerManager.isIgnoringBatteryOptimizations(
                context.packageName
            )
        ) {

            val intent =
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                ).apply {
                    data =
                        android.net.Uri.parse(
                            "package:${context.packageName}"
                        )
                }

            context.startActivity(intent)
        }
    }
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

    fun stopWallpaperWorker(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("wallpaper_worker")
    }
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
    LaunchedEffect(Unit) {
        selectedPhotos =
            PhotoStorage.loadPhotos(context)
                .map { Uri.parse(it) }

        wallpaperHistory =
            WallpaperHistory.loadHistory(context)
    }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia()
        ) { uris ->

            selectedPhotos = uris

            uris.forEach { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                }
            }

            scope.launch {
                PhotoStorage.savePhotos(
                    context,
                    uris.map { it.toString() }
                )
            }
        }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CenterAlignedTopAppBar(
                title = {
                    Text("Wallpaper Application")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            ) {
                Text("Select Photos")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {

                    if (selectedPhotos.isEmpty()) {
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
                                Random.nextInt(selectedPhotos.size)
                            ]

                        val bitmap =
                            BitmapFactory.decodeStream(
                                context.contentResolver.openInputStream(randomUri)
                            )

                        val wm = WallpaperManager.getInstance(context)

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

                            "Both" -> {

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

                        WallpaperHistory.saveWallpaper(
                            context,
                            randomUri.toString()
                        )

                        wallpaperHistory =
                            WallpaperHistory.loadHistory(context)

                        Toast.makeText(
                            context,
                            "Wallpaper Updated!",
                            Toast.LENGTH_SHORT
                        ).show()

                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            e.message ?: "Error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                Text("Set Random Wallpaper")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = intervalMinutes,
                onValueChange = { intervalMinutes = it },
                label = { Text("Interval (minutes)") }
            )
            Text(
                text = "Wallpaper Target",
                style = MaterialTheme.typography.titleMedium
            )

            Row {

                listOf(
                    "Home",
                    "Lock",
                    "Both"
                ).forEach { target ->

                    FilterChip(
                        selected = selectedTarget == target,
                        onClick = {
                            selectedTarget = target
                        },
                        label = {
                            Text(target)
                        }
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {

                    val minutes =
                        intervalMinutes.toLongOrNull() ?: 15

                    Toast.makeText(
                        context,
                        "Checking Battery Optimization",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (minutes >= 15) {
                        Toast.makeText(
                            context,
                            "Requesting Exemption",
                            Toast.LENGTH_SHORT
                        ).show()
                        scheduleWallpaperWorker(
                            context,
                            minutes
                        )
                        scheduleWallpaperWorker(
                            context,
                            intervalMinutes = minutes
                        )
                        startForegroundWallpaperService()

                        Toast.makeText(
                            context,
                            "Auto Wallpaper Started",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text("Start Auto Wallpaper")
            }

            Button(
                onClick = {

                    stopWallpaperWorker(context)

                    stopForegroundWallpaperService()

                    Toast.makeText(
                        context,
                        "Auto Wallpaper Stopped",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ) {
                Text("Stop Auto Wallpaper")
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedPhotos.isNotEmpty()) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {

                    items(selectedPhotos) { photo ->

                        AsyncImage(
                            model = photo,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(8.dp)
                                .aspectRatio(9f / 16f)
                                .clickable {

                                    try {

                                        val bitmap =
                                            BitmapFactory.decodeStream(
                                                context.contentResolver.openInputStream(photo)
                                            )

                                        val wm = WallpaperManager.getInstance(context)

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

                                            "Both" -> {

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
                                        WallpaperHistory.saveWallpaper(
                                            context,
                                            photo.toString()
                                        )

                                        wallpaperHistory =
                                            WallpaperHistory.loadHistory(context)

                                        Toast.makeText(
                                            context,
                                            "Wallpaper Set!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } catch (_: Exception) {
                                    }
                                }
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    2.dp,
                                    Color.Gray,
                                    RoundedCornerShape(16.dp)
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "History Count: ${wallpaperHistory.size}"
            )
        }
    }
}
