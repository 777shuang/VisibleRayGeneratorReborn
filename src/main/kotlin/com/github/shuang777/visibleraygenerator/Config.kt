package com.github.shuang777.visibleraygenerator

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.ModConfigSpec

object Config {
  private val BUILDER = ModConfigSpec.Builder()

  val BASE_ENERGY_GENERATION = BUILDER
    .comment("Base environment generator")
    .defineInRange("base_enegy_generation", 1, 0, Int.MAX_VALUE)
  /*
    val MAGIC_NUMBER: ModConfigSpec.IntValue = BUILDER
      .comment("A magic number")
      .defineInRange("magicNumber", 42, 0, Int.MAX_VALUE)

    val MAGIC_NUMBER_INTRODUCTION: ModConfigSpec.ConfigValue<String> = BUILDER
      .comment("What you want the introduction message to be for the magic number")
      .define("magicNumberIntroduction", "The magic number is... ")

    val ITEM_STRINGS: ModConfigSpec.ConfigValue<List<String>> = BUILDER
      .comment("A list of items to log on common setup.")
      .defineListAllowEmpty("items", listOf("minecraft:iron_ingot"), { "" }, ::validateItemName)*/

  val SPEC: ModConfigSpec = BUILDER.build()

  private fun validateItemName(obj: Any?): Boolean {
    return obj is String && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(obj))
  }
}
