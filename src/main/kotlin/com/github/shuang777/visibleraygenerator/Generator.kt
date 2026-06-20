package com.github.shuang777.visibleraygenerator

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.neoforged.neoforge.capabilities.Capabilities

// Block that creates the Generator BlockEntity
class GeneratorBlock : Block, EntityBlock {
  companion object {
    // 発電機の等級が上がるごとに発電量が何倍されるか，という階比数列
    private val energyGenerrationDifferenceSequence = arrayOf(2, 4, 4, 4, 4, 4, 4, 4, 4)
    val NUMBER_OF_GRADE = energyGenerrationDifferenceSequence.size+1

    // 発電機の等級
    val GRADE = IntegerProperty.create("generate_fe", 0, energyGenerrationDifferenceSequence.size)

    // 実際の発電量[FE]
    val amountOfGeneration by lazy {
      var amount = Array<Int>(NUMBER_OF_GRADE) { Config.BASE_ENERGY_GENERATION.get() }
      for (i in 1..energyGenerrationDifferenceSequence.size) {
        amount[i] = amount[i-1] * energyGenerrationDifferenceSequence[i-1];
      }
      return@lazy amount
    }
  }

  constructor(props: BlockBehaviour.Properties) : super(props) {
    registerDefaultState(stateDefinition.any().setValue(GRADE, 0))
  }

  override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
    builder.add(GRADE)
  }

  override fun newBlockEntity(pos: BlockPos, state: BlockState) = GeneratorBlockEntity(pos, state)
  override fun <T : BlockEntity> getTicker(
    level: Level, state: BlockState, type: BlockEntityType<T>
  ): BlockEntityTicker<T>? =
    if (level.isClientSide) null
    else BlockEntityTicker { _, pos, _, be -> (be as? GeneratorBlockEntity)?.tick(level, pos) }
}

// BlockEntity that generates and pushes FE each tick
class GeneratorBlockEntity : BlockEntity {
  // 毎tick周囲の機械に供給するエネルギー
  val generateEnergy: Int by lazy { GeneratorBlock.amountOfGeneration[blockState.getValue(GeneratorBlock.GRADE)] }

  constructor(pos: BlockPos, blockState: BlockState) : super(Register.GENERATOR_BE.get(), pos, blockState) {
  }

  fun tick(level: Level, pos: BlockPos) {
    if (level.isClientSide) return

    for (dir in Direction.entries) {
      val neighbor =
        level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(dir), dir.opposite) ?: continue
      if (neighbor.canReceive()) {
        neighbor.receiveEnergy(generateEnergy, false)
        break
      }
    }
  }
}