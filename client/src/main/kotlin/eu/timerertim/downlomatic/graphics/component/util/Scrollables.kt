package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import eu.timerertim.downlomatic.graphics.window.sdp

private val scrollBarStyle
    @Composable get() = ScrollbarStyle(
        minimalHeight = 16.sdp,
        thickness = 6.sdp,
        shape = MaterialTheme.shapes.small,
        hoverDurationMillis = 300,
        unhoverColor = Color.Black.copy(alpha = 0.12f),
        hoverColor = Color.Black.copy(alpha = 0.50f)
    )

@Composable
fun ScrollableLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    content: LazyListScope.() -> Unit
) {
    val (size, setSize) = remember { mutableStateOf(IntSize.Zero) }
    Box(modifier) {
        LazyColumn(
            Modifier.onSizeChanged(setSize),
            state,
            contentPadding,
            reverseLayout,
            verticalArrangement,
            horizontalAlignment,
            flingBehavior,
            content
        )
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).height(size.height.dp),
            style = scrollBarStyle,
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}

@Composable
fun ScrollableLazyRow(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal =
        if (!reverseLayout) Arrangement.Start else Arrangement.End,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    content: LazyListScope.() -> Unit
) {
    val (size, setSize) = remember { mutableStateOf(IntSize.Zero) }
    Box(modifier) {
        LazyRow(
            Modifier.onSizeChanged(setSize),
            state,
            contentPadding,
            reverseLayout,
            horizontalArrangement,
            verticalAlignment,
            flingBehavior,
            content
        )
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomCenter).width(size.width.dp),
            style = scrollBarStyle,
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}

@Composable
fun ScrollableColumn(
    modifier: Modifier = Modifier,
    state: ScrollState = rememberScrollState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val (size, setSize) = remember { mutableStateOf(IntSize.Zero) }
    Box(modifier) {
        Column(
            Modifier.verticalScroll(state).onSizeChanged(setSize),
            verticalArrangement,
            horizontalAlignment,
            content
        )

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).height(size.height.dp),
            style = scrollBarStyle,
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}

@Composable
fun ScrollableRow(
    modifier: Modifier = Modifier,
    state: ScrollState = rememberScrollState(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    val (size, setSize) = remember { mutableStateOf(IntSize.Zero) }
    Box(modifier) {
        Row(
            Modifier.horizontalScroll(state).onSizeChanged(setSize),
            horizontalArrangement,
            verticalAlignment,
            content
        )

        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomCenter).width(size.width.dp),
            style = scrollBarStyle,
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}
