package com.example

import android.app.Application
import android.content.Context
import com.example.di.AppContainer

class SubscriptionApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}

val Context.appContainer: AppContainer
    get() = (applicationContext as SubscriptionApp).container
