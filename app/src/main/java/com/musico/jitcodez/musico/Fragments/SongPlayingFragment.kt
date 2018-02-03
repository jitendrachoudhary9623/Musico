package com.musico.jitcodez.musico.Fragments

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.musico.jitcodez.musico.CurrentSongHelper
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

    var myActivity: Activity? = null
    var mediaPlayer: MediaPlayer? = null

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


    var currentSongHelper: CurrentSongHelper? = null

    var currentPosition: Int = 0
    var fetchSongs: ArrayList<Songs>? = null

    var updateSongTime=object:Runnable{
        override fun run() {

            var getCurrent=mediaPlayer?.currentPosition
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


        return view;
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
    }

    fun clickHandler() {
        shuffleImageButton?.setOnClickListener({
            if (currentSongHelper?.isShuffle as Boolean) {
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            } else {
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
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

            if (currentSongHelper?.isLoop as Boolean) {
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            } else {
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
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

        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            processInformation(mediaPlayer as MediaPlayer)

        } catch (e: Exception) {

        }
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
}// Required empty public constructor
