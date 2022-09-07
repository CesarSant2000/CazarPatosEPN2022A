package com.santacruz.cesar.cazarpatossc.interfaces

interface FileHandler {
    fun saveInformation(toSaveList:Pair<String,String>)
    fun readInformation():Pair<String,String>
}