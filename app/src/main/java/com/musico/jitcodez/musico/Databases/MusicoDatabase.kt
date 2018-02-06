package com.musico.jitcodez.musico.Databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by jitu on 6/2/18.
 */
class MusicoDatabase: SQLiteOpenHelper {

    val DB_NAME="FavoriteDatabase"
    val TABLE_NAME="FavoriteTable"
    val COLUMN_Id="SongID"
    val COLUMN_SONG_TITLE="SongTitle"
    val COLUMN_SONG_ARTIST="SongArtist"
    val COLUMN_SONG_PATH="SongPath"
    override fun onCreate(db: SQLiteDatabase?) {

    db?.execSQL("CREATE TABLE"+TABLE_NAME+"( "+COLUMN_Id+" INTEGER, "+COLUMN_SONG_ARTIST+" STRING,"+COLUMN_SONG_TITLE+" STRING,"+COLUMN_SONG_PATH+" STRING);")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)

    fun storeAsFavorites(id:Int?,artist:String?,songTitle:String?,path:String?)
    {
        val db=this.writableDatabase
        var contentValues=ContentValues()
        contentValues.put(COLUMN_Id,id)
        contentValues.put(COLUMN_SONG_ARTIST,artist)
        contentValues.put(COLUMN_SONG_TITLE,songTitle)
        contentValues.put(COLUMN_SONG_PATH,path)

        db.insert(TABLE_NAME,null,contentValues)
        db.close()
    }

}