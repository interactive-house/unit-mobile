package com.example.unitmobile.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitmobile.Song
import com.example.unitmobile.R

@Composable
fun TextFieldWithToggle(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showText by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (showText) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { showText = !showText },
            ) {
                Icon(
                    if (showText) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (showText) "Hide Text" else "Show Text"
                )
            }

        }
    )
}
@Composable
fun BottomTrackController(
    currentTrack: Song,
    status: String,
    handleAction : (String) -> Unit
) {
    Log.i("BottomTrackController", "currentTrack: $currentTrack")
    val song = remember { mutableStateOf(currentTrack) }

    //Player

        Column(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
        ) {

            Row(
                Modifier
                    .fillMaxHeight()
                    .background(Color.White)
                    .fillMaxWidth()
                    .animateContentSize()
            ) {

            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colors.primary)
        ) {

            Image(
                    painter = painterResource(id = currentTrack.albumIMG),
                    contentDescription = "Song image",
                    modifier = Modifier.fillMaxHeight().padding(5.dp).width(50.dp),
                contentScale = ContentScale.Crop,

            )

            Column(
                Modifier
                    .padding(start = 10.dp)
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth(0.7f)
            ) {
                Text(
                    text = song.value.song,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Text(
                    text = song.value.artist,
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .width(150.dp)
                        .padding(end = 10.dp)
                ) {

                    IconButton(
                                    onClick = {
                                        val newStatus = if (status == "Playing") "pause" else "play"
                                        handleAction(newStatus)
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    if (status == "Playing") {
                                        Icon(Icons.Default.Pause, contentDescription = "Pause")
                                    } else {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                                    }
                                }
                }

            }
        }
        Spacer(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.Black)
        )


}