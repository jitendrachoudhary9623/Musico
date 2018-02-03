package com.musico.jitcodez.musico.Fragments

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.musico.jitcodez.musico.R


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SongPlayingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SongPlayingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SongPlayingFragment : Fragment() {

    var myActivity:Activity?=null
    var mediaPlayer:MediaPlayer?=null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_song_playing, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity=context as Activity
    }
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity=activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var path:String?=null
        var _songTitle:String?=null
        var _songArtist:String?=null
        var songId:Long=0

        try{
            path=arguments.getString("path")
            _songTitle=arguments.getString("songTitle")
            _songArtist=arguments.getString("songArtist")
            songId=arguments.getInt("SongId").toLong()

        }catch(e:Exception)
        {

        }
        mediaPlayer=MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try{
            mediaPlayer?.setDataSource(myActivity, Uri.parse(path))
            mediaPlayer?.prepare()
        }catch(e:Exception)
        {

        }
        mediaPlayer?.start()
    }
}// Required empty public constructor
