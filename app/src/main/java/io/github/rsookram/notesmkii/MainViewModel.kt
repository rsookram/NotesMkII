package io.github.rsookram.notesmkii

import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val uriData: UriData) : ViewModel() {

    private var editedContent: String? = null

    private val uri = MutableLiveData<Uri>()

    val title: LiveData<String> = uri.switchMap {
        liveData<String> {
            emit(uriData.getName(it) ?: "")
        }
    }

    val content: LiveData<String> = uri.switchMap {
        liveData {
            emit(uriData.readContent(it))
        }
    }

    fun onOpenClicked() {
    }

    fun onCreateClicked() {
    }

    fun onUriSelected(uri: Uri) {
        this.uri.value = uri
    }

    fun onTextChanged(text: String) {
        editedContent = text
    }

    fun save() {
        val uri = uri.value ?: return
        val content = editedContent ?: return

        GlobalScope.launch {
            uriData.writeContent(uri, content)
        }
    }
}
