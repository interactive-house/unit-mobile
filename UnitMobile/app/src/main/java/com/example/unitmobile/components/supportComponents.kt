package com.example.unitmobile.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitmobile.Song
import com.example.unitmobile.R


@Composable
fun CenteredClickableText(text: String, onClick: () -> Unit) {
    Box {
        ClickableText(
            modifier = Modifier
                .align(Alignment.TopCenter),

            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)) {
                    append(text)
                }

            },
            onClick = {
                onClick()
            }


        )
    }
}
@Composable
fun TextFieldWithToggle(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    var showText by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
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
fun ItemDivider() {
    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
}
@Composable
fun BottomTrackController(
    currentTrack: Song,
    status: String,
    handleAction : (String) -> Unit,
    previousSong: () -> Unit,
    nextSong: () -> Unit,
    openSheet: () -> Unit
) {
    Log.i("BottomTrackController", "currentTrack: $currentTrack")


    //Player

        Row(
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colors.primary)
                .clickable{
                    openSheet()
                }
        ) {
            if(currentTrack.song != "") {
                Image(
                    painter = painterResource(id = currentTrack.albumIMG),
                    contentDescription = "Song image",
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(5.dp)
                        .width(50.dp),
                    contentScale = ContentScale.Crop,

                    )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.default_img),
                    contentDescription = "Song image",
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(5.dp)
                        .width(50.dp),
                    contentScale = ContentScale.Crop,

                    )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = { previousSong() }) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous song"
                    )
                }
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
                IconButton(onClick = { handleAction("stop") }) {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "Stop song"
                    )
                }
                IconButton(onClick = { nextSong() }) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next song"
                    )
                }
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