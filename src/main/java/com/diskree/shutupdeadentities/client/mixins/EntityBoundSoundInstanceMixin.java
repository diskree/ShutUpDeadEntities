package com.diskree.shutupdeadentities.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBoundSoundInstance.class)
public class EntityBoundSoundInstanceMixin {

    @Unique
    private static final int SMOOTH_FADE_OUT_TICKS = 20;

    @Unique
    private int smoothFadeOutTicksCounter = SMOOTH_FADE_OUT_TICKS;

    @Unique
    private float initialVolume = -1;

    @Unique
    private float initialPitch = -1;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void init(
        SoundEvent event, SoundSource source, float volume, float pitch, Entity entity, long seed, CallbackInfo ci
    ) {
        initialVolume = volume;
        initialPitch = pitch;
    }

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;isRemoved()Z"
        )
    )
    public boolean stopWhenLivingEntityDead(Entity entity, Operation<Boolean> original) {
        return entity instanceof LivingEntity livingEntity ? livingEntity.isDeadOrDying() : original.call(entity);
    }

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/EntityBoundSoundInstance;stop()V"
        )
    )
    public void smoothFadeOut(EntityBoundSoundInstance instance, Operation<Void> original) {
        EntityBoundSoundInstance entityTrackingSoundInstance = (EntityBoundSoundInstance) (Object) this;
        if (smoothFadeOutTicksCounter >= 0) {
            float progress = (float) smoothFadeOutTicksCounter / SMOOTH_FADE_OUT_TICKS;
            entityTrackingSoundInstance.volume = initialVolume * progress;
            entityTrackingSoundInstance.pitch = initialPitch * progress;
            smoothFadeOutTicksCounter--;
        } else {
            original.call(instance);
        }
    }
}
