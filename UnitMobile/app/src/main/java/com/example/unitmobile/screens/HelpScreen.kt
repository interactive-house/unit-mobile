package com.example.unitmobile.screens

import android.inputmethodservice.Keyboard.Row
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.unitmobile.R
import com.github.skgmn.composetooltip.AnchorEdge
import com.github.skgmn.composetooltip.Tooltip

@Composable
fun HelpScreen() {

    var toolTipVisible = remember { mutableStateOf(true) }



    Box(

        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,


            ) {
            Text(text = "Voice Commands:", textAlign = TextAlign.Center, style = TextStyle(
                fontWeight = FontWeight.Bold
            ))
            Divider(
                modifier = Modifier.padding(4.dp),
                color = Color.White
            )
            if(toolTipVisible.value){
                Tooltip(
                    anchorEdge = AnchorEdge.Top,
                    onDismissRequest = { toolTipVisible.value = false },

                    ){
                    Text(text = "Click on the cards to see the voice commands", textAlign = TextAlign.Center, style = TextStyle(
                        fontWeight = FontWeight.Bold
                    ))
                }
            }
            FlipCard(R.drawable.lamp_on

            ) {
               Column {
                     Row(
                          modifier = Modifier,
                          verticalAlignment = Alignment.CenterVertically
                     ) {
                          Text(
                            text = "Turn on the light: ", style = TextStyle(
                                 fontWeight = FontWeight.Bold
                            )
                          )
                          Text(text = "Turns on the light")
                     }
                     Divider(modifier = Modifier.padding(2.dp))
                     Row(
                          modifier = Modifier,
                          verticalAlignment = Alignment.CenterVertically
                     ) {
                          Text(
                            text = "Turn off the light: ", style = TextStyle(
                                 fontWeight = FontWeight.Bold
                            )
                          )
                          Text(text = "Turns off the light")
                     }

               }

            }
            Divider(
                modifier = Modifier.padding(4.dp),
                color = Color.White
            )

            FlipCard(img = R.drawable.door_closed) {
                Column {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Open the door: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "Opens the door")
                    }
                    Divider(modifier = Modifier.padding(2.dp))
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Close the door: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "Closes the door")
                    }
                }

            }
            Divider(
                modifier = Modifier.padding(4.dp),
                color = Color.White
            )
            FlipCard(img = R.drawable.window_closed) {
                Column {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Open the window: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "Opens the window")
                    }
                    Divider(modifier = Modifier.padding(2.dp))
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Close the window: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "Closes the window")
                    }
                }

            }
            Divider(
                modifier = Modifier.padding(4.dp),
                color = Color.White
            )
            FlipCard(img = R.drawable.music_record) {
                Column {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Play: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "Resumes current song")
                    }
                    Divider(modifier = Modifier.padding(2.dp))
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pause: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "Pauses current song")
                    }
                    Divider(modifier = Modifier.padding(2.dp))
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "Plays next song")
                    }
                    Divider(modifier = Modifier.padding(2.dp))
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Previous: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "Plays previous song")
                    }
                    Divider(modifier = Modifier.padding(2.dp))
                    Column(
                        modifier = Modifier,

                        ) {
                        Text(
                            text = "Play <SongName>: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(text = "Plays the specified song")
                    }
                }

            }
            Divider(
                modifier = Modifier.padding(4.dp),
                color = Color.White
            )


        }


    }
}

@Composable
fun FlipCard(img: Int, column: @Composable () -> Unit) {

    var rotated by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(500)
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!rotated) 1f else 0f,
        animationSpec = tween(500)
    )

    val animateBack by animateFloatAsState(
        targetValue = if (rotated) 1f else 0f,
        animationSpec = tween(500)
    )

    val animateColor by animateColorAsState(
        targetValue = if (rotated) Color.Red else Color.Blue,
        animationSpec = tween(500)
    )

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 8 * density
                }
                .clickable {
                    rotated = !rotated
                },
        )
        {
            if (!rotated) {
                Image(
                    painter = painterResource(img),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                )

            } else {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .graphicsLayer {
                            alpha = if (rotated) animateBack else animateFront
                            rotationY = rotation
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    column()

                }

            }
        }
    }
}