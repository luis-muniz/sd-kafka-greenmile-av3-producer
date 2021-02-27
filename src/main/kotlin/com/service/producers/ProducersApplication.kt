package com.service.producers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ProducersApplication

fun main(args: Array<String>) {
	runApplication<ProducersApplication>(*args)
}
