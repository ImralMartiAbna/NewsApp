package com.example.newsapp.domain.usecase

import kotlin.random.Random

class RequestOtpUseCase {

    fun execute(): String {
        return Random.nextInt(
            from = 100000,
            until = 1000000
        ).toString()
    }
}