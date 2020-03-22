package io.github.rsookram.notesmkii

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Supplier

class UriData(private val context: Context, private val bgExecutor: Executor) {

    fun getName(uri: Uri): CompletableFuture<String?> =
        CompletableFuture.supplyAsync(
            Supplier { DocumentFile.fromSingleUri(context, uri)!!.name },
            bgExecutor
        )

    fun readContent(uri: Uri): CompletableFuture<String> =
        CompletableFuture.supplyAsync(
            Supplier {
                val stream = context.contentResolver.openInputStream(uri) ?: return@Supplier ""
                stream.use { it.reader().readText() }
            },
            bgExecutor
        )

    fun writeContent(uri: Uri, content: String): CompletableFuture<Void> =
        CompletableFuture.runAsync(
            Runnable {
                val stream = context.contentResolver.openOutputStream(uri)
                    ?: throw IOException("Failed to open $uri")

                stream.use {
                    it.writer().use { writer ->
                        writer.write(content)
                    }
                }
            },
            bgExecutor
        )
}
