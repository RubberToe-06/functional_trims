package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

public final class IronTrimEffect {

    private static final double KB_STRENGTH =
            ConfigManager.get()
                    .iron
                    .shieldKnockbackStrengthMultiplier;

    private static final double CHANCE_TO_DEFLECT =
            ConfigManager.get()
                    .iron
                    .projectileReflectChance;

    private static final double Y_BOOST = 0.15;

    private static final double MAX_RANGE = 4.0;

    private static final boolean COOLDOWN_NEGATION_ENABLED =
            ConfigManager.get()
                    .iron
                    .axeAttackResistanceEnabled;

    private static final List<PendingShieldKnockback>
            PENDING_SHIELD_KNOCKBACKS =
            new ArrayList<>();

    private IronTrimEffect() {
    }

    public static void register() {

        NeoForge.EVENT_BUS.addListener(
                IronTrimEffect::onIncomingDamage
        );

        NeoForge.EVENT_BUS.addListener(
                IronTrimEffect::onDamagePre
        );

        NeoForge.EVENT_BUS.addListener(
                IronTrimEffect::onShieldBlock
        );

        NeoForge.EVENT_BUS.addListener(
                IronTrimEffect::onServerTick
        );
    }

    private static void onIncomingDamage(
            LivingIncomingDamageEvent event) {

        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (!TrimHelper.hasFullTrim(
                player,
                TrimMaterials.IRON)) {
            return;
        }

        if (!FTConfig.isTrimEnabled("iron")) {
            return;
        }

        DamageSource source =
                event.getSource();

        Entity sourceEntity =
                source.getDirectEntity();

        boolean isProjectile =
                sourceEntity instanceof Projectile;

        boolean isFallingBlock =
                source.is(DamageTypes.FALLING_BLOCK)
                        || sourceEntity
                        instanceof FallingBlockEntity;

        if (!isProjectile
                && !isFallingBlock) {
            return;
        }

        if (player.getRandom().nextFloat()
                >= CHANCE_TO_DEFLECT) {
            return;
        }

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ANVIL_PLACE,
                SoundSource.PLAYERS,
                0.6F,
                1.6F
        );

        player.level().sendParticles(
                ParticleTypes.CRIT,
                player.getX(),
                player.getY(0.5),
                player.getZ(),
                8,
                0.2,
                0.2,
                0.2,
                0.03
        );

        if (sourceEntity instanceof Projectile projectile) {

            Vec3 velocity =
                    projectile.getDeltaMovement();

            projectile.setDeltaMovement(
                    -velocity.x,
                    velocity.y * 0.75,
                    -velocity.z
            );

            projectile.needsSync = true;

            ModCriteria.TRIM_TRIGGER
                    .get()
                    .trigger(
                            player,
                            "iron",
                            "reflect_projectile"
                    );
        }

