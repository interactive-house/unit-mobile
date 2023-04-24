package com.example.unitmobile

import android.app.Application
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

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val viewModelJob = Job()
    private val db = Firebase.database("https://smarthome-3bb7b-default-rtdb.firebaseio.com/")
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var currentTrack = MutableLiveData<MutableMap<String, String>>()
    var songs = MutableLiveData<List<Song>>()


     fun initSongs() {
        val songListRef = db.getReference("simulatedDevices").child("songList")
        songListRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val songsList = mutableListOf<Song>()
                snapshot.children.forEach { songSnapshot ->
                    val song = Song(
                        (songSnapshot.value as Map<*, *>)["song"].toString(),
                        (songSnapshot.value as Map<*, *>)["artist"].toString(),
                        (songSnapshot.value as Map<*, *>)["trackId"].toString()
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
    fun initCurrentTrack(){
        val songListRef = db.getReference("simulatedDevices").child("action")
        songListRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("SharedViewModel current ", "Snapshot: $snapshot")
                var TrackID = (snapshot.value as Map<*, *>)["trackId"].toString()
                var status = (snapshot.value as Map<*, *>)["type"].toString()
                currentTrack.postValue(mutableMapOf("trackId" to TrackID, "status" to status))


            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("SharedViewModel", "Error: ${error.message}")
            }
        })
    }



}