package com.gh.desmondfox

object Opcodes {
    fun op0x00(cpu: CPU): Int {
        // NOP (No Operation)

        return 1
    }

    // JP a16 (Jump to address)
    fun op0xC3(cpu: CPU): Int {
        val lowByte = cpu.mmu.readByte(cpu.registers.pc++)
        val highByte = cpu.mmu.readByte(cpu.registers.pc++)
        cpu.registers.pc = (highByte shl 8) or lowByte
        return 4
    }

    // LD r16, d16
    fun LDr16d16(setReg: (Int) -> Unit): (CPU) -> Int = { cpu ->
        val lowByte = cpu.mmu.readByte(cpu.registers.pc++)
        val highByte = cpu.mmu.readByte(cpu.registers.pc++)
        val value = (highByte shl 8) or lowByte
        setReg(value)
        3
    }

    // LD r8, r8
    fun LDr8r8(cyclesCount: Int, setReg: (Int) -> Unit, getReg: (CPU) -> Int): (CPU) -> Int = { cpu ->
        val value = getReg(cpu)
        setReg(value)

        cyclesCount
    }

    // LD r, d8
    fun LDrd8(cyclesCount: Int = 2, setReg: (Int) -> Unit): (CPU) -> Int = { cpu ->
        val value = cpu.mmu.readByte(cpu.registers.pc++)
        setReg(value)
        cyclesCount
    }

}