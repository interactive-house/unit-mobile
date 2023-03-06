package com.example.unitmobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.unitmobile.ui.theme.UnitMobileTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Firebase.database("https://softwareengineering-d0cdc-default-rtdb.europe-west1.firebasedatabase.app/")
        setContent {
            UnitMobileTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyApp(db)
                }
            }
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
                title = { Text("Smart House App") }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
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
    Column() {
        TitleHomeScreen("Smart House App")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        ItemSwitch(
            icon = Icons.Filled.Circle,
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
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        ItemSwitch(
            icon = Icons.Filled.Circle,
            label = "Door",
            onCheckedChange = { isChecked -> db.getReference("items").child("door").setValue(if (isChecked) "OPEN" else "CLOSED") },
            isChecked = switches["door"]?.value ?: false,
            imageResOn = R.drawable.door_open,
            imageResOff = R.drawable.door_closed,
            itemStateFalse = itemStateFalse[1],
            itemStateTrue = itemStateTrue[1]
        )
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        ItemSwitch(
            icon = Icons.Filled.Circle,
            label = "Window",
            onCheckedChange = { isChecked -> db.getReference("items").child("window").setValue(if (isChecked) "OPEN" else "CLOSED") },
            isChecked = switches["window"]?.value ?: false,
            imageResOn = R.drawable.window_open,
            imageResOff = R.drawable.window_closed,
            itemStateFalse = itemStateFalse[1],
            itemStateTrue = itemStateTrue[1]
        )
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        HumidityReader(
            db = db,
            humidity = remember { mutableStateOf(0) }
        )
    }
}
@Composable
fun TitleHomeScreen(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
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
    val tint = if (isChecked) Color(0xFF4CAF50) else Color.Red
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
            modifier = Modifier.size(24.dp)
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

                    // Turn on lamp if humidity is below 3
                    /*
                    if (humidity.value < 3) {
                        db.getReference("items").child("lamp").setValue("ON")

                    } else {
                        db.getReference("items").child("lamp").setValue("OFF")
                    }
                    */
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
    Text(
        text = "Humidity: ${humidity.value}",
        fontSize = 18.sp
    )
    LinearProgressIndicator(progress = humidity.value.toFloat() / 10)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val db = Firebase.database("https://softwareengineering-d0cdc-default-rtdb.europe-west1.firebasedatabase.app/")
    UnitMobileTheme {
        MyApp(db)
    }
}