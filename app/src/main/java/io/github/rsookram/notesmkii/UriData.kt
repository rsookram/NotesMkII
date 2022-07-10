package io.github.rsookram.notesmkii

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class UriData(private val context: Context) {

    private val bgExecutor = Executors.newSingleThreadExecutor()

    fun getName(uri: Uri): CompletableFuture<String?> = execute {
        val resolver = context.contentResolver

        resolver.query(
            uri,
            arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                cursor.getString(0)
            } else {
                null
            }
        }
    }

    fun readContent(uri: Uri): CompletableFuture<String> = execute {
        val stream = context.contentResolver.openInputStream(uri) ?: return@execute ""
        stream.use { it.reader().readText() }
    }

    fun writeContent(uri: Uri, content: String): CompletableFuture<Unit> = execute {
        val stream = context.contentResolver.openOutputStream(uri, "rwt")
            ?: throw IOException("Failed to open $uri")

        stream.use {
            it.writer().use { writer ->
                writer.write(content)
            }
        }
    }

    private inline fun <T> execute(crossinline f: () -> T): CompletableFuture<T> =
        CompletableFuture.supplyAsync(
            { f() },
            bgExecutor
        )
}
