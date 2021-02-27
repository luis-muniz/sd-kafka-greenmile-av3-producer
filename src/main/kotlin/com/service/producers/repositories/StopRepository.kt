package com.service.producers.repositories


import com.service.producers.domain.Stop
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface StopRepository: ReactiveMongoRepository<Stop, Int>