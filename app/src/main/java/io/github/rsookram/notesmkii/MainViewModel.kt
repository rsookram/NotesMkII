package io.github.rsookram.notesmkii

import android.net.Uri
import androidx.lifecycle.*
import java.util.concurrent.Future

class MainViewModel(private val uriData: UriData) : ViewModel() {

    private val cancellables = mutableListOf<Future<*>>()

    private var editedContent: String? = null

    var uri: Uri? = null
        private set

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _content = MutableLiveData<String>()
    val content: LiveData<String> = _content

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
        this.uri = uri

        cancelPendingWork()

        cancellables += uriData.getName(uri).thenAccept(_title::postValue)
        cancellables += uriData.readContent(uri).thenAccept(_content::postValue)
    }

    fun onTextChanged(text: String) {
        editedContent = text
    }

    fun save() {
        val uri = uri ?: return
        val content = editedContent ?: return

        uriData.writeContent(uri, content)
    }

    override fun onCleared() {
        cancelPendingWork()
    }

    private fun cancelPendingWork() {
        cancellables.forEach { it.cancel(false) }
    }
}

private fun <T : Any> eventLiveData() = object : MutableLiveData<T>() {

    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer { t ->
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
