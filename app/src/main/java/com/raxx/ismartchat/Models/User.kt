package com.raxx.ismartchat.Models

class User{
    private var uid : String = ""
    private var userName : String = ""
    private var status : String = ""
    private var search : String = ""
    private var profile : String = ""
    private var email : String = ""
    private var cover : String = ""
    private var DOB : String = ""

    constructor()
    constructor(
        uid: String,
        userName: String,
        status: String,
        search: String,
        profile: String,
        email: String,
        cover: String,
        DOB: String
    ) {
        this.uid = uid
        this.userName = userName
        this.status = status
        this.search = search
        this.profile = profile
        this.email = email
        this.cover = cover
        this.DOB = DOB
    }


    fun getUid(): String? {
        return uid
    }

    fun setUid(uid: String?) {
        this.uid = uid!!
    }

    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String?) {
        this.userName = userName!!
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status!!
    }

    fun getSearch(): String? {
        return search
    }

    fun setSearch(search: String?) {
        this.search = search!!
    }

    fun getProfile(): String? {
        return profile
    }

    fun setProfile(profile: String?) {
        this.profile = profile!!
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email!!
    }

    fun getCover(): String? {
        return cover
    }

    fun setCover(cover: String?) {
        this.cover = cover!!
    }

    fun getDOB(): String? {
        return DOB
    }

    fun setDOB(DOB: String?) {
        this.DOB = DOB!!
    }


}