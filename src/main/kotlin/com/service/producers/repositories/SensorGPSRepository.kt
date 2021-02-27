package com.service.producers.repositories

import com.service.producers.domain.SensorGPS
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface SensorGPSRepository: ReactiveMongoRepository<SensorGPS, Int>