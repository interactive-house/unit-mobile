package com.example.unitmobile

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
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
fun MyApp(
    db: FirebaseDatabase
) {
    val context = LocalContext.current
    // Change to false to skip login
    var shouldShowLogin by rememberSaveable {
        mutableStateOf(false)
    }
    val navController = rememberNavController()

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
                handleSpeechToText(results, db)
            }

        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Smart House App") }
                )
            },
            bottomBar = { BottomNavigation(navController = navController) },
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
                        if (shouldShowLogin) {
                            LoginScreen(onLoginClicked = { shouldShowLogin = false })
                        } else {
                            NavigationGraph(
                                navController = navController,
                                db = db,
                                itemStateTrue = itemStateTrue,
                                itemStateFalse = itemStateFalse
                            )
                        }
                    }
                }
            }
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
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

@Composable
fun NavigationGraph(navController: NavHostController, db: FirebaseDatabase, itemStateTrue: List<String>, itemStateFalse: List<String>) {
    NavHost(navController, startDestination = BottomNavItem.Home.screen_route) {
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
                label = { Text(text = item.title,
                    fontSize = 9.sp) },
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

fun handleSpeechToText(text: String, db : FirebaseDatabase) {
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

    val lowercaseText = text.lowercase()

    if (lampValues.any { lowercaseText.contains(it) }) {
        if (lowercaseText.contains("on")) {
            lampRef.setValue("on")
        } else if (lowercaseText.contains("off")) {
            lampRef.setValue("off")
        }
    } else if (lowercaseText.contains("window")) {
        if (lowercaseText.contains("open")) {
            windowRef.setValue("open")
        } else if (lowercaseText.contains("close")) {
            windowRef.setValue("close")
        }
    } else if (lowercaseText.contains("door")) {
        if (lowercaseText.contains("open")) {
            doorRef.setValue("open")
        } else if (lowercaseText.contains("close")) {
            doorRef.setValue("close")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val db = Firebase.database("https://smarthome-3bb7b-default-rtdb.firebaseio.com/")
    UnitMobileTheme {
        MyApp(db)
    }
}