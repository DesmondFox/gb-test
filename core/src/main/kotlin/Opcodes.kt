package com.gh.desmondfox

object Opcodes {
    fun op0x00(cpu: CPU): Int {
        // NOP (No Operation)

        return 4
    }

    fun op0xC3(cpu: CPU): Int {
        // JP a16 (Jump to address)
        val lowByte = cpu.mmu.readByte(cpu.registers.pc++)
        val highByte = cpu.mmu.readByte(cpu.registers.pc++)
        cpu.registers.pc = (highByte shl 8) or lowByte
        return 16
    }
}