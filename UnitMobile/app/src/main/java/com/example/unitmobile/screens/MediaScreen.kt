package com.example.unitmobile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitmobile.components.mediaControls.MediaControls
import com.google.firebase.database.FirebaseDatabase

@Composable
fun MediaScreen(db: FirebaseDatabase) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Media Screen",
            fontSize = 24.sp,
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )
        MediaControls(db = db)
    }

}