package com.hawkcatcher.example

class SimpleUserManager {
    private lateinit var _name: String
    var name: String
        set(value) {
            _name = value
        }
        get() {
            return _name
        }

    fun isInitialize(): Boolean = ::_name.isInitialized
}