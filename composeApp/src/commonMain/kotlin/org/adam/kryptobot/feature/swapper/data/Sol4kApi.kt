package org.adam.kryptobot.feature.swapper.data

import co.touchlab.kermit.Logger
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
        blockhash: String,
        instruction: Instruction,
        rpcUrl: RpcUrl = RpcUrl.DEVNET,
        commitment: Commitment = Commitment.CONFIRMED
    ) {
        val solanaClient = Connection(rpcUrl, commitment)

        val feePayer = PublicKey(feePayerAddress)

        val transaction = Transaction(
            feePayer = feePayer,
            recentBlockhash = blockhash,
            instruction = instruction
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
}