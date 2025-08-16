package com.client.xvideos.l.model

//BytopRated
//"display":"rating_14_days
//rating_7_days rating_14_days rating_30_days rating_90_days rating_1_year rating_all_time

data class DataAlbumFilterDisplay(
    val primary: String,
    val secondary : String,
    val request : String
)

val byDate = "By Date"
val byTopRated = "By Top Rated"

val albumFilterDisplay = listOf(

    //-- By Top Rated
    DataAlbumFilterDisplay( primary = byTopRated, secondary = "7 Days", request = "rating_7_days" ),
    DataAlbumFilterDisplay( primary = byTopRated, secondary = "14 Days", request = "rating_14_days" ),
    DataAlbumFilterDisplay( primary = byTopRated, secondary = "30 Days", request = "rating_30_days" ),
    DataAlbumFilterDisplay( primary = byTopRated, secondary = "90 Days", request = "rating_90_days" ),
    DataAlbumFilterDisplay( primary = byTopRated, secondary = "1 Year", request = "rating_1_year" ),
    DataAlbumFilterDisplay( primary = byTopRated, secondary = "90 Days", request = "rating_90_days" ),
    DataAlbumFilterDisplay( primary = byTopRated, secondary = "All Time", request = "rating_all_time" ),

    //-- First Letter


    //-- By Date
    DataAlbumFilterDisplay( primary = byDate, secondary = "Trending", request = "???" ),
    DataAlbumFilterDisplay( primary = byDate, secondary = "Newest First", request = "???" ),



    )

