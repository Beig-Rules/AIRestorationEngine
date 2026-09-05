package com.restoration.feature.result

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object MediaActions {

    fun saveToGallery(context: Context, sourceUriOrPath: String?): Boolean {
        if (sourceUriOrPath.isNullOrBlank()) {
            toast(context, "No restored image to save")
            return false
        }
        return try {
            val bitmap = decodeBitmap(context, sourceUriOrPath)
                ?: run {
                    toast(context, "Could not read image")
                    return false
                }
            val name = "restored_${System.currentTimeMillis()}.png"
            val saved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveViaMediaStore(context, bitmap, name)
            } else {
                saveLegacy(context, bitmap, name)
            }
            toast(context, if (saved) "Saved to Gallery" else "Save failed")
            saved
        } catch (e: Exception) {
            toast(context, "Save error: ${e.message}")
            false
        }
    }

    fun share(context: Context, sourceUriOrPath: String?, title: String = "Restored photo") {
        if (sourceUriOrPath.isNullOrBlank()) {
            toast(context, "No image to share")
            return
        }
        try {
            val uri = toShareableUri(context, sourceUriOrPath)
                ?: run {
                    toast(context, "Could not prepare share")
                    return
                }
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share restored image"))
        } catch (e: Exception) {
            toast(context, "Share error: ${e.message}")
        }
    }

    private fun decodeBitmap(context: Context, uriOrPath: String): Bitmap? {
        return try {
            when {
                uriOrPath.startsWith("content://") || uriOrPath.startsWith("file://") -> {
                    context.contentResolver.openInputStream(Uri.parse(uriOrPath))?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
                else -> {
                    val f = File(uriOrPath)
                    if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun saveViaMediaStore(context: Context, bitmap: Bitmap, displayName: String): Boolean {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AIRestoration")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
        resolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } ?: return false
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return true
    }

    @Suppress("DEPRECATION")
    private fun saveLegacy(context: Context, bitmap: Bitmap, displayName: String): Boolean {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val folder = File(dir, "AIRestoration")
        if (!folder.exists()) folder.mkdirs()
        val file = File(folder, displayName)
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
        return true
    }

    private fun toShareableUri(context: Context, uriOrPath: String): Uri? {
        return try {
            when {
                uriOrPath.startsWith("content://") -> Uri.parse(uriOrPath)
                uriOrPath.startsWith("file://") -> {
                    val file = File(Uri.parse(uriOrPath).path ?: return null)
                    fileProviderUri(context, file)
                }
                else -> {
                    val file = File(uriOrPath)
                    if (!file.exists()) return null
                    fileProviderUri(context, file)
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun fileProviderUri(context: Context, file: File): Uri {
        return try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (_: Exception) {
            Uri.fromFile(file)
        }
    }

    private fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
