package com.example.unitmobile.navigation

import com.example.unitmobile.R

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){

    object Home : BottomNavItem("Home", R.drawable.ic_home,"home")
    object Media: BottomNavItem("Media",R.drawable.ic_media,"media")
}