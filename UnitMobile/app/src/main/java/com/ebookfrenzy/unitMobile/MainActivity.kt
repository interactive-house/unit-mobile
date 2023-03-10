package com.ebookfrenzy.unitMobile

import androidx.compose.material.icons.filled.*
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebookfrenzy.testsoftware.R
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.database("https://lab2se-c0094-default-rtdb.europe-west1.firebasedatabase.app/")

        setContent {
            MyApp(db)
        }
    }
}

@Composable
fun MyApp(db: FirebaseDatabase) {

    val switches = hashMapOf(
        "lamp" to remember { mutableStateOf(false) },
        "door" to remember { mutableStateOf(false) },
        "window" to remember { mutableStateOf(false) },
    )

    val itemStateTrue = listOf(
        "ON",
        "OPEN"
    )
    val itemStateFalse = listOf(
        "OFF",
        "CLOSED"
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Home App") }
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HomeScreen(switches, db, itemStateTrue, itemStateFalse)
            }
        }
    )
}

@Composable
fun HomeScreen(
    switches: HashMap<String, MutableState<Boolean>>,
    db: FirebaseDatabase,
    itemStateTrue: List<String>,
    itemStateFalse: List<String>
) {
    db.getReference("items").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (item in snapshot.children) {
                switches[item.key.toString()]?.value = item.value.toString() in itemStateTrue
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ItemSwitch(
            icon = Icons.Filled.ToggleOn,
            label = "Lamp",
            onCheckedChange = { isChecked ->
                db.getReference("items").child("lamp").setValue(if (isChecked) "ON" else "OFF")
            },
            isChecked = switches["lamp"]?.value ?: false,
            imageResOn = R.drawable.lamp_on,
            imageResOff = R.drawable.lamp_off,
            itemStateFalse = itemStateFalse[0],
            itemStateTrue = itemStateTrue[0]
        )
        ItemSwitch(
            icon = Icons.Filled.ToggleOn,
            label = "Door",
            onCheckedChange = { isChecked -> db.getReference("items").child("door").setValue(if (isChecked) "OPEN" else "CLOSED") },
            isChecked = switches["door"]?.value ?: false,
            imageResOn = R.drawable.door_open,
            imageResOff = R.drawable.door_closed,
            itemStateFalse = itemStateFalse[0],
            itemStateTrue = itemStateTrue[0]
        )
        ItemSwitch(
            icon = Icons.Filled.ToggleOn,
            label = "Window",
            onCheckedChange = { isChecked -> db.getReference("items").child("window").setValue(if (isChecked) "OPEN" else "CLOSED") },
            isChecked = switches["window"]?.value ?: false,
            imageResOn = R.drawable.door_open,
            imageResOff = R.drawable.door_closed,
            itemStateFalse = itemStateFalse[1],
            itemStateTrue = itemStateTrue[1]
        )
        HumidityReader(
            db = db,
            humidity = remember { mutableStateOf(0) }
        )
    }
}
@Composable
fun ItemSwitch(
    icon: ImageVector,
    label: String,
    onCheckedChange: (Boolean) -> Unit,
    isChecked: Boolean,
    imageResOn: Int,
    imageResOff: Int,
    itemStateFalse: String,
    itemStateTrue: String
) {
    val tint = if (isChecked) Color(0xFF4CAF50) else Color.Gray
    val imageRes = if (isChecked) imageResOn else imageResOff
    val itemState = if (isChecked) "$label is $itemStateTrue" else "$label is $itemStateFalse"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = itemState,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Image(
            painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun HumidityReader(
    db: FirebaseDatabase,
    humidity: MutableState<Int>
){
    db.getReference("items").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (item in snapshot.children) {
                if (item.key.toString() == "humidity") {
                    humidity.value = item.value.toString().toInt()
                    Log.d("Humidity", humidity.value.toString())

                    if (humidity.value < 3) {
                        db.getReference("items").child("lamp").setValue("ON")

                    } else {
                        db.getReference("items").child("lamp").setValue("OFF")
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
    Text(text = "Humidity: ${humidity.value}")
    LinearProgressIndicator(progress = humidity.value.toFloat() / 10)
}

@Preview
@Composable
fun MyAppPreview() {
    val db = Firebase.database("https://lab2se-c0094-default-rtdb.europe-west1.firebasedatabase.app/")
    MyApp(db)
}