package functional_trims.trim_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AmethystVisionEffect extends StatusEffect {
    public AmethystVisionEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xAA00FF);
        this.applySound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME);
    }

    private static final HashMap<UUID, Set<LivingEntity>> glowingByPlayer = new HashMap<>();
    private static final String TEAM_NAME = "amethyst_glow";

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true; // runs every tick
    }


    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        UUID id = entity.getUuid();
        glowingByPlayer.putIfAbsent(id, new HashSet<>());
        Set<LivingEntity> glowing = glowingByPlayer.get(id);

        double radius = 25.0;
        var box = entity.getBoundingBox().expand(radius);
        var nearby = world.getEntitiesByClass(LivingEntity.class, box, e -> e != entity && e.isAlive());

        // Ensure team exists
        Scoreboard scoreboard = world.getScoreboard();
        Team team = scoreboard.getTeam(TEAM_NAME);
        if (team == null) {
            team = scoreboard.addTeam(TEAM_NAME);
            team.setColor(Formatting.LIGHT_PURPLE);
            team.setShowFriendlyInvisibles(false);
        }

        // Remove entities that left range
        glowing.removeIf(e -> {
            if (!nearby.contains(e) || !e.isAlive() || e.isRemoved()) {
                if (e.isGlowing()) e.setGlowing(false);
                if (e.getScoreboardTeam() != null && TEAM_NAME.equals(e.getScoreboardTeam().getName())) {
                    scoreboard.removeScoreHolderFromTeam(e.getNameForScoreboard(), e.getScoreboardTeam());
                }
                return true;
            }
            return false;
        });

        // Add new entities (only once)
        for (var e : nearby) {
            if (!glowing.contains(e)) {
                // Assign to purple team first
                if (e.getScoreboardTeam() != team) {
                    scoreboard.addScoreHolderToTeam(e.getNameForScoreboard(), team);
                }
                // Then glow (only once)
                if (!e.isGlowing()) {
                    e.setGlowing(true);
                }
                glowing.add(e);
            }
        }

        return true;
    }

    @Override
    public void onRemoved(AttributeContainer attributes) {
        glowingByPlayer.forEach((uuid, set) -> {
            for (var e : set) {
                if (e.isAlive()) {
                    if (e.isGlowing()) e.setGlowing(false);
                    var team = e.getScoreboardTeam();
                    if (team != null && TEAM_NAME.equals(team.getName())) {
                        e.getWorld().getScoreboard().removeScoreHolderFromTeam(e.getNameForScoreboard(), team);
                    }
                }
            }
        });
        glowingByPlayer.clear();
    }

    @Override
    public void onEntityRemoval(ServerWorld world, LivingEntity entity, int amplifier, net.minecraft.entity.Entity.RemovalReason reason) {
        Set<LivingEntity> set = glowingByPlayer.remove(entity.getUuid());
        if (set != null) {
            for (var e : set) {
                if (e.isAlive()) {
                    if (e.isGlowing()) e.setGlowing(false);
                    var team = e.getScoreboardTeam();
                    if (team != null && TEAM_NAME.equals(team.getName())) {
                        world.getScoreboard().removeScoreHolderFromTeam(e.getNameForScoreboard(), team);
                    }
                }
            }
        }
    }
}
