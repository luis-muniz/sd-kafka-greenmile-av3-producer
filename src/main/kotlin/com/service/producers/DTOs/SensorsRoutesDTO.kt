package com.service.producers.DTOs

import com.service.producers.domain.SensorGPS


data class SensorsRoutesDTO(
        val coordinateSensorGPSVehicle: SensorGPS,
        val coordinateSensorGPSTelephone: SensorGPS,
) {
}