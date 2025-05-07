package com.gh.desmondfox

class PPU {
    private val vram: ByteArray = ByteArray(0x2000) // 8KB VRAM size
    private val oam: ByteArray = ByteArray(0x100) // 256 bytes OAM size

    fun readVram(address: Int): Int {
        return vram[address].toInt() and 0xFF
    }

    fun writeVram(address: Int, byteValue: Byte) {

    }

    fun readOam(address: Int): Int {
        return oam[address].toInt() and 0xFF
    }

    fun writeOam(address: Int, byteValue: Byte) {
        oam[address] = byteValue
    }
}