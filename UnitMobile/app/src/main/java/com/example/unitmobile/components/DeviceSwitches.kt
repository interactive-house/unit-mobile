package com.example.unitmobile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.unitmobile.R
import com.google.firebase.database.FirebaseDatabase

@Composable
fun LampSwitch(db: FirebaseDatabase, itemStateTrue: String, itemStateFalse: String) {
    val switch = rememberSaveable { mutableStateOf(false) }
    val reference = db.getReference("SmartHomeValueLight").child("StatusOflight")
    ItemSwitch(
        icon = Icons.Filled.Circle,
        label = "Lamp",
        onCheckedChange = { isChecked ->
            reference.setValue(if (isChecked) "on" else "off")
        },
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
fun DoorSwitch(db: FirebaseDatabase, itemStateTrue: String, itemStateFalse: String) {
    val switch = rememberSaveable { mutableStateOf(false) }
    val reference = db.getReference("SmartHomeValueDoor").child("StatusOfDoor")
    ItemSwitch(
        icon = Icons.Filled.Circle,
        label = "Door",
        onCheckedChange = { isChecked ->
            reference.setValue(if (isChecked) "open" else "closed")
        },
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
    val reference = db.getReference("SmartHomeValueWindow").child("StatusOfWindow")
    ItemSwitch(
        icon = Icons.Filled.Circle,
        label = "Window",
        onCheckedChange = { isChecked ->
            reference.setValue(if (isChecked) "open" else "closed")
        },
        isChecked = switch.value,
        imageResOn = R.drawable.window_open,
        imageResOff = R.drawable.window_closed,
        itemStateFalse = itemStateFalse,
        itemStateTrue = itemStateTrue,
        switch = switch,
        reference = reference
    )
}