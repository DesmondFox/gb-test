package com.gh.desmondfox

import java.io.File

class GameConsole {
    suspend fun start() {
        val cartridge = Cartridge()
        val ppu = PPU()
        val mmu = MMU(
            cartridge = cartridge,
            ppu = ppu,
        )
        val cpu = CPU(
            mmu = mmu
        )

        // Load a ROM file into the cartridge
        val inputStream = this::class.java.getResourceAsStream("/raw/01-special.gb")
        val byteArray = inputStream?.readBytes()
        if (byteArray != null) {
            cartridge.loadRom(byteArray)
        } else {
            println("Failed to load ROM file.")
            return
        }

        // Start the CPU
        println("Starting CPU...")
        while (true) {
            val cycles = cpu.step()
            // Add a delay to simulate the CPU speed
//            Thread.sleep(16) // Adjust the delay as needed
        }
    }
}