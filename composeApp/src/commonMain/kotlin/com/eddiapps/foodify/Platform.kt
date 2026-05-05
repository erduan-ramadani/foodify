package com.eddiapps.foodify

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform