package com.santacruz.cesar.cazarpatossc

interface FileHandler {
    fun saveInformation(dataToStore:Pair<String,String>)
    fun saveInformationEncrypted(dataToStore:Pair<String,String>)
    fun saveInformationFile(dataToStore:Pair<String,String>)
    fun readInformation():Pair<String,String>
    fun readInformationEncrypted():Pair<String,String>
    fun readInformationFile():Pair<String,String>

}
