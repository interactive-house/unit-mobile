package com.example.unitmobile.components

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.compose.*
import com.example.unitmobile.R
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
    Log.i("MediaControls RECOMPOSE", "MediaControls")


    val viewModel: SharedViewModel = ViewModelProvider(
        LocalContext.current as androidx.activity.ComponentActivity
    )[SharedViewModel::class.java]
    var songList: List<Song> = remember { mutableStateListOf<Song>() }

    val context = LocalContext.current
    val deviceStatus = rememberSaveable { mutableStateOf("No device") }
    val status = rememberSaveable { mutableStateOf("No status") }
    val currentTrack = rememberSaveable(
        stateSaver = SongSaver()
    ) { mutableStateOf(Song("", "", "", 0)) }

    val statusRef = db.getReference("simulatedDevices").child("action")
    val simulatedDevicesRef = db.getReference("simulatedDevices")
    viewModel.initSongs()




    viewModel.songs.observe(LocalContext.current as androidx.activity.ComponentActivity) { songs ->
        Log.d("MediaControls", "Songs: $songs")
        songList = songs
        viewModel.initCurrentTrack()

    }
    viewModel.currentTrack.observe(LocalContext.current as androidx.activity.ComponentActivity) { track ->
        Log.d("MediaControls current track123", "Current track: $track")
        songList.find { it.trackID == track["trackId"] }?.let { currentTrack.value = it }


    }





    fun nextSong() {
        val currentIndex =
            songList.indexOf(songList.find { it.trackID == currentTrack.value.trackID })
        val nextIndex = (currentIndex + 1) % songList.size
        Log.i("MediaControls", "Playing next song: $currentIndex => $nextIndex")
        currentTrack.value = songList[nextIndex]

        val data = mapOf(
            "id" to UUID.randomUUID().toString(),
            "type" to "play",
            "trackId" to currentTrack.value.trackID
        )
        simulatedDevicesRef.child("action").setValue(data)

    }

    fun previousSong() {
        val currentIndex =
            songList.indexOf(songList.find { it.trackID == currentTrack.value.trackID })
        val previousIndex = if (currentIndex == 0) songList.size - 1 else currentIndex - 1
        Log.i("MediaControls", "Playing previous song: $currentIndex => $previousIndex")

        currentTrack.value = songList[previousIndex]

        val data = mapOf(
            "id" to UUID.randomUUID().toString(),
            "type" to "play",
            "trackId" to currentTrack.value.trackID
        )
        simulatedDevicesRef.child("action").setValue(data)

    }

    fun handleAction(action: String) {
        val data: Map<String, Any>

        if (action == "stop") {

            currentTrack.value = Song("", "", "", 0)
            data = mapOf(
                "id" to UUID.randomUUID().toString(),
                "type" to action
            )
        } else {
            if (currentTrack.value.trackID == "") {
                return
            }
            data = mapOf(
                "id" to UUID.randomUUID().toString(),
                "type" to action,
                "trackId" to currentTrack.value.trackID
            )

        }



        simulatedDevicesRef.child("action").setValue(data)


    }

    fun playSong(song: Song) {
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
        val phrase = phrase.lowercase()

        if (phrase != "") {
            viewModel.ttsPhrase.value = ""

        }
        if (phrase.contains("next")) {
            nextSong()
        } else if (phrase.contains("previous")) {
            previousSong()
        } else if (phrase.contains("play")) {
            if (phrase.length == 4) {
                handleAction("play")

            } else {
                val song =
                    songList.find { it.song.lowercase().contains(phrase.split("play")[1].trim()) }
                if (song != null) {
                    playSong(song)
                } else {
                    Toast.makeText(context, "Song not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (phrase.contains("pause")) {
            handleAction("pause")


        } else if (phrase.contains("stop")) {
            handleAction("stop")
        }


    }



    DisposableEffect(simulatedDevicesRef) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    status.value =
                        snapshot.child("action").child("type").getValue(String::class.java)!!
                    deviceStatus.value =
                        snapshot.child("deviceStatus").getValue(String::class.java)!!

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

    Scaffold(
        topBar = { /* Your app bar goes here */ },
        bottomBar = {
            Card() {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current track:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentTrack.value.song,
                        fontSize = 18.sp
                    )
//                Image(
//                    painter = painterResource(id = R.drawable.chumbawumba),
//                    contentDescription = "Song image",
//                    modifier = Modifier.size(100.dp)
//                )
                    SpinningImage(status.value == "play", currentTrack.value)
//                if(currentTrack.value != null && status.value == "play") {
//                    MusicAnimation()
//                }
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
                                handleAction(newStatus)
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
                                handleAction("stop")

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



                }
            }
        }
<<<<<<< Updated upstream

        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Song list: ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
=======
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(end = 2.dp) // add padding to the right side
                        .wrapContentWidth(align = Alignment.Start)


>>>>>>> Stashed changes
                ) {
                    Text(
                        text = "Device status:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = deviceStatus.value,
                        fontSize = 18.sp
                    )
                }
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterVertically)
                        .padding(start = 2.dp), // add padding to the left side

                    horizontalAlignment = Alignment.End,

                    ) {
                    Text(
                        text = "Status:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = status.value,
                        fontSize = 18.sp
                    )
                }

            }


