package io.github.rsookram.notesmkii

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import android.widget.Toolbar

private const val REQUEST_CODE_OPEN = 1
private const val REQUEST_CODE_CREATE = 2

private const val STATE_URI = "uri"

class MainActivity : Activity() {

    private lateinit var vm: MainViewModel

    private lateinit var editor: TextView

    private var bottomIgnoreAreaHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        vm = lastNonConfigurationInstance as? MainViewModel
            ?: MainViewModel(UriData(applicationContext))
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
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        type = "text/plain"
                    }

                    startActivityForResult(intent, REQUEST_CODE_OPEN)
                    true
                }
                R.id.create -> {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        type = "text/plain"
                    }

                    startActivityForResult(intent, REQUEST_CODE_CREATE)
                    true
                }
                else -> false
            }
        }

        vm.onTitleChange = { toolbar.title = it }

        editor.apply {
            filters = arrayOf(RemoveFormattingFilter())
            doOnTextChanged(vm::onTextChanged)
        }

        vm.onContentLoad = {
            if (editor.text.toString() != it) {
                editor.text = it
            }
        }

        applySystemUiVisibility(toolbar, editor)

        findViewById<View>(android.R.id.content).setOnApplyWindowInsetsListener { _, insets ->
            bottomIgnoreAreaHeight = insets.getInsets(WindowInsets.Type.systemBars()).bottom

            insets
        }
    }

    override fun onRetainNonConfigurationInstance(): Any = vm

    override fun onPause() {
        super.onPause()
        vm.save()

        editor.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()

        vm.onTitleChange = {}
        vm.onContentLoad = {}

        if (isFinishing) {
            vm.onCleared()
        }
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.y > window.decorView.height - bottomIgnoreAreaHeight) {
            return false
        }

        return super.dispatchTouchEvent(ev)
    }
}
