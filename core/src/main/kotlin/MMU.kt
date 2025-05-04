package com.gh.desmondfox

class MMU {
    fun readByte(address: Int): Int {
        // Simulate reading a byte from memory
        return 0xFF
    }

    fun writeByte(address: Int, value: Int) {
        // Simulate writing a byte to memory
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
}