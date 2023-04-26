package com.example.unitmobile.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun LoginScreen(
    onLogIn: (Any?) -> Unit,
    showRegisterCallback: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
){
    val auth = FirebaseAuth.getInstance()

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Smart House App")
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = {
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onLogIn(task.result?.user)
                        } else {
                            Toast.makeText(
                                context,
                                "Login failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        ) {
            Text("Login")
        }
        CenteredClickableText(text = "Don't have an account? Sign up", onClick={
            Log.i("showRegister", "clicked")
            showRegisterCallback()


        })

        }

    }

@Composable
fun CenteredClickableText(text: String, onClick: () -> Unit) {
    Box(
    ) {
        ClickableText(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .align(Alignment.TopCenter),


            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)) {
                    append("Don't have an account? Sign up ")
                }

            },
            onClick = {
               onClick()
            }


        )
    }
}
