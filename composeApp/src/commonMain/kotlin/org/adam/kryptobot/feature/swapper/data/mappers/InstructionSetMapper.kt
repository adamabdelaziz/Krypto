package org.adam.kryptobot.feature.swapper.data.mappers

import org.adam.kryptobot.feature.swapper.data.dto.InstructionSet
import org.sol4k.AccountMeta
import org.sol4k.Base58
import org.sol4k.PublicKey
import org.sol4k.instruction.BaseInstruction

/*
    To map instruction set from Jupiter Api for usage with Sol4k
 */

fun InstructionSet.toSolInstruction(): BaseInstruction =
    BaseInstruction(
        data = Base58.decode(this.data), //this.data.toByteArray(), //TODO see if this is correct
        keys = this.accounts.map { account ->
            AccountMeta(
                publicKey = PublicKey(account.pubkey),
                signer = account.isSigner,
                writable = account.isWritable
            )
        },
        programId = PublicKey(this.programId)
    )

