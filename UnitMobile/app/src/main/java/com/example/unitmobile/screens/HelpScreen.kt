package com.example.unitmobile.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unitmobile.R

@Composable
fun HelpScreen() {
    Box(

        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),



            ) {
            Text(text = "Voice Commands:")
            Divider(
                modifier = Modifier.padding(4.dp),
                color = Color.White
            )
            Card() {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(R.drawable.lamp_on),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Column {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Turn the lamp on: " , style = TextStyle(
                                fontWeight = FontWeight.Bold
                            ))
                            Text(text = "Turns on the lamp")
                        }
                        Divider(modifier = Modifier.padding(2.dp))
                        Row( modifier = Modifier.align(Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Turn the lamp off: ", style = TextStyle(
                                fontWeight = FontWeight.Bold
                            ))
                            Text(text = "Turns off the lamp")
                        }
                    }

                }

            }
            Divider(
                modifier = Modifier.padding(4.dp),
                color = Color.White
            )


            Card() {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.door_open),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Column {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
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
                            modifier = Modifier.align(Alignment.CenterHorizontally),
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

            }
            Divider(
                modifier = Modifier.padding(4.dp),
                color = Color.White
            )
            Card() {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(R.drawable.window_open),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Column {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
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
                            modifier = Modifier.align(Alignment.CenterHorizontally),
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

            }
            Divider(
                modifier = Modifier.padding(4.dp),
                color = Color.White
            )


        }


    }
}