package com.jh.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.FillBounds
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jh.murun.R
import com.jh.presentation.base.BaseActivity
import com.jh.presentation.enums.CadenceType.*
import com.jh.presentation.ui.BorderedRoundedCornerButton
import com.jh.presentation.ui.LoadingScreen
import com.jh.presentation.ui.clickableWithoutRipple
import com.jh.presentation.ui.main.MainUiEvent.*
import com.jh.presentation.ui.main.favorite.FavoriteActivity
import com.jh.presentation.ui.repeatOnStarted
import com.jh.presentation.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComposeUi {
            MainActivityContent(viewModel = viewModel)
        }

        repeatOnStarted {
            viewModel.sideEffectChannelFlow.collectLatest { sideEffect ->
                when(sideEffect) {
                    is MainSideEffect.GoToFavorite -> {
                        startActivity(FavoriteActivity.newIntent(this@MainActivity))
                    }
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainActivityContent(
    viewModel: MainViewModel
) {
    with(viewModel.state.collectAsStateWithLifecycle().value) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .blur(
                            radiusX = 2.dp,
                            radiusY = 2.dp
                        ),
                    painter = if (image != null) BitmapPainter(image) else painterResource(id = R.drawable.music_default),
                    contentDescription = "songInfoBackground",
                    contentScale = Crop,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(color = DarkFilter1)
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(
                            vertical = 24.dp,
                            horizontal = 24.dp
                        )
                        .fillMaxWidth()
                ) {
                    Box {
                        Image(
                            modifier = Modifier
                                .clip(shape = Shapes.large)
                                .size(120.dp),
                            painter = if (image != null) BitmapPainter(image) else painterResource(id = R.drawable.music_default),
                            contentDescription = "albumCover",
                            contentScale = if (image != null) FillBounds else Crop
                        )

                        Text(
                            modifier = Modifier.align(Center),
                            text = "No Music",
                            style = Typography.body1,
                            color = Gray0
                        )
                    }


                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .height(120.dp),
                        verticalArrangement = SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = title,
                                style = Typography.h3,
                                color = Color.White
                            )

                            Text(
                                text = artist,
                                style = Typography.body1,
                                color = Gray0
                            )
                        }

                        Text(
                            text = "$bpm BPM",
                            style = Typography.h4,
                            color = MainColor
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(top = 168.dp)
                    .align(BottomCenter)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        )
                    )
                    .fillMaxSize()
                    .background(color = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 36.dp)
                        .height(48.dp)
                        .align(CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(36.dp)
                ) {
                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickSkipToPrev() },
                        painter = painterResource(id = R.drawable.ic_skip_prev),
                        contentDescription = "skipToPrevIcon",
                        tint = Color.LightGray
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickPlayOrPause() },
                        painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                        contentDescription = "playOrPauseIcon",
                        tint = MainColor
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickSkipToNext() },
                        painter = painterResource(id = R.drawable.ic_skip_next),
                        contentDescription = "skipToNextIcon",
                        tint = Color.LightGray
                    )

                    Icon(
                        modifier = Modifier.clickableWithoutRipple { viewModel.onClickRepeatOne() },
                        painter = painterResource(id = R.drawable.ic_repeat_one),
                        contentDescription = "repeatIcon",
                        tint = if (isRepeatingOne) MainColor else Color.LightGray
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = SpaceBetween
                ) {
                    val cadenceSettingAlphaState = animateFloatAsState(
                        targetValue = if (isRunning) 0.3f else 1f,
                        animationSpec = tween(durationMillis = 500)
                    )

                    Column(
                        modifier = Modifier.alpha(cadenceSettingAlphaState.value),
                    ) {
                        val cadenceTrackingColorState = animateColorAsState(targetValue = if (cadenceType == TRACKING) MainColor else Color.LightGray)
                        val cadenceAssignColorState = animateColorAsState(targetValue = if (cadenceType == ASSIGN) MainColor else Color.LightGray)

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BorderedRoundedCornerButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                borderColor = cadenceTrackingColorState.value,
                                backgroundColor = Color.White,
                                text = "케이던스 트래킹",
                                textColor = cadenceTrackingColorState.value,
                                onClick = { viewModel.onClickTrackCadence() }
                            )

                            BorderedRoundedCornerButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                borderColor = cadenceAssignColorState.value,
                                backgroundColor = Color.White,
                                text = "케이던스 입력",
                                textColor = cadenceAssignColorState.value,
                                onClick = { viewModel.onClickAssignCadence() }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 12.dp
                                )
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .border(
                                        shape = Shapes.large,
                                        width = 1.dp,
                                        color = cadenceTrackingColorState.value,
                                    )
                                    .weight(1f)
                                    .height(200.dp)
                            ) {
                                Text(
                                    modifier = Modifier.align(Center),
                                    text = "$cadence",
                                    style = Typography.h5,
                                    color = cadenceTrackingColorState.value,
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .border(
                                        shape = Shapes.large,
                                        width = 1.dp,
                                        color = cadenceAssignColorState.value,
                                    )
                                    .weight(1f)
                                    .height(200.dp)
                            ) {
                                val focusManager = LocalFocusManager.current
                                val cadenceAssignTextState = remember { mutableStateOf("") }

                                CompositionLocalProvider(
                                    LocalTextSelectionColors.provides(
                                        TextSelectionColors(
                                            handleColor = MainColor,
                                            backgroundColor = Gray0
                                        )
                                    )
                                ) {
                                    TextField(
                                        modifier = Modifier.align(Center),
                                        value = cadenceAssignTextState.value,
                                        onValueChange = { cadenceAssignTextState.value = it },
                                        placeholder = {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = "입력",
                                                style = Typography.h6,
                                                color = cadenceAssignColorState.value,
                                            )
                                        },
                                        textStyle = Typography.h6,
                                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                        singleLine = true,
                                        colors = TextFieldDefaults.textFieldColors(
                                            textColor = MainColor,
                                            backgroundColor = Color.White,
                                            cursorColor = MainColor,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                        ),
                                        enabled = cadenceType == ASSIGN
                                    )
                                }
                            }
                        }
                    }

                    val buttonTextColorState = animateColorAsState(targetValue = if (isRunning) Red else Color.White)
                    val buttonBackgroundColorState = animateColorAsState(targetValue = if (isRunning) Color.White else MainColor)
                    val buttonBorderColorState = animateColorAsState(targetValue = if (isRunning) Red else MainColor)

                    Box {
                        BorderedRoundedCornerButton(
                            modifier = Modifier
                                .padding(all = 12.dp)
                                .fillMaxWidth()
                                .height(48.dp)
                                .align(BottomCenter)
                                .combinedClickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = {
                                        if (!isRunning) {
                                            viewModel.onClickStartOrStopRunning()
                                        }
                                    },
                                    onLongClick = {
                                        if (isRunning) {
                                            viewModel.onClickStartOrStopRunning()
                                        }
                                    }
                                ),
                            borderColor = buttonBorderColorState.value,
                            backgroundColor = buttonBackgroundColorState.value,
                            text = if (isRunning) "길게 눌러 러닝 종료" else "러닝 시작",
                            textColor = buttonTextColorState.value
                        )

                        if (!isRunning) {
                            FloatingActionButton(
                                modifier = Modifier
                                    .padding(
                                        end = 24.dp,
                                        bottom = 48.dp
                                    )
                                    .size(48.dp)
                                    .align(BottomEnd),
                                onClick = { viewModel.onClickFavorite() }) {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Center),
                                    painter = painterResource(id = R.drawable.ic_favorite),
                                    contentDescription = "favoriteIcon",
                                    tint = MainColor
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                LoadingScreen()
            }
        }
    }
}
