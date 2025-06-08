package com.gh.desmondfox.interrupts

enum class InterruptType(val bit: Int, val mask: Int) {
    VBLANK(0, 0x40),
    LCD_STAT(1, 0x48),
    TIMER(2, 0x50),
    SERIAL(3, 0x58),
    JOYPAD(4, 0x60);
}