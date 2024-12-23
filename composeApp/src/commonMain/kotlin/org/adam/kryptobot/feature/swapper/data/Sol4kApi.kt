package org.adam.kryptobot.feature.swapper.data

import androidx.compose.runtime.key
import androidx.compose.ui.input.key.Key
import co.touchlab.kermit.Logger
import okio.ByteString.Companion.decodeBase64
import org.adam.kryptobot.util.DEV_WALLET_PRIVATE_KEY
import org.adam.kryptobot.util.MAIN_WALLET_PUBLIC_KEY
import org.adam.kryptobot.util.SECOND_WALLET_PUBLIC_KEY
import org.adam.kryptobot.util.base64ToBase58
import org.adam.kryptobot.util.base64ToBase58ByteArray
import org.adam.kryptobot.util.decodeBase58
import org.adam.kryptobot.util.encodeBase58
import org.hipparchus.analysis.function.Log
import org.sol4k.Base58
import org.sol4k.Connection
import org.sol4k.Keypair
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.Transaction
import org.sol4k.TransactionMessage
import org.sol4k.VersionedTransaction
import org.sol4k.api.Commitment
import org.sol4k.api.TransactionSimulationError
import org.sol4k.api.TransactionSimulationSuccess
import org.sol4k.instruction.Instruction
import java.math.BigInteger
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class Sol4kApi {

    fun restoreWalletFromPrivateKey(privateKey: String = DEV_WALLET_PRIVATE_KEY): Keypair {
        val wallet = Keypair.fromSecretKey(Base58.decode(privateKey))
        Logger.d("Public Key ${wallet.publicKey} private key $privateKey")
        Logger.d("Encoded Private ${wallet.secret.encodeBase58()}")
        return wallet
    }

    fun generateWallet() {
        val wallet = Keypair.generate()
        val publicKey = wallet.publicKey
        val privateKey = wallet.secret

        Logger.d("Public Key $publicKey private key $privateKey")
        Logger.d("Encoded Private ${Base58.encode(privateKey)}")
    }

    fun getProgramAccounts(
        programId: String = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA",
        rpcUrl: RpcUrl = RpcUrl.DEVNET,
        commitment: Commitment = Commitment.CONFIRMED
    ) {

    }

    fun getWalletBalance(
        walletKey: String,
        rpcUrl: RpcUrl = RpcUrl.MAINNNET,
        commitment: Commitment = Commitment.CONFIRMED
    ): BigInteger {
        val connection = Connection(rpcUrl, commitment)
        val key = PublicKey(walletKey)
        val balance = connection.getBalance(key)
        Logger.d("Balance is $balance")
        return balance
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun performSwapTransaction(
        feePayerAddress: String, //public wallet key probably change param name
        privateKey: String,
        instructions: String,
        rpcUrl: RpcUrl = RpcUrl.MAINNNET,
        commitment: Commitment = Commitment.FINALIZED,
        simulation: Boolean = false,
    ) {
        val solanaClient = Connection(rpcUrl, commitment)

        val feePayer = PublicKey(feePayerAddress)

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

    fun performSwapTransaction(
        feePayerAddress: String, //public wallet key probably change param name
        privateKey: String,
        instructions: List<Instruction>,
        rpcUrl: RpcUrl = RpcUrl.MAINNNET,
        commitment: Commitment = Commitment.FINALIZED,
        simulation: Boolean = true,
    ) {
        val solanaClient = Connection(rpcUrl, commitment)

        val feePayer = PublicKey(feePayerAddress)

        val balance = getWalletBalance(SECOND_WALLET_PUBLIC_KEY)
        Logger.d("Balance is $balance")

        val hash = solanaClient.getLatestBlockhashExtended(commitment).blockhash
        val valid = solanaClient.isBlockhashValid(hash, commitment)
        Logger.d("Hash valid: $valid")

        val transactionMessage = TransactionMessage.newMessage(feePayer = feePayer, recentBlockhash = hash, instructions = instructions)

        val transaction = VersionedTransaction(
            message = transactionMessage
        )

        val keypair = Keypair.fromSecretKey(privateKey.decodeBase58())
        transaction.sign(keypair)

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

    fun getMintDecimalsAmount(address: String): Int {
        val solanaClient = Connection(RpcUrl.MAINNNET, Commitment.CONFIRMED)
        return solanaClient.getTokenSupply(address).decimals
    }

    companion object {
        const val SPL_PROGRAM_ID = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"
    }

}