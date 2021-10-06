package com.onionmonster.tutorial.fileexplorer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.onionmonster.tutorial.fileexplorer.fragments.CardFragment
import com.onionmonster.tutorial.fileexplorer.fragments.HomeFragment
import com.onionmonster.tutorial.fileexplorer.fragments.InternalFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(this,
                                                                  drawerLayout,
                                                                  toolbar,
                                                                  R.string.open_drawer,
                                                                  R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Show HomeFragment instead of a blank screen when app opens up
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
        navigationView.setCheckedItem(R.id.nav_home)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).addToBackStack(null).commit()
            }

            R.id.nav_internal -> {
                val internalFragment = InternalFragment()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit()
            }

            R.id.nav_card -> {
                val cardFragment = CardFragment()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, cardFragment).addToBackStack(null).commit()
            }

            R.id.nav_about -> {
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        supportFragmentManager.popBackStackImmediate()
        if (drawerLayout.isDrawerOpen((GravityCompat.START))) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}