package com.example.unitmobile

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import java.io.ByteArrayOutputStream

class SongSaver : Saver<Song, Bundle> {
    override fun restore(value: Bundle): Song {

        return Song(
            value.getString("song").orEmpty(),
            value.getString("artist").orEmpty(),
            value.getString("trackID").orEmpty(),
            value.getInt("albumIMG", 0)

        )
    }

    override fun SaverScope.save(value: Song): Bundle {
        val bundle = Bundle()
        bundle.putString("song", value.song)
        bundle.putString("artist", value.artist)
        bundle.putString("trackID", value.trackID)
        bundle.putInt("albumIMG", value.albumIMG)
        return bundle
    }
}
