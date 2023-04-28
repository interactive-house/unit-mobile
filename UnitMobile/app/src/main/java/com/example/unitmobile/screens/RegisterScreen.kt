package com.example.unitmobile.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RegisterScreen(
    onRegister: (Any?) -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    db: FirebaseDatabase

) {

    val auth = FirebaseAuth.getInstance()

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var validationCode by rememberSaveable { mutableStateOf("") }
    var validationCodeFromDB by rememberSaveable { mutableStateOf("") }

    db.getReference("ValidationCode").get().addOnSuccessListener {
        validationCodeFromDB = it.value.toString()
    }


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Smart House App")
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Email") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        TextField(
            value = validationCode,
            onValueChange = { validationCode = it },
            label = { Text("Validation Code") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = {
                if (validationCode == validationCodeFromDB){
                    auth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onRegister(task.result?.user)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Login failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Validation Code is wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        ) {
            Text("Sign Up")
        }

    }
}



