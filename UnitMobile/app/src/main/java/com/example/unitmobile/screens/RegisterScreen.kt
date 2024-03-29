package com.example.unitmobile.screens


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitmobile.components.CenteredClickableText
import com.example.unitmobile.components.TextFieldWithToggle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegister: (Any?) -> Unit,
    modifier: Modifier = Modifier,
    db: FirebaseDatabase,
    showLoginCallback: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmedPassword by rememberSaveable { mutableStateOf("") }
    var validationCode by rememberSaveable { mutableStateOf("") }
    var validationCodeFromDB by rememberSaveable { mutableStateOf("") }
    var scaffoldState = rememberScaffoldState()
    var coroutineScope = rememberCoroutineScope()

    db.getReference("ValidationCode").get().addOnSuccessListener {
        validationCodeFromDB = it.value.toString()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Fill in the fields below to register!",
                    modifier.padding(bottom = 24.dp),
                    style = TextStyle(fontSize = 18.sp)
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") }
                )

                TextFieldWithToggle(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Enter your password"
                )

                TextFieldWithToggle(
                    label = "Confirm Password",
                    value = confirmedPassword,
                    onValueChange = { confirmedPassword = it },
                    placeholder = "Confirm your password"
                )

                TextFieldWithToggle(
                    label = "Validation Code",
                    value = validationCode,
                    onValueChange = { validationCode = it },
                    placeholder = "Enter the validation code"
                )
                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    onClick = {

                        val passwordPattern = Regex("^.{6,}$")
                        val emailPattern = Regex("^[A-Za-z].+@.+\\..+")

                        if (!username.matches(emailPattern)) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "Invalid email format. Email must start with a letter and follow the standard email format (e.g., example@example.com).",
                                    "OK"
                                )
                            }
                        } else if (!password.matches(passwordPattern)) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "Invalid password format. Password must be at least 6 characters long.",
                                    "OK"
                                )
                            }
                        } else if (password != confirmedPassword) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "Passwords do not match.",
                                    "OK"
                                )
                            }
                        } else if (validationCode != validationCodeFromDB){
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "Validation code is incorrect.",
                                    "OK"
                                )
                            }
                        } else {
                            auth.createUserWithEmailAndPassword(username, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        onRegister(task.result?.user)
                                    } else {
                                        task.exception?.let {
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    "Error: ${it.message}",
                                                    "OK"
                                                )
                                            }
                                        }
                                    }
                                }
                        }
                    }
                ) {
                    Text("Sign Up")
                }
                CenteredClickableText(text = "Already have an account? Log in", onClick={
                    Log.i("showLogin", "clicked")
                    showLoginCallback()
                })
            }
        }
    )
}
