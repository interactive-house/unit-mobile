package com.example.unitmobile

import android.os.Bundle
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope

class SongSaver : Saver<Song, Bundle> {
    override fun restore(value: Bundle): Song {
        return Song(
            value.getString("song").orEmpty(),
            value.getString("artist").orEmpty(),
            value.getString("trackID").orEmpty()
        )
    }

    override fun SaverScope.save(value: Song): Bundle {
        val bundle = Bundle()
        bundle.putString("song", value.song)
        bundle.putString("artist", value.artist)
        bundle.putString("trackID", value.trackID)
        return bundle
    }
}
