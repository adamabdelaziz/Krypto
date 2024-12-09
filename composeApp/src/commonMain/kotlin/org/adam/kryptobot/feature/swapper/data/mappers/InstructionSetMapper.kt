package org.adam.kryptobot.feature.swapper.data.mappers

import org.adam.kryptobot.feature.swapper.data.dto.InstructionSet
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapInstructionsDto
import org.sol4k.AccountMeta
import org.sol4k.Base58
import org.sol4k.PublicKey
import org.sol4k.instruction.BaseInstruction
import org.sol4k.instruction.Instruction

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

 /*
    Add compute budget instructions first.
    Add setup instructions.
    Add the swap instruction.
    Add the cleanup instruction if required.
    TODO: See if other is used
 */
fun JupiterSwapInstructionsDto.toInstructionList(): List<Instruction> {
    val instructionList: MutableList<Instruction> = mutableListOf()

    this.computeBudgetInstructions?.let { list ->
        list.forEach {
            instructionList.add(it.toSolInstruction())
        }
    }

    this.setupInstructions?.let { list ->
        list.forEach {
            instructionList.add(it.toSolInstruction())
        }
    }

    instructionList.add(this.swapInstruction.toSolInstruction())

    this.cleanupInstruction?.let {
        instructionList.add(it.toSolInstruction())
    }

    return instructionList.toList()
}