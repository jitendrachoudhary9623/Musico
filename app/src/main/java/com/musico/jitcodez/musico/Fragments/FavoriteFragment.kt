package com.musico.jitcodez.musico.Fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.musico.jitcodez.musico.Adapters.FavoriteAdapter
import com.musico.jitcodez.musico.R
import com.musico.jitcodez.musico.Songs


/**
 * A simple [Fragment] subclass.
 */
class FavoriteFragment : Fragment() {

    var myActivity: Activity? = null
    var trackPosition: Int = 0
    var getSongsList: ArrayList<Songs>? = null
    var noFavorites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    object statified{

        var mediaPlayer: MediaPlayer?=null
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater?.inflate(R.layout.fragment_favorite, container, false)

        noFavorites = view?.findViewById(R.id.noFavorites)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarMainScreen)
        songTitle = view?.findViewById(R.id.songTitle)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        recyclerView = view?.findViewById(R.id.favoriteRecycler)




        return view;
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getSongFromPhone()
        if (getSongsList == null) {
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE

        } else {
            //keep it as it is
            var favoriteAdapter = FavoriteAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayputManager = LinearLayoutManager(activity)
            recyclerView?.layoutManager = mLayputManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = favoriteAdapter
            recyclerView?.setHasFixedSize(true)

        }
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
            statified.mediaPlayer=SongPlayingFragment.Statified.mediaPlayer
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
}// Required empty public constructor
