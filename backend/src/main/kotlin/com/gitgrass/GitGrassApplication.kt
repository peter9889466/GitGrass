package com.gitgrass

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class GitGrassApplication

fun main(args: Array<String>) {
    runApplication<GitGrassApplication>(*args)
}
