package com.example.unitmobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.unitmobile.navigation.BottomNavItem
import com.example.unitmobile.screens.HomeScreen
import com.example.unitmobile.screens.LoginScreen
import com.example.unitmobile.screens.MediaScreen
import com.example.unitmobile.screens.RegisterScreen
import com.example.unitmobile.ui.theme.UnitMobileTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Firebase.database("https://smarthome-3bb7b-default-rtdb.firebaseio.com/")
        setContent {
            UnitMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyApp(db, this)
                }
            }
        }
    }
}

@Composable
fun MyApp(
    db: FirebaseDatabase,
    activity: Activity
) {
    // Change to false to skip login
    val auth = remember { FirebaseAuth.getInstance() }
    var userState by remember(auth) { mutableStateOf(auth.currentUser) }

    var startDestination by remember { mutableStateOf("login") }

    fun signOut() {
        auth.signOut()
        userState = null

    }

    if(userState != null){
        startDestination = "home"
    }else{
        startDestination = "login"
    }


    val navController = rememberNavController()
    val viewModel: SharedViewModel = ViewModelProvider(
        LocalContext.current as ComponentActivity
    )[SharedViewModel::class.java]
    val lifeCycleOwner = LocalLifecycleOwner.current
    viewModel.initSongs()

    val itemStateTrue = listOf(
        "on",
        "open"
    )
    val itemStateFalse = listOf(
        "off",
        "closed"
    )
    var text by remember { mutableStateOf("") }
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MyApp", "activityResultLauncher triggered")
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            // first element of results
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            Log.d("MainActivity", "onActivityResult: $results")
            if (results != null) {

                handleSpeechToText(results, db, activity, navController, viewModel)
            }


        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Smart House App") },
                    actions = {
                        if (userState != null) {
                            DropdownMenuDemo(signOut = { signOut() }, userState)

                        }

                    }
                )
            },
            bottomBar = {
                if (userState != null) {
                    BottomNavigation(navController = navController)
                }
            },

            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 56.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                    }
                    NavigationGraph(
                        navController = navController,
                        db = db,
                        itemStateTrue = itemStateTrue,
                        itemStateFalse = itemStateFalse,
                        startDestination = startDestination,
                        showRegisterCallback = {
                            navController.navigate("register") {

                                navController.graph.startDestinationRoute?.let { screen_route ->
                                    popUpTo(screen_route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true

                            }
                        },
                        onLoginCallback = {
                            userState = it as FirebaseUser?
                            navController.navigate("home") {




                            }
                        },
                        onRegisterCallback = {
                            userState = it as FirebaseUser?
                            navController.navigate("home") {

                            }
                        },


                    )

                }

            }
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            if (userState != null) {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                        }
                        activityResultLauncher.launch(intent)
                    },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Add")
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    db: FirebaseDatabase,
    itemStateTrue: List<String>,
    itemStateFalse: List<String>,
    startDestination: String,
    showRegisterCallback: () -> Unit,
    onLoginCallback: (Any?) -> Unit,
    onRegisterCallback: (Any?) -> Unit
) {
    NavHost(navController, startDestination = startDestination) {
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen(db = db, itemStateTrue = itemStateTrue, itemStateFalse = itemStateFalse)
        }
        composable(BottomNavItem.Media.screen_route) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MediaScreen(db = db)
            }
        }
        composable("register") {
            RegisterScreen(
                onRegister = {
                    onRegisterCallback(it)
                },
                Modifier,
                context = LocalContext.current,
                db

            )
        }
        composable("login") {
            LoginScreen(
                onLogIn = {
                    onLoginCallback(it)

                },
                showRegisterCallback = { showRegisterCallback() },
                modifier = Modifier,
                context = LocalContext.current

            )
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Media
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screen_route,
                onClick = {
                    navController.navigate(item.screen_route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

fun handleSpeechToText(
    text: String,
    db: FirebaseDatabase,
    activity: Activity,
    navController: NavController,
    viewModel: SharedViewModel
) {
    Log.d("MainActivity", "HandleSpeechToText: $text")


    val lampValues = listOf(
        "light",
        "lamp"
    )

    val lampRef = db
        .getReference("SmartHomeValueLight")
        .child("StatusOflight")
    val windowRef = db
        .getReference("SmartHomeValueWindow")
        .child("StatusOfWindow")
    val doorRef = db
        .getReference("SmartHomeValueDoor")
        .child("StatusOfDoor")


    val currentScreen = navController.currentDestination?.route

    val lowercaseText = text.lowercase()
    if (lowercaseText.contains("play") || lowercaseText.contains("pause") ||
        lowercaseText.contains("stop") || lowercaseText.contains("next") || lowercaseText.contains("previous")
    ) {

        viewModel.ttsPhrase.postValue(lowercaseText)

        if (currentScreen != BottomNavItem.Media.screen_route) {
            navController.navigate(BottomNavItem.Media.screen_route) {

                navController.graph.startDestinationRoute?.let { screen_route ->
                    popUpTo(screen_route) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true

            }
        }

    } else if (lampValues.any { lowercaseText.contains(it) } || lowercaseText.contains("window") || lowercaseText.contains(
            "door"
        )) {

        if (currentScreen != BottomNavItem.Home.screen_route) {
            navController.navigate(BottomNavItem.Home.screen_route) {

                navController.graph.startDestinationRoute?.let { screen_route ->
                    popUpTo(screen_route) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true

            }
        }
        if (lampValues.any { lowercaseText.contains(it) }) {
            if (lowercaseText.contains("on")) {
                sendToast("Lamp turned on", activity)
                lampRef.setValue("on")
            } else if (lowercaseText.contains("off")) {
                sendToast("Lamp turned off", activity)
                lampRef.setValue("off")
            }
        } else if (lowercaseText.contains("window")) {
            if (lowercaseText.contains("open")) {
                sendToast("Window opened", activity)
                windowRef.setValue("open")
            } else if (lowercaseText.contains("close")) {
                sendToast("Window closed", activity)
                windowRef.setValue("close")
            }
        } else if (lowercaseText.contains("door")) {
            if (lowercaseText.contains("open")) {
                sendToast("Door opened", activity)
                doorRef.setValue("open")
            } else if (lowercaseText.contains("close")) {
                sendToast("Door closed", activity)
                doorRef.setValue("close")
            }
        }
    }


}

@Composable
fun DropdownMenuDemo(signOut: () -> Unit, userState: FirebaseUser?) {
    var expanded by remember { mutableStateOf(false) }

    val items = listOf(
        Pair(Icons.Filled.VerifiedUser, "Currently Logged in as: ${userState?.email}"),
        Pair(Icons.Filled.Logout, "Logout"),
    )

    Column {
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(onClick = {
            expanded = true

        }) {
            Icon(Icons.Filled.Person, contentDescription = "Add")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    if (index == 1) {
                        signOut()


                    }

                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(item.first, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(item.second)
                    }
                }
            }

        }
    }
}


fun sendToast(text: String, activity: Activity) {
    Toast.makeText(
        activity,
        text,
        Toast.LENGTH_SHORT
    ).show()
}

