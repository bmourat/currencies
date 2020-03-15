package ru.bmourat.converter.domain

sealed class Failure {
    object NetworkConnection : Failure()
    abstract class FeatureFailure: Failure()
}