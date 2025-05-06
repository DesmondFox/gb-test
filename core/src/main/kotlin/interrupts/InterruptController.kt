package com.gh.desmondfox.interrupts

data class InterruptController(
    private var ieRegister: Int = 0xFFFF, // Interrupt Enable Register
    private var ifRegister: Int = 0xFFFE, // Interrupt Flag Register
) {
    fun readIE(): Int {
        return ieRegister
    }

    fun readIF(): Int {
        return ifRegister
    }

    fun writeIF(byteValue: Byte) {
        ifRegister = byteValue.toInt() and 0xFF
    }

    fun writeIE(byteValue: Byte) {
        ieRegister = byteValue.toInt() and 0xFF
    }
}