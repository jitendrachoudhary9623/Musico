package com.musico.jitcodez.musico.Activities

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.LinearLayout
import com.musico.jitcodez.musico.Fragments.MainScreenFragment
import com.musico.jitcodez.musico.R


class MainActivity : AppCompatActivity() {

    var drawerLayout:DrawerLayout?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        val toolbar=findViewById<Toolbar>(R.id.toolbar);
        setSupportActionBar(toolbar)
        drawerLayout=findViewById(R.id.drawer_layout);
        val toggle=ActionBarDrawerToggle(this@MainActivity,drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout?.setDrawerListener(toggle);
        toggle.syncState()

        val m= MainScreenFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.details_fragment,m,"MainScreenFragement")
                .commit()

        var nav_rv=findViewById<RecyclerView>(R.id.rv_navigation_list);
        nav_rv.layoutManager=LinearLayoutManager(this@MainActivity,LinearLayout.VERTICAL,false)
        nav_rv.itemAnimator=DefaultItemAnimator()
    }

    override fun onStart() {
        super.onStart()
    }
}

