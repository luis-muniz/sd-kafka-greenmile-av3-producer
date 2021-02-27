package com.service.producers.DTOs

import com.service.producers.domain.Coordinate


data class NotificationDTO(
        val routeId: Int,
        val lastCoordinateSensorGPSFromVehicle: Coordinate?,
        val actualCoordinateSensorGPSFromVehicle: Coordinate?,
        val lastCoordinateSensorGPSFromTelephone: Coordinate?,
        val actualCoordinateSensorGPSFromTelephone: Coordinate?
){
}