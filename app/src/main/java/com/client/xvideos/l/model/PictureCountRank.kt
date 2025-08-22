package com.client.xvideos.l.model

enum class PictureCountRank(val count: Int){
    all(-1),
    c0_25(0),       //0 to 25
    c25_50(1),      //25 to 50
    c50_100(2),     //50 to 100
    c100_200(3),    //100 to 200
    c200_800(4),    //200 to 800
    c800_3200(5),   //800 to 3200
    c3200_12800(6), //3200 to 12800
}