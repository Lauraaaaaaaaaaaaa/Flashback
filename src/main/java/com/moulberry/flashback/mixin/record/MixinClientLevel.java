package com.moulberry.flashback.mixin.record;

import com.moulberry.flashback.Flashback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class MixinClientLevel {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "levelEvent", at = @At("HEAD"))
    public void levelEvent(Player player, int type, BlockPos blockPos, int data, CallbackInfo ci) {
        if (Flashback.RECORDER != null && !Flashback.RECORDER.isPaused()) {
            Flashback.RECORDER.writeLevelEvent(type, blockPos, data, false);
        }
    }

    @Inject(method = "globalLevelEvent", at = @At("HEAD"))
    public void globalLevelEvent(int type, BlockPos blockPos, int data, CallbackInfo ci) {
        if (Flashback.RECORDER != null && !Flashback.RECORDER.isPaused()) {
            Flashback.RECORDER.writeLevelEvent(type, blockPos, data, true);
        }
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("RETURN"))
    public void playSeededSoundEntity(@Nullable Player player, Entity entity, Holder<SoundEvent> holder, SoundSource soundSource, float volume, float pitch, long seed, CallbackInfo ci) {
        if (player != null && player == this.minecraft.player && Flashback.RECORDER != null && !Flashback.RECORDER.isPaused()) {
            Flashback.RECORDER.writePacketAsync(new ClientboundSoundEntityPacket(holder, soundSource, entity, volume, pitch, seed), ConnectionProtocol.PLAY);
        }
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("RETURN"))
    public void playSeededSoundNormal(@Nullable Player player, double x, double y, double z, Holder<SoundEvent> holder, SoundSource soundSource, float volume, float pitch, long seed, CallbackInfo ci) {
        if (player != null && player == this.minecraft.player && Flashback.RECORDER != null && !Flashback.RECORDER.isPaused()) {
            Flashback.RECORDER.writePacketAsync(new ClientboundSoundPacket(holder, soundSource, x, y, z, volume, pitch, seed), ConnectionProtocol.PLAY);
        }
    }

}
