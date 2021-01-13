package com.raxx.ismartchat.Models

class Chatlist {
    private var id :String =""
    private var chatlistTime :String =""

    constructor()
    constructor(id: String,chatlistTime: String) {
        this.id = id
        this.chatlistTime = chatlistTime
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id!!
    }

    fun getChatlistTime(): String? {
        return chatlistTime
    }

    fun setChatlistTime(chatlistTime: String?) {
        this.chatlistTime = chatlistTime!!
    }


}