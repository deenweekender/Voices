package com.voices.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.voices.core.common.util.SessionStore
import com.voices.core.database.ChatStore
import com.voices.core.network.NetworkFactory
import com.voices.feature.auth.AuthScreen
import com.voices.feature.auth.AuthViewModel
import com.voices.feature.chat.ChatScreen
import com.voices.feature.chat.NetworkChatSyncGateway
import com.voices.feature.chat.ChatViewModel
import com.voices.feature.history.HistoryScreen
import com.voices.feature.history.HistoryViewModel
import com.voices.feature.profile.ProfileScreen
import com.voices.feature.profile.ProfileViewModel
import com.voices.feature.settings.SettingsScreen
import com.voices.feature.settings.SettingsViewModel
import com.voices.ui.theme.VoicesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoicesTheme {
                VoicesNavGraph()
            }
        }
    }
}

private object Routes {
    const val Auth = "auth"
    const val Chat = "chat"
    const val History = "history"
    const val Settings = "settings"
    const val Profile = "profile"
}

@Composable
fun VoicesNavGraph(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val chatStore = remember { ChatStore.create(context.applicationContext) }
    val api = remember {
        NetworkFactory.create(
            baseUrl = BuildConfig.API_BASE_URL,
            tokenProvider = { SessionStore.userId.value },
        )
    }
    val syncGateway = remember { NetworkChatSyncGateway(api = api, store = chatStore) }

    NavHost(navController = navController, startDestination = Routes.Auth) {
        composable(Routes.Auth) {
            val viewModel: AuthViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()
            AuthScreen(
                state = state,
                onGoogleSignIn = viewModel::onGoogleSignIn,
                onAppleSignIn = viewModel::onAppleSignIn,
                onContinue = { navController.navigate(Routes.Chat) },
            )
        }
        composable(Routes.Chat) {
            val viewModel: ChatViewModel = viewModel(
                factory = ChatViewModel.factory(chatStore, syncGateway),
            )
            val state by viewModel.uiState.collectAsState()
            ChatScreen(
                state = state,
                onMessageChange = viewModel::onInputChanged,
                onSend = viewModel::sendMessage,
                onStop = viewModel::stopStreaming,
                onSync = viewModel::syncNow,
                onOpenHistory = { navController.navigate(Routes.History) },
                onOpenSettings = { navController.navigate(Routes.Settings) },
                onOpenProfile = { navController.navigate(Routes.Profile) },
                onModelSelect = viewModel::selectModel,
            )
        }
        composable(Routes.History) {
            val viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(chatStore))
            val state by viewModel.uiState.collectAsState()
            HistoryScreen(state = state, onBack = navController::popBackStack)
        }
        composable(Routes.Settings) {
            val viewModel: SettingsViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()
            SettingsScreen(
                state = state,
                onSetDefaultModel = viewModel::setDefaultModel,
                onBack = navController::popBackStack,
            )
        }
        composable(Routes.Profile) {
            val viewModel: ProfileViewModel = viewModel()
            val userId by viewModel.userId.collectAsState()
            val provider by viewModel.provider.collectAsState()
            ProfileScreen(
                userId = userId,
                provider = provider,
                onBack = navController::popBackStack,
            )
        }
    }
}
