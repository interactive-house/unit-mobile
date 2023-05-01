package com.example.unitmobile.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.unitmobile.components.TextFieldWithToggle
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
    var confirmedPassword by rememberSaveable { mutableStateOf("") }

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
        TextFieldWithToggle(
            label = "Password",
            value = password,
            onValueChange = { password = it }
        )
        TextFieldWithToggle(
            label = "Confirm Password",
            value = confirmedPassword,
            onValueChange = { confirmedPassword = it }
        )
        TextFieldWithToggle(
            label = "Validation Code",
            value = validationCode,
            onValueChange = { validationCode = it }
        )
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = {
                if (password == confirmedPassword){
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
                } else {
                    Toast.makeText(
                        context,
                        "Passwords are not the same",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            Text("Sign Up")
        }

    }
}



