package com.solitrix.postureminder.shared.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.solitrix.postureminder.shared.generated.resources.Res
import com.solitrix.postureminder.shared.generated.resources.*
import com.solitrix.postureminder.shared.ui.viewmodel.Action
import com.solitrix.postureminder.shared.ui.viewmodel.AppScreen
import com.solitrix.postureminder.shared.ui.viewmodel.PostureViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostureApp(modifier: Modifier = Modifier) {
    val viewModel: PostureViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    when (state.screen) {
        AppScreen.Run -> StartScreen(
            state = state,
            onAction = viewModel::onAction,
            modifier = modifier,
        )
        AppScreen.Edit -> Column(modifier = modifier.fillMaxSize()) {
            Button(onClick = { viewModel.onAction(Action.NavigateToRun) }) {
                Text(stringResource(Res.string.btn_back))
            }
            PostureScreen(
                state = state,
                onAction = viewModel::onAction,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