        event.setCanceled(true);
    }

    private static void onDamagePre(
            LivingDamageEvent.Pre event) {

        ServerPlayer player =
                getEligibleIronPlayer(
                        event.getEntity()
                );

        if (player == null) {
            return;
        }

        DamageSource source =
                event.getSource();

        handleCriticalHitReduction(
                player,
                source,
                event
        );
    }

    private static void onShieldBlock(
            LivingShieldBlockEvent event) {

        ServerPlayer player =
                getEligibleIronPlayer(
                        event.getEntity()
                );

        if (player == null) {
            return;
        }

        if (!event.getBlocked()) {
            return;
        }

        DamageSource source =
                event.getDamageSource();

        handleShieldKnockback(
                player,
                source
        );
    }

    private static ServerPlayer getEligibleIronPlayer(
            LivingEntity entity) {

        if (!(entity instanceof ServerPlayer player)) {
            return null;
        }

        if (player.isDeadOrDying()) {
            return null;
        }

        if (!TrimHelper.hasFullTrim(
                player,
                TrimMaterials.IRON)) {
            return null;
        }

        if (!FTConfig.isTrimEnabled("iron")) {
            return null;
        }

        return player;
    }

    private static void handleCriticalHitReduction(
            ServerPlayer player,
            DamageSource source,
            LivingDamageEvent.Pre event) {

        Entity attacker =
                source.getEntity();

        if (!(attacker
                instanceof ServerPlayer playerAttacker)) {
            return;
        }

        boolean isCriticalHit =
                playerAttacker.fallDistance > 0.0
                        && !playerAttacker.onGround()
                        && !playerAttacker.onClimbable()
                        && !playerAttacker.isInWater();

        if (!isCriticalHit) {
            return;
        }

        float finalDamage =
                event.getNewDamage();

        if (finalDamage <= 0.0F) {
            return;
        }

        event.setNewDamage(
                finalDamage * 0.5F
        );

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.ANVIL_PLACE,
                SoundSource.PLAYERS,
                0.3F,
                1.15F
        );
    }

    private static void handleShieldKnockback(
            ServerPlayer player,
            DamageSource source) {

        Entity attackerEntity =
                source.getEntity();

        if (!(attackerEntity
                instanceof LivingEntity attacker)) {
            return;
        }

        if (!source.isDirect()
                || source.getDirectEntity()
                instanceof Projectile) {
            return;
        }

        double distanceSq =
                player.distanceToSqr(attacker);

        if (distanceSq >
                MAX_RANGE * MAX_RANGE) {
            return;
        }

        double dx =
                attacker.getX()
                        - player.getX();

        double dz =
                attacker.getZ()
                        - player.getZ();

        double distance =
                Math.sqrt(
                        dx * dx
                                + dz * dz
                );

        if (distance < 1.0E-6) {
            distance = 1.0E-6;
        }

        PENDING_SHIELD_KNOCKBACKS.add(
                new PendingShieldKnockback(
                        player,
                        attacker,
                        dx / distance,
                        dz / distance
                )
        );
    }

    private static void applyPendingShieldKnockbacks() {

        if (PENDING_SHIELD_KNOCKBACKS.isEmpty()) {
            return;
        }

        List<PendingShieldKnockback> pending =
                new ArrayList<>(
                        PENDING_SHIELD_KNOCKBACKS
                );

        PENDING_SHIELD_KNOCKBACKS.clear();

        for (PendingShieldKnockback knockback :
                pending) {

            ServerPlayer defender =
                    knockback.defender();

            LivingEntity attacker =
                    knockback.attacker();

            if (defender.isRemoved()
                    || attacker.isRemoved()
                    || attacker.isDeadOrDying()) {
                continue;
            }

            attacker.knockback(
                    KB_STRENGTH,
                    -knockback.nx(),
                    -knockback.nz()
            );

            attacker.push(
                    0.0,
                    Y_BOOST,
                    0.0
            );

            attacker.needsSync = true;

            if (attacker
                    instanceof ServerPlayer attackerPlayer) {

                attackerPlayer.connection.send(
                        new ClientboundSetEntityMotionPacket(
                                attacker
                        )
                );
            }

            defender.level().playSound(
                    null,
                    defender.blockPosition(),
                    SoundEvents.ANVIL_PLACE,
                    SoundSource.PLAYERS,
                    0.3F,
                    1.15F
            );

            ModCriteria.TRIM_TRIGGER
                    .get()
                    .trigger(
                            defender,
                            "iron",
                            "knockback_attacker"
                    );
        }
    }

    private static void onServerTick(
            ServerTickEvent.Post event) {

        applyPendingShieldKnockbacks();

        if (!FTConfig.isTrimEnabled("iron")) {
            return;
        }

        if (!COOLDOWN_NEGATION_ENABLED) {
            return;
        }

        for (ServerPlayer player :
                event.getServer()
                        .getPlayerList()
                        .getPlayers()) {

            if (!TrimHelper.hasFullTrim(
                    player,
                    TrimMaterials.IRON)) {

                continue;
            }

            ItemCooldowns cooldowns =
                    player.getCooldowns();

            ItemStack offhand =
                    player.getOffhandItem();

            if (offhand.is(Items.SHIELD)) {

                Identifier group =
                        cooldowns.getCooldownGroup(
                                offhand
                        );

                cooldowns.removeCooldown(group);
            }

            ItemStack mainHand =
                    player.getMainHandItem();

            if (mainHand.is(Items.SHIELD)) {

                Identifier group =
                        cooldowns.getCooldownGroup(
                                mainHand
                        );

                cooldowns.removeCooldown(group);
            }
        }
    }

    private record PendingShieldKnockback(
            ServerPlayer defender,
            LivingEntity attacker,
            double nx,
            double nz) {
    }

}