package com.github.shuang777.visibleraygenerator

import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

@Mod(value = VisibleRayGeneratorReborn.MODID, dist = [Dist.CLIENT])
class VisibleRayGeneratorRebornClient(container: ModContainer) {
  init {
    container.registerExtensionPoint(
      IConfigScreenFactory::class.java,
      IConfigScreenFactory { c, p -> ConfigurationScreen(c, p) })
  }

  @EventBusSubscriber(/*modid = VisibleRayGeneratorReborn.MODID, value = [Dist.CLIENT], bus = EventBusSubscriber.Bus
  .MOD*/
  )
  companion object {
    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
      VisibleRayGeneratorReborn.LOGGER.info("HELLO FROM CLIENT SETUP")
      VisibleRayGeneratorReborn.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().user.name)
    }
  }
}
