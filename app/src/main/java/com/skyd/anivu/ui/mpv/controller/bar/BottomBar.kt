package com.skyd.anivu.ui.mpv.controller.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ClosedCaption
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct
import com.skyd.anivu.R
import com.skyd.anivu.ext.alwaysLight
import com.skyd.anivu.ui.mpv.controller.ControllerBarGray
import com.skyd.anivu.ui.mpv.controller.state.PlayState
import com.skyd.anivu.ui.mpv.controller.state.PlayStateCallback
import kotlin.math.abs


@Immutable
data class BottomBarCallback(
    val onAudioTrackClick: () -> Unit,
    val onSubtitleTrackClick: () -> Unit,
)

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    playState: () -> PlayState,
    playStateCallback: PlayStateCallback,
    bottomBarCallback: BottomBarCallback,
    onRestartAutoHideControllerRunnable: () -> Unit,
) {
    val playPositionStateValue = playState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, ControllerBarGray)
                )
            )
            .windowInsetsPadding(
                WindowInsets.displayCutout.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .padding(top = 30.dp)
            .padding(horizontal = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val sliderInteractionSource = remember { MutableInteractionSource() }
            var sliderValue by rememberSaveable {
                mutableFloatStateOf(playPositionStateValue.currentPosition.toFloat())
            }
            var valueIsChanging by rememberSaveable { mutableStateOf(false) }
            if (!valueIsChanging && !playPositionStateValue.isSeeking &&
                sliderValue != playPositionStateValue.currentPosition.toFloat()
            ) {
                sliderValue = playPositionStateValue.currentPosition.toFloat()
            }
            Text(
                text = playPositionStateValue.currentPosition.toDurationString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
            )
            Slider(
                modifier = Modifier
                    .padding(6.dp)
                    .height(10.dp)
                    .weight(1f),
                value = sliderValue,
                onValueChange = {
                    valueIsChanging = true
                    onRestartAutoHideControllerRunnable()
                    sliderValue = it
                },
                onValueChangeFinished = {
                    playStateCallback.onSeekTo(sliderValue.toInt())
                    valueIsChanging = false
                },
                valueRange = 0f..playPositionStateValue.duration.toFloat(),
                interactionSource = sliderInteractionSource,
                thumb = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .clip(CircleShape)
                                .size(14.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary
                                        .alwaysLight(true)
                                        .toHct()
                                        .withTone(90.0)
                                        .toColor()
                                )
                        )
                    }
                },
                track = {
                    Spacer(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(SliderDefaults.colors().activeTrackColor)
                    )
                },
            )
            Text(
                text = playPositionStateValue.duration.toDurationString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
                    .clickable(onClick = playStateCallback.onPlayStateChanged)
                    .padding(7.dp),
                imageVector = if (playPositionStateValue.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = stringResource(if (playPositionStateValue.isPlaying) R.string.pause else R.string.play),
            )

            Spacer(modifier = Modifier.weight(1f))

            BarIconButton(
                onClick = bottomBarCallback.onAudioTrackClick,
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = stringResource(R.string.player_audio_track),
            )
            BarIconButton(
                onClick = bottomBarCallback.onSubtitleTrackClick,
                imageVector = Icons.Rounded.ClosedCaption,
                contentDescription = stringResource(R.string.player_subtitle_track),
            )
        }
    }
}

fun Int.toDurationString(sign: Boolean = false, splitter: String = ":"): String {
    if (sign) return (if (this >= 0) "+" else "-") + abs(this).toDurationString()

    val hours = this / 3600
    val minutes = this % 3600 / 60
    val seconds = this % 60
    return if (hours == 0) "%02d$splitter%02d".format(minutes, seconds)
    else "%d$splitter%02d$splitter%02d".format(hours, minutes, seconds)
}