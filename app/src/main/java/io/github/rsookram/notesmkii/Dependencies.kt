package io.github.rsookram.notesmkii

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers

object Dependencies {

    @Suppress("UNCHECKED_CAST")
    fun viewModelLazy(activity: ComponentActivity): Lazy<MainViewModel> =
        ViewModelLazy(MainViewModel::class, { activity.viewModelStore }) {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                    MainViewModel(UriData(activity.applicationContext, Dispatchers.IO)) as T
            }
        }
}
