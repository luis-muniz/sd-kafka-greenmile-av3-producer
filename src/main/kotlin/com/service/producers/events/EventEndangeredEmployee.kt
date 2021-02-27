package com.service.producers.events

import com.service.producers.DTOs.NotificationDTO
import com.service.producers.publisher.Publisher
import com.service.producers.repositories.RouteRepository
import com.service.producers.utils.haversineDistance
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

const val limitDistanceFromVehicle = 0.05 // = 50m (0.1 = 100m)
const val timeLimitAwayFromVehicle = 10000 // = 10s (1000 = 1s)

@Component
class EventEndangeredEmployee(
    private val routeRepository: RouteRepository,
    private val publisher : Publisher
){

    private val log = LoggerFactory.getLogger(EventArrival::class.java)

    fun processCoordinate(notificationDTO: com.service.producers.DTOs.NotificationDTO) {
        if (notificationDTO.actualCoordinateSensorGPSFromTelephone == null || notificationDTO.actualCoordinateSensorGPSFromVehicle == null || notificationDTO.lastCoordinateSensorGPSFromTelephone == null || notificationDTO.lastCoordinateSensorGPSFromVehicle == null){
            throw Error("Coordenadas invalidas")
        }

        val routeId = notificationDTO.routeId
        val actualCoordinateVehicle = notificationDTO.actualCoordinateSensorGPSFromVehicle
        val actualCoordinateTelephone = notificationDTO.actualCoordinateSensorGPSFromTelephone

        val distanceEmployeeFromVehicle = haversineDistance(actualCoordinateVehicle.latitude, actualCoordinateVehicle.longitude, actualCoordinateTelephone.latitude, actualCoordinateTelephone.longitude)

        this.routeRepository.findById(routeId)
            .flatMap{ route ->
                if (distanceEmployeeFromVehicle > limitDistanceFromVehicle){
                    // verify if this employee is in the client
                    for (stop in route.stops) {
                        if (haversineDistance(actualCoordinateTelephone.latitude, actualCoordinateTelephone.longitude, stop.coordinate.latitude, stop.coordinate.longitude) <= geoFence) {
                            Mono.empty<Unit>()
                        }
                    }

                    // verify if this employee is at break time
                    if (this.isWorkDay(actualCoordinateTelephone.datePing)){
                        Mono.empty<Unit>()
                    }

                    // verify time limit
                    if (route.employee.telephone.lastTimeCoordinateAwayFromVehicle == null) {
                        route.employee.telephone.lastTimeCoordinateAwayFromVehicle = actualCoordinateTelephone.datePing
                        this.routeRepository.save(route).subscribe()
                        Mono.empty<Unit>()
                    }

                    if (actualCoordinateTelephone.datePing.getTime() - route.employee.telephone.lastTimeCoordinateAwayFromVehicle!!.getTime() >= timeLimitAwayFromVehicle) {
                        val str = "${route.employee.name} may is in danger, he is ${(actualCoordinateTelephone.datePing.getTime() - route.employee.telephone.lastTimeCoordinateAwayFromVehicle!!.getTime())/1000} seconds away from the vehicle ${route.vehicle.plate}"

                        this.publisher.publicEventEndangeredEmployee(str)
                    }

                }else{
                    if (route.employee.telephone.lastTimeCoordinateAwayFromVehicle != null) {
                        route.employee.telephone.lastTimeCoordinateAwayFromVehicle = null
                        this.routeRepository.save(route).subscribe()
                    }
                }
                Mono.empty<Unit>()
            }.subscribe()
    }

    private fun isWorkDay(date: Date):Boolean{
        // WorkDay
        // Mon - Fry: 8h - 12h and 14h - 18h
        if (date.day != 6 && date.day != 5){
            if ((date.hours >= 8 && date.hours <= 12)|| (date.hours >= 14 && date.hours <= 18)){
                return true
            }
        }
        return false
    }

}