package org.adam.kryptobot.feature.swapper.data

import androidx.compose.ui.input.key.Key
import co.touchlab.kermit.Logger
import org.adam.kryptobot.util.encodeBase58
import org.sol4k.Base58
import org.sol4k.Connection
import org.sol4k.Keypair
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.Transaction
import org.sol4k.TransactionMessage
import org.sol4k.api.Commitment
import org.sol4k.instruction.Instruction
import java.math.BigInteger


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

    fun getWalletBalance(
        walletKey: String,
        rpcUrl: RpcUrl = RpcUrl.DEVNET,
        commitment: Commitment = Commitment.CONFIRMED
    ): BigInteger {
        val connection = Connection(rpcUrl, commitment)
        val key = PublicKey(walletKey)
        return connection.getBalance(key)
    }

    fun performSwapTransaction(
        feePayerAddress: String,
        privateKey: String,
        instructions: List<Instruction>,
        rpcUrl: RpcUrl = RpcUrl.DEVNET,
        commitment: Commitment = Commitment.CONFIRMED
    ) {
        val solanaClient = Connection(rpcUrl, commitment)

        val feePayer = PublicKey(feePayerAddress)

        val transaction = Transaction(
            feePayer = feePayer,
            recentBlockhash = solanaClient.getLatestBlockhash(commitment),
            instructions = instructions
        )

        val keypair = Keypair.fromSecretKey(Base58.decode(privateKey))
        transaction.sign(keypair)

        try {
            val transactionSignature = solanaClient.sendTransaction(transaction)
            Logger.d("Transaction successfully sent! Signature: $transactionSignature")
        } catch (e: Exception) {
            Logger.d("Transaction failed: ${e.message}")
        }
    }

    fun getMintDecimalsAmount(address: String): Int {
        val solanaClient = Connection(RpcUrl.MAINNNET, Commitment.CONFIRMED)
        return solanaClient.getTokenSupply(address).decimals
    }

    companion object {
        //TODO encrypt/save wallet info and move to repo
        private const val DEV_WALLET_PRIVATE_KEY =
            "5MfbR9MuTYeNxtiYoAnqsuhchWTtQHNmM7uagXMqNgzVbmLdBHpu44Fj4pDPfYDdH43uB58WXaZX5B4XJASKBxwR"
    }
}