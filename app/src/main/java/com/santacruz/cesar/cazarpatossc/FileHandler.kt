package com.santacruz.cesar.cazarpatossc

interface FileHandler {
    fun saveInformation(dataToStore:Pair<String,String>)
    fun readInformation():Pair<String,String>
}
