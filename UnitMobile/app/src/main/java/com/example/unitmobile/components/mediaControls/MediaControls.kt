package com.example.unitmobile.components.mediaControls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import java.util.*

@Composable
fun MediaControls(db: FirebaseDatabase) {
    val mediaControlsViewModel = remember { MediaControlsViewModel(db) }
    val currentTrackId = mediaControlsViewModel.currentTrackId.value
    val deviceStatus = mediaControlsViewModel.deviceStatus.value
    val status = mediaControlsViewModel.status.value
    val currentTrack = mediaControlsViewModel.currentTrack.value
    val songList = mediaControlsViewModel.songList

    val currentTrackIdLocal = rememberSaveable { mutableStateOf(currentTrackId) }
    val currentTrackLocal = rememberSaveable { mutableStateOf(currentTrack) }

    val simulatedDevicesRef = db.getReference("simulatedDevices")
    val statusRef = simulatedDevicesRef.child("action")


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Current track:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = currentTrack,
            fontSize = 18.sp
        )
        Text(
            text = "Device status:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = deviceStatus,
            fontSize = 18.sp
        )
        Text(
            text = "Status:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = status,
            fontSize = 18.sp
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = {
                    val currentIndex = songList.indexOf(songList.find { it["trackId"] == currentTrackIdLocal.value })
                    val previousIndex = if (currentIndex == 0) songList.size - 1 else currentIndex - 1
                    currentTrackIdLocal.value = songList[previousIndex]["trackId"].toString()
                    currentTrackLocal.value = "${songList[previousIndex]["song"].toString()}: ${songList[previousIndex]["artist"].toString()}"
                    val data = mapOf(
                        "id" to UUID.randomUUID().toString(),
                        "type" to "play",
                        "trackId" to currentTrackIdLocal.value)
                    statusRef.setValue(data)
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Track")
            }
            IconButton(
                onClick = {
                    val newStatus = if (status == "play") "pause" else "play"
                    val data = mapOf(
                        "id" to UUID.randomUUID().toString(),
                        "type" to newStatus)
                    statusRef.setValue(data)
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                if (status == "play") {
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
                    val currentIndex = songList.indexOf(songList.find { it["trackId"] == currentTrackIdLocal.value})
                    val nextIndex = (currentIndex + 1) % songList.size
                    currentTrackIdLocal.value = songList[nextIndex]["trackId"].toString()
                    currentTrackLocal.value = "${songList[nextIndex]["song"].toString()}: ${songList[nextIndex]["artist"].toString()}"
                    val data = mapOf(
                        "id" to UUID.randomUUID().toString(),
                        "type" to "play",
                        "trackId" to currentTrackIdLocal.value)
                    statusRef.setValue(data)
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
                            currentTrackIdLocal.value = songList[index]["trackId"].toString()
                            currentTrackLocal.value = "${songList[index]["song"].toString()}: ${songList[index]["artist"].toString()}"
                            val data = mapOf(
                                "id" to UUID.randomUUID().toString(),
                                "type" to "play",
                                "trackId" to currentTrackIdLocal.value)
                            statusRef.setValue(data)
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