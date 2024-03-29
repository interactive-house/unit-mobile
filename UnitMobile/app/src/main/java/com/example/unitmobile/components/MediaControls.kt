package com.example.unitmobile.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.compose.*
import com.example.unitmobile.R
import com.example.unitmobile.SharedViewModel
import com.example.unitmobile.Song
import com.example.unitmobile.SongSaver
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun MediaControls(db: FirebaseDatabase) {
    Log.i("MediaControls RECOMPOSE", "MediaControls")


    val viewModel: SharedViewModel = ViewModelProvider(
        LocalContext.current as androidx.activity.ComponentActivity
    )[SharedViewModel::class.java]
    var songList: List<Song> = remember { mutableStateListOf<Song>() }
    var sheetOpen by remember { mutableStateOf(false) }

    val isLoading = remember { mutableStateOf(true) }

    val context = LocalContext.current
    val deviceStatus = rememberSaveable { mutableStateOf("No device") }
    val status = rememberSaveable { mutableStateOf("No status") }
    val currentTrack = rememberSaveable(
        stateSaver = SongSaver()
    ) { mutableStateOf(Song("", "", "", 0)) }
    val currentIndex = remember { mutableStateOf(0) }

    val simulatedDevicesRef = db.getReference("simulatedDevices")
    viewModel.initSongs()




    viewModel.songs.observe(LocalContext.current as androidx.activity.ComponentActivity) { songs ->
        Log.d("MediaControls", "Songs: $songs")
        songList = songs
        viewModel.initCurrentTrack()

    }
    viewModel.currentTrack.observe(LocalContext.current as androidx.activity.ComponentActivity) { track ->
        Log.d("MediaControls current track", "Current track: $track")
        if (track.trackID == "") {
            currentTrack.value = Song("", "", "", 0)
            currentIndex.value = -1
        } else {
            songList.find { it.trackID == track.trackID }?.let { currentTrack.value = it }
            Log.i(
                "MediaControls current track",
                "Current index: ${songList.indexOf(songList.find { it.trackID == currentTrack.value.trackID })}"
            )
            currentIndex.value =
                songList.indexOf(songList.find { it.trackID == currentTrack.value.trackID })
        }

        isLoading.value = false

    }
    viewModel.currentStatus.observe(LocalContext.current as androidx.activity.ComponentActivity) { it ->
        Log.d("MediaControls", "Status: $status")
        status.value = it.toString()

    }





    fun nextSong() {
        val currentIndex =
            songList.indexOf(songList.find { it.trackID == currentTrack.value.trackID })
        val nextIndex = (currentIndex + 1) % songList.size
        Log.i("MediaControls", "Playing next song: $currentIndex => $nextIndex")

        val data = mapOf(
            "id" to UUID.randomUUID().toString(),
            "type" to "next"
        )
        simulatedDevicesRef.child("action").setValue(data)

    }

    fun previousSong() {
        val currentIndex =
            songList.indexOf(songList.find { it.trackID == currentTrack.value.trackID })
        val previousIndex = if (currentIndex == 0) songList.size - 1 else currentIndex - 1
        Log.i("MediaControls", "Playing previous song: $currentIndex => $previousIndex")

        val data = mapOf(
            "id" to UUID.randomUUID().toString(),
            "type" to "prev"
        )
        simulatedDevicesRef.child("action").setValue(data)

    }

    fun handleAction(action: String) {
        val data: Map<String, Any>
        Log.i("MediaControls", "Action: $action")

        data = mapOf(
            "id" to UUID.randomUUID().toString(),
            "type" to action
        )

        simulatedDevicesRef.child("action").setValue(data)

    }

    fun playSong(song: Song) {
        val data = mapOf(
            "id" to UUID
                .randomUUID()
                .toString(),
            "type" to "play",
            "trackId" to song.trackID
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


                    deviceStatus.value =
                        snapshot.child("deviceStatus").getValue(String::class.java)!!.lowercase()

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

    Crossfade(targetState = isLoading.value) {
        when(it) {
            true -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen()
                }
            }

            false -> {
                Box(Modifier.fillMaxSize()) {
                    Box(
                        Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxHeight()
                    ) {
                        Scaffold(


                            bottomBar = {
                                AnimatedVisibility(visible = !sheetOpen && deviceStatus.value == "online") {
                                    BottomTrackController(
                                        currentTrack = currentTrack.value,
                                        status = status.value,
                                        handleAction = { handleAction(it) },
                                        previousSong = { previousSong() },
                                        nextSong = { nextSong()},
                                        openSheet = {
                                            Log.i("sheet", "open sheet")

                                            sheetOpen = true},
                                    )

                                }

                            }

                        ) {


                            Crossfade(targetState = sheetOpen, modifier = Modifier.padding(it)) {
                                when (it) {
                                    true -> {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                                        ) {


                                            Card(

                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight()

                                            ) {


                                                Column(
                                                    modifier = Modifier.padding(8.dp)
                                                ) {
                                                    IconButton(
                                                        onClick = { sheetOpen = false },
                                                        modifier = Modifier.align(Alignment.End)

                                                    )
                                                    {
                                                        Icon(
                                                            imageVector = Icons.Filled.ExpandMore,

                                                            contentDescription = "Stop song",

                                                            )
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .wrapContentSize(Alignment.Center)
                                                            .background(
                                                                color = MaterialTheme.colors.primary,
                                                                shape = RoundedCornerShape(16.dp)
                                                            )
                                                    ) {
                                                        Column(

                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            modifier = Modifier

                                                        ) {
                                                            Spacer(modifier = Modifier.height(64.dp))

                                                            Text(
                                                                text = "Current track:",
                                                                fontSize = 18.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )

                                                            Text(
                                                                text = currentTrack.value.song,
                                                                fontSize = 18.sp
                                                            )
                                                            Spacer(modifier = Modifier.height(32.dp))
                                                            Text(
                                                                text = "Artist: ",
                                                                fontSize = 18.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                            Text(
                                                                text = currentTrack.value.artist,
                                                                fontSize = 18.sp
                                                            )
                                                            Spacer(modifier = Modifier.height(48.dp))

//                Image(
//                    painter = painterResource(id = R.drawable.chumbawumba),
//                    contentDescription = "Song image",
//                    modifier = Modifier.size(100.dp)
//                )
                                                            SpinningImage(
                                                                status.value == "playing",
                                                                currentTrack.value,
                                                                size = 150.dp
                                                            )
                                                            Spacer(modifier = Modifier.height(32.dp))
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
                                                                    Icon(
                                                                        Icons.Default.SkipPrevious,
                                                                        contentDescription = "Previous Track"
                                                                    )
                                                                }
                                                                IconButton(
                                                                    onClick = {
                                                                        val newStatus =
                                                                            if (status.value == "playing") "pause" else "play"
                                                                        handleAction(newStatus)
                                                                    },
                                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                                ) {
                                                                    if (status.value == "playing") {
                                                                        Icon(
                                                                            Icons.Default.Pause,
                                                                            contentDescription = "Pause"
                                                                        )
                                                                    } else {
                                                                        Icon(
                                                                            Icons.Default.PlayArrow,
                                                                            contentDescription = "Play"
                                                                        )
                                                                    }
                                                                }
                                                                IconButton(
                                                                    onClick = {
                                                                        handleAction("stop")

                                                                    },
                                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                                ) {
                                                                    Icon(
                                                                        Icons.Default.Stop,
                                                                        contentDescription = "Stop"
                                                                    )
                                                                }
                                                                IconButton(
                                                                    onClick = {
                                                                        nextSong()
                                                                    },
                                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                                ) {
                                                                    Icon(
                                                                        Icons.Default.SkipNext,
                                                                        contentDescription = "Next Track"
                                                                    )
                                                                }
                                                            }
                                                            Spacer(modifier = Modifier.height(16.dp))

                                                        }


                                                    }


                                                }
                                            }
                                        }

                                    }

                                    false -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        ) {
                                            Column(

                                                horizontalAlignment = Alignment.CenterHorizontally


                                            ) {
                                                Text(
                                                    text = "Device status:",
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                )
                                                Text(
                                                    text = deviceStatus.value,
                                                    fontSize = 18.sp,
                                                    textAlign = TextAlign.Center,
                                                )
                                            }






                                            Spacer(modifier = Modifier.height(8.dp))
                                            Card(modifier = Modifier.fillMaxSize()) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "Song list: ",
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    LazyColumn(
                                                        modifier = Modifier.padding(16.dp),
                                                    ) {
                                                        items(songList.size) { index ->
                                                            Divider(
                                                                startIndent = 0.dp,
                                                                thickness = 1.dp,
                                                                color = Color.Gray
                                                            )
                                                            Card(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(8.dp)
                                                                    .clickable(enabled = deviceStatus.value == "online") {


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


                                                                        if (songList[index].albumIMG != 0) {
                                                                            Image(
                                                                                painter = painterResource(id = songList[index].albumIMG),
                                                                                contentDescription = "Album Image",
                                                                                contentScale = ContentScale.Crop,
                                                                                modifier = Modifier
                                                                                    .size(40.dp)
                                                                                    .align(Alignment.CenterVertically)
                                                                            )
                                                                        }
                                                                        Column(
                                                                            modifier = Modifier
                                                                                .weight(1f)
                                                                                .fillMaxHeight()
                                                                                .align(Alignment.CenterVertically)
                                                                                .padding(end = 8.dp) // add padding to the right side
                                                                                .wrapContentWidth(
                                                                                    align = Alignment.CenterHorizontally
                                                                                )
                                                                        ) {
                                                                            Text(
                                                                                text = "${songList[index].song} \n",
                                                                                modifier = Modifier.align(
                                                                                    Alignment.Start
                                                                                )
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
                                                                            if (currentTrack.value.song == songList[index].song && deviceStatus.value == "online") {

                                                                                MusicAnimation(status.value == "playing")

                                                                            }
                                                                        }
                                                                    }


                                                                }

                                                            }
                                                            if (index == songList.size - 1) {
                                                                Divider(
                                                                    startIndent = 0.dp,
                                                                    thickness = 1.dp,
                                                                    color = Color.Gray
                                                                )
                                                            }

                                                        }
                                                    }


                                                }


                                            }

                                        }

                                    }
                                }

                            }


                        }

                    }
                    if (deviceStatus.value == "offline") {
                        sheetOpen = false
                        MyDialog(onDismiss = { /*TODO*/ })
                    }


                }

            }
        }

    }




}

@Composable
fun MyDialog(onDismiss: () -> Unit) {
    // Content of your dialog goes here
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {


        DeviceOfflineAnimation()


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
fun SpinningImage(spin: Boolean, currentTrack: Song, size: Dp) {

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
                animation = tween(durationMillis = 5000, easing = LinearEasing),
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
                .size(size)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
                .fillMaxSize()
                .aspectRatio(1f)
                .graphicsLayer {
                    if (shouldSpin) {
                        rotationZ = rotationState
                    }
                }
        )
    }
}

@Composable
fun DeviceOfflineAnimation() {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.sleeping)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Device status: Offline")
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )


    }


}
