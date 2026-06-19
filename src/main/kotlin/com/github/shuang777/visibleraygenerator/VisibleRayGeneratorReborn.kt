package com.github.shuang777.visibleraygenerator

import com.mojang.logging.LogUtils
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Blocks
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.event.server.ServerStartingEvent

@Mod(VisibleRayGeneratorReborn.MODID)
class VisibleRayGeneratorReborn(modEventBus: IEventBus, modContainer: ModContainer) {
  companion object {
    const val MODID = "visibleraygenerator"
    val LOGGER = LogUtils.getLogger()
  }

  init {
    modEventBus.addListener(this::commonSetup)
    modEventBus.addListener(this::registerCapabilities)
    Register.register(modEventBus)
    NeoForge.EVENT_BUS.register(this)
    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
  }

  private fun commonSetup(event: FMLCommonSetupEvent) {
    LOGGER.info("HELLO FROM COMMON SETUP")
    if (Config.LOG_DIRT_BLOCK.get()) {
      LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT))
    }
    LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.get())
    Config.ITEM_STRINGS.get().forEach { item ->
      LOGGER.info("ITEM >> {}", item)
    }
  }

  private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
    event.registerBlockEntity<IEnergyStorage, Direction?, Generator>(
      Capabilities.EnergyStorage.BLOCK,
      Register.GENERATOR_BE.get()
    ) { generator, side ->
      generator.energy
    }
  }

  @SubscribeEvent
  fun onServerStarting(event: ServerStartingEvent) {
    LOGGER.info("HELLO from server starting")
  }
}
