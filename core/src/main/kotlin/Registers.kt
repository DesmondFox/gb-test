package com.gh.desmondfox

class Registers {
    var a: Int = 0
    var f: Int = 0
    var b: Int = 0
    var c: Int = 0
    var d: Int = 0
    var e: Int = 0
    var h: Int = 0
    var l: Int = 0
    var sp: Int = 0
    var pc: Int = 0

    var af: Int
        get() = (a shl 8) or f
        set(value) {
            a = value shr 8
            f = value and 0xFF
        }

    var bc: Int
        get() = (b shl 8) or c
        set(value) {
            b = value shr 8
            c = value and 0xFF
        }

    var de: Int
        get() = (d shl 8) or e
        set(value) {
            d = value shr 8
            e = value and 0xFF
        }

    var hl: Int
        get() = (h shl 8) or l
        set(value) {
            h = value shr 8
            l = value and 0xFF
        }

    fun setFlagZ(value: Boolean) = if (value) f = f or 0x80 else f = f and 0x7F
    fun setFlagN(value: Boolean) = if (value) f = f or 0x40 else f = f and 0xBF
    fun setFlagH(value: Boolean) = if (value) f = f or 0x20 else f = f and 0xDF
    fun setFlagC(value: Boolean) = if (value) f = f or 0x10 else f = f and 0xEF

    fun getFlagZ() = (f and 0x80) != 0
    fun getFlagN() = (f and 0x40) != 0
    fun getFlagH() = (f and 0x20) != 0
    fun getFlagC() = (f and 0x10) != 0

    fun getIndexedRegister(index: Int) = when (index) {
        0 -> ::b
        1 -> ::c
        2 -> ::d
        3 -> ::e
        4 -> ::h
        5 -> ::l
        6 -> ::hl
        7 -> ::a
        else -> throw IllegalArgumentException("Invalid register index: $index")
    }
}
