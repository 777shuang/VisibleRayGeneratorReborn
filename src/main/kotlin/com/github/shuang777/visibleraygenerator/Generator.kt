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

class GeneratorEnergyStorage(private val capacity: Int = 1_000) : IEnergyStorage {
  private var energy = 0
  fun generate(amount: Int) {
    energy = (energy + amount).coerceAtMost(capacity)
  }

  override fun receiveEnergy(maxReceive: Int, simulate: Boolean) = 0
  override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
    val extracted = maxExtract.coerceAtMost(energy)
    if (!simulate) energy -= extracted
    return extracted
  }

  override fun getEnergyStored() = energy
  override fun getMaxEnergyStored() = capacity
  override fun canExtract() = true
  override fun canReceive() = false
}

// Block that creates the Generator BlockEntity
class GeneratorBlock(props: BlockBehaviour.Properties) : Block(props), EntityBlock {
  override fun newBlockEntity(pos: BlockPos, state: BlockState) = Generator(pos, state)
  override fun <T : BlockEntity> getTicker(
    level: Level,
    state: BlockState,
    type: BlockEntityType<T>
  ): BlockEntityTicker<T>? =
    if (level.isClientSide) null
    else BlockEntityTicker { _, pos, _, be ->
      (be as? Generator)?.tick(level, pos)
    }
}

// BlockEntity that generates and pushes 1 FE each tick
class Generator(pos: BlockPos, blockState: BlockState) : BlockEntity(Register.GENERATOR_BE.get(), pos, blockState) {
  val energy = GeneratorEnergyStorage()
  fun tick(level: Level, pos: BlockPos) {
    if (level.isClientSide) return
    energy.generate(1)
    for (dir in Direction.entries) {
      if (energy.energyStored == 0) break
      val neighbor = level.getCapability<IEnergyStorage, Direction?>(
        Capabilities.EnergyStorage.BLOCK,
        pos.relative(dir),
        dir.opposite
      ) ?: continue
      if (neighbor.canReceive()) {
        val sent = neighbor.receiveEnergy(1, false)
        if (sent > 0) {
          energy.extractEnergy(sent, false)
          break
        }
      }
    }
  }
}