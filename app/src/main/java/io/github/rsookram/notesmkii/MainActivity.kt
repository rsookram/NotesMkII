package io.github.rsookram.notesmkii

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.ComponentActivity

private const val REQUEST_CODE_OPEN = 1
private const val REQUEST_CODE_CREATE = 2

private const val STATE_URI = "uri"

class MainActivity : ComponentActivity(R.layout.activity_main) {

    private lateinit var vm: MainViewModel

    private lateinit var editor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = Dependencies.viewModel(this)
        editor = findViewById(R.id.text)

        val uri = savedInstanceState?.getParcelable<Uri>(STATE_URI)
        if (uri != null) {
            vm.onUriSelected(uri)
        }

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

        editor.doOnTextChanged(vm::onTextChanged)

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

        applySystemUiVisibility(toolbar, editor)
    }

    override fun onPause() {
        super.onPause()
        vm.save()

        editor.clearFocus()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_URI, vm.uri)
    }
}
