package com.diskree.shutupdeadentities.client.mixins;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackingSoundInstance.class)
public class EntityTrackingSoundInstanceMixin {

    @Unique
    private static final int SMOOTH_FADE_OUT_TICKS = 30;

    @Unique
    private int smoothFadeOutTicksCounter = SMOOTH_FADE_OUT_TICKS;

    @Unique
    private float initialVolume = -1;

    @Unique
    private float initialPitch = -1;

    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity, long seed, CallbackInfo ci) {
        initialVolume = volume;
        initialPitch = pitch;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isRemoved()Z"))
    public boolean stopWhenLivingEntityDead(Entity entity) {
        return entity instanceof LivingEntity livingEntity ? livingEntity.isDead() : this.entity.isRemoved();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/EntityTrackingSoundInstance;setDone()V"))
    public void smoothFadeOut(EntityTrackingSoundInstance soundInstance) {
        EntityTrackingSoundInstance entityTrackingSoundInstance = (EntityTrackingSoundInstance) (Object) this;
        if (smoothFadeOutTicksCounter >= 0) {
            float progress = (float) smoothFadeOutTicksCounter / SMOOTH_FADE_OUT_TICKS;
            entityTrackingSoundInstance.volume = initialVolume * progress;
            entityTrackingSoundInstance.pitch = initialPitch * progress;
            smoothFadeOutTicksCounter--;
        } else {
            soundInstance.setDone();
        }
    }
}
