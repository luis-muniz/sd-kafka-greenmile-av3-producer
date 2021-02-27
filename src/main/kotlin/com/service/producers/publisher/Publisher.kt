package com.service.producers.publisher

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class Publisher(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun publicEventArrival(message:String): Mono<String> {
        this.kafkaTemplate.send("event-arrival", message);
        return Mono.just(message)
    }

    fun publicEventEndangeredEmployee(message:String): Mono<String> {
        this.kafkaTemplate.send("event-endangered-employee", message);
        return Mono.just(message)
    }
}