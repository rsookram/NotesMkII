package io.github.rsookram.notesmkii

import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import kotlinx.coroutines.Dispatchers

class MainActivity : FragmentActivity(R.layout.activity_main) {

    @Suppress("UNCHECKED_CAST")
    private val vm by ViewModelLazy(MainViewModel::class, { this.viewModelStore }) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                MainViewModel(UriData(applicationContext, Dispatchers.IO)) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.toolbar)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.open -> {
                    vm.onOpenClicked()
                    true
                }
                R.id.create -> {
                    vm.onCreateClicked()
                    true
                }
                else -> false
            }
        }

        vm.title.observe(this) { toolbar.title = it }

        val editor = findViewById<TextView>(R.id.text)
        editor.doOnTextChanged { text, _, _, _ -> vm.onTextChanged(text.toString()) }

        vm.content.observe(this) {
            if (editor.text.toString() != it) {
                editor.text = it
            }
        }
    }

    override fun onPause() {
        super.onPause()
        vm.save()
    }
}
