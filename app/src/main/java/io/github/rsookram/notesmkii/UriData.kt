package io.github.rsookram.notesmkii

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class UriData(private val context: Context, private val bgExecutor: Executor) {

    fun getName(uri: Uri): CompletableFuture<String?> = execute {
        DocumentFile.fromSingleUri(context, uri)!!.name
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
