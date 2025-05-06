package com.gh.desmondfox

import kotlin.experimental.and

class Cartridge {
    private var rom: ByteArray = ByteArray(0x8000) // 32KB ROM size
    private var ram: ByteArray = ByteArray(0x2000) // 8KB RAM size

    fun loadRom(romData: ByteArray) {
        rom = romData
    }

    fun readRom(address: Int): Int {
        return rom[address].toInt() and 0xFF
    }

    fun writeRom(address: Int, value: Byte) {
        rom[address] = value
    }

    fun readRam(address: Int): Int {
        return ram[address].toInt() and 0xFF
    }

    fun writeRam(address: Int, value: Byte) {
        ram[address] = value
    }
}