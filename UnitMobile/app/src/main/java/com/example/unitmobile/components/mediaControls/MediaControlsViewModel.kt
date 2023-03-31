package com.example.unitmobile.components.mediaControls

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MediaControlsViewModel(private val db : FirebaseDatabase) : ViewModel() {

    init {
        Log.d("MediaControlsViewModel", "MediaControlsViewModel created!")
    }

    val currentTrackId = mutableStateOf("")
    val deviceStatus = mutableStateOf("No device")
    val status = mutableStateOf("No status")
    val currentTrack = mutableStateOf("")
    val songList = mutableStateListOf<Map<*, *>>()

    private val simulatedDevicesRef = db.getReference("simulatedDevices")
    private val songListRef = db.getReference("simulatedDevices").child("songList")

    private val songListEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                songList.clear()
                snapshot.children.forEach {
                    val songMap = it.value as Map<*, *>?
                    if (songMap != null) {
                        songList.add(songMap)
                    }
                }
                currentTrackId.value = songList[0]["trackId"].toString()
                currentTrack.value = "${songList[0]["song"].toString()}: ${songList[0]["artist"].toString()}"

                Log.d("onDataChangeMedia", "Song list: ${songList.toList()}")
            } catch (e: Exception) {
                Log.d("onDataChangeMedia", "Error: ${e.message}")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("onCancelledMedia", "Failed to read value.", error.toException())
        }
    }

    private val simulatedDevicesEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                status.value = snapshot.child("action").child("type").getValue(String::class.java)!!
                deviceStatus.value = snapshot.child("deviceStatus").getValue(String::class.java)!!

                Log.d("onDataChangeMedia", "Status: ${status.value}")
                Log.d("onDataChangeMedia", "Device status: ${deviceStatus.value}")

            } catch (e: Exception) {
                Log.d("onDataChangeMedia", "ErrorSimulatedDevices: ${e.message}")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("onCancelledMedia", "Failed to read value.", error.toException())
        }
    }

    init {
        songListRef.addValueEventListener(songListEventListener)
        simulatedDevicesRef.addValueEventListener(simulatedDevicesEventListener)
    }

    override fun onCleared() {
        super.onCleared()
        songListRef.removeEventListener(songListEventListener)
        simulatedDevicesRef.removeEventListener(simulatedDevicesEventListener)
    }
}