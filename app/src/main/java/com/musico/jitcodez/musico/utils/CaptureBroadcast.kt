package com.musico.jitcodez.musico.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.musico.jitcodez.musico.Activities.MainActivity
import com.musico.jitcodez.musico.Fragments.SongPlayingFragment
import com.musico.jitcodez.musico.R
import kotlinx.android.synthetic.main.fragment_favorite.view.*

/**
 * Created by jitu on 7/2/18.
 */
class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try{
            MainActivity.statified.notificationManager?.cancel(1978)
            }catch(e:Exception)
            {

            }


            try {
                if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaPlayer?.pause()
                    SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {

            }
        } else {//Telephony
            val tm: TelephonyManager = context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tm?.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    try{
                        MainActivity.statified.notificationManager?.cancel(1978)
                    }catch(e:Exception)
                    {

                    }
                    try {
                        if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                            SongPlayingFragment.Statified.mediaPlayer?.pause()
                            SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    } catch (e: Exception) {

                    }
                }
                else -> {

                }
            }

        }

    }

}