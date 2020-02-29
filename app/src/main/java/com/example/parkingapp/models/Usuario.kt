package com.example.parkingapp.models

import java.io.Serializable

data class Usuario(
    var username : String,
    var password : String,
    var parqueo_reservado : String
) : Serializable