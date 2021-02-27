package com.service.producers.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "sensorGPS")
data class SensorGPS (
        @Id
        val id: Int,
        var coordinate: Coordinate? = null,
        ){
}