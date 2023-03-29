package com.example.unitmobile.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

@Composable
fun MediaControls(db: FirebaseDatabase) {
    val currentTrackId = rememberSaveable { mutableStateOf("") }
    val deviceStatus = rememberSaveable { mutableStateOf("No device") }
    val status = rememberSaveable { mutableStateOf("No status") }
    val currentTrack = rememberSaveable { mutableStateOf("") }

    val statusRef = db.getReference("simulatedDevices").child("action")
    val simulatedDevicesRef = db.getReference("simulatedDevices")

    val songList = remember { mutableStateListOf<Map<*, *>>() }

    val songListRef = db.getReference("simulatedDevices").child("songList")

    DisposableEffect(songListRef) {
        val valueEventListener = object : ValueEventListener {
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
        songListRef.addValueEventListener(valueEventListener)
        onDispose {
            songListRef.removeEventListener(valueEventListener)
        }

    }

    DisposableEffect(simulatedDevicesRef) {
        val listener = object : ValueEventListener {
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
        simulatedDevicesRef.addValueEventListener(listener)

        onDispose {
            simulatedDevicesRef.removeEventListener(listener)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Current track:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = currentTrack.value,
            fontSize = 18.sp
        )
        Text(
            text = "Device status:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = deviceStatus.value,
            fontSize = 18.sp
        )
        Text(
            text = "Status:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = status.value,
            fontSize = 18.sp
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = {
                    val currentIndex = songList.indexOf(songList.find { it["trackId"] == currentTrackId.value})
                    val previousIndex = if (currentIndex == 0) songList.size - 1 else currentIndex - 1
                    currentTrackId.value = songList[previousIndex]["trackId"].toString()
                    currentTrack.value = "${songList[previousIndex]["song"].toString()}: ${songList[previousIndex]["artist"].toString()}"
                    val data = mapOf(
                        "id" to UUID.randomUUID().toString(),
                        "type" to "play",
                        "trackId" to currentTrackId.value)
                    simulatedDevicesRef.child("action").setValue(data)
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Track")
            }
            IconButton(
                onClick = {
                    val newStatus = if (status.value == "play") "pause" else "play"
                    val data = mapOf(
                        "id" to UUID.randomUUID().toString(),
                        "type" to newStatus)
                    statusRef.setValue(data)
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                if (status.value == "play") {
                    Icon(Icons.Default.Pause, contentDescription = "Pause")
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                }
            }
            IconButton(
                onClick = {
                    val data = mapOf(
                        "id" to UUID.randomUUID().toString(),
                        "type" to "stop")
                    statusRef.setValue(data)
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Stop")
            }
            IconButton(
                onClick = {
                    val currentIndex = songList.indexOf(songList.find { it["trackId"] == currentTrackId.value})
                    val nextIndex = (currentIndex + 1) % songList.size
                    currentTrackId.value = songList[nextIndex]["trackId"].toString()
                    currentTrack.value = "${songList[nextIndex]["song"].toString()}: ${songList[nextIndex]["artist"].toString()}"
                    val data = mapOf(
                        "id" to UUID.randomUUID().toString(),
                        "type" to "play",
                        "trackId" to currentTrackId.value)
                    simulatedDevicesRef.child("action").setValue(data)
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next Track")
            }
        }
        Text(text = "Song list: ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        LazyColumn(){
            items(songList.size) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            currentTrackId.value = songList[index]["trackId"].toString()
                            currentTrack.value = "${songList[index]["song"].toString()}: ${songList[index]["artist"].toString()}"
                            val data = mapOf(
                                "id" to UUID.randomUUID().toString(),
                                "type" to "play",
                                "trackId" to currentTrackId.value)
                            simulatedDevicesRef.child("action").setValue(data)
                        }) {
                       Column(
                           modifier = Modifier.padding(8.dp)
                       ) {
                           Text(text = "${songList[index]["song"].toString()}: ${songList[index]["artist"].toString()}")
                       }

                }
            }
        }
    }
}