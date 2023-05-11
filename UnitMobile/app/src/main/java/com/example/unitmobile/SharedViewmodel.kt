package com.example.unitmobile

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.tasks.await
import kotlin.math.log

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val viewModelJob = Job()
    private val db = Firebase.database("https://smarthome-3bb7b-default-rtdb.firebaseio.com/")
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var currentStatus = MutableLiveData<String>()
    var currentTrack = MutableLiveData<Song>()
    var songs = MutableLiveData<List<Song>>()
    var ttsPhrase = MutableLiveData<String>()


    fun initSongs() {
        val songListRef = db.getReference("simulatedDevices").child("songList")
        songListRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val songsList = mutableListOf<Song>()
                snapshot.children.forEach { songSnapshot ->

                    var albumDrawable =
                        (songSnapshot.value as Map<*, *>)["song"].toString().trim().lowercase()
                            .replace(" ", "_").replace("[", "").replace("]", "")
                    Log.i("SharedViewModel album", "AlbumDrawable: $albumDrawable")
                    var resID = getApplication<Application>().resources.getIdentifier(
                        albumDrawable, "drawable", getApplication<Application>().packageName
                    )
                    Log.i("SharedViewModel album res", "ResID: $resID")
                    val song = Song(
                        (songSnapshot.value as Map<*, *>)["song"].toString().replace("[", "")
                            .replace("]", ""),
                        (songSnapshot.value as Map<*, *>)["artist"].toString().replace("[", "")
                            .replace("]", ""),
                        (songSnapshot.value as Map<*, *>)["trackId"].toString().replace("[", "")
                            .replace("]", ""),
                        resID
                    )
                    if (song != null) {
                        Log.i("SharedViewModel", "Song: $song")
                        songsList.add(song)
                    }
                }
                songs.postValue(songsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("SharedViewModel", "Error: ${error.message}")
            }
        })


    }

    fun initCurrentTrack() {
        val songListRef = db.getReference("simulatedDevices")
            .child("playerState")
        songListRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var track = snapshot.child("currentTrack").value

                Log.i("SharedViewModel", "Track: $track")

                var albumDrawable =
                    (track as Map<*, *>)["track"].toString().trim().lowercase().replace(" ", "_")
                        .replace("[", "").replace("]", "")
                Log.i("SharedViewModel album track", "AlbumDrawable: $albumDrawable")
                var resID = getApplication<Application>().resources.getIdentifier(
                    albumDrawable, "drawable", getApplication<Application>().packageName
                )
                Log.i("SharedViewModel album res track", "ResID: $resID")
                val song = Song(
                    track["track"].toString(),
                    track["artist"].toString(),
                    track["trackId"].toString(),
                    resID
                )
                var status = snapshot.child("state").value.toString()
                Log.i("SharedViewModel", "Statusweqqqqqqqqqqqqqq: $status")

                currentTrack.postValue(song)
                currentStatus.value = status

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("SharedViewModel", "Error: ${error.message}")
            }
        })
    }

    fun initStatus() {
        val songListRef = db.getReference("simulatedDevices")
            .child("playerState").child("state")
        songListRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                var status = snapshot.value.toString()
                Log.i("SharedViewModel", "Statusweqqqqqqqqqqqqqq: $status")


                currentStatus.value = status

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("SharedViewModel", "Error: ${error.message}")
            }
        })

    }


}