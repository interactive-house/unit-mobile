package com.example.unitmobile.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun MediaControls(db: FirebaseDatabase) {
    val currentTrack = rememberSaveable { mutableStateOf("No track") }
    val deviceStatus = rememberSaveable { mutableStateOf("No device") }
    val status = rememberSaveable { mutableStateOf("No status") }
    val songList = remember { mutableStateListOf<String>() }

    val songListRef = db.getReference("simulatedDevices").child("songList")
    val statusRef = db.getReference("simulatedDevices").child("status")
    val simulatedDevicesRef = db.getReference("simulatedDevices")

    simulatedDevicesRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            currentTrack.value = snapshot.child("currentTrack").getValue(String::class.java)!!
            deviceStatus.value = snapshot.child("deviceStatus").getValue(String::class.java)!!
            status.value = snapshot.child("status").getValue(String::class.java)!!
            Log.d("onDataChangeMedia", "Current track: ${currentTrack.value}")
            Log.d("onDataChangeMedia", "Device status: ${deviceStatus.value}")
            Log.d("onDataChangeMedia", "Status: ${status.value}")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("onCancelledMedia", "Failed to read value.", error.toException())
        }
    })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Media Controller",
            fontSize = 24.sp,
            style = TextStyle(textDecoration = TextDecoration.Underline)

        )
        Text(
            text = "Current track: ${currentTrack.value}",
            fontSize = 18.sp
        )
        Text(
            text = "Device status: ${deviceStatus.value}",
            fontSize = 18.sp
        )
        Text(
            text = "Status: ${status.value}",
            fontSize = 18.sp
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Button(
                onClick = {
                    statusRef.setValue("play")
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(text = "Play")
            }
            Button(
                onClick = {
                    statusRef.setValue("pause")
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(text = "Pause")
            }
            Button(
                onClick = {
                    statusRef.setValue("stop")
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(text = "Stop")
            }
        }
        Text(text = "Song list: ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        songList.forEach {
            Text(text = it)
        }
    }
}