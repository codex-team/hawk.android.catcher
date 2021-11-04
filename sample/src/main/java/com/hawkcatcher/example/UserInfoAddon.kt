package com.hawkcatcher.example

import com.hawkcatcher.android.addons.UserAddon

class UserInfoAddon(private val userManager: SimpleUserManager) : UserAddon {
    override val name: String = "user"
    override fun provideData(): Map<String, Any> {
        return if (userManager.isInitialize()) {
            mapOf("name" to userManager.name)
        } else {
            emptyMap()
        }
    }

}