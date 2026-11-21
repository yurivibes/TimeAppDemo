package com.blackjackfruit.timeappdemo.time

/**
 * The TemperatureSensor hub the app reads the time from.
 *
 * Same fixed address as TemperatureSensor-App's `AppConstants.Hub.DEFAULT_ADDRESS`
 * until the hub gets a stable address or the app discovers it.
 */
object Hub {
    const val DEFAULT_ADDRESS = "192.168.125.6"
    const val DEFAULT_PORT = 80
    const val TIME_ENDPOINT = "/time"

    val timeUrl: String get() = "http://$DEFAULT_ADDRESS:$DEFAULT_PORT$TIME_ENDPOINT"
}
