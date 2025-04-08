// ui/screens/CameraScreen.kt
package com.example.photosharingapp.ui.screens

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    onPhotoCaptured: (Uri?) -> Unit,
    onCancel: () -> Unit,
    paddingValues: PaddingValues,
    onNavigateToPreview: (Uri) -> Unit // Nuevo callback para manejar la navegación
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var isCameraReady by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Vista previa de la cámara
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                        isCameraReady = true
                        Log.d("CameraScreen", "Camera successfully bound to lifecycle")
                    } catch (e: Exception) {
                        Log.e("CameraScreen", "Failed to bind camera: ${e.message}", e)
                        errorMessage = "Error al iniciar la cámara: ${e.message}"
                        isCameraReady = false
                    }
                }, ContextCompat.getMainExecutor(context))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Mostrar mensaje de error si ocurre
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }

        // Botones en la parte inferior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { onCancel() }) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    if (!isCameraReady) {
                        Log.e("CameraScreen", "Camera is not ready yet")
                        errorMessage = "La cámara no está lista. Por favor, espera."
                        return@Button
                    }

                    if (isCapturing) {
                        Log.d("CameraScreen", "Capture in progress, ignoring new request")
                        return@Button
                    }

                    isCapturing = true

                    val saveCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(
                        context.contentResolver,
                        saveCollection,
                        contentValues
                    ).build()

                    imageCapture?.takePicture(
                        outputOptions,
                        executor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = outputFileResults.savedUri
                                if (savedUri == null) {
                                    Log.e("CameraScreen", "Saved URI is null")
                                    errorMessage = "Error: No se pudo guardar la foto"
                                    isCapturing = false
                                    return
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    val values = ContentValues().apply {
                                        put(MediaStore.Images.Media.IS_PENDING, 0)
                                    }
                                    try {
                                        context.contentResolver.update(savedUri, values, null, null)
                                        Log.d("CameraScreen", "Marked photo as non-pending: $savedUri")
                                    } catch (e: Exception) {
                                        Log.e("CameraScreen", "Failed to mark photo as non-pending: ${e.message}", e)
                                        errorMessage = "Error al guardar la foto: ${e.message}"
                                    }
                                }

                                // Llamar a onPhotoCaptured y manejar la navegación en el hilo principal
                                onPhotoCaptured(savedUri)
                                coroutineScope.launch(Dispatchers.Main) {
                                    onNavigateToPreview(savedUri)
                                }
                                isCapturing = false
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraScreen", "Failed to capture photo: ${exception.message}", exception)
                                errorMessage = "Error al capturar la foto: ${exception.message}"
                                isCapturing = false
                            }
                        }
                    )
                },
                enabled = imageCapture != null && errorMessage == null && !isCapturing && isCameraReady
            ) {
                Text("Capturar")
            }
        }
    }

    // Limpiar el executor y la cámara al salir
    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
            cameraProviderFuture.get().unbindAll()
            isCameraReady = false
        }
    }
}