//        Image(painterResource(id = R.drawable.chumbawumba), contentDescription = "Song image")
            Text(text = "Song list: ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            LazyColumn(
                modifier = Modifier.padding(16.dp),
            ) {
                items(songList.size) { index ->
                    Divider(startIndent = 0.dp, thickness = 1.dp, color = Color.Gray)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {


                                playSong(songList[index])
                            }) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .wrapContentWidth()
                        ) {


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .align(Alignment.CenterVertically)
                                        .padding(end = 8.dp) // add padding to the right side
                                        .wrapContentWidth(align = Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = "${songList[index].song} \n",
                                        modifier = Modifier.align(Alignment.Start)
                                    )
                                    Text(text = "${songList[index].artist}")
                                }
                                Column(

                                    modifier = Modifier
                                        .wrapContentSize()
                                        .align(Alignment.CenterVertically)
                                        .padding(start = 8.dp), // add padding to the left side

                                    horizontalAlignment = Alignment.End,

                                    ) {
                                    if (currentTrack.value.song == songList[index].song) {

                                        MusicAnimation(status.value == "play")

                                    }
                                }
                            }


                        }

                    }
                    if (index == songList.size - 1) {
                        Divider(startIndent = 0.dp, thickness = 1.dp, color = Color.Gray)
                    }

                }
            }


        }


    }
}


@Composable
fun MusicAnimation(play: Boolean) {

// for speed
    var speed by remember {
        mutableStateOf(1f)
    }

    val composition by rememberLottieComposition(

        LottieCompositionSpec
            // here `code` is the file name of lottie file
            // use it accordingly
            .RawRes(R.raw.music)
    )

    // to control the animation
    val progress by animateLottieCompositionAsState(
        // pass the composition created above
        composition,

        // Iterates Forever
        iterations = LottieConstants.IterateForever,

        // pass isPlaying we created above,
        // changing isPlaying will recompose
        // Lottie and pause/play
        isPlaying = play,

        // pass speed we created above,
        // changing speed will increase Lottie
        speed = speed,

        // this makes animation to restart
        // when paused and play
        // pass false to continue the animation
        // at which it was paused
        restartOnPlay = false

    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // LottieAnimation
        // Pass the composition
        // and the progress state
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(30.dp)
        )


    }
}


@Composable
fun SpinningImage(spin: Boolean, currentTrack: Song) {

    var shouldSpin by remember { mutableStateOf(spin) }
    var albumImage by remember { mutableStateOf(currentTrack.albumIMG) }


    var rotationState by remember { mutableStateOf(0f) }
    val rotationAnim = remember {
        Animatable(0f)
    }
    LaunchedEffect(spin) {
        shouldSpin = spin
        rotationAnim.animateTo(
            targetValue = rotationAnim.value + 360f, // modify targetValue to be a multiple of 360
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }
    rotationState = rotationAnim.value
    albumImage = currentTrack.albumIMG

    if (albumImage != 0) {
        Image(
            painter = painterResource(id = albumImage),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
                .graphicsLayer {
                    if (shouldSpin) {
                        rotationZ = rotationState
                    }
                }
        )
    }
}

