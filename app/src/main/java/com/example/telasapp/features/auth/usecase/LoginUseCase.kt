package com.example.telasapp.features.auth.usecase

import com.example.telasapp.features.auth.data.models.LoginResponse
import com.example.telasapp.features.auth.data.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String): Result<LoginResponse> {
        // Aquí podrías validar antes de llamar al repo
        if (email.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Campos vacíos"))
        }
        return repository.login(email, pass)
    }
}