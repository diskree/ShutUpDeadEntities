package com.diskree.shutupdeadentities.client.mixins;

import com.diskree.shutupdeadentities.client.PortalTriggerSoundInstance;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @WrapOperation(
        method = "handlePortalTransitionEffect",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forLocalAmbience(Lnet/minecraft/sounds/SoundEvent;FF)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"
        )
    )
    public SimpleSoundInstance overridePortalTriggerSound(SoundEvent sound, float pitch, float volume, Operation<SimpleSoundInstance> original) {
        LocalPlayer localPlayer = (LocalPlayer) (Object) this;
        return new PortalTriggerSoundInstance(localPlayer.portalProcess, sound, pitch, volume);
    }
}
