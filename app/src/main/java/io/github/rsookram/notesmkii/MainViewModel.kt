package io.github.rsookram.notesmkii

import android.net.Uri
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Future

class MainViewModel(private val uriData: UriData) {

    private val handler = Handler(Looper.getMainLooper())
    private val cancellables = mutableListOf<Future<*>>()

    private var editedContent: String? = null

    var uri: Uri? = null
        private set

    private val title: String? = null
    var onTitleChange: (String?) -> Unit = {}
        set(value) {
            if (title != null) {
                onTitleChange(title)
            }
            field = value
        }

    private val content: String? = null
    var onContentLoad: (String) -> Unit = {}
        set(value) {
            if (content != null) {
                onContentLoad(content)
            }
            field = value
        }

    fun onUriSelected(uri: Uri) {
        this.uri = uri

        cancelPendingWork()

        cancellables += uriData.getName(uri).thenAccept { title ->
            handler.post { onTitleChange(title) }
        }
        cancellables += uriData.readContent(uri).thenAccept { content ->
            handler.post { onContentLoad(content) }
        }
    }

    fun onTextChanged(text: String) {
        editedContent = text
    }

    fun save() {
        val uri = uri ?: return
        val content = editedContent ?: return

        uriData.writeContent(uri, content)
    }

    fun onCleared() {
        cancelPendingWork()
    }

    private fun cancelPendingWork() {
        cancellables.forEach { it.cancel(false) }
        cancellables.clear()

        handler.removeCallbacksAndMessages(null)
    }
}
