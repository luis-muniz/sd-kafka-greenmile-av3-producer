package com.service.producers.repositories

import com.service.producers.domain.Telephone
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface TelephoneRepository: ReactiveMongoRepository<Telephone,Int>