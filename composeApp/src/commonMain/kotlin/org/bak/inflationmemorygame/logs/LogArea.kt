package org.bak.inflationmemorygame.logs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.bak.inflationmemorygame.util.animationTrigger
import org.bak.inflationmemorygame.util.toVertical
import org.jetbrains.compose.resources.stringResource

@Composable
fun LogArea(modifier: Modifier = Modifier, state: LogState) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull() }
            .filterNotNull()
            .map { lastItem ->
                // 一番下にあるログが何番目かと、
                // それに対して一番上にあるログが映り切っているか
                val firstVisibleLog =
                    state.logs.first { log -> log.visibility != Logs.Visibility.Disappeared }
                val isLastItemHidden = lastItem.index < state.logs.lastIndex ||
                        lastItem.size + lastItem.offset >= lazyListState.layoutInfo.viewportSize.height
                firstVisibleLog to isLastItemHidden
            }
            .distinctUntilChanged()
            .collectLatest { (firstVisibleLog, isLastItemHidden) ->
                if (isLastItemHidden) {
                    firstVisibleLog.requestDismiss()
                }
            }
    }
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxHeight().background(color = Color.LightGray),
        reverseLayout = true,
        userScrollEnabled = false
    ) {
        items(items = state.logs, key = { it.instanceId }) { log ->
            // フェードイン・アウトはStateで制御して、完了が分かるようにしておく
            val animatedAlpha by animateFloatAsState(
                targetValue = when (log.visibility) {
                    Logs.Visibility.Appearing -> animationTrigger(from = 0f, to = 1f)
                    Logs.Visibility.Appeared -> 1f
                    else -> 0f
                }
            ) { alpha ->
                if (log.visibility == Logs.Visibility.Appearing && alpha == 1f) {
                    log.onShow()
                } else if (log.visibility == Logs.Visibility.Disappearing && alpha == 0f) {
                    log.onDismiss()
                }
            }
            val isVisible by remember {
                derivedStateOf { animatedAlpha > 0f }
            }
            AnimatedVisibility(
                visible = isVisible,
                modifier = Modifier
                    .animateItem()
                    .graphicsLayer { alpha = animatedAlpha }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                enter = slideIn { IntOffset(x = it.width, y = 0) }
            ) {
                LogItem(log = log)
            }
        }
    }
}

@Composable
fun LogItem(modifier: Modifier = Modifier, log: Logs) {
    Row(
        modifier = modifier
            .height(intrinsicSize = IntrinsicSize.Max)
            .background(color = log.color, shape = RoundedCornerShape(size = 8.dp))
            .padding(all = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .padding(all = 2.dp)
        ) {
            Text(
                text = stringResource(log.tag).toVertical(),
                modifier = Modifier.align(Alignment.Center),
                fontSize = 10.sp,
                lineHeight = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .padding(all = 2.dp)
        ) {
            Text(
                text = log.message,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}