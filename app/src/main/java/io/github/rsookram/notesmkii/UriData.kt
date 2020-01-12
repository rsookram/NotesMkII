package io.github.rsookram.notesmkii

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

class UriData(private val context: Context, private val bgDispatcher: CoroutineDispatcher) {

    suspend fun getName(uri: Uri): String? = withContext(bgDispatcher) {
        DocumentFile.fromSingleUri(context, uri)!!.name
    }

    suspend fun readContent(uri: Uri): String = withContext(bgDispatcher) {
        val stream = context.contentResolver.openInputStream(uri) ?: return@withContext ""
        stream.use { it.bufferedReader().readText() }
    }

    suspend fun writeContent(uri: Uri, content: String) = withContext(bgDispatcher) {
        val stream = context.contentResolver.openOutputStream(uri)
            ?: throw IOException("Failed to open $uri")

        stream.use {
            it.bufferedWriter().use { writer ->
                writer.write(content)
            }
        }
    }
}
