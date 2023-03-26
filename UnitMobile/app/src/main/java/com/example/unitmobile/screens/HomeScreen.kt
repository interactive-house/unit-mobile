package com.example.unitmobile.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
        , horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Smart House App",
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        LampSwitch(db, itemStateTrue[0], itemStateFalse[0])
        ItemDivider()
        DoorSwitch(db, itemStateTrue[1], itemStateFalse[1])
        ItemDivider()
        WindowSwitch(db, itemStateTrue[1], itemStateFalse[1])
        ItemDivider()
        HumidityReader(db = db)
        ItemDivider()
        MediaControls(db = db)
    }
}
@Composable
fun ItemDivider() {
    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
}