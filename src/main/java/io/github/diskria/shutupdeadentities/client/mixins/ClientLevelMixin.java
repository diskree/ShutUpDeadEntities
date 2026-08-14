package io.github.diskria.shutupdeadentities.client.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract void playSeededSound(@Nullable Entity except, Entity sourceEntity, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed);

    @Inject(
        method = "playSound",
        at = @At("HEAD"),
        cancellable = true
    )
    private void redirectSoundToEntity(
        double x,
        double y,
        double z,
        SoundEvent sound,
        SoundSource source,
        float volume,
        float pitch,
        boolean distanceDelay,
        long seed,
        CallbackInfo ci
    ) {
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        Identifier soundId = sound.location();
        String soundPath = soundId.getPath();
        List<? extends Entity> candidates = null;
        if (soundPath.startsWith("item.")) {
            String[] itemSoundParts = soundPath.split("\\.");
            if (itemSoundParts.length < 2) {
                return;
            }
            Identifier entityId = Identifier.fromNamespaceAndPath(soundId.getNamespace(), itemSoundParts[1]);
            Item item = BuiltInRegistries.ITEM.getValue(entityId);
            if (item == Items.CROSSBOW) {
                candidates = minecraft.level.getEntitiesOfClass(
                    LivingEntity.class,
                    new AABB(x, y, z, x, y, z).inflate(1),
                    entity -> entity instanceof CrossbowAttackMob
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
            Identifier entityId = Identifier.fromNamespaceAndPath(soundId.getNamespace(), entitySoundParts[1]);
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(entityId);
            candidates = minecraft.level.getEntities(
                entityType,
                new AABB(x, y, z, x, y, z).inflate(1),
                entity -> entity instanceof LivingEntity livingEntity && sound != livingEntity.getDeathSound()
            );
        }
        if (candidates == null || candidates.isEmpty()) {
            return;
        }
        for (Entity candidate : candidates) {
            if (candidate.isAlive()) {
                playSeededSound(
                    minecraft.player,
                    candidate,
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound),
                    source,
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
