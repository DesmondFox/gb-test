package com.gh.desmondfox

private typealias Instruction = (CPU) -> Int

class CPU(
    val registers: Registers = Registers(),
    val mmu: MMU,
) {
    var cycles: Long = 0L

    // Interrupt Master Enable
    var ime: Boolean = false

    private val opcodes: Array<Instruction> = Array(256) { { _: CPU ->
        throw IllegalStateException("Unimplemented opcode: 0x${it.toString(16).padStart(4, '0')}")
        0
    } }
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

    fun step(): Int {
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

        return cyclesUsed
    }

    private fun initializeOpcodes() {
        // TODO: Initialize opcodes
        opcodes[0x00] = Opcodes::op0x00 // NOP

        // Jumps
        opcodes[0xC3] = Opcodes::op0xC3 // JP a16

        // LD instructions
        opcodes[0x21] = Opcodes::ld_hl_a16 // LD HL, a16
    }

    private fun initializeCBOpcodes() {
        // TODO: Initialize CB opcodes
    }

    private fun handleInterrupts(): Int {
        // TODO: Implement interrupt handling
        return 0
    }
}