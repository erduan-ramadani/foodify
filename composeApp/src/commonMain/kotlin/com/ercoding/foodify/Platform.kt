package com.ercoding.foodify

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform