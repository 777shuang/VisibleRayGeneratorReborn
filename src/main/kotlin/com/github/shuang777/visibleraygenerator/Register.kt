package com.github.shuang777.visibleraygenerator

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object Register {
    val CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VisibleRayGeneratorReborn.MODID)
    val BLOCKS = DeferredRegister.createBlocks(VisibleRayGeneratorReborn.MODID)
    val ITEMS = DeferredRegister.createItems(VisibleRayGeneratorReborn.MODID)
    val BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, VisibleRayGeneratorReborn.MODID)

    fun register(modEventBus: IEventBus) {
        BLOCKS.register(modEventBus)
        ITEMS.register(modEventBus)
        BLOCK_ENTITIES.register(modEventBus)
        CREATIVE_MODE_TABS.register(modEventBus)
    }

    val DUMMY_SOLAR = BLOCKS.register("dummy_solar", Supplier {
        Block(BlockBehaviour.Properties.of().sound(SoundType.METAL))
    })

    val TEST_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("dummy_solar", DUMMY_SOLAR)

    val VISIBLE_RAY_GENERATOR = BLOCKS.register("visible_ray_generator", Supplier {
        GeneratorBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL))
    })

    val VISIBLE_RAY_GENERATOR_ITEM = ITEMS.registerSimpleBlockItem("visible_ray_generator", VISIBLE_RAY_GENERATOR)

    val GENERATOR_BE: DeferredHolder<BlockEntityType<*>?, BlockEntityType<Generator?>?> =
        BLOCK_ENTITIES.register("generator", Supplier {
            BlockEntityType.Builder.of(::Generator, VISIBLE_RAY_GENERATOR.get()).build(null)
        })

    val VRGENERATOR_TAB = CREATIVE_MODE_TABS.register("vrgenerator_tab", Supplier {
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.visibleraygenerator"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(Items.CRAFTER::getDefaultInstance)
            .displayItems { parameters, output ->
                output.accept(TEST_BLOCK_ITEM)
                output.accept(VISIBLE_RAY_GENERATOR_ITEM)
            }.build()
    })
}
