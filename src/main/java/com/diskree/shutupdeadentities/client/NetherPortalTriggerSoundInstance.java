package com.diskree.shutupdeadentities.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class NetherPortalTriggerSoundInstance extends PositionedSoundInstance implements TickableSoundInstance {

    private static final int SMOOTH_FADE_OUT_TICKS = 20;

    private final float initialVolume;
    private final float initialPitch;
    private final ClientPlayerEntity player;

    private int smoothFadeOutTicksCounter = SMOOTH_FADE_OUT_TICKS;
    private boolean done;

    public NetherPortalTriggerSoundInstance(
        ClientPlayerEntity player,
        SoundEvent soundEvent,
        float pitch,
        float volume
    ) {
        super(
            soundEvent.getId(),
            SoundCategory.AMBIENT,
            volume,
            pitch,
            SoundInstance.createRandom(),
            false,
            0,
            SoundInstance.AttenuationType.NONE,
            0.0,
            0.0,
            0.0,
            true
        );
        this.player = player;
        initialVolume = volume;
        initialPitch = pitch;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void tick() {
        if (player == null || player.portalManager != null && !player.portalManager.isInPortal()) {
            if (smoothFadeOutTicksCounter >= 0) {
                float progress = (float) smoothFadeOutTicksCounter / SMOOTH_FADE_OUT_TICKS;
                volume = initialVolume * progress;
                pitch = initialPitch * progress;
                smoothFadeOutTicksCounter--;
            } else {
                done = true;
            }
        }
    }
}
