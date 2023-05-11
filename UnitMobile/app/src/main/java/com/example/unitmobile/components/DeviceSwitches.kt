package com.example.unitmobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unitmobile.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun LampSwitch(
    db: FirebaseDatabase,
    itemStateTrue: String,
    itemStateFalse: String
) {
    val switch = rememberSaveable { mutableStateOf(false) }
    val reference = db
        .getReference("SmartHomeValueLight")
        .child("StatusOflight")
    ItemSwitch(
        label = "Lamp",
        isChecked = switch.value,
        imageResOn = R.drawable.lamp_on,
        imageResOff = R.drawable.lamp_off,
        itemStateFalse = itemStateFalse,
        itemStateTrue = itemStateTrue,
        switch = switch,
        reference = reference
    )
}

@Composable
fun DoorSwitch(
    db: FirebaseDatabase,
    itemStateTrue: String,
    itemStateFalse: String
) {
    val switch = rememberSaveable { mutableStateOf(false) }
    val reference = db
        .getReference("SmartHomeValueDoor")
        .child("StatusOfDoor")
    ItemSwitch(
        label = "Door",
        isChecked = switch.value,
        imageResOn = R.drawable.door_open,
        imageResOff = R.drawable.door_closed,
        itemStateFalse = itemStateFalse,
        itemStateTrue = itemStateTrue,
        switch = switch,
        reference = reference
    )
}

@Composable
fun WindowSwitch(db: FirebaseDatabase, itemStateTrue: String, itemStateFalse: String) {
    val switch = rememberSaveable { mutableStateOf(false) }
    val reference = db
        .getReference("SmartHomeValueWindow")
        .child("StatusOfWindow")
    ItemSwitch(
        label = "Window",
        isChecked = switch.value,
        imageResOn = R.drawable.window_open,
        imageResOff = R.drawable.window_closed,
        itemStateFalse = itemStateFalse,
        itemStateTrue = itemStateTrue,
        switch = switch,
        reference = reference

    )
}

