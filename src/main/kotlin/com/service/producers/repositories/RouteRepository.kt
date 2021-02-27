package com.service.producers.repositories

import com.service.producers.domain.Route
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface RouteRepository:ReactiveMongoRepository<Route, Int>{
    @Query("{ 'vehicle.SensorGPS.id': ?0 }")
    fun getRouteBySensorGPSIdFromVehicle(id: Int): Mono<Route>
}