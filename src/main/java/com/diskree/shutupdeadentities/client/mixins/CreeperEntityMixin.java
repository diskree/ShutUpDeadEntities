package com.diskree.shutupdeadentities.client.mixins;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/CreeperEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    public void playPrimed(CreeperEntity entity, SoundEvent sound, float volume, float pitch) {
        CreeperEntity creeperEntity = (CreeperEntity) (Object) this;
        creeperEntity.getWorld().playSoundFromEntity(creeperEntity, sound, creeperEntity.getSoundCategory(), volume, pitch);
    }
}
