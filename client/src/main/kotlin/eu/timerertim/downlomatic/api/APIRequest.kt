package eu.timerertim.downlomatic.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias SimpleAPIRequest<T> = APIRequest<T, T>

class APIRequest<I, O>(val path: APIPath, val transformer: (I) -> O) {

    var state: APIState<O> by mutableStateOf(APIState.Initial)
        private set

    var Companion._state
        get() = state
        set(value) {
            state = value
        }

    companion object {
        operator fun <T> invoke(path: APIPath): APIRequest<T, T> = APIRequest(path) { it }

        suspend inline fun <reified I, reified O> APIRequest<I, O>.executeRequest(
            vararg arguments: Pair<APIPathArgument, String>
        ) {
            val query = path.query(*arguments)
            with(this) {
                withContext(Dispatchers.Main) {
                    _state = APIState.Waiting
                }

                val newState = try {
                    val result = mockRequest(path, *arguments) as I //ktorClient.get(query)
                    APIState.Loaded(transformer(result))

                } catch (exception: Exception) {
                    APIState.Error(exception)
                }

                withContext(Dispatchers.Main) {
                    _state = newState
                }
            }
        }
    }
}
