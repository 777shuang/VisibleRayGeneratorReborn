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
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage

class GeneratorEnergyStorage(val capacity: Int, val maxExtract: Int) : IEnergyStorage {
  private var energy = 0

  fun generate(amount: Int) {
    energy = (energy + amount).coerceAtMost(capacity)
  }

  override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = 0

  override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
    val extracted = maxExtract.coerceAtMost(energy).coerceAtMost(this.maxExtract)
    if (!simulate) {
      energy -= extracted
    }
    return extracted
  }

  override fun getEnergyStored(): Int = energy

  override fun getMaxEnergyStored(): Int = capacity

  override fun canExtract(): Boolean = true

  override fun canReceive(): Boolean = false
}

class GeneratorBlock(properties: BlockBehaviour.Properties) : Block(properties), EntityBlock {
  override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
    return Generator(pos, state)
  }

  override fun <T : BlockEntity> getTicker(
    level: Level,
    state: BlockState,
    type: BlockEntityType<T>
  ): BlockEntityTicker<T>? {
    if (level.isClientSide) return null
    return BlockEntityTicker { lvl, pos, st, blockEntity ->
      if (blockEntity is Generator) {
        blockEntity.tick(lvl, pos, st)
      }
    }
  }
}

class Generator(pos: BlockPos, blockState: BlockState) : BlockEntity(Register.GENERATOR_BE.get(), pos, blockState) {
  val energy = GeneratorEnergyStorage(1000, 1)

  fun tick(level: Level, pos: BlockPos, state: BlockState) {
    if (level.isClientSide) return

    // 毎Tick 1 FE 発電する
    energy.generate(1)

    // 隣接するブロックにエネルギーを分配する
    for (direction in Direction.entries) {
      if (energy.energyStored <= 0) break

      val neighborPos = pos.relative(direction)
      val neighborEnergy = level.getCapability<IEnergyStorage, Direction?>(
        Capabilities.EnergyStorage.BLOCK,
        neighborPos,
        direction.opposite
      )
      if (neighborEnergy != null && neighborEnergy.canReceive()) {
        val accepted = neighborEnergy.receiveEnergy(1, false)
        if (accepted > 0) {
          energy.extractEnergy(accepted, false)
          break // 1 FE出力したためこのTickの送電は終了
        }
      }
    }
  }
}