package com.example.unitmobile.components

import android.content.Context
import android.util.Log
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.sp
import com.example.unitmobile.MyNotification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

@Composable
fun HumidityReader(
    db: FirebaseDatabase,
    context: Context = LocalContext.current
){
    val humidity = rememberSaveable { mutableStateOf("") }

    if (humidity.value == "") {
        db.getReference("SmartHomeValueSoil").child("StatusOfSoil").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                humidity.value = snapshot.value.toString()
                if (humidity.value.lowercase() == "dry") {
                    val notice = MyNotification(context, "Smart House App", "Soil is dry!")
                    notice.fireNotfication()
                } else if (humidity.value.lowercase() == "wet") {
                    val notice = MyNotification(context, "Smart House App", "Soil is wet!")
                    notice.fireNotfication()
                } else {
                    val notice = MyNotification(context, "Smart House App", "Soil is ${humidity.value}")
                    notice.fireNotfication()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: ${error.message}")
            }
        })
    }
    Text(
        text = "Humidity: ${humidity.value}",
        fontSize = 18.sp
    )
    LinearProgressIndicator(progress = when (humidity.value.lowercase()) {
        "dry" -> 0.0f
        "perfect" -> 0.5f
        "wet" -> 1.0f
        else -> 0.0f
    })
}