package com.scherule.users.management

data class SystemUser(val id: Long = -1) {

    companion object {
        fun get(): SystemUser {
            return SystemUser()
        }
    }

}