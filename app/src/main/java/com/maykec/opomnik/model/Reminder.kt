package com.maykec.opomnik.model

class Reminder (val id: String, val medsName: String,  val takeEveryDay: Boolean, val timeToTake: String ) {
    constructor(): this ("", "",true,"")
}