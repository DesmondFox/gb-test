package com.gh.desmondfox

class PPU {
    private val vram: ByteArray = ByteArray(0x2000) // 8KB VRAM size

    fun readVram(address: Int): Int {
        return vram[address].toInt() and 0xFF
    }

    fun writeVram(address: Int, byteValue: Byte) {

    }
}