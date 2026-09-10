package com.example.turfbook

import android.app.Application
import com.example.turfbook.data.local.TurfDatabase
import com.example.turfbook.data.repository.TurfRepository

class TurfApplication : Application() {
    val database: TurfDatabase by lazy { TurfDatabase.getDatabase(this) }
    val repository: TurfRepository by lazy { TurfRepository(database.turfDao()) }
}
