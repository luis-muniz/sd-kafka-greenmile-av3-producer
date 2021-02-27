package com.service.producers.controllers

import com.service.producers.repositories.RouteRepository
import com.service.producers.domain.Route
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/route")
class RouteController (
        private val routeRepository: RouteRepository
        ){

    @PostMapping
    fun addRoute(@RequestBody route: Route): Mono<Route> {
        return routeRepository.save(route)
    }

    @GetMapping
    fun getRoutes(): Flux<Route> {
        return routeRepository.findAll()
    }
}