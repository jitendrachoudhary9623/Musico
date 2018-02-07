package com.musico.jitcodez.musico.Activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NotificationBuilderWithBuilderAccessor
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
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment
import com.musico.jitcodez.musico.R


class MainActivity : AppCompatActivity() {
    var navigationIconList=intArrayOf(R.drawable.navigation_allsongs,R.drawable.navigation_favorites,R.drawable.navigation_settings,R.drawable.navigation_aboutus)
    var navigationTextList:ArrayList<String> = arrayListOf();
    object statified{
        var drawerLayout:DrawerLayout?=null;
        var notificationManager:NotificationManager?=null


    }

    var trackNotificationBuilder: Notification?=null

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
        nav_rv.layoutManager= LinearLayoutManager(this@MainActivity,LinearLayout.VERTICAL,false) as RecyclerView.LayoutManager?
        nav_rv.itemAnimator=DefaultItemAnimator()
        nav_rv.adapter=navAdapter;
        nav_rv.setHasFixedSize(true)

        val intent= Intent(this@MainActivity,MainActivity::class.java)
        val pIntent=PendingIntent.getActivity(this@MainActivity,System.currentTimeMillis() as Int,
                intent,0)
        trackNotificationBuilder=Notification.Builder(this)
                .setContentTitle("A track is played in background")
                .setSmallIcon(R.drawable.play_icon)
                .setContentIntent(pIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()

        statified.notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStart() {
        super.onStart()
        try{
            statified.notificationManager?.cancel(1978)
        }catch(e:Exception)
        {

        }
    }

    override fun onStop() {
        super.onStop()
        try{
            if( SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                statified.notificationManager?.notify(1978,trackNotificationBuilder)
            }
        }catch(e:Exception)
        {

        }
    }

    override fun onResume() {
        super.onResume()
        try{
            statified.notificationManager?.cancel(1978)
        }catch(e:Exception)
        {

        }
    }
}

