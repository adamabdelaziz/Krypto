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
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.adam.kryptobot.BuildConfig.HELIUS_URL
import org.adam.kryptobot.util.SECOND_WALLET_PRIVATE_KEY
import org.adam.kryptobot.util.SOLANA_MINT_ADDRESS
import org.adam.kryptobot.util.decodeBase58
import org.adam.kryptobot.util.encodeBase58
import org.drinkless.tdlib.TdApi.StarTransactionTypeTelegramApiUsage
import org.sol4k.AccountMeta
import org.sol4k.Base58
import org.sol4k.Connection
import org.sol4k.Constants.ASSOCIATED_TOKEN_PROGRAM_ID
import org.sol4k.Constants.COMPUTE_BUDGET_PROGRAM_ID
import org.sol4k.Constants.SYSTEM_PROGRAM
import org.sol4k.Constants.SYSVAR_RENT_ADDRESS
import org.sol4k.Constants.TOKEN_PROGRAM_ID
import org.sol4k.Keypair
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.Transaction
import org.sol4k.VersionedTransaction
import org.sol4k.api.Commitment
import org.sol4k.instruction.BaseInstruction
import org.sol4k.instruction.Instruction
import java.math.BigInteger
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface SolanaApi {
    fun getWalletBalance(
        walletKey: String,
        rpcUrl: String = HELIUS_URL,
        commitment: Commitment = Commitment.CONFIRMED
    ): BigInteger

    suspend fun createATAForMint(
        rpcUrl: String = HELIUS_URL,
        ownerWalletAddress: String,
        mintAddress: String = SOLANA_MINT_ADDRESS
    )

    fun performSwapTransaction(
        privateKey: String,
        instructions: String,
        rpcUrl: String = HELIUS_URL,
        commitment: Commitment = Commitment.FINALIZED,
    ): Result<String>

    fun getMintDecimalsAmount(address: String): Int

    suspend fun getTokenBalances(publicKey: String): List<Pair<String, Double>>
    suspend fun checkTokenValidity(mintAddress: String, rpcUrl: String = HELIUS_URL): Boolean
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
        rpcUrl: String,
        commitment: Commitment
    ): BigInteger {
        val connection = Connection(rpcUrl, commitment)
        val key = PublicKey(walletKey)
        val balance = connection.getBalance(key)
        //Logger.d("Balance is $balance")
        return balance
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun createATAForMint(rpcUrl: String, ownerWalletAddress: String, mintAddress: String) {
        val solanaClient = Connection(rpcUrl, Commitment.CONFIRMED)
        val mintKey = PublicKey(mintAddress)
        val ownerPublicKey = PublicKey(ownerWalletAddress)

        val ataAddress = PublicKey.findProgramDerivedAddress(ownerPublicKey, mintKey)

        val accountInfo = solanaClient.getAccountInfo(ataAddress.publicKey)

        if (accountInfo == null) {
            try {
                val createAccountInstruction = createAssociatedTokenAccountInstruction(
                    ownerPublicKey,
                    ownerPublicKey,
                    mintKey
                )

                val recentBlockhash = solanaClient.getLatestBlockhashExtended(Commitment.FINALIZED).blockhash
                val isValid = solanaClient.isBlockhashValid(recentBlockhash, Commitment.CONFIRMED)
                Logger.d("Hash valid $isValid")

                val transaction = Transaction(
                    recentBlockhash = recentBlockhash,
                    instructions = listOf(createAccountInstruction),
                    feePayer = ownerPublicKey
                )

                val serializedTransaction = transaction.serialize()
                val base64EncodedTransaction = Base64.encode(serializedTransaction)
                val versionedTransaction = VersionedTransaction.from(base64EncodedTransaction)

                val keypair = Keypair.fromSecretKey(SECOND_WALLET_PRIVATE_KEY.decodeBase58())
                versionedTransaction.sign(keypair)

                val signature = solanaClient.sendTransaction(versionedTransaction)
                Logger.d("ATA created for wSOL at $ataAddress signature $signature")
            } catch (e: Exception) {
                Logger.e("Create ATA Exception: ${e.message}")
            }
        } else {
            Logger.d("ATA already exists for wSOL at $ataAddress")
        }
    }

    private fun requestUnits(units: Int): BaseInstruction {
        val data = ByteArray(4) // Represent the compute units as a byte array
        data[0] = (units shr 24).toByte()
        data[1] = (units shr 16).toByte()
        data[2] = (units shr 8).toByte()
        data[3] = units.toByte()

        return BaseInstruction(
            programId = COMPUTE_BUDGET_PROGRAM_ID,
            keys = listOf(),
            data = data
        )
    }

    private fun createAssociatedTokenAccountInstruction(
        payer: PublicKey,
        owner: PublicKey,
        mint: PublicKey
    ): BaseInstruction {
        val associatedTokenAddress = PublicKey.findProgramAddress(
            listOf(owner, TOKEN_PROGRAM_ID, mint),
            ASSOCIATED_TOKEN_PROGRAM_ID
        ).publicKey

        val keys = listOf(
            AccountMeta(payer, signer = true, writable = true),
            AccountMeta(associatedTokenAddress, signer = false, writable = true),
            AccountMeta(owner, signer = false, writable = false),
            AccountMeta(mint, signer = false, writable = false),
            AccountMeta(SYSTEM_PROGRAM, signer = false, writable = false),
            AccountMeta(TOKEN_PROGRAM_ID, signer = false, writable = false),
            AccountMeta(SYSVAR_RENT_ADDRESS, signer = false, writable = false)
        )

        val data = ByteArray(0)

        return BaseInstruction(
            programId = ASSOCIATED_TOKEN_PROGRAM_ID,
            keys = keys,
            data = data
        )
    }

    /*
      This is the one that works
   */
    override fun performSwapTransaction(
        privateKey: String,
        instructions: String,
        rpcUrl: String,
        commitment: Commitment,
    ): Result<String> {
        val solanaClient = Connection(rpcUrl, commitment)
        val hash = solanaClient.getLatestBlockhashExtended(commitment).blockhash
        val valid = solanaClient.isBlockhashValid(hash, commitment)

        val transaction = VersionedTransaction.from(instructions)
        val keypair = Keypair.fromSecretKey(privateKey.decodeBase58())

        transaction.sign(keypair)
        Logger.d("Signed transaction")

        return try {
            val transactionSignature = solanaClient.sendTransaction(transaction)
            Logger.d("Transaction successfully sent! Signature: $transactionSignature")
            Result.success(transactionSignature)
        } catch (e: Exception) {
            Logger.d("Transaction failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
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

    override suspend fun checkTokenValidity(mintAddress: String, rpcUrl: String): Boolean {
        val requestBody = """
        {
            "jsonrpc": "2.0",
            "id": 1,
            "method": "getAccountInfo",
            "params": [
                "$mintAddress",
                { "encoding": "jsonParsed" }
            ]
        }
    """

        return try {
            val response: HttpResponse = client.post(rpcUrl) {
                setBody(requestBody)
                headers { append("Content-Type", "application/json") }
            }

            val responseBody: JsonObject =
                Json.parseToJsonElement(response.body<String>()).jsonObject

            val value = responseBody["result"]
                ?.jsonObject?.get("value")

            if (value == null) {
                Logger.d("Token $mintAddress is NOT valid (null response).")
                return false
            }

            val data = value.jsonObject["data"]
                ?.jsonObject?.get("parsed")
                ?.jsonObject?.get("info")

            val decimals = data?.jsonObject?.get("decimals")?.jsonPrimitive?.intOrNull
            val supply = data?.jsonObject?.get("supply")?.jsonPrimitive?.contentOrNull

            if (decimals != null && supply != null) {
                Logger.d("Token $mintAddress is valid with decimals: $decimals, supply: $supply")
                true
            } else {
                Logger.d("Token $mintAddress is NOT valid or doesn't exist.")
                false
            }
        } catch (e: Exception) {
            Logger.d("Error verifying token $mintAddress: ${e.message}")
            false
        }
    }
}