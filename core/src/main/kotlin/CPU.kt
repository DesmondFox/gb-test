package com.gh.desmondfox

private typealias Instruction = (CPU) -> Int

class CPU(
    val registers: Registers = Registers(),
    val mmu: MMU,
) {
    var cycles: Long = 0L

    // Interrupt Master Enable
    var ime: Boolean = false

    private val opcodes: Array<Instruction> = Array(256) {
        { _: CPU ->
            throw IllegalStateException("Unimplemented opcode: 0x${it.toString(16).padStart(4, '0')}")
            0
        }
    }
    private val cbOpcodes: Array<Instruction> = Array(256) { { _: CPU -> 0 } }

    init {
        initializeOpcodes()
        initializeCBOpcodes()

        // TODO: Implement BIOS
        // Initialize registers (should be done in BIOS)
        registers.pc = 0x0100 // Start of the program counter
        registers.sp = 0xFFFE // Stack pointer
        registers.af = 0x01B0 // Initial value of AF register
        registers.bc = 0x0013 // Initial value of BC register
        registers.de = 0x00D8 // Initial value of DE register
        registers.hl = 0x014D // Initial value of HL register
    }

    fun step(): Long {
        val handledInterruptCycles = handleInterrupts()
        if (handledInterruptCycles > 0) {
            cycles += handledInterruptCycles
            return handledInterruptCycles
        }

        val opcode = mmu.readByte(registers.pc)
        registers.pc = (registers.pc + 1) and 0xFFFF // Increment program counter

        val instruction = opcodes[opcode]
        val cyclesUsed = instruction(this)

        cycles += cyclesUsed

        println(
            "Opcode: 0x${opcode.toString(16).padStart(2, '0')} | Cycles: $cycles | PC: 0x${
                registers.pc.toString(16).padStart(4, '0')
            } | AF: 0x${registers.af.toString(16).padStart(4, '0')} | BC: 0x${
                registers.bc.toString(16).padStart(4, '0')
            } | DE: 0x${registers.de.toString(16).padStart(4, '0')} | HL: 0x${
                registers.hl.toString(16).padStart(4, '0')
            }"
        )

        return cycles

    }

    private fun initializeOpcodes() {
        // TODO: Initialize opcodes
        opcodes[0x00] = Opcodes::op0x00 // NOP

        // Jumps
        opcodes[0xC3] = Opcodes::op0xC3 // JP a16

        // LD d16, r16
        opcodes[0x01] = Opcodes.LDr16d16 { cpu, value -> cpu.registers.bc = value } // LD BC, d16
        opcodes[0x11] = Opcodes.LDr16d16 { cpu, value -> cpu.registers.de = value } // LD DE, d16
        opcodes[0x21] = Opcodes.LDr16d16 { cpu, value -> cpu.registers.hl = value } // LD HL, d16
        opcodes[0x31] = Opcodes.LDr16d16 { cpu, value -> cpu.registers.sp = value } // LD SP, d16

        // LD r8, r8
        opcodes[0x40] = Opcodes.LDr8r8(4, { registers.b = it }, { registers.b }) // LD B, B
        opcodes[0x41] = Opcodes.LDr8r8(4, { registers.b = it }, { registers.c }) // LD B, C
        opcodes[0x42] = Opcodes.LDr8r8(4, { registers.b = it }, { registers.d }) // LD B, D
        opcodes[0x43] = Opcodes.LDr8r8(4, { registers.b = it }, { registers.e }) // LD B, E
        opcodes[0x44] = Opcodes.LDr8r8(4, { registers.b = it }, { registers.h }) // LD B, H
        opcodes[0x45] = Opcodes.LDr8r8(4, { registers.b = it }, { registers.l }) // LD B, L
        opcodes[0x46] = Opcodes.LDr8r8(8, { registers.b = it }, { mmu.readByte(registers.hl) }) // LD B, (HL)
        opcodes[0x47] = Opcodes.LDr8r8(4, { registers.b = it }, { registers.a }) // LD B, A
        opcodes[0x48] = Opcodes.LDr8r8(4, { registers.c = it }, { registers.b }) // LD C, B
        opcodes[0x49] = Opcodes.LDr8r8(4, { registers.c = it }, { registers.c }) // LD C, C
        opcodes[0x4A] = Opcodes.LDr8r8(4, { registers.c = it }, { registers.d }) // LD C, D
        opcodes[0x4B] = Opcodes.LDr8r8(4, { registers.c = it }, { registers.e }) // LD C, E
        opcodes[0x4C] = Opcodes.LDr8r8(4, { registers.c = it }, { registers.h }) // LD C, H
        opcodes[0x4D] = Opcodes.LDr8r8(4, { registers.c = it }, { registers.l }) // LD C, L
        opcodes[0x4E] = Opcodes.LDr8r8(8, { registers.c = it }, { mmu.readByte(registers.hl) }) // LD C, (HL)
        opcodes[0x4F] = Opcodes.LDr8r8(4, { registers.c = it }, { registers.a }) // LD C, A
        opcodes[0x50] = Opcodes.LDr8r8(4, { registers.d = it }, { registers.b }) // LD D, B
        opcodes[0x51] = Opcodes.LDr8r8(4, { registers.d = it }, { registers.c }) // LD D, C
        opcodes[0x52] = Opcodes.LDr8r8(4, { registers.d = it }, { registers.d }) // LD D, D
        opcodes[0x53] = Opcodes.LDr8r8(4, { registers.d = it }, { registers.e }) // LD D, E
        opcodes[0x54] = Opcodes.LDr8r8(4, { registers.d = it }, { registers.h }) // LD D, H
        opcodes[0x55] = Opcodes.LDr8r8(4, { registers.d = it }, { registers.l }) // LD D, L
        opcodes[0x56] = Opcodes.LDr8r8(8, { registers.d = it }, { mmu.readByte(registers.hl) }) // LD D, (HL)
        opcodes[0x57] = Opcodes.LDr8r8(4, { registers.d = it }, { registers.a }) // LD D, A
        opcodes[0x58] = Opcodes.LDr8r8(4, { registers.e = it }, { registers.b }) // LD E, B
        opcodes[0x59] = Opcodes.LDr8r8(4, { registers.e = it }, { registers.c }) // LD E, C
        opcodes[0x5A] = Opcodes.LDr8r8(4, { registers.e = it }, { registers.d }) // LD E, D
        opcodes[0x5B] = Opcodes.LDr8r8(4, { registers.e = it }, { registers.e }) // LD E, E
        opcodes[0x5C] = Opcodes.LDr8r8(4, { registers.e = it }, { registers.h }) // LD E, H
        opcodes[0x5D] = Opcodes.LDr8r8(4, { registers.e = it }, { registers.l }) // LD E, L
        opcodes[0x5E] = Opcodes.LDr8r8(8, { registers.e = it }, { mmu.readByte(registers.hl) }) // LD E, (HL)
        opcodes[0x5F] = Opcodes.LDr8r8(4, { registers.e = it }, { registers.a }) // LD E, A
        opcodes[0x60] = Opcodes.LDr8r8(4, { registers.h = it }, { registers.b }) // LD H, B
        opcodes[0x61] = Opcodes.LDr8r8(4, { registers.h = it }, { registers.c }) // LD H, C
        opcodes[0x62] = Opcodes.LDr8r8(4, { registers.h = it }, { registers.d }) // LD H, D
        opcodes[0x63] = Opcodes.LDr8r8(4, { registers.h = it }, { registers.e }) // LD H, E
        opcodes[0x64] = Opcodes.LDr8r8(4, { registers.h = it }, { registers.h }) // LD H, H
        opcodes[0x65] = Opcodes.LDr8r8(4, { registers.h = it }, { registers.l }) // LD H, L
        opcodes[0x66] = Opcodes.LDr8r8(8, { registers.h = it }, { mmu.readByte(registers.hl) }) // LD H, (HL)
        opcodes[0x67] = Opcodes.LDr8r8(4, { registers.h = it }, { registers.a }) // LD H, A
        opcodes[0x68] = Opcodes.LDr8r8(4, { registers.l = it }, { registers.b }) // LD L, B
        opcodes[0x69] = Opcodes.LDr8r8(4, { registers.l = it }, { registers.c }) // LD L, C
        opcodes[0x6A] = Opcodes.LDr8r8(4, { registers.l = it }, { registers.d }) // LD L, D
        opcodes[0x6B] = Opcodes.LDr8r8(4, { registers.l = it }, { registers.e }) // LD L, E
        opcodes[0x6C] = Opcodes.LDr8r8(4, { registers.l = it }, { registers.h }) // LD L, H
        opcodes[0x6D] = Opcodes.LDr8r8(4, { registers.l = it }, { registers.l }) // LD L, L
        opcodes[0x6E] = Opcodes.LDr8r8(8, { registers.l = it }, { mmu.readByte(registers.hl) }) // LD L, (HL)
        opcodes[0x6F] = Opcodes.LDr8r8(4, { registers.l = it }, { registers.a }) // LD L, A
        opcodes[0x70] = Opcodes.LDr8r8(8, { mmu.writeByte(registers.hl, it) }, { registers.b }) // LD (HL), B
        opcodes[0x71] = Opcodes.LDr8r8(8, { mmu.writeByte(registers.hl, it) }, { registers.c }) // LD (HL), C
        opcodes[0x72] = Opcodes.LDr8r8(8, { mmu.writeByte(registers.hl, it) }, { registers.d }) // LD (HL), D
        opcodes[0x73] = Opcodes.LDr8r8(8, { mmu.writeByte(registers.hl, it) }, { registers.e }) // LD (HL), E
        opcodes[0x74] = Opcodes.LDr8r8(8, { mmu.writeByte(registers.hl, it) }, { registers.h }) // LD (HL), H
        opcodes[0x75] = Opcodes.LDr8r8(8, { mmu.writeByte(registers.hl, it) }, { registers.l }) // LD (HL), L
        opcodes[0x77] = Opcodes.LDr8r8(8, { mmu.writeByte(registers.hl, it) }, { registers.a }) // LD (HL), A
        opcodes[0x78] = Opcodes.LDr8r8(4, { registers.a = it }, { registers.b }) // LD A, B
        opcodes[0x79] = Opcodes.LDr8r8(4, { registers.a = it }, { registers.c }) // LD A, C
        opcodes[0x7A] = Opcodes.LDr8r8(4, { registers.a = it }, { registers.d }) // LD A, D
        opcodes[0x7B] = Opcodes.LDr8r8(4, { registers.a = it }, { registers.e }) // LD A, E
        opcodes[0x7C] = Opcodes.LDr8r8(4, { registers.a = it }, { registers.h }) // LD A, H
        opcodes[0x7D] = Opcodes.LDr8r8(4, { registers.a = it }, { registers.l }) // LD A, L
        opcodes[0x7E] = Opcodes.LDr8r8(8, { registers.a = it }, { mmu.readByte(registers.hl) }) // LD A, (HL)
        opcodes[0x7F] = Opcodes.LDr8r8(4, { registers.a = it }, { registers.a }) // LD A, A

        opcodes[0x02] = Opcodes.LDr8r8(8, { mmu.writeByte(registers.bc, it) }, { registers.a }) // LD (BC), A
        opcodes[0x12] = Opcodes.LDr8r8(8, { mmu.writeByte(registers.de, it) }, { registers.a }) // LD (DE), A
        opcodes[0x0A] = Opcodes.LDr8r8(8, { registers.a = it }, { mmu.readByte(registers.bc) }) // LD A, (BC)
        opcodes[0x1A] = Opcodes.LDr8r8(8, { registers.a = it }, { mmu.readByte(registers.de) }) // LD A, (DE)

        opcodes[0x22] = Opcodes.LDr8r8(
            8,
            { mmu.writeByte(registers.hl, it) },
            { registers.a },
            { Opcodes.incHL(this) }) // LD (HL+), A
        opcodes[0x32] = Opcodes.LDr8r8(
            8,
            { mmu.writeByte(registers.hl, it) },
            { registers.a },
            { Opcodes.decHL(this) }) // LD (HL-), A
        opcodes[0x2A] = Opcodes.LDr8r8(
            8,
            { registers.a = it },
            { mmu.readByte(registers.hl) },
            { Opcodes.incHL(this) }) // LD A, (HL+)
        opcodes[0x3A] = Opcodes.LDr8r8(
            8,
            { registers.a = it },
            { mmu.readByte(registers.hl) },
            { Opcodes.decHL(this) }) // LD A, (HL-)

        // LD r8, d8
        opcodes[0x0E] = Opcodes.LDrd8(8, { registers.c = it }) // LD C, d8
        opcodes[0x1E] = Opcodes.LDrd8(8, { registers.e = it }) // LD E, d8
        opcodes[0x2E] = Opcodes.LDrd8(8, { registers.l = it }) // LD L, d8
        opcodes[0x3E] = Opcodes.LDrd8(8, { registers.a = it }) // LD A, d8
        opcodes[0x26] = Opcodes.LDrd8(8, { registers.h = it }) // LD H, d8
        opcodes[0x36] = Opcodes.LDrd8(12, { mmu.writeByte(registers.hl, it) }) // LD (HL), d8
        opcodes[0x06] = Opcodes.LDrd8(8, { registers.b = it }) // LD B, d8
        opcodes[0x16] = Opcodes.LDrd8(8, { registers.d = it }) // LD D, d8
        opcodes[0x46] = Opcodes.LDrd8(3, { mmu.writeByte(registers.hl, it) }) // LD (HL), d8

        // LD (a16), SP
        opcodes[0x08] = Opcodes.LD_SP_a16() // LD (a16), SP
        // LD (a16), A
        opcodes[0xEA] = Opcodes.LD_A_a16() // LD (a16), A
        // LDH (a8), A
        opcodes[0xE0] = Opcodes.LDH_a8_A() // LDH (a8), A

        // INC r
        opcodes[0x04] = Opcodes.INCr(4, { it.registers.b }, { cpu, i -> cpu.registers.b = i }) // INC B
        opcodes[0x14] = Opcodes.INCr(4, { it.registers.d }, { cpu, i -> cpu.registers.d = i }) // INC D
        opcodes[0x24] = Opcodes.INCr(4, { it.registers.h }, { cpu, i -> cpu.registers.h = i }) // INC H
        opcodes[0x34] =
            Opcodes.INCr(12, { mmu.readByte(it.registers.hl) }, { cpu, i -> cpu.registers.hl = i }) // INC (HL)
        opcodes[0x0C] = Opcodes.INCr(4, { it.registers.c }, { cpu, i -> cpu.registers.c = i }) // INC C
        opcodes[0x1C] = Opcodes.INCr(4, { it.registers.e }, { cpu, i -> cpu.registers.e = i }) // INC E
        opcodes[0x2C] = Opcodes.INCr(4, { it.registers.l }, { cpu, i -> cpu.registers.l = i }) // INC L
        opcodes[0x3C] = Opcodes.INCr(4, { it.registers.a }, { cpu, i -> cpu.registers.a = i }) // INC A

        // INC r16
        opcodes[0x03] = Opcodes.INCr16(8, { it.registers.bc }, { cpu, i -> cpu.registers.bc = i }) // INC BC
        opcodes[0x13] = Opcodes.INCr16(8, { it.registers.de }, { cpu, i -> cpu.registers.de = i }) // INC DE
        opcodes[0x23] = Opcodes.INCr16(8, { it.registers.hl }, { cpu, i -> cpu.registers.hl = i }) // INC HL
        opcodes[0x33] = Opcodes.INCr16(8, { it.registers.sp }, { cpu, i -> cpu.registers.sp = i }) // INC SP

        // DEC r
        opcodes[0x05] = Opcodes.DECr(4, { it.registers.b }, { cpu, i -> cpu.registers.b = i }) // DEC B
        opcodes[0x15] = Opcodes.DECr(4, { it.registers.d }, { cpu, i -> cpu.registers.d = i }) // DEC D
        opcodes[0x25] = Opcodes.DECr(4, { it.registers.h }, { cpu, i -> cpu.registers.h = i }) // DEC H
        opcodes[0x35] =
            Opcodes.DECr(12, { mmu.readByte(it.registers.hl) }, { cpu, i -> cpu.registers.hl = i }) // DEC (HL)
        opcodes[0x0D] = Opcodes.DECr(4, { it.registers.c }, { cpu, i -> cpu.registers.c = i }) // DEC C
        opcodes[0x1D] = Opcodes.DECr(4, { it.registers.e }, { cpu, i -> cpu.registers.e = i }) // DEC E
        opcodes[0x2D] = Opcodes.DECr(4, { it.registers.l }, { cpu, i -> cpu.registers.l = i }) // DEC L
        opcodes[0x3D] = Opcodes.DECr(4, { it.registers.a }, { cpu, i -> cpu.registers.a = i }) // DEC A

        // DEC e16
        opcodes[0x0B] = Opcodes.DECr16(8, { it.registers.bc }, { cpu, i -> cpu.registers.bc = i }) // DEC BC
        opcodes[0x1B] = Opcodes.DECr16(8, { it.registers.de }, { cpu, i -> cpu.registers.de = i }) // DEC DE
        opcodes[0x2B] = Opcodes.DECr16(8, { it.registers.hl }, { cpu, i -> cpu.registers.hl = i }) // DEC HL
        opcodes[0x3B] = Opcodes.DECr16(8, { it.registers.sp }, { cpu, i -> cpu.registers.sp = i }) // DEC SP

        // Jump instructions
        opcodes[0x20] = Opcodes.JRs8 { !it.registers.getFlagZ() } // JR NZ, s8
        opcodes[0x30] = Opcodes.JRs8 { !it.registers.getFlagC() } // JR NC, s8
        opcodes[0x28] = Opcodes.JRs8 { it.registers.getFlagZ() } // JR Z, s8
        opcodes[0x38] = Opcodes.JRs8 { it.registers.getFlagC() } // JR C, s8

        // Enable / Disable Interrupts
        opcodes[0xF3] = Opcodes.opToggleInterrupts(false) // DI
        opcodes[0xFB] = Opcodes.opToggleInterrupts(true) // EI


    }

    private fun initializeCBOpcodes() {
        // TODO: Initialize CB opcodes
    }

    private fun handleInterrupts(): Long {
        // TODO: Implement interrupt handling
        return 0
    }
}