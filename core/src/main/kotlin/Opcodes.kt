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

    // LD, d16 instructions
    fun ldRRD16(setReg: (Int) -> Unit): (CPU) -> Int = { cpu ->
        val lowByte = cpu.mmu.readByte(cpu.registers.pc++)
        val highByte = cpu.mmu.readByte(cpu.registers.pc++)
        val value = (highByte shl 8) or lowByte
        setReg(value)
        12
    }

    // LD r8, r8
    fun LDr8r8(cyclesCount: Int, setReg: (Int) -> Unit, getReg: (CPU) -> Int): (CPU) -> Int = { cpu ->
        val value = getReg(cpu)
        setReg(value)

        cyclesCount
    }
}