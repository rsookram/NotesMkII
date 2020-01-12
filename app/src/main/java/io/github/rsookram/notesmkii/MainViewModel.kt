package io.github.rsookram.notesmkii

import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val uriData: UriData) : ViewModel() {

    private var editedContent: String? = null

    private val uri = MutableLiveData<Uri>()
    val currentUri get() = uri.value

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

    private val openEventData = eventLiveData<Unit>()
    val opens: LiveData<Unit> = openEventData

    private val createEventData = eventLiveData<Unit>()
    val creates: LiveData<Unit> = createEventData

    fun onOpenClicked() {
        openEventData.setValue(Unit)
    }

    fun onCreateClicked() {
        createEventData.setValue(Unit)
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

private fun <T : Any> eventLiveData() = object : MutableLiveData<T>() {

    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer<T> { t ->
            if (pending) {
                pending = false
                observer.onChanged(t)
            }
        })
    }

    override fun setValue(value: T) {
        pending = true
        super.setValue(value)
    }
}
