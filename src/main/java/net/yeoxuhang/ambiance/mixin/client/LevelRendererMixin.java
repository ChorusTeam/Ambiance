package net.yeoxuhang.ambiance.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.*;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.yeoxuhang.ambiance.Ambiance;
import net.yeoxuhang.ambiance.client.particle.ParticleRegistry;
import net.yeoxuhang.ambiance.client.particle.option.AshOption;
import net.yeoxuhang.ambiance.client.particle.option.TrialOption;
import net.yeoxuhang.ambiance.client.SoundRegistry;
import net.yeoxuhang.ambiance.config.AmbianceConfig;
import net.yeoxuhang.ambiance.util.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable{
    @Shadow @Nullable private ClientLevel level;


    @WrapWithCondition(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 7))
    public boolean ambiance$levelEvent(ClientLevel instance, ParticleOptions particleOptions, double d, double e, double f, double g, double h, double i) {
        return AmbianceConfig.smokeType == AmbianceConfig.TYPE.VANILLA;
    }

    @WrapWithCondition(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", ordinal = 28))
    public boolean ambiance$levelEventSound(ClientLevel instance, BlockPos pos, SoundEvent soundEvent, SoundSource soundSource, float v, float v1, boolean b) {
        return AmbianceConfig.eyePlaceSoundType == AmbianceConfig.TYPE.VANILLA;
    }

    @WrapWithCondition(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 1))
    public <T extends ParticleOptions> boolean ambiance$levelEvent$EyeOfEnderPop(LevelRenderer instance, T particleOptions, double d, double e, double f, double g, double h, double i) {
        return AmbianceConfig.eyeEnderParticles == AmbianceConfig.TYPE.VANILLA;
    }
    @WrapWithCondition(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 2))
    public <T extends ParticleOptions> boolean ambiance$levelEvent$EyeOfEnderPop1(LevelRenderer instance, T particleOptions, double d, double e, double f, double g, double h, double i) {
        return AmbianceConfig.eyeEnderParticles == AmbianceConfig.TYPE.VANILLA;
    }

    @Inject(method = "levelEvent", at = @At("HEAD"))
    public void ambiance$levelEvent(int i, BlockPos blockPos, int j, CallbackInfo ci) {
        assert this.level != null;
        RandomSource randomSource = this.level.random;
        int o;
        switch (i) {
            case 1503:
                if (AmbianceConfig.eyePlaceSoundType == AmbianceConfig.TYPE.FANCY){
                    this.level.playLocalSound(blockPos, SoundRegistry.ENDER_EYE_PLACED, SoundSource.BLOCKS, AmbianceConfig.eyePlaceSoundVolume, 1.0F, false);
                }
                if (AmbianceConfig.enableEnderEyePlace){
                    if (level.getBlockState(blockPos.above()).isAir()){
                        if (Ambiance.isModLoaded("endrem") && AmbianceConfig.endremCompat){
                            ParticlesUtil.endremEyePlace(level, blockPos);
                        } else {
                            this.level.addAlwaysVisibleParticle(ColorParticleOption.create(ParticleRegistry.ENDER_EYE_PLACE, MthHelper.convertHexToDec(AmbianceConfig.endPortalEyePlaced)), blockPos.getX() + 0.5, blockPos.getY() + 1.075, blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                            for(o = 0; o < 4; ++o) {
                                double p = blockPos.getY() + (1 - randomSource.nextDouble()) + 0.6;
                                VoxelShape voxelShape = level.getBlockState(blockPos).getShape(level, blockPos);
                                Vec3 vec3 = voxelShape.bounds().getCenter();
                                double d = (double)blockPos.getX() + vec3.x;
                                double e = (double)blockPos.getZ() + vec3.z;
                                this.level.addParticle(AshOption.create(20 + randomSource.nextInt(10), AmbianceConfig.   enderEyePlaceSize / 10, 0.5F, 0F, MthHelper.convertHexToDec(AmbianceConfig.endPortalEyePlaced), 0.7F), d + randomSource.nextDouble() / 5.0, p, e + randomSource.nextDouble() / 5.0, 0.0, 0.0, 0.0);
                            }
                        }
                    }
                }
                break;
            case 3006:
                int u = j >> 6;
                if (AmbianceConfig.enableSculkCharge){
                    if (u > 0) {
                        if (randomSource.nextFloat() < 0.3f + (float)u * 0.1f) {
                            float n = 0.15f + 0.02f * (float)u * (float)u * randomSource.nextFloat();
                            float y = 0.4f + 0.3f * (float)u * randomSource.nextFloat();
                            this.level.playLocalSound(blockPos, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, n, y, false);
                        }
                        byte b = (byte)(j & 0x3F);
                        UniformInt intProvider = UniformInt.of(0, u);
                        Supplier<Vec3> supplier = () -> new Vec3(Mth.nextDouble(randomSource, -0.005f, 0.005f), Mth.nextDouble(randomSource, -0.005f, 0.005f), Mth.nextDouble(randomSource, -0.005f, 0.005f));
                        if (b == 0) {
                            for (Direction direction : Direction.values()) {
                                double r = direction.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                                ParticleUtils.spawnParticlesOnBlockFace(this.level, blockPos.above(), new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SCULK.defaultBlockState()), intProvider, direction, supplier, r);
                            }
                        } else {
                            for (Direction direction2 : MultifaceBlock.unpack(b)) {
                                double q = 0.35;
                                ParticleUtils.spawnParticlesOnBlockFace(this.level, blockPos.above(), new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SCULK.defaultBlockState()), intProvider, direction2, supplier, q);

                            }
                        }
                    }
                }
                break;
            case 3011:
                if (AmbianceConfig.enableSpawner){
                    for(int count = 0; count < 15; ++count) {
                        VaultAndTrialSpawnerUtil.addTrialSpawnerParticles(level, blockPos, randomSource, j);
                    }
                }
                break;
            case 3012:
                if (AmbianceConfig.enableSpawner){
                    for(int count = 0; count < 15; ++count) {
                        VaultAndTrialSpawnerUtil.addTrialSpawnerMobParticles(level, blockPos, randomSource, j);
                    }
                }
                break;
            case 3013:
                if (AmbianceConfig.enableSpawner){
                    for(int count = 0; count < 15; ++count) {
                        VaultAndTrialSpawnerUtil.addTrialSpawnerDetectPlayersParticles(level, blockPos, randomSource);
                    }
                }
                break;
            case 3014:
                if (AmbianceConfig.enableItemPop){
                    VaultAndTrialSpawnerUtil.addEjectItemParticles(level, blockPos);
                }
                break;
            case 3016:
                if (AmbianceConfig.enableVault){
                    VaultAndTrialSpawnerUtil.addFlamesParticles(level, blockPos, randomSource, j);
                }
                break;
            case 3017:
                if (AmbianceConfig.enableItemPop){
                    VaultAndTrialSpawnerUtil.addEjectItemParticles(level, blockPos);
                }
                break;
            case 3019:
                if (AmbianceConfig.enableSpawner){
                    for(int count = 0; count < 5; ++count) {
                        VaultAndTrialSpawnerUtil.addOminousTrialSpawnerDetectPlayersParticles(level, blockPos, randomSource);
                    }
                }
                break;
            case 3020:
                if (AmbianceConfig.enableSpawner){
                    for(int count = 0; count < 20; ++count) {
                        VaultAndTrialSpawnerUtil.addBecomeOminousParticles(level, blockPos, randomSource);
                    }
                }
                break;

            case 2003:
                if (AmbianceConfig.enableEyeEnder && AmbianceConfig.eyeEnderParticles == AmbianceConfig.TYPE.FANCY){
                    double d = (double)blockPos.getX() + 0.5;
                    double e = blockPos.getY();
                    double f = (double)blockPos.getZ() + 0.5;
                    for (double g = 0.0; g < Math.PI * 2; g += 0.15707963267948966) {
                        level.addParticle(TrialOption.create(ParticleRegistry.PORTAL ,(int)(Math.random() * 10.0) + 40, 1F, 0.01F,0.1F, MthHelper.randomDarkerColor("CC00FA") , 1F), d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -5.0, 0.0, Math.sin(g) * -5.0);
                        level.addParticle(TrialOption.create(ParticleRegistry.PORTAL ,(int)(Math.random() * 10.0) + 40, 1F, 0.01F,0.1F, MthHelper.randomDarkerColor("CC00FA") , 1F), d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -7.0, 0.0, Math.sin(g) * -7.0);
                    }
                }
                break;
        }

    }
}
