package com.service.producers.events

import com.service.producers.domain.Route
import com.service.producers.domain.Stop
import com.service.producers.repositories.RouteRepository
import com.service.producers.utils.haversineDistance
import com.service.producers.domain.Coordinate
import com.service.producers.publisher.Publisher
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.util.*

const val geoFence = 0.03 // 30 meters

@Component
class EventArrival(
    private val routeRepository: RouteRepository,
    private val publisher : Publisher
){

    fun processCoordinate(notificationDTO: com.service.producers.DTOs.NotificationDTO){

        if (notificationDTO.actualCoordinateSensorGPSFromTelephone == null || notificationDTO.actualCoordinateSensorGPSFromVehicle == null || notificationDTO.lastCoordinateSensorGPSFromTelephone == null || notificationDTO.lastCoordinateSensorGPSFromVehicle == null){
            throw Error("Coordenadas invalidas")
        }

        val routeId = notificationDTO.routeId
        val lastCoordinateVehicle = notificationDTO.lastCoordinateSensorGPSFromVehicle
        val actualCoordinateVehicle = notificationDTO.actualCoordinateSensorGPSFromVehicle

        if (lastCoordinateVehicle.longitude == actualCoordinateVehicle.longitude && lastCoordinateVehicle.latitude == actualCoordinateVehicle.latitude){
            this.routeRepository.findById(routeId)
                .map{ route ->
                    Pair(filterListStops(actualCoordinateVehicle,route.stops).toFlux(),route)
                }
                .flatMap { pair ->
                    pair.first.flatMap { stop ->
                        arrivedStopOnRoute(pair.second, stop)
                            .flatMap {  registerEvent(pair.second, stop)}
                    }.then()
                }.subscribe()
        }
    }

    private fun filterListStops(actualCoordinateVehicle: Coordinate,stops: List<Stop>) = stops.filter { stop ->
            stop.arrivalAt == null && haversineDistance(actualCoordinateVehicle.latitude,actualCoordinateVehicle.longitude,stop.coordinate.latitude,stop.coordinate.longitude) <= geoFence
    }

    private fun arrivedStopOnRoute(route: Route, oldStop:Stop): Mono<Route> {
        val updateStop = oldStop.copy(arrivalAt = Date())
        val indexOf = route.stops.indexOf(oldStop)
        route.stops.removeAt(indexOf)
        route.stops.add(updateStop)
        return routeRepository.save(route)
    }

    private fun registerEvent(route: Route, stop:Stop): Mono<String>{
        val str = "${route.vehicle.plate} arrive at stop in client: ${stop.address}"

        return this.publisher.publicEventArrival(str)
    }
}