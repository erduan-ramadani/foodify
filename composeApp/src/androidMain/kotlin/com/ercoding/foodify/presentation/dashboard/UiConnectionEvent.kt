package com.ercoding.foodify.presentation.dashboard

sealed class UiConnectionEvent {
    data object NoInternet : UiConnectionEvent()
    data object Timeout : UiConnectionEvent()
    data object UnknownError : UiConnectionEvent()
}