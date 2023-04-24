package com.example.unitmobile.components

import android.app.Application
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unitmobile.SharedViewModel
import com.example.unitmobile.Song
import com.example.unitmobile.SongSaver
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

@Composable
fun MediaControls(db: FirebaseDatabase) {


    val viewModel: SharedViewModel = ViewModelProvider(
        LocalContext.current as androidx.activity.ComponentActivity
    )[SharedViewModel::class.java]
    var songList: List<Song> = remember { mutableStateListOf<Song>() }

    val context = LocalContext.current
    val deviceStatus = rememberSaveable { mutableStateOf("No device") }
    val status = rememberSaveable { mutableStateOf("No status") }
    val currentTrack = rememberSaveable(
        stateSaver = SongSaver()
    ) { mutableStateOf(Song("", "", "")) }

    val statusRef = db.getReference("simulatedDevices").child("action")
    val simulatedDevicesRef = db.getReference("simulatedDevices")



    viewModel.songs.observe(LocalContext.current as androidx.activity.ComponentActivity) { songs ->
        Log.d("MediaControls", "Songs: $songs")
        songList = songs
        viewModel.initCurrentTrack()

    }
    viewModel.currentTrack.observe(LocalContext.current as androidx.activity.ComponentActivity) { track ->
        Log.d("MediaControls current track123", "Current track: $track")
        songList.find { it.trackID ==  track["trackId"] }?.let {  currentTrack.value = it }


    }



    viewModel.initSongs()

    fun nextSong(){
        val currentIndex = songList.indexOf(songList.find { it.trackID == currentTrack.value.trackID})
        val nextIndex = (currentIndex + 1) % songList.size
        currentTrack.value = songList[nextIndex]

        val data = mapOf(
            "id" to UUID.randomUUID().toString(),
            "type" to "play",
            "trackId" to currentTrack.value.trackID)
        simulatedDevicesRef.child("action").setValue(data)

    }
    fun previousSong(){
        val currentIndex = songList.indexOf(songList.find { it.trackID == currentTrack.value.trackID })
        val previousIndex = if (currentIndex == 0) songList.size - 1 else currentIndex - 1

        currentTrack.value = songList[previousIndex]

        val data = mapOf(
            "id" to UUID.randomUUID().toString(),
            "type" to "play",
            "trackId" to currentTrack.value.trackID)
        simulatedDevicesRef.child("action").setValue(data)

    }
    fun handleAction(action: String){

            val data = mapOf(
                "id" to UUID.randomUUID().toString(),
                "type" to action,
                "trackId" to currentTrack.value.trackID)
            simulatedDevicesRef.child("action").setValue(data)


    }

    fun playSong(song: Song){
        currentTrack.value = song
        val data = mapOf(
            "id" to UUID
                .randomUUID()
                .toString(),
            "type" to "play",
            "trackId" to currentTrack.value.trackID
        )
        simulatedDevicesRef
            .child("action")
            .setValue(data)

    }
    viewModel.ttsPhrase.observe(LocalContext.current as androidx.activity.ComponentActivity) { phrase ->
        Log.d("MediaControls", "TTS phrase: $phrase")
        if(phrase.contains("next")){
            nextSong()
        }else if(phrase.contains("previous")){
            previousSong()
        }else if(phrase.contains("play") ){
            if(phrase.length == 4){
                handleAction("play")

            }else {
                val song =
                    songList.find { it.song.lowercase().contains(phrase.split("play")[1].trim()) }
                if (song != null) {
                    playSong(song)
                } else {
                    Toast.makeText(context, "Song not found", Toast.LENGTH_SHORT).show()
                }
            }
        }else if(phrase.contains("pause")  ){
            handleAction("pause")


        }else if( phrase.contains("stop")){
            handleAction("stop")
        }

        if(phrase != ""){
            viewModel.ttsPhrase.postValue("")

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
        Text(text = currentTrack.value.song,
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
                  previousSong()
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
                    nextSong()
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


                            playSong(songList[index])
                        }) {
                       Column(
                           modifier = Modifier.padding(8.dp)
                       ) {
                        Text(text = "${songList[index].song} - ${songList[index].artist}")

                       }

                }
            }
        }
    }
}