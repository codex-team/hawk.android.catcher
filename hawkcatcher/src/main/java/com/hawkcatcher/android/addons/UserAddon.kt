package com.hawkcatcher.android.addons

interface UserAddon {
    val name: String
    fun provideData(): Map<String, Any>
}