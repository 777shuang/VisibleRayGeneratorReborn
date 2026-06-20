package com.github.shuang777.visibleraygenerator

import com.mojang.logging.LogUtils
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.server.ServerStartingEvent

@Mod(VisibleRayGeneratorReborn.MODID)
class VisibleRayGeneratorReborn {
  companion object {
    const val MODID = "visibleraygenerator"
    val LOGGER = LogUtils.getLogger()
  }

  constructor(modEventBus: IEventBus, modContainer: ModContainer) {
    Register.register(modEventBus)
    NeoForge.EVENT_BUS.register(this)
    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
  }


  @SubscribeEvent
  fun onServerStarting(event: ServerStartingEvent) {
  }
}
