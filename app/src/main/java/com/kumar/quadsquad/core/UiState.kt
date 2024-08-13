package com.kumar.quadsquad.core

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    class Success<T>(val data: T) : UiState<T>
    class Error(val message: String): UiState<Nothing>
}