package voidjam.jamswom.gameassets;

import java.util.Set;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import reascer.wom.animation.attacks.BasicAttackNoRotAnimation;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.gameasset.WOMWeaponColliders;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.world.damagesources.WOMExtraDamageInstance;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.TimePeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.TimeStampedEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.damagesource.StunType;

public class WOMRAnimations {
   public static StaticAnimation RUINE_AUTO_4;
   public static StaticAnimation MOONLESS_FULLMOON;

   public WOMRAnimations() {
   }
   public static void registerAnimations(AnimationRegistryEvent event) {
      event.getRegistryMap().put("jamswom", WOMRAnimations::build);
   }
   private static void build() {
      HumanoidArmature biped = Armatures.BIPED;
    RUINE_AUTO_4 = (new BasicMultipleAttackAnimation(0.05F, "biped/ruine/ruine_auto_4", biped, 
        new AttackAnimation.Phase[]{
            new AttackAnimation.Phase(0.0F, 0.5F, 0.6F, 0.65F, 0.65F, biped.toolR, WOMWeaponColliders.RUINE_COMET), 
            new AttackAnimation.Phase(0.65F, 0.8F, 1.05F, 1.45F, Float.MAX_VALUE, biped.toolR, (Collider)null)
            .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
            .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(7.5F))
        }))
        .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F))
        .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(WOMExtraDamageInstance.TARGET_LOST_HEALTH.create(new float[]{0.1F})), 1)
        .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.4F))
        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
        .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG, 1)
        .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE), 1)
        .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.5F)
        .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
        .addEvents(new AnimationEvent.TimeStampedEvent[]{TimeStampedEvent.create(0.4F, reascer.wom.gameasset.WOMAnimations.ReuseableEvents.RUINE_COMET_AIRBURST, Side.CLIENT)});
    
    
        MOONLESS_FULLMOON = (new BasicAttackNoRotAnimation(0.05F, "biped/moonless/moonless_fullmoon", biped, new AttackAnimation.Phase[]{
        new AttackAnimation.Phase(0.0F, 0.0F, 0.15F, 0.19F, 0.19F, biped.toolR, WOMWeaponColliders.MOONLESS_BYPASS), 
        new AttackAnimation.Phase(0.19F, 0.2F, 0.35F, 0.39F, 0.39F, biped.toolR, WOMWeaponColliders.MOONLESS_FULLMOON), 
        new AttackAnimation.Phase(0.39F, 0.4F, 0.55F, 0.59F, 0.59F, biped.toolR, WOMWeaponColliders.MOONLESS_FULLMOON), 
        new AttackAnimation.Phase(0.59F, 0.6F, 0.75F, 0.79F, 0.79F, biped.toolR, WOMWeaponColliders.MOONLESS_FULLMOON), 
        new AttackAnimation.Phase(0.79F, 0.8F, 1.0F, 1.6F, Float.MAX_VALUE, biped.toolR, WOMWeaponColliders.MOONLESS_FULLMOON)
    })).addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
    .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F))
    .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
    .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F), 1)
    .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F), 1)
    .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 1)
    .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F), 2)
    .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F), 2)
    .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 2)
    .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F), 3)
    .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F), 3)
    .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD, 3)
    .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F), 4)
    .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.75F), 4)
    .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.FALL, 4)
    .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.4F)
    .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
    .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
    .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
    .addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
    .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.4F}))
    .addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, false).newTimePair(0.0F, 2.0F)
    .addState(EntityState.UPDATE_LIVING_MOTION, false)
    .addEvents(
           TimePeriodEvent.create(0.0F, 1.8F, (entitypatch, self, params) -> {
         ((LivingEntity)entitypatch.getOriginal()).resetFallDistance();
         if (entitypatch.getOriginal() instanceof Player) {
            Player player = (Player)entitypatch.getOriginal();
            player.yCloak = 0.0;
            player.yCloakO = 0.0;
         }

      }, Side.BOTH))
      .addEvents(new AnimationEvent.TimeStampedEvent[]{TimeStampedEvent.create(1.4F, (entitypatch, self, params) -> {
         Vec3 bodyFloorPos = reascer.wom.gameasset.WOMAnimations.ReuseableEvents.getfloor(entitypatch, self, new Vec3f(0.0F, 0.0F, 0.0F), Armatures.BIPED.rootJoint);
         Vec3 position2 = new Vec3(0.0, bodyFloorPos.y - ((LivingEntity)entitypatch.getOriginal()).getY(), 0.0);
         ((LivingEntity)entitypatch.getOriginal()).move(MoverType.SELF, position2);
         ((LivingEntity)entitypatch.getOriginal()).resetFallDistance();
         ((LivingEntity)entitypatch.getOriginal()).setDeltaMovement(0.0, 0.0, 0.0);
      }, Side.CLIENT), TimeStampedEvent.create(1.4F, (entitypatch, self, params) -> {
         PlayerPatch<?> playerPatch = (PlayerPatch)entitypatch;
         if (playerPatch.getSkill(WOMSkills.TIME_TRAVEL) != null && (Integer)playerPatch.getSkill(WOMSkills.TIME_TRAVEL).getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) == -1) {
            playerPatch.getSkill(WOMSkills.TIME_TRAVEL).getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.TIMER.get(), 0, (ServerPlayer)playerPatch.getOriginal());
         }
       }, Side.SERVER), TimeStampedEvent.create(0.7F, (entitypatch, self, params) -> {
         PlayerPatch<?> playerPatch = (PlayerPatch)entitypatch;
         if (playerPatch.getSkill(WOMSkills.TIME_TRAVEL) != null && (Integer)playerPatch.getSkill(WOMSkills.TIME_TRAVEL).getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) > 0) {
            if ((Integer)playerPatch.getSkill(WOMSkills.TIME_TRAVEL).getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) > 6) {
               if (!playerPatch.isLogicalClient()) {
                  playerPatch.getSkill(WOMSkills.TIME_TRAVEL).getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.TIMER.get(), (Integer)playerPatch.getSkill(WOMSkills.TIME_TRAVEL).getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) - 10, (ServerPlayer)playerPatch.getOriginal());
                  if ((Integer)playerPatch.getSkill(WOMSkills.TIME_TRAVEL).getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) < 6) {
                     playerPatch.getSkill(WOMSkills.TIME_TRAVEL).getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.TIMER.get(), -1, (ServerPlayer)playerPatch.getOriginal());
                  }
               }

               if (entitypatch instanceof ServerPlayerPatch) {
                  ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)entitypatch;
                  ((ServerPlayer)serverPlayerPatch.getOriginal()).level().playSound((Player)null, ((ServerPlayer)serverPlayerPatch.getOriginal()).blockPosition(), SoundEvents.ENDERMAN_TELEPORT, ((ServerPlayer)serverPlayerPatch.getOriginal()).getSoundSource(), 0.5F, 2.0F);
                  Vec3 directionVec3 = Vec3.directionFromRotation(0.0F, ((ServerPlayer)serverPlayerPatch.getOriginal()).getYRot() + 90.0F).scale(0.20000000298023224);
                  ((ServerLevel)((ServerPlayer)serverPlayerPatch.getOriginal()).level()).sendParticles(ParticleTypes.ENCHANT, ((ServerPlayer)serverPlayerPatch.getOriginal()).getX() + directionVec3.x, ((ServerPlayer)serverPlayerPatch.getOriginal()).getY() + (double)(((ServerPlayer)serverPlayerPatch.getOriginal()).getBbHeight() / 2.0F), ((ServerPlayer)serverPlayerPatch.getOriginal()).getZ() + directionVec3.z, 15, 0.0, 0.0, 0.0, 1.0);
               }
               entitypatch.getAnimator().getPlayerFor(self).setElapsedTimeCurrent(0.35F);
               ((LivingEntity)entitypatch.getOriginal()).setDeltaMovement(0.0, 0.0, 0.0);
            } else if (!playerPatch.isLogicalClient()) {
               playerPatch.getSkill(WOMSkills.TIME_TRAVEL).getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.TIMER.get(), -1, (ServerPlayer)playerPatch.getOriginal());
            }
         }
        }, Side.BOTH), TimeStampedEvent.create(1.15F, reascer.wom.gameasset.WOMAnimations.ReuseableEvents.BLINK, Side.SERVER), TimeStampedEvent.create(1.55F, reascer.wom.gameasset.WOMAnimations.ReuseableEvents.BLINK, Side.SERVER)});
    }
}
