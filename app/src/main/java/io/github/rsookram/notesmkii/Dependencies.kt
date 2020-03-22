package io.github.rsookram.notesmkii

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers

object Dependencies {

    fun viewModel(activity: ComponentActivity): MainViewModel {
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                MainViewModel(UriData(activity.applicationContext, Dispatchers.IO)) as T
        }

        return ViewModelProvider(activity.viewModelStore, factory).get(MainViewModel::class.java)
    }
}
