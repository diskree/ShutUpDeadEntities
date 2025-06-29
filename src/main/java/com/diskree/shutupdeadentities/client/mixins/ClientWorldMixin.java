package com.diskree.shutupdeadentities.client.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void playSoundFromEntity(
        @Nullable Entity source,
        Entity entity,
        RegistryEntry<SoundEvent> sound,
        SoundCategory category,
        float volume,
        float pitch,
        long seed
    );

    @Inject(
        method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZJ)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void redirectSoundToEntity(
        double x,
        double y,
        double z,
        SoundEvent sound,
        SoundCategory category,
        float volume,
        float pitch,
        boolean useDistance,
        long seed,
        CallbackInfo ci
    ) {
        if (client.world == null || client.player == null) {
            return;
        }
        Identifier soundId = sound.id();
        String soundPath = soundId.getPath();
        List<? extends Entity> candidates = null;
        if (soundPath.startsWith("item.")) {
            String[] itemSoundParts = soundPath.split("\\.");
            if (itemSoundParts.length < 2) {
                return;
            }
            Item item = Registries.ITEM
                .getOptionalValue(Identifier.of(soundId.getNamespace(), itemSoundParts[1]))
                .orElse(null);
            if (item == null) {
                return;
            }
            if (item == Items.CROSSBOW) {
                candidates = client.world.getEntitiesByClass(
                    LivingEntity.class,
                    new Box(x, y, z, x, y, z).expand(1),
                    entity -> entity instanceof CrossbowUser
                );
            }
        } else {
            if (!soundPath.startsWith("entity.")) {
                return;
            }
            String[] entitySoundParts = soundPath.split("\\.");
            if (entitySoundParts.length < 2) {
                return;
            }
            EntityType<? extends Entity> filter = Registries.ENTITY_TYPE
                .getOptionalValue(Identifier.of(soundId.getNamespace(), entitySoundParts[1]))
                .orElse(null);
            if (filter == null) {
                return;
            }
            candidates = client.world.getEntitiesByType(
                filter,
                new Box(x, y, z, x, y, z).expand(1),
                entity -> entity instanceof LivingEntity livingEntity && sound != livingEntity.getDeathSound()
            );
        }
        if (candidates == null || candidates.isEmpty()) {
            return;
        }
        for (Entity candidate : candidates) {
            if (candidate.isAlive()) {
                playSoundFromEntity(
                    client.player,
                    candidate,
                    Registries.SOUND_EVENT.getEntry(sound),
                    category,
                    volume,
                    pitch,
                    seed
                );
                break;
            }
        }
        ci.cancel();
    }
}
