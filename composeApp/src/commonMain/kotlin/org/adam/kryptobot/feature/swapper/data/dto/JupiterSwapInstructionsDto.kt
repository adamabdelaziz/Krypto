package org.adam.kryptobot.feature.swapper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class JupiterSwapInstructionsDto(
    val tokenLedgerInstruction: InstructionSet? = null,
    val otherInstructions: List<InstructionSet>? = null,
    val computeBudgetInstructions: List<InstructionSet>? = null,
    val setupInstructions: List<InstructionSet>? = null,
    val swapInstruction: InstructionSet,
    val cleanupInstruction: InstructionSet? = null,
    val addressLookupTableAddresses: List<String>
)

@Serializable
data class InstructionSet(
    val programId: String,
    val accounts: List<Account>,
    val data: String
)

@Serializable
data class Account(
    val pubkey: String,
    val isSigner: Boolean,
    val isWritable: Boolean
)