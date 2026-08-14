package io.github.diskria.shutupdeadentities.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PortalProcessor;

@Environment(EnvType.CLIENT)
public class PortalTriggerSoundInstance extends SimpleSoundInstance implements TickableSoundInstance {

    private static final int SMOOTH_FADE_OUT_TICKS = 20;

    private final float initialVolume;
    private final float initialPitch;
    private final PortalProcessor portalProcess;

    private int smoothFadeOutTicksCounter = SMOOTH_FADE_OUT_TICKS;
    private boolean stopped;

    public PortalTriggerSoundInstance(PortalProcessor portalProcess, SoundEvent sound, float pitch, float volume) {
        super(
            sound.location(), SoundSource.AMBIENT,
            volume, pitch,
            SoundInstance.createUnseededRandom(),
            false, 0, SoundInstance.Attenuation.NONE,
            0.0, 0.0, 0.0, true
        );
        this.portalProcess = portalProcess;
        initialVolume = volume;
        initialPitch = pitch;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void tick() {
        if (portalProcess != null && portalProcess.isInsidePortalThisTick()) {
            return;
        }
        if (smoothFadeOutTicksCounter >= 0) {
            float progress = (float) smoothFadeOutTicksCounter / SMOOTH_FADE_OUT_TICKS;
            volume = initialVolume * progress;
            pitch = initialPitch * progress;
            smoothFadeOutTicksCounter--;
        } else {
            stopped = true;
        }
    }
}
