package com.musico.jitcodez.musico.Fragments

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.musico.jitcodez.musico.CurrentSongHelper
import com.musico.jitcodez.musico.Databases.MusicoDatabase
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Staticated.onSongComplete
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Staticated.playNext
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Staticated.processInformation
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Staticated.updateTextView
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.mediaPlayer
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.nextImageButton
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.playpauseImageButton
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.seekbar
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.startTimeText
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.audioVisualization
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.currentPosition
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.currentSongHelper
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.endTimeText
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.fab
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.favoriteContent
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.fetchSongs
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.glView
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.loopImageButton
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.myActivity
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.previousImageButton
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.shuffleImageButton
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.songArtistView
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment.Statified.songTitleView


import com.musico.jitcodez.musico.R
import com.musico.jitcodez.musico.Songs
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SongPlayingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SongPlayingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SongPlayingFragment : Fragment() {

    object Statified{
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var audioVisualization: AudioVisualization? = null
        var glView:GLAudioVisualizationView?=null
        //views

        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var fab:ImageButton?=null


        var currentSongHelper: CurrentSongHelper? = null

        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null


        var favoriteContent:MusicoDatabase?=null
    }

    object Staticated{
        var MY_PREFS_SHUFFLE="shuffle feature"
        var MY_PREFS_LOOP=" Loop feature"


        var updateSongTime=object:Runnable{
            override fun run() {

                var getCurrent=
                        mediaPlayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long)-
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long))))
                Handler().postDelayed(this,1000)

            }
        }
        fun onSongComplete(){

            if(currentSongHelper?.isShuffle as Boolean)
            {
                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlaying=true
                var nextSong = fetchSongs?.get(currentPosition)
                currentSongHelper?.songPath = nextSong?.songData
                currentSongHelper?.songTitle = nextSong?.songTitle
                currentSongHelper?.songArtist = nextSong?.artist
                currentSongHelper?.songId = nextSong?.songID as Long

                updateTextView(currentSongHelper?.songTitle as String,currentSongHelper?.songArtist as String)
                mediaPlayer?.reset()
                try {
                    mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                    mediaPlayer?.prepare()
                    mediaPlayer?.start()
                    processInformation(mediaPlayer as MediaPlayer)

                } catch (e: Exception) {

                }
            }
            else
            {
                if(currentSongHelper?.isLoop as Boolean)
                {
                    currentSongHelper?.isPlaying=true;

                }else
                {
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlaying=true
                }
            }
            if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean){
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_on))
            }
            else
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_off))

        }

        fun updateTextView(title:String,songArtist:String)
        {
            songTitleView?.setText(title)
            songArtistView?.setText(songArtist)
        }

        fun processInformation(mediaPlayer: MediaPlayer)
        {
            var finalTime=mediaPlayer.duration
            var startTime=mediaPlayer.currentPosition
            seekbar?.max=finalTime
            startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(startTime?.toLong() as Long)-
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long)))
            )
            endTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime?.toLong() as Long)-
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long)))
            )
            seekbar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime,1000)
        }
        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                currentPosition = currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = randomPosition


            }
            if (currentPosition == fetchSongs?.size) {
                currentPosition = 0
            }
            currentSongHelper?.isLoop = false
            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songId = nextSong?.songID as Long

            updateTextView(currentSongHelper?.songTitle as String,currentSongHelper?.songArtist as String)
            mediaPlayer?.reset()
            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInformation(mediaPlayer as MediaPlayer)

            } catch (e: Exception) {

            }
            if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean){
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_on))
            }
            else
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_off))

        }

    }


    var updateSongTime=object:Runnable{
        override fun run() {

            var getCurrent=
                    mediaPlayer?.currentPosition
            startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                            TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long)-
                                    TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long))))
            Handler().postDelayed(this,1000)

        }
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater?.inflate(R.layout.fragment_song_playing, container, false)


        seekbar = view?.findViewById(R.id.seekBar)
        startTimeText = view?.findViewById(R.id.startTime)
        endTimeText = view?.findViewById(R.id.endTime)
        playpauseImageButton = view?.findViewById(R.id.playPauseButton)
        nextImageButton = view?.findViewById(R.id.nextButton)
        previousImageButton = view?.findViewById(R.id.previousButton)
        loopImageButton = view?.findViewById(R.id.loopButton)
        shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        songArtistView = view?.findViewById(R.id.songArtist)
        songTitleView = view?.findViewById(R.id.songTitle)
        glView=view?.findViewById(R.id.visualizer_view)
        fab=view?.findViewById(R.id.favoriteIcon)
        fab?.alpha=0.8f
        return view;
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioVisualization=glView as AudioVisualization
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        favoriteContent= MusicoDatabase(myActivity as Context)

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true;
        currentSongHelper?.isLoop = false;
        currentSongHelper?.isShuffle = false
        try {
            path = arguments.getString("path")
            _songTitle = arguments.getString("songTitle")
            _songArtist = arguments.getString("songArtist")
            songId = arguments.getInt("SongId").toLong()

            currentPosition = arguments.getInt("songPosition")
            fetchSongs = arguments.getParcelableArrayList("songData")


            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songId = songId

            currentSongHelper?.currentPostion = currentPosition

            updateTextView(currentSongHelper?.songTitle as String,currentSongHelper?.songArtist as String)
        } catch (e: Exception) {

        }
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer?.setDataSource(myActivity, Uri.parse(path))
            mediaPlayer?.prepare()
        } catch (e: Exception) {

        }
        mediaPlayer?.start()
        processInformation(mediaPlayer as MediaPlayer)
        if (currentSongHelper?.isPlaying as Boolean) {
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

        } else {
            playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)

        }
        mediaPlayer?.setOnCompletionListener {
        onSongComplete()
        }
        clickHandler()
        var visualizationHandler=DbmHandler.Factory.newVisualizerHandler(myActivity as Context,0)
        audioVisualization?.linkTo(visualizationHandler)

        var prefsForShuffle=myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isShuffleAllowed=prefsForShuffle?.getBoolean("feature",false)
        if(isShuffleAllowed as Boolean)
        {
            currentSongHelper?.isShuffle=true
            currentSongHelper?.isLoop=false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        else
        {
            currentSongHelper?.isShuffle=false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)


        }

        var prefsForLoop=myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)
        var isLoopAllowed=prefsForLoop?.getBoolean("feature",false)
        if(isLoopAllowed as Boolean)
        {
            currentSongHelper?.isShuffle=false
            currentSongHelper?.isLoop=true
            shuffleImageButton?.setBackgroundResource(R.drawable.loop_icon)
            loopImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        else
        {
            currentSongHelper?.isLoop=false
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)


        }
        if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean){
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_on))
        }
        else
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_off))

    }

    override fun onPause() {
        super.onPause()
        audioVisualization?.onPause()
    }

    override fun onResume() {
        super.onResume()
        audioVisualization?.onResume()
    }

    override fun onDestroyView() {
        audioVisualization?.release()
        super.onDestroyView()

    }
    fun clickHandler() {
        fab?.setOnClickListener({
            if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean){

                favoriteContent?.deleteFavorite(currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(myActivity,"Removed From Favorites",Toast.LENGTH_SHORT).show()
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_off))

            }
            else
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_on))
            favoriteContent?.storeAsFavorites(currentSongHelper?.songId?.toInt(),currentSongHelper?.songArtist,currentSongHelper?.songTitle,currentSongHelper?.songPath)
            Toast.makeText(myActivity,"Added to favorites",Toast.LENGTH_SHORT).show()

        })
        shuffleImageButton?.setOnClickListener({
            var editorShuffle=myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            var editorLoop=myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()

            if (currentSongHelper?.isShuffle as Boolean) {
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
            } else {
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature",true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }


        })
        nextImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true;
            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                playNext("PlayNextNormal")

            }
        })
        previousImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isLoop as Boolean) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
            }

            playPrevious()
        })
        loopImageButton?.setOnClickListener({
            var editorShuffle=myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            var editorLoop=myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()

            if (currentSongHelper?.isLoop as Boolean) {
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            } else {
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
            }

        })
        playpauseImageButton?.setOnClickListener({
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
                currentSongHelper?.isPlaying = false
                playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                mediaPlayer?.start()
                currentSongHelper?.isPlaying = true

                playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

            }


        })
    }


    fun playPrevious() {
        currentPosition = currentPosition - 1
        if (currentPosition == -1) {
            currentPosition = 0;
        }
        if (currentSongHelper?.isPlaying as Boolean) {
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

        } else {
            playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)

        }

        currentSongHelper?.isLoop = false

        var nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songArtist = nextSong?.artist
        currentSongHelper?.songId = nextSong?.songID as Long
        updateTextView(currentSongHelper?.songTitle as String,currentSongHelper?.songArtist as String)

        mediaPlayer?.reset()
        try {
            mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            processInformation(mediaPlayer as MediaPlayer)

        } catch (e: Exception) {

        }
        if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean){
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_on))
        }
        else
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity,R.drawable.favorite_off))

    }

}// Required empty public constructor
