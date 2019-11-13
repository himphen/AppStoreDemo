package com.hibernatev2.appstoredemo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hibernatev2.appstoredemo.R
import com.hibernatev2.appstoredemo.ui.fragment.MainFragment
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_no_drawer)
        setSupportActionBar(toolbar)

        val ab = supportActionBar
        ab!!.elevation = 100f
        ab.title = "AppStoreDemo"
        ab.setDisplayHomeAsUpEnabled(false)
        ab.setHomeButtonEnabled(false)

        val fragment = MainFragment.newInstance()
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }
}
