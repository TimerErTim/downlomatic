package eu.timerertim.downlomatic.api

import java.util.*

class APIPathArgument(
    val name: String, val getDefault: () -> Any = {
        throw MissingFormatArgumentException(name)
    }
) : CharSequence by name {

    override fun toString() = "{$name}"
}
