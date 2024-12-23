package org.adam.kryptobot.feature.swapper.data.mappers

import co.touchlab.kermit.Logger
import org.adam.kryptobot.feature.swapper.data.dto.InstructionSet
import org.adam.kryptobot.feature.swapper.data.dto.JupiterSwapInstructionsDto
import org.adam.kryptobot.util.base64ToBase58
import org.adam.kryptobot.util.base64ToBase58ByteArray
import org.sol4k.AccountMeta
import org.sol4k.Base58
import org.sol4k.PublicKey
import org.sol4k.instruction.BaseInstruction
import org.sol4k.instruction.Instruction
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/*
    To map instruction set from Jupiter Api for usage with Sol4k
 */
@OptIn(ExperimentalEncodingApi::class)
fun InstructionSet.toSolInstruction(): BaseInstruction =
    BaseInstruction(
        data = Base64.decode(this.data), //this.data.toByteArray(), //Base58.decode(this.data), //TODO see if this is correct
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
     this.setupInstructions?.forEach {
         Logger.d("Setup instruction is $it")
     }
     this.computeBudgetInstructions?.forEach {
         Logger.d("compute instruction is $it")
     }
     this.otherInstructions?.forEach {
         Logger.d("other instruction is $it")
     }
     this.tokenLedgerInstruction?.let {
         Logger.d("token instruction is $it")
     }
     this.cleanupInstruction?.let {
         Logger.d("cleanup instruction is $it")
     }
     this.swapInstruction.let {
         Logger.d("swap instruction is $it")
     }
     instructionList.addAll(addInstructions(setupInstructions))
     instructionList.addAll(addInstructions(computeBudgetInstructions))
     instructionList.addAll(addInstructions(otherInstructions))
     instructionList.addAll(addInstructions(tokenLedgerInstruction))
     instructionList.addAll(addInstructions(cleanupInstruction))
     instructionList.addAll(addInstructions(swapInstruction))

    return instructionList.toList()
}

private fun addInstructions(instructions: List<InstructionSet>?): List<BaseInstruction> {
    return instructions?.map { it.toSolInstruction() } ?: emptyList()
}

private fun addInstructions(instruction: InstructionSet?): List<BaseInstruction> {
    return if (instruction != null) {
        listOf(instruction.toSolInstruction())
    } else {
        emptyList()
    }
}