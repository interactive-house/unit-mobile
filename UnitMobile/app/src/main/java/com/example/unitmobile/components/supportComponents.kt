package com.example.unitmobile.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldWithToggle(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showText by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (showText) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { showText = !showText },
            ) {
                Icon(
                    if (showText) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (showText) "Hide Text" else "Show Text"
                )
            }

        }
    )
}
@Composable
fun LoadingScreen() {
    CircularProgressIndicator(
        modifier = Modifier.size(32.dp)
    )
}
