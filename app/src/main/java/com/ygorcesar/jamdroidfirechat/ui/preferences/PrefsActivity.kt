package com.ygorcesar.jamdroidfirechat.ui.preferences

import android.os.Bundle
import android.view.MenuItem
import com.ygorcesar.jamdroidfirechat.ui.BaseActivity

class PrefsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeScreen()
    }

    private fun initializeScreen() {
        fragmentManager.beginTransaction()
                .add(android.R.id.content, PrefsFragment())
                .commit()

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}