package com.shopply.appEcommerce.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LocalStorageService - Almacenamiento local de imágenes (sin Firebase)
 *
 * Guarda las imágenes en el almacenamiento interno del dispositivo.
 * Las imágenes se guardan en: /data/data/com.shopply.appEcommerce/files/images/
 *
 * VENTAJAS:
 * - ✅ No requiere Firebase Storage (plan Blaze)
 * - ✅ Gratuito y sin límites
 * - ✅ Funciona offline
 *
 * DESVENTAJAS:
 * - ❌ Las imágenes se pierden si se desinstala la app
 * - ❌ Ocupan espacio en el dispositivo
 * - ❌ No se sincronizan entre dispositivos
 */
@Singleton
class LocalStorageService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val IMAGES_DIR = "product_images"
        private const val MAX_DIMENSION = 1920
        private const val JPEG_QUALITY = 85
        private const val TAG = "LocalStorageService"
    }

    private val imagesDir: File by lazy {
        File(context.filesDir, IMAGES_DIR).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    /**
     * Guarda una imagen del producto en almacenamiento local
     *
     * @param uri URI de la imagen (galería o cámara)
     * @return Result con la ruta local de la imagen guardada
     */
    suspend fun saveProductImage(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 1. Leer y comprimir imagen
            val compressedBitmap = compressImage(uri)

            // 2. Generar nombre único
            val fileName = "${UUID.randomUUID()}.jpg"
            val imageFile = File(imagesDir, fileName)

            // 3. Guardar en disco
            FileOutputStream(imageFile).use { out ->
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }

            // 4. Limpiar memoria
            compressedBitmap.recycle()

            // 5. Retornar ruta relativa (se guardará en la BD)
            val relativePath = "file://${imageFile.absolutePath}"

            Log.d(TAG, "Imagen guardada: $relativePath")
            Result.success(relativePath)

        } catch (e: Exception) {
            Log.e(TAG, "Error guardando imagen", e)
            Result.failure(e)
        }
    }

    /**
     * Elimina una imagen del almacenamiento local
     *
     * @param imagePath Ruta de la imagen (ejemplo: file:///data/data/.../image.jpg)
     */
    suspend fun deleteProductImage(imagePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (imagePath.startsWith("file://")) {
                val path = imagePath.removePrefix("file://")
                val file = File(path)

                if (file.exists()) {
                    file.delete()
                    Log.d(TAG, "Imagen eliminada: $path")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando imagen", e)
            Result.failure(e)
        }
    }

    /**
     * Comprime la imagen para reducir tamaño
     */
    private suspend fun compressImage(uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        try {
            // 1. Leer bitmap original
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // 2. Calcular nuevas dimensiones (mantener aspect ratio)
            val (newWidth, newHeight) = calculateDimensions(
                originalBitmap.width,
                originalBitmap.height
            )

            // 3. Escalar bitmap si es necesario
            if (newWidth < originalBitmap.width || newHeight < originalBitmap.height) {
                val scaledBitmap = Bitmap.createScaledBitmap(
                    originalBitmap,
                    newWidth,
                    newHeight,
                    true
                )
                originalBitmap.recycle()
                scaledBitmap
            } else {
                originalBitmap
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error comprimiendo imagen", e)
            // Crear bitmap vacío si falla
            Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        }
    }

    /**
     * Calcula dimensiones respetando aspect ratio
     */
    private fun calculateDimensions(width: Int, height: Int): Pair<Int, Int> {
        if (width <= MAX_DIMENSION && height <= MAX_DIMENSION) {
            return Pair(width, height)
        }

        val aspectRatio = width.toFloat() / height.toFloat()

        return if (width > height) {
            // Landscape
            Pair(MAX_DIMENSION, (MAX_DIMENSION / aspectRatio).toInt())
        } else {
            // Portrait
            Pair((MAX_DIMENSION * aspectRatio).toInt(), MAX_DIMENSION)
        }
    }

    /**
     * Obtiene el tamaño total de imágenes almacenadas
     */
    fun getTotalStorageSize(): Long {
        return imagesDir.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }

    /**
     * Limpia todas las imágenes (útil para testing o limpiar caché)
     */
    suspend fun clearAllImages(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            imagesDir.listFiles()?.forEach { it.delete() }
            Log.d(TAG, "Todas las imágenes eliminadas")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error limpiando imágenes", e)
            Result.failure(e)
        }
    }
}

