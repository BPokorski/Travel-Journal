package com.example.traveljournal.utils

class StringUtills {

    fun connectorChanger(word: String, connector: String?, newConnector: String?): String {
        var newWord:String? = null
        if (word.contains(connector!!)) {
            newWord = word.replace(connector, newConnector!!)
        } else {
            newWord = word
        }
        return newWord
    }

    fun toLowerCaseConverter(word: String): String {
        var newWord:String? = null
        if (word.contains(" ")) {
            val words = word.split(" ")
           newWord = words[0].toLowerCase() + " " +
                    words[1].toLowerCase()
        } else {
            newWord = word.toLowerCase()
        }
        return newWord
    }
}