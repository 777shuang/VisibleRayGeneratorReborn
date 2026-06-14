package com.github.shuang777.visibleraygenerator;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Register {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
    DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VisibleRayGeneratorReborn.MODID);
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(VisibleRayGeneratorReborn.MODID);
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VisibleRayGeneratorReborn.MODID);

  public static void register(IEventBus modEventBus) {
    BLOCKS.register(modEventBus);
    ITEMS.register(modEventBus);
    CREATIVE_MODE_TABS.register(modEventBus);
  }

  public static final DeferredBlock<Block> DUMMY_SOLAR = BLOCKS.register(
    "dummy_solar",
    () -> new Block(BlockBehaviour.Properties.of().sound(SoundType.METAL))
  );
  public static final DeferredItem<BlockItem> TEST_BLOCK_ITEM =
    ITEMS.registerSimpleBlockItem("dummy_solar", DUMMY_SOLAR);

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VRGENERATOR_TAB =
    CREATIVE_MODE_TABS.register("vrgenerator_tab", () -> CreativeModeTab.builder()
      .title(Component.translatable("itemGroup.visibleraygenerator")) //The language key for the title of your CreativeModeTab
      .withTabsBefore(CreativeModeTabs.COMBAT)
      .icon(Items.CRAFTER::getDefaultInstance)
      .displayItems((parameters, output) -> {
        output.accept(Register.TEST_BLOCK_ITEM);
      }).build());
}
