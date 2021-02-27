package com.service.producers.domain

import java.util.*

data class Stop (
        val id: Int,
        val coordinate: Coordinate,
        val address: String,
        val arrivalAt: Date? = null,
        val departureAt: Date? = null,
        ){
}