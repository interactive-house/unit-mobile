package com.example.unitmobile.screens

import android.net.wifi.ScanResult.InformationElement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitmobile.components.*
import com.google.firebase.database.FirebaseDatabase

@Composable
fun HomeScreen(
    db: FirebaseDatabase,
    itemStateTrue: List<String>,
    itemStateFalse: List<String>
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
        , horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Smart House App",
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Information()
        ItemDivider()
        LampSwitch(db, itemStateTrue[0], itemStateFalse[0])
        ItemDivider()
        DoorSwitch(db, itemStateTrue[1], itemStateFalse[1])
        ItemDivider()
        WindowSwitch(db, itemStateTrue[1], itemStateFalse[1])
        ItemDivider()
        HumidityReader(db = db, lifecycleOwner = LocalLifecycleOwner.current)
        //ItemDivider()
        //MediaControls(db = db)
    }
}
@Composable
fun ItemDivider() {
    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
}
@Composable
fun Information() {
    val showInformation = rememberSaveable { mutableStateOf(false) }
    Button(onClick = {
        showInformation.value = !showInformation.value
    }) {
        Text(text = if (showInformation.value) "Hide Information" else "Show Information")
    }
    if (showInformation.value) {
        Text(text = "Welcome to the smart house app, here you can control your house devices and see the current status of your house. Try it out by clicking on the switches below.")
    }
}