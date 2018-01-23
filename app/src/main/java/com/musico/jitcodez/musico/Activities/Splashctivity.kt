package com.musico.jitcodez.musico.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.musico.jitcodez.musico.R

class Splashctivity : AppCompatActivity() {

    //Permissions
    var permissionSettings = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO);


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashctivity)

        if (!checkPermissions1(this@Splashctivity, *permissionSettings)) {
            ActivityCompat.requestPermissions(this, permissionSettings, 131)
        } else {
            Handler().postDelayed({

                var startApp = Intent(this@Splashctivity, MainActivity::class.java);
                startActivity(startApp);
                this.finish();

            }, 1000)

        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            131 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        ) {
                    Handler().postDelayed({

                        var startApp = Intent(this@Splashctivity, MainActivity::class.java);
                        startActivity(startApp);
                        this.finish();

                    }, 1000)
                } else {
                    Toast.makeText(this@Splashctivity, "Grant Permissions", Toast.LENGTH_LONG).show();
                    this.finish()
                }
                return;
            }
            else -> {
                Toast.makeText(this@Splashctivity, "SOmethng Went wrong", Toast.LENGTH_LONG).show();
                this.finish();
                return;
            }
        }


    }

    fun checkPermissions1(context: Context, vararg Permission:String):Boolean{
        var hasAllPermissions=true;
        for(permission in Permission)
        {
            val res=context.checkCallingOrSelfPermission(permission);
            if(res!=PackageManager.PERMISSION_GRANTED)
            {
                hasAllPermissions=false;
            }
        }
        return hasAllPermissions;
    }
}