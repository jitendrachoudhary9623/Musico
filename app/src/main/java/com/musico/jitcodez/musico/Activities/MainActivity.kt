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
import com.musico.jitcodez.musico.Adapters.NavigationDrawer_Adapter
import com.musico.jitcodez.musico.Fragments.MainScreenFragment
import com.musico.jitcodez.musico.R


class MainActivity : AppCompatActivity() {
    var navigationIconList=intArrayOf(R.drawable.navigation_allsongs,R.drawable.navigation_favorites,R.drawable.navigation_settings,R.drawable.navigation_aboutus)
    var navigationTextList:ArrayList<String> = arrayListOf();
    object statified{
        var drawerLayout:DrawerLayout?=null;

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        val toolbar=findViewById<Toolbar>(R.id.toolbar);
        setSupportActionBar(toolbar)
        MainActivity.statified.drawerLayout=findViewById(R.id.drawer_layout);

        navigationTextList.add("All Songs")
        navigationTextList.add("Favorites")
        navigationTextList.add("Settings")
        navigationTextList.add("About US")
        val toggle=ActionBarDrawerToggle(this@MainActivity,MainActivity.statified.drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        MainActivity.statified.drawerLayout?.setDrawerListener(toggle);
        toggle.syncState()

        val m= MainScreenFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.details_fragment,m,"MainScreenFragement")
                .commit()


        var navAdapter=NavigationDrawer_Adapter(navigationTextList,navigationIconList,this)
        navAdapter.notifyDataSetChanged();
        var nav_rv=findViewById<RecyclerView>(R.id.rv_navigation_list);
        nav_rv.layoutManager=LinearLayoutManager(this@MainActivity,LinearLayout.VERTICAL,false)
        nav_rv.itemAnimator=DefaultItemAnimator()
        nav_rv.adapter=navAdapter;
        nav_rv.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
    }
}

