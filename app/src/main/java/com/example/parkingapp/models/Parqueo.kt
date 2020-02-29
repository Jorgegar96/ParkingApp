package com.example.parkingapp.models

import java.io.Serializable

data class Parqueo(
    var disponible: Boolean,
    var numero: String,
    var seccion: String,
    var establecimiento: String,
    var codigo_reserva: String
) : Serializable