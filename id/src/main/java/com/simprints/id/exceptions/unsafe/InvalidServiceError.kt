package com.simprints.id.exceptions.unsafe


class InvalidServiceError(msg: String): Error(msg) {

    companion object {

        fun <T> forService(serviceClass: Class<T>) =
                InvalidServiceError("Cannot resolve service ${serviceClass.simpleName}.")

    }

}