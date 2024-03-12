package com.diskree.shutupdeadentities.client.mixins;

import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {

    @Redirect(method = "playAngrySound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    public void playAngry(World world, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        EndermanEntity endermanEntity = (EndermanEntity) (Object) this;
        world.playSoundFromEntity(endermanEntity, sound, category, volume, pitch);
    }
}
