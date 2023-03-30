package com.example.unitmobile.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

@Composable
fun ItemSwitch(
    label: String,
    isChecked: Boolean,
    imageResOn: Int,
    imageResOff: Int,
    itemStateFalse: String,
    itemStateTrue: String,
    switch: MutableState<Boolean>,
    reference: DatabaseReference
) {

    val tint = if (isChecked) Color(0xFF4CAF50) else Color.Red
    val imageRes = if (isChecked) imageResOn else imageResOff
    val itemState = if (isChecked) "$label is $itemStateTrue" else "$label is $itemStateFalse"

    DisposableEffect(reference) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                switch.value = (itemStateTrue == snapshot.value.toString().lowercase())
                Log.d("TAG", "onDataChange: ${snapshot.key}${snapshot.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: ${error.message}")
            }
        }

        reference.addValueEventListener(listener)

        onDispose {
            reference.removeEventListener(listener)
        }
    }

    Card(
        modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp),
        elevation = 4.dp
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                Icons.Filled.Circle,
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
                onCheckedChange = { isChecked ->
                    reference.setValue(if (isChecked) itemStateTrue else itemStateFalse)
                },
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

}
