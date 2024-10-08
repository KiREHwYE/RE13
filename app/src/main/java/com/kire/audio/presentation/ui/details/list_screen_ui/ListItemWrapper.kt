package com.kire.audio.presentation.ui.details.list_screen_ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.Divider
import com.kire.audio.presentation.ui.details.common.MediaControls
import com.kire.audio.presentation.ui.details.common.SliderBlock
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import kotlinx.coroutines.flow.StateFlow

/**
 * Обертка для ListItem, раскрывает панель с дополнительным функционалом вокруг ListItem
 *
 * @param track трек, который соответствует данному ListItem в списке
 * @param trackState состояние воспроизведения
 * @param mediaController для управления воспроизведением
 * @param modifier модификатор
 * @param showBottomBar действие при скрытии ListItemWrapper из поля зрения
 * @param goToPlayerScreen навигация на PlayerScreen
 * @param listItem сам ListItem
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun ListItemWrapper(
    id: String,
    trackState: StateFlow<TrackState>,
    mediaController: MediaController?,
    modifier: Modifier = Modifier,
    showBottomBar: (Boolean) -> Unit = {},
    goToPlayerScreen: () -> Unit = {},
    listItem: @Composable (Modifier, () -> Unit) -> Unit,
) {

    val trackState by trackState.collectAsStateWithLifecycle()

    /** Флаг клика на ListItem */
    var isClicked by rememberSaveable {
        mutableStateOf(false)
    }

    /** Определяет какой трек сейчас играет,
     * то есть должен отрисовывать обертку вокруг себя.
     * Опускает PlayerBottomBar, если обертка в пределах экрана.
     * */
    LaunchedEffect(trackState.currentTrackPlaying?.id) {
        isClicked = (trackState.currentTrackPlaying?.id == id)
            .also { if (it) showBottomBar(false) }
    }

     /** При исчезновении из поля зрения / закрытия ListItemWrapper скрывает PlayerBottomBar */
    DisposableEffect(Unit) {
        onDispose {
            if (isClicked)
                showBottomBar(true)
        }
    }

    /** Степень прозрачности фона */
    val backgroundAlpha by animateFloatAsState(targetValue = if (isClicked) 1f else 0f)

    /** Отступ от границы обертки внутреннего контента*/
    val padFromBorders by animateDpAsState(targetValue = if (isClicked) Dimens.universalPad else 0.dp)

    Column(
        modifier = modifier
            .wrapContentSize()
            .background(
                color = AudioExtendedTheme.extendedColors.controlElementsBackground.copy(alpha = backgroundAlpha),
                shape = RoundedCornerShape(Dimens.universalRoundedCorner)
            )
            .padding(padFromBorders)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            /** Базовый компонент, представляющий отдельный трек его обложкой, названием и иссполнителем */
            listItem(
                Modifier
                    .weight(1f)
                    .padding(end = Dimens.universalPad)
            ) { isClicked = !isClicked }

            /** Кнопки управления воспроизведением */
            AnimatedVisibility(visible = isClicked) {
                MediaControls(
                    trackState = trackState,
                    mediaController = mediaController,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy)
                )
            }
        }

        AnimatedVisibility(visible = isClicked) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.sliderAndDividerSpacedBy)
            ) {
                /** Слайдер для перемотки трека */
                SliderBlock(mediaController = mediaController)

                /** Служит как кнопка для открытия экрана плеера */
                Divider(
                    color = AudioExtendedTheme.extendedColors.sliderDurationAndDivider,
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures {
                                goToPlayerScreen()
                            }
                        }
                )
            }
        }
    }
}