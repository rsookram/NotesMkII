package io.github.rsookram.notesmkii

import android.os.Bundle
import android.widget.Toolbar
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.toolbar)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.open -> {
                    true
                }
                R.id.create -> {
                    true
                }
                else -> false
            }
        }
    }
}
