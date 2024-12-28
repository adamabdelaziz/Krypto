package org.adam.kryptobot.feature.swapper.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.adam.kryptobot.util.SECOND_WALLET_PUBLIC_KEY
import org.adam.kryptobot.util.decodeBase58
import org.adam.kryptobot.util.encodeBase58
import org.sol4k.Base58
import org.sol4k.Connection
import org.sol4k.Keypair
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.VersionedTransaction
import org.sol4k.api.Commitment
import org.sol4k.api.TransactionSimulationError
import org.sol4k.api.TransactionSimulationSuccess
import java.math.BigInteger
import kotlin.io.encoding.ExperimentalEncodingApi
import org.adam.kryptobot.util.SECOND_WALLET_PRIVATE_KEY

interface SolanaApi {

    fun getWalletBalance(
        walletKey: String,
        rpcUrl: RpcUrl = RpcUrl.MAINNNET,
        commitment: Commitment = Commitment.CONFIRMED
    ): BigInteger

    fun performSwapTransaction(
        privateKey: String,
        instructions: String,
        rpcUrl: RpcUrl = RpcUrl.MAINNNET,
        commitment: Commitment = Commitment.FINALIZED,
        simulation: Boolean = false,
    )

    fun getMintDecimalsAmount(address: String): Int

    suspend fun getTokenBalances(publicKey: String): List<Pair<String, Double>>
}

/**
 * ATM Sol4k can't do everything, so some "custom" Solana API functions are also used.
 * @see getTokenBalances
 */
class SolanaApiImpl(private val client: HttpClient) : SolanaApi {

    //TODO: Seems to only be necessary if we want to get the private key from the public key or for signing for transactions(which means it wouldnt need to be public)
    private fun getWalletKeypair(privateKey: String): Keypair {
        val wallet = Keypair.fromSecretKey(Base58.decode(privateKey))
        Logger.d("Public Key ${wallet.publicKey} private key $privateKey")
        Logger.d("Encoded Private ${wallet.secret.encodeBase58()}")
        return wallet
    }

    override fun getWalletBalance(
        walletKey: String,
        rpcUrl: RpcUrl,
        commitment: Commitment
    ): BigInteger {
        val connection = Connection(rpcUrl, commitment)
        val key = PublicKey(walletKey)
        val balance = connection.getBalance(key)
        Logger.d("Balance is $balance")
        return balance
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun performSwapTransaction(
        privateKey: String,
        instructions: String,
        rpcUrl: RpcUrl,
        commitment: Commitment,
        simulation: Boolean,
    ) {
        val solanaClient = Connection(rpcUrl, commitment)

        val balance = getWalletBalance(SECOND_WALLET_PUBLIC_KEY)
        Logger.d("Balance is $balance")

        val hash = solanaClient.getLatestBlockhashExtended(commitment).blockhash
        val valid = solanaClient.isBlockhashValid(hash, commitment)
        Logger.d("Hash valid: $valid")

        val transaction = VersionedTransaction.from(instructions)
        val keypair = Keypair.fromSecretKey(privateKey.decodeBase58())

        transaction.sign(keypair)
        Logger.d("Signed transaction")

        try {
            if (simulation) {
                when (val response = solanaClient.simulateTransaction(transaction)) {
                    is TransactionSimulationError -> {
                        Logger.d("Simulation error ${response.error}")
                    }

                    is TransactionSimulationSuccess -> {
                        response.logs.forEach {
                            Logger.d("Simulation log: $it")
                        }
                    }
                }
            } else {
                val transactionSignature = solanaClient.sendTransaction(transaction)
                Logger.d("Transaction successfully sent! Signature: $transactionSignature")
            }
        } catch (e: Exception) {
            Logger.d("Transaction failed: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun getMintDecimalsAmount(address: String): Int {
        val solanaClient = Connection(RpcUrl.MAINNNET, Commitment.CONFIRMED)
        return solanaClient.getTokenSupply(address).decimals
    }

    override suspend fun getTokenBalances(publicKey: String): List<Pair<String, Double>> {
        val returnList = mutableListOf<Pair<String, Double>>()
        val requestBody = """
        {
            "jsonrpc": "2.0",
            "id": 1,
            "method": "getTokenAccountsByOwner",
            "params": [
                "$publicKey",
                { "programId": "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA" },
                { "encoding": "jsonParsed" }
            ]
        }
    """

        try {
            val response: HttpResponse = client.post(RpcUrl.MAINNNET.value) {
                setBody(requestBody)
                headers { append("Content-Type", "application/json") }
            }

            val responseBody: JsonObject =
                Json.parseToJsonElement(response.body<String>()).jsonObject

            val tokenAccounts = responseBody["result"]
                ?.jsonObject?.get("value")
                ?.jsonArray

            tokenAccounts?.forEach { account ->
                val accountInfo = account.jsonObject["account"]
                    ?.jsonObject?.get("data")
                    ?.jsonObject?.get("parsed")
                    ?.jsonObject?.get("info")
                    ?.jsonObject

                val mint = accountInfo?.get("mint")?.jsonPrimitive?.content
                val balance = accountInfo?.get("tokenAmount")
                    ?.jsonObject?.get("uiAmount")
                    ?.jsonPrimitive?.double

                if (mint != null && balance != null) returnList.add(mint to balance)
            }
        } catch (e: Exception) {
            Logger.d("Error fetching token balances: ${e.message}")
        }

        return returnList
    }

//    companion object {
//        const val SPL_PROGRAM_ID = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"
//    }

}