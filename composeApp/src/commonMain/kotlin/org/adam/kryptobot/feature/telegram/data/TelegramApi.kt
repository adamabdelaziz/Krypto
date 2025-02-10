package org.adam.kryptobot.feature.telegram.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import org.adam.kryptobot.BuildConfig
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.Client.ExceptionHandler
import org.drinkless.tdlib.TdApi

interface TelegramApi {
    suspend fun initClient(apiId: Int = BuildConfig.TELEGRAM_API_ID, apiHash: String = BuildConfig.TELEGRAM_API_HASH): Flow<Boolean>

    suspend fun authenticateClient(phoneNumber: String): Flow<Boolean>
    suspend fun authenticateWithCode(code: String): Flow<Boolean>
    suspend fun authenticateWithPassword(password: String): Flow<Boolean>

    suspend fun getChannels()
    suspend fun getMessages()

    val authState: StateFlow<TdApi.AuthorizationState?>
    val channels: StateFlow<String>
    val messages: StateFlow<String>
}

class TelegramApiImpl() : TelegramApi, ExceptionHandler {
    private val client: Client = Client.create(
        { update -> onUpdate(update) },
        { exception -> Logger.d("Exception in TDLib: $exception") },
        { Logger.d("TDLib closed") }
    )

    private val _authStateFlow = MutableStateFlow<TdApi.AuthorizationState?>(null)
    override val authState: StateFlow<TdApi.AuthorizationState?> = _authStateFlow.asStateFlow()

    private val _channelsFlow = MutableStateFlow("")
    override val channels: StateFlow<String> = _channelsFlow.asStateFlow()

    private val _messagesFlow = MutableStateFlow("")
    override val messages: StateFlow<String> = _messagesFlow.asStateFlow()

    private fun onUpdate(update: TdApi.Object) {
        if (update is TdApi.UpdateAuthorizationState) {
            _authStateFlow.value = update.authorizationState
        }
        when (_authStateFlow.value) {
            is TdApi.AuthorizationStateWaitPhoneNumber -> {

            }
        }
    }

    override suspend fun initClient(apiId: Int, apiHash: String) = callbackFlow {
        val parameters = TdApi.SetTdlibParameters(
            false, // useTestDc
            "tdlib", // databaseDirectory
            null, // filesDirectory (set to null if your version does not require it)
            ByteArray(0), // databaseEncryptionKey (empty array for no encryption)
            true, // useFileDatabase
            true, // useChatInfoDatabase
            true, // useMessageDatabase
            true, // useSecretChats
            apiId, // apiId
            apiHash, // apiHash
            "en", // systemLanguageCode
            "KotlinClient", // deviceModel
            "1.0", // systemVersion
            "1.0", // applicationVersion
        )

        client.send(parameters) { obj ->
            when (obj) {
                is TdApi.Ok -> {
                    Logger.d("TDLib parameters set successfully")
                    trySend(true)
                }

                is TdApi.Error -> {
                    Logger.d("Error setting TDLib parameters: ${obj.message}")
                    trySend(false)
                }

                else -> Logger.d("Unexpected response: $obj")
            }
        }

        awaitClose { }
    }

    override suspend fun authenticateClient(phoneNumber: String) = callbackFlow {
        client.send(TdApi.SetAuthenticationPhoneNumber(phoneNumber, null)) { response ->
            when (response) {
                is TdApi.Ok -> {
                    Logger.d("Phone number sent, awaiting confirmation code")
                    trySend(true)
                }

                is TdApi.Error -> {
                    Logger.d("Error sending phone number: ${response.message}")
                    trySend(false)
                }
            }
        }
    }

    override suspend fun authenticateWithCode(code: String) = callbackFlow {
        client.send(TdApi.CheckAuthenticationCode(code)) { response ->
            when (response) {
                is TdApi.Ok -> {
                    Logger.d("Authenticated successfully")
                    trySend(true)
                }

                is TdApi.Error -> {
                    Logger.d("Error with authentication code: ${response.message}")
                    trySend(false)
                }
            }
        }

        awaitClose { }
    }

    override suspend fun authenticateWithPassword(password: String) = callbackFlow {
        client.send(TdApi.CheckAuthenticationPassword(password)) { response ->
            when (response) {
                is TdApi.Ok -> {
                    Logger.d("Password checked successfully")
                    trySend(true)
                }

                is TdApi.Error -> {
                    Logger.d("Error with password: ${response.message}")
                    trySend(false)
                }
            }
        }

        awaitClose { }
    }

    override suspend fun getChannels() {
        TODO("Not yet implemented")
    }

    override suspend fun getMessages() {
        TODO("Not yet implemented")
    }

    override fun onException(e: Throwable?) {
        Logger.e(e?.message ?: "Null TdLib Exception")
    }

}