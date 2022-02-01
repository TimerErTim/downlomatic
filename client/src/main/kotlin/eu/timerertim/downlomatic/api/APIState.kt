package eu.timerertim.downlomatic.api

sealed class APIState<out T> {
    object Waiting : APIState<Nothing>() {
        override fun toString(): String {
            return "Waiting"
        }
    }

    object Initial : APIState<Nothing>() {
        override fun toString(): String {
            return "Initial"
        }
    }

    class Error(val exception: Exception) : APIState<Nothing>() {
        override fun toString(): String {
            return "Error(exception=$exception)"
        }
    }

    class Loaded<T>(val payload: T) : APIState<T>() {
        override fun toString(): String {
            return "Loaded(payload=$payload)"
        }
    }
}
