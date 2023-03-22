package com.example.unitmobile

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
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

        val db = Firebase.database("https://smarthome-3bb7b-default-rtdb.firebaseio.com/")
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

    val itemStateTrue = listOf(
        "on",
        "open"
    )
    val itemStateFalse = listOf(
        "off",
        "closed"
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
                HomeScreen(db, itemStateTrue, itemStateFalse)
            }
        }
    )
}

@Composable
fun HomeScreen(
    db: FirebaseDatabase,
    itemStateTrue: List<String>,
    itemStateFalse: List<String>
) {
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TitleHomeScreen("Smart House App")
        LampSwitch(db, itemStateTrue[0], itemStateFalse[0])
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
        DoorSwitch(db, itemStateTrue[1], itemStateFalse[1])
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
        WindowSwitch(db, itemStateTrue[1], itemStateFalse[1])
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
        HumidityReader(db = db)
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
    }
}
@Composable
fun LampSwitch(db: FirebaseDatabase, itemStateTrue: String, itemStateFalse: String) {
    val switch = remember { mutableStateOf(false) }

    ItemSwitch(
        icon = Icons.Filled.Circle,
        label = "Lamp",
        onCheckedChange = { isChecked ->
            db.getReference("SmartHomeValueLight").child("StatusOflight").setValue(if (isChecked) "on" else "off")
        },
        isChecked = switch.value,
        imageResOn = R.drawable.lamp_on,
        imageResOff = R.drawable.lamp_off,
        itemStateFalse = itemStateFalse,
        itemStateTrue = itemStateTrue,
        switch = switch,
        db = db,
        reference = "SmartHomeValueLight"
    )
}
@Composable
fun DoorSwitch(db: FirebaseDatabase, itemStateTrue: String, itemStateFalse: String) {
    val switch = remember { mutableStateOf(false) }

    ItemSwitch(
        icon = Icons.Filled.Circle,
        label = "Door",
        onCheckedChange = { isChecked ->
            db.getReference("SmartHomeValueDoor").child("StatusOfDoor").setValue(if (isChecked) "open" else "closed")
        },
        isChecked = switch.value,
        imageResOn = R.drawable.door_open,
        imageResOff = R.drawable.door_closed,
        itemStateFalse = itemStateFalse,
        itemStateTrue = itemStateTrue,
        switch = switch,
        db = db,
        reference = "SmartHomeValueDoor"
    )
}
@Composable
fun WindowSwitch(db: FirebaseDatabase, itemStateTrue: String, itemStateFalse: String) {
    val switch = remember { mutableStateOf(false) }

    ItemSwitch(
        icon = Icons.Filled.Circle,
        label = "Window",
        onCheckedChange = { isChecked ->
            db.getReference("SmartHomeValueWindow").child("StatusOfWindow").setValue(if (isChecked) "open" else "closed")
        },
        isChecked = switch.value,
        imageResOn = R.drawable.window_open,
        imageResOff = R.drawable.window_closed,
        itemStateFalse = itemStateFalse,
        itemStateTrue = itemStateTrue,
        switch = switch,
        db = db,
        reference = "SmartHomeValueWindow"
    )
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
    itemStateTrue: String,
    switch: MutableState<Boolean>,
    db: FirebaseDatabase,
    reference: String
) {

    val tint = if (isChecked) Color(0xFF4CAF50) else Color.Red
    val imageRes = if (isChecked) imageResOn else imageResOff
    val itemState = if (isChecked) "$label is $itemStateTrue" else "$label is $itemStateFalse"

    db.getReference(reference).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (item in snapshot.children) {
                switch.value = (itemStateTrue == item.value)
                Log.d("TAG", "onDataChange: ${item.key}${item.value} ${switch.value}")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })

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
    context: Context = LocalContext.current
){
    val humidity = remember { mutableStateOf("dry") }

    db.getReference("SmartHomeValueSoil").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (item in snapshot.children) {
                Log.d("TAG", "onDataChange: ${item.key} ${item.value} ${humidity.value}")
                if (item.key.toString() == "StatusOfSoil") {
                    humidity.value = item.value.toString()

                    if (humidity.value == "dry") {
                        val notice = MyNotification(context, "Smart House App", "Soil is dry!")
                        notice.fireNotfication()
                    } else if (humidity.value == "wet") {
                        val notice = MyNotification(context, "Smart House App", "Soil is wet!")
                        notice.fireNotfication()
                    }
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
    LinearProgressIndicator(progress = if (humidity.value == "wet") 1.0f else 0.0f)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val db = Firebase.database("https://smarthome-3bb7b-default-rtdb.firebaseio.com/")
    UnitMobileTheme {
        MyApp(db)
    }
}