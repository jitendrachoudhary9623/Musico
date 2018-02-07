package com.musico.jitcodez.musico.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.musico.jitcodez.musico.Adapters.MainScreenAdapter
import com.musico.jitcodez.musico.R
import com.musico.jitcodez.musico.Songs
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class MainScreenFragment : Fragment() {

    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var visibleLayout: RelativeLayout? = null
    var noSongs: RelativeLayout? = null
    var recyclerView: RecyclerView? = null

    var myActivity: Activity? = null
    var getSongList: ArrayList<Songs>? = null
    var mainScreenAdapter: MainScreenAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
activity?.title="All Songs"
        visibleLayout = view?.findViewById<RelativeLayout>(R.id.visibleLayout)
        noSongs = view?.findViewById<RelativeLayout>(R.id.noSongs)
        nowPlayingBottomBar = view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
        songTitle = view?.findViewById<TextView>(R.id.songTitleMainScreen)
        playPauseButton = view?.findViewById<ImageButton>(R.id.playPauseButton)
        recyclerView = view?.findViewById<RecyclerView>(R.id.contentMain)

        return view;
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongList = getSongFromPhone()

        val prefs=activity.getSharedPreferences("action_sort",Context.MODE_PRIVATE)
        val action_sort_ascending=prefs.getString("action_sort_ascending","true")
        val action_sort_recent=prefs.getString("action_sort_recent","false")




        if(getSongList!=null){
            mainScreenAdapter = MainScreenAdapter(getSongList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(myActivity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = mainScreenAdapter
            if(action_sort_ascending!!.equals("true",true))
            {
                Collections.sort(getSongList,Songs.Statified.nameComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            }
            else if(action_sort_recent!!.equals("true",true))
            {
                Collections.sort(getSongList,Songs.Statified.dateComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            }
        }
        else
        {
            visibleLayout?.visibility=View.INVISIBLE
            noSongs?.visibility=View.VISIBLE

        }
        bottomBarSetup()

    }

    fun bottomBarSetup() {
        try {
            bottomBarClickHanler()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)

            }
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {

        }
    }

    fun bottomBarClickHanler() {
        nowPlayingBottomBar?.setOnClickListener({
            FavoriteFragment.statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment();
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("SongId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentSongHelper?.currentPostion?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
            args.putString("FavBottomBar", "success")
            songPlayingFragment.arguments = args
            fragmentManager.beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()

        })

        playPauseButton?.setOnClickListener({
            var trackPosition:Int=0
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    fun getSongFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()

        //getting song data from content provider
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            var songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            var songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            var songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            var songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            var dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentArtists = songCursor.getString(songArtist)
                var currentTitle = songCursor.getString(songTitle)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)

                arrayList.add(Songs(currentId, currentTitle, currentArtists, currentData, currentDate))


            }


        }
        return arrayList
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending) {
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()

            if (getSongList != null) {
                Collections.sort(getSongList, Songs.Statified.nameComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false

        } else if (switcher == R.id.action_sort_recent) {
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "false")
            editor?.putString("action_sort_recent", "true")
            editor?.apply()
            if (getSongList != null) {
                Collections.sort(getSongList, Songs.Statified.dateComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
        return false;
        }
return true
    }

}// Required empty public constructor
