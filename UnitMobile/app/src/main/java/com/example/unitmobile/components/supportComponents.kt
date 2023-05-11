package com.example.unitmobile.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

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