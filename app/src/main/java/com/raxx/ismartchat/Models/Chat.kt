package com.raxx.ismartchat.Models

class Chat {

    private var sender : String = ""
    private var message : String = ""
    private var receiver : String = ""
    private var url : String = ""
    private var messageId : String = ""
    private var isseen = false
    private var tymdate = ""

    

    constructor()
    constructor(
        sender: String,
        message: String,
        receiver: String,
        url: String,
        messageId: String,
        isseen: Boolean,
        tymdate:String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.url = url
        this.messageId = messageId
        this.isseen = isseen
        this.tymdate = tymdate
    }


    fun getTymdate(): String? {
        return tymdate
    }

    fun setTymdate(tymdate: String?) {
        this.tymdate = tymdate!!
    }



    fun getSender(): String? {
        return sender
    }

    fun setSender(sender: String?) {
        this.sender = sender!!
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message!!
    }

    fun getReceiver(): String? {
        return receiver
    }

    fun setReceiver(receiver: String?) {
        this.receiver = receiver!!
    }

    fun getUrl(): String? {
        return url
    }

    fun setUrl(url: String?) {
        this.url = url!!
    }

    fun getMessageId(): String? {
        return messageId
    }

    fun setMessageId(messageId: String?) {
        this.messageId = messageId!!
    }

    fun getIsseen(): Boolean {
        return isseen
    }

    fun setIsseen(isseen: Boolean) {
        this.isseen = isseen
    }
}