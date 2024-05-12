package com.diskree.shutupdeadentities.client.mixins;

import com.diskree.shutupdeadentities.client.NetherPortalTriggerSoundInstance;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Redirect(
        method = "updateNausea",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sound/PositionedSoundInstance;ambient(Lnet/minecraft/sound/SoundEvent;FF)Lnet/minecraft/client/sound/PositionedSoundInstance;"
        )
    )
    public PositionedSoundInstance playPortal(SoundEvent sound, float pitch, float volume) {
        ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity) (Object) this;
        return new NetherPortalTriggerSoundInstance(clientPlayerEntity, sound, pitch, volume);
    }
}
