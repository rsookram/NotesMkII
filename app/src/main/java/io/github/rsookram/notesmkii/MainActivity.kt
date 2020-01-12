package io.github.rsookram.notesmkii

import android.app.Activity
import android.content.Intent
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

private const val REQUEST_CODE_OPEN = 1
private const val REQUEST_CODE_CREATE = 2

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

        vm.opens.observe(this) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                type = "text/plain"
            }

            startActivityForResult(intent, REQUEST_CODE_OPEN)
        }

        vm.creates.observe(this) {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                type = "text/plain"
            }

            startActivityForResult(intent, REQUEST_CODE_CREATE)
        }
    }

    override fun onPause() {
        super.onPause()
        vm.save()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data?.data
        if (resultCode != Activity.RESULT_OK || uri == null) {
            finish()
            return
        }

        vm.onUriSelected(uri)
    }
}
