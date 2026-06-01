package com.example.wallpaperapplication

import kotlinx.coroutines.launch
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import kotlin.random.Random
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridItemSpan

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WallpaperScreen()
        }
    }
}

@Composable
fun WallpaperScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedPhotos by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    var photoToDelete by remember {
        mutableStateOf<Uri?>(null)
    }
    if (photoToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                photoToDelete = null
            },
            title = {
                Text("Delete Photo")
            },
            text = {
                Text("Remove this photo from the collection?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedPhotos =
                            selectedPhotos.filter {
                                it != photoToDelete
                            }

                        scope.launch {
                            PhotoStorage.savePhotos(
                                context,
                                selectedPhotos.map { it.toString() }
                            )
                        }

                        photoToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        photoToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    LaunchedEffect(Unit) {
        val savedUris = PhotoStorage.loadPhotos(context)

        selectedPhotos = savedUris.map {
            Uri.parse(it)
        }
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
                } catch (e: Exception) {
                    e.printStackTrace()
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
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Wallpaper Application",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(20.dp))

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

                                val inputStream =
                                    context.contentResolver.openInputStream(randomUri)

                                val bitmap =
                                    BitmapFactory.decodeStream(inputStream)

                                WallpaperManager
                                    .getInstance(context)
                                    .setBitmap(
                                        bitmap,
                                        null,
                                        true,
                                        WallpaperManager.FLAG_SYSTEM
                                    )

                                Toast.makeText(
                                    context,
                                    "Wallpaper Updated!",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    e.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    ) {
                        Text("Set Random Wallpaper")
                    }
                    Spacer(modifier = Modifier.height(20.dp))

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

                            selectedPhotos = emptyList()

                            scope.launch {
                                PhotoStorage.savePhotos(
                                    context,
                                    emptyList()
                                )
                            }
                        }
                    ) {
                        Text("Clear All Photos")
                    }

                        Text("Select Photos")


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
                                        .size(150.dp)
                                        .clickable {
                                            photoToDelete = photo
                                        },
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            item(
                                span = {
                                    GridItemSpan(maxLineSpan)
                                }
                            ) {
                                Text(
                                    text = "Selected Photos: ${selectedPhotos.size}"
                                )
                            }
                        }
                    }
                    }}}



