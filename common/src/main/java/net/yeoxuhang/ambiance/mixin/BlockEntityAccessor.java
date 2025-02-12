package net.yeoxuhang.ambiance.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {

    @Invoker("saveAdditional")
    void invokeSaveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries);
}
