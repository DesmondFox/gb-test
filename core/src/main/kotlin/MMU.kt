package com.gh.desmondfox

import com.gh.desmondfox.interrupts.InterruptController

class MMU(
    private val cartridge: Cartridge,
    private val ppu: PPU,
    private val interruptController: InterruptController = InterruptController(),
) {
    private val wram = ByteArray(0x2000) // 8KB WRAM size
    private val hram = ByteArray(0x7F) // 128 bytes HRAM size

    fun readByte(address: Int): Int {
        return when (address) {
            in 0x0000..0x7FFF -> cartridge.readRom(address) // Read from ROM,
            in 0x8000..0x9FFF -> ppu.readVram(address) // Read from VRAM
            in 0xA000..0xBFFF -> cartridge.readRam(address) // Read from RAM
            in 0xC000..0xCFFF -> return wram[address - 0xC000].toInt() and 0xFF // Read from WRAM
            in 0xD000..0xDFFF -> return wram[address - 0xC000].toInt() and 0xFF // Read from WRAM
            in 0xE000..0xFDFF -> return wram[address - 0x2000].toInt() and 0xFF // Read from echo RAM
            in 0xFE00..0xFE9F -> return ppu.readOam(address) // Read from OAM
            in 0xFEA0..0xFEFF -> return 0xFF // Unusable area
            in 0xFF00..0xFF7F -> return readIORegister(address) // Read from IO registers
//            0xFF0F -> return interruptController.readIF() // Read from IF register
            in 0xFF80..0xFFFE -> return hram[address - 0xFF80].toInt() and 0xFF // Read from HRAM
            0xFFFF -> return interruptController.readIE() // Read from IE register
            else -> {
                // TODO: Implement other memory regions

                return 0xFF
            }
        }

        return 0xFF // Default return value for unhandled addresses
    }

    fun writeByte(address: Int, value: Int) {
        // Simulate writing a byte to memory
        val addr = address and 0xFFFF
        val byteValue = address.toByte()

        when (addr) {
            in 0x0000..0x7FFF -> cartridge.writeRom(addr, byteValue) // Write to ROM
            in 0x8000..0x9FFF -> ppu.writeVram(addr, byteValue) // Write to VRAM
            in 0xA000..0xBFFF -> cartridge.writeRam(addr, byteValue) // Write to external RAM
            in 0xC000..0xCFFF -> wram[addr - 0xC000] = byteValue // Write to WRAM
            in 0xD000..0xDFFF -> wram[addr - 0xC000] = byteValue // Write to WRAM
            in 0xE000..0xFDFF -> wram[addr - 0x2000] = byteValue // Write to echo RAM
            in 0xFE00..0xFE9F -> ppu.writeOam(addr, byteValue) // Write to OAM
            in 0xFEA0..0xFEFF -> {} // Unusable area
            in 0xFF00..0xFF7F -> writeIORegister(addr) // Write to IO registers
//            0xFF0F -> interruptController.writeIF(byteValue) // Write to IF register
            in 0xFF80..0xFFFE -> hram[addr - 0xFF80] = byteValue // Write to HRAM
            0xFFFF -> interruptController.writeIE(byteValue) // Write to IE register
            else -> {
                throw IllegalArgumentException("Invalid address to write: $address")
            }
        }
    }

    fun readWord(address: Int): Int {
        val low = readByte(address)
        val high = readByte(address + 1)
        return (high shl 8) or low
    }

    fun writeWord(address: Int, value: Int) {
        val low = value and 0xFF
        val high = (value shr 8) and 0xFF
        writeByte(address, low)
        writeByte(address + 1, high)
    }

    fun readIORegister(address: Int): Int {
        return 0xFF // TODO: Implement IO register read
    }

    private fun writeIORegister(addr: Int) {

    }
}