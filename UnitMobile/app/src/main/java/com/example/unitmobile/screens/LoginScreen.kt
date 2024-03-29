package com.example.unitmobile.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitmobile.R
import com.example.unitmobile.components.CenteredClickableText
import com.example.unitmobile.components.TextFieldWithToggle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLogIn: (Any?) -> Unit,
    showRegisterCallback: () -> Unit,
    modifier: Modifier = Modifier
){
    val auth = FirebaseAuth.getInstance()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var scaffoldState = rememberScaffoldState()
    var coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        content = { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Welcome to Smart House App",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
                )
                Image(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .size(150.dp),
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "Logo"
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )

                TextFieldWithToggle(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Enter your password",
                )
                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        onLogIn(task.result?.user)
                                    } else {
                                        task.exception?.message?.let {
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    message = it
                                                )
                                            }
                                        }
                                    }
                                }
                        } else {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Please fill in the fields"
                                )
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
    )
}
