package com.example.chatapp.models

import java.io.Serializable

class Users:Serializable {
    var uid: String? = null
    var email: String? = null
    var name: String? = null
    var imgLink: String? = null

    constructor(uid: String?, email: String?, name: String?, imgLink: String?) {
        this.uid = uid
        this.email = email
        this.name = name
        this.imgLink = imgLink
    }

    constructor()

}