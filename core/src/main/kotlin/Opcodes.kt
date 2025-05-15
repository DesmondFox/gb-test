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
    fun LDr8r8(
        cyclesCount: Int,
        setReg: (Int) -> Unit,
        getReg: (CPU) -> Int,
        doAfter: (CPU) -> Unit = {},
    ): (CPU) -> Int = { cpu ->
        val value = getReg(cpu)
        setReg(value)
        doAfter(cpu)

        cyclesCount
    }

    // LD r, d8
    fun LDrd8(
        cyclesCount: Int = 8,
        setReg: (Int) -> Unit,
        doAfter: (CPU) -> Unit = {}
    ): (CPU) -> Int = { cpu ->
        val value = cpu.mmu.readByte(cpu.registers.pc++)
        setReg(value)
        doAfter(cpu)
        cyclesCount
    }

    // INC r
    fun INCr(cyclesCount: Int, getReg: (CPU) -> Int, setReg: (CPU, Int) -> Unit): (CPU) -> Int = { cpu ->
        val value = getReg(cpu)
        val newValue = (value + 1) and 0xFF
        setReg(cpu, newValue)
        cpu.registers.setFlagZ(newValue == 0)
        cpu.registers.setFlagN(false)
        cpu.registers.setFlagH((value and 0x0F) + 1 > 0x0F)

        cyclesCount
    }

    // INC r16
    fun INCr16(cyclesCount: Int, getReg: (CPU) -> Int, setReg: (CPU, Int) -> Unit): (CPU) -> Int = { cpu ->
        val value = getReg(cpu)
        val newValue = (value + 1) and 0xFFFF
        setReg(cpu, newValue)

        cyclesCount
    }

    // DEC r
    fun DECr(cyclesCount: Int, getReg: (CPU) -> Int, setReg: (CPU, Int) -> Unit): (CPU) -> Int = { cpu ->
        val value = getReg(cpu)
        val newValue = (value - 1) and 0xFF
        setReg(cpu, newValue)
        cpu.registers.setFlagZ(newValue == 0)
        cpu.registers.setFlagN(true)
        cpu.registers.setFlagH(value and 0x0F == 0)

        cyclesCount
    }

    // DEC r16
    fun DECr16(cyclesCount: Int, getReg: (CPU) -> Int, setReg: (CPU, Int) -> Unit): (CPU) -> Int = { cpu ->
        val value = getReg(cpu)
        val newValue = (value - 1) and 0xFFFF
        setReg(cpu, newValue)

        cyclesCount
    }


    // JR {Condition}, s8
    fun JR(condition: (CPU) -> Boolean): (CPU) -> Int = { cpu ->
        val offset = cpu.mmu.readByte(cpu.registers.pc++).toByte()
        if (condition(cpu)) {
            cpu.registers.pc = (cpu.registers.pc + offset) and 0xFFFF

            12
        } else {
            8
        }
    }

    // LD (a16), SP
    fun LD_SP_a16(): (CPU) -> Int = { cpu ->
        val lowByte = cpu.mmu.readByte(cpu.registers.pc++)
        val highByte = cpu.mmu.readByte(cpu.registers.pc++)
        val address = (highByte shl 8) or lowByte
        cpu.mmu.writeWord(address, cpu.registers.sp)

        20
    }

    fun incHL(cpu: CPU) {
        cpu.registers.hl = (cpu.registers.hl + 1) and 0xFFFF
    }

    fun decHL(cpu: CPU) {
        cpu.registers.hl = (cpu.registers.hl - 1) and 0xFFFF
    }
}