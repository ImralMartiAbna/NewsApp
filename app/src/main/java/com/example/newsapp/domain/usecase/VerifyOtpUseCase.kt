package com.example.newsapp.domain.usecase

class VerifyOtpUseCase {

    fun execute(
        inputOtp: String,
        generatedOtp: String
    ): Boolean {
        return inputOtp == generatedOtp
    }
}