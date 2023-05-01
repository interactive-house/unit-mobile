package com.example.unitmobile.components

import android.content.Context
import android.util.Log
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.unitmobile.MyNotification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun HumidityReader(
    db: FirebaseDatabase,
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner
) {
    val humidity = rememberSaveable { mutableStateOf("") }

    val humidityRef = db.getReference("SmartHomeValueSoil").child("StatusOfSoil")

    DisposableEffect(humidityRef) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                humidity.value = snapshot.value.toString()
                Log.d("onDataChangeHumidity", "Humidity: ${humidity.value}")
                Log.i("onDataChangeHumidity", "current state: ${lifecycleOwner.lifecycle.currentState}")
                if (lifecycleOwner.lifecycle.currentState != (Lifecycle.State.STARTED ) &&
                        lifecycleOwner.lifecycle.currentState != (Lifecycle.State.RESUMED)) {
                    sendNotification(context, humidity.value)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("onCancelledHumidity", "Failed to read value.", error.toException())
            }
        }
        humidityRef.addValueEventListener(listener)
        onDispose {
            humidityRef.removeEventListener(listener)
        }
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
fun sendNotification(
    context: Context,
    humidityValue: String
) {
    if (humidityValue.lowercase() == "dry") {
        val notice = MyNotification(context,
            "Humidity is dry",
            "Humidity is too low")
        notice.fireNotification()
    } else if (humidityValue.lowercase() == "wet") {
        val notice = MyNotification(context,
            "Humidity is wet",
            "Humidity is too high")
        notice.fireNotification()
    } else {
        val notice = MyNotification(context,
            "Humidity is perfect",
            "Humidity is just right")
        notice.fireNotification()
    }
}