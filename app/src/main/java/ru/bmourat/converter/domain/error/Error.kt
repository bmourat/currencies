package ru.bmourat.converter.domain.error

sealed class Error {
    object NetworkConnection : Error()
    object InputFormat: Error()
}