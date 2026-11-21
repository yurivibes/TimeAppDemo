package com.blackjackfruit.timeappdemo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform