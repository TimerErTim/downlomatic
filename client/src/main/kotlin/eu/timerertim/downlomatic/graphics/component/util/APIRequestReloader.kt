package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import eu.timerertim.downlomatic.api.APIPathArgument
import eu.timerertim.downlomatic.api.APIRequest
import eu.timerertim.downlomatic.api.APIRequest.Companion.executeRequest
import eu.timerertim.downlomatic.api.APIState
import eu.timerertim.downlomatic.graphics.theme.icons
import eu.timerertim.downlomatic.graphics.window.sdp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
inline fun <reified I, reified O> APIRequestReloader(
    request: APIRequest<I, O>,
    size: Dp,
    modifier: Modifier = Modifier,
    crossinline parameters: () -> List<Pair<APIPathArgument, Any>> = { emptyList() }
) {
    when (request.state) {
        is APIState.Error, is APIState.Loaded -> Icon(
            MaterialTheme.icons.Refresh, "Reload",
            modifier = modifier then Modifier.size(size).clip(CircleShape)
                .clickable {
                    CoroutineScope(Dispatchers.IO).launch {
                        request.executeRequest(*(parameters().toTypedArray()))
                    }
                }
        )
        is APIState.Waiting -> CircularProgressIndicator(strokeWidth = 1.sdp, modifier = Modifier.size(size))
        else -> {}
    }
}
