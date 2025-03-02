package voidjam.jamswom.changes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.weaponinnate.TrueBerserkSkill;
import reascer.wom.world.capabilities.item.WOMWeaponCategories;
import voidjam.jamswom.changes.WOMRSkillDataKeys;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.level.block.FractureBlockState;

public class FixTormentPassive extends PassiveSkill {
   private static final UUID EVENT_UUID = UUID.fromString("72eabb8f-f889-4302-80bb-690bb557a008");

   public FixTormentPassive(Skill.Builder<?> builder) {
      super(builder.setActivateType(ActivateType.DURATION_INFINITE));
   }

   public void onInitiate(SkillContainer container) {
      container.getExecuter().getEventListener().addEventListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
         if (((LocalPlayerPatch)event.getPlayerPatch()).getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.TORMENT && container.getExecuter().getEntityState().canBasicAttack() && container.getExecuter().getStamina() > 0.0F) {
            ((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).startUsingItem(InteractionHand.MAIN_HAND);
            ((Player)container.getExecuter().getOriginal()).setSprinting(false);
         }

      });
      container.getExecuter().getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
         if (((ServerPlayerPatch)event.getPlayerPatch()).getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WOMWeaponCategories.TORMENT && container.getExecuter().getEntityState().canBasicAttack() && container.getExecuter().getStamina() > 0.0F) {
            ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).startUsingItem(InteractionHand.MAIN_HAND);
            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGING.get(), true, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
            ((Player)container.getExecuter().getOriginal()).setSprinting(false);
         }

      });
      container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID, (event) -> {
         if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get()) || (Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get())) {
            event.setDamage(event.getDamage() * 3.0F);
         }

      });
      container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_DAMAGE, EVENT_UUID, (event) -> {
         if (event.getDamageSource().getAnimation() instanceof AttackAnimation) {
            ServerPlayerPatch entitypatch = (ServerPlayerPatch)event.getPlayerPatch();
            AttackAnimation anim = (AttackAnimation)event.getDamageSource().getAnimation();
            AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(event.getDamageSource().getAnimation());
            float elapsedTime = player.getElapsedTime();
            AttackAnimation.Phase phase = anim.getPhaseByTime(elapsedTime);
            if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get()) && phase == anim.phases[0]) {
               container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get(), false, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
               ((ServerLevel)((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).level()).sendParticles(ParticleTypes.SMOKE, event.getTarget().getX() - 0.15, event.getTarget().getY() + 1.2, event.getTarget().getZ() - 0.15, 25, 0.0, 0.0, 0.0, 0.2);
               ((ServerLevel)((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).level()).sendParticles(ParticleTypes.SMOKE, event.getTarget().getX() - 0.15, event.getTarget().getY() + 1.2, event.getTarget().getZ() - 0.15, 25, 0.0, 0.0, 0.0, 1.0);
            } else {
               container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get(), false, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
               container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get(), false, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
            }
         }

      });
      container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
         if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.SUPER_ARMOR.get()) || (Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING.get())) {
            event.setAmount(event.getAmount() * 0.8F);
            ((EpicFightDamageSource)event.getDamageSource()).setStunType(StunType.NONE);
         }

      });
      container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
         if (event.getAnimation().equals(WOMAnimations.SHADOWSTEP_BACKWARD) || event.getAnimation().equals(WOMAnimations.SHADOWSTEP_FORWARD)) {
            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.MOVESPEED.get(), false, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
         }

         if (!event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_1) && !event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_2) && !event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_3) && !event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_4)) {
         }

         container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get(), false, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
         if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get())) {
            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get(), true, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
         }

         container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGING.get(), false, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
         if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.SAVED_CHARGE.get()) < 20) {
            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.SAVED_CHARGE.get(), (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()), (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
         }

         container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get(), 0, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
      });
      container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
         if (!event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_1) && !event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_2) && !event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_3) && !event.getAnimation().equals(WOMAnimations.TORMENT_AUTO_4)) {
         }

         container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get(), false, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
         container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get(), false, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
      });
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      container.getExecuter().getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID);
      container.getExecuter().getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
      container.getExecuter().getEventListener().removeListener(EventType.MODIFY_DAMAGE_EVENT, EVENT_UUID);
      container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_DAMAGE, EVENT_UUID);
      container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_POST, EVENT_UUID);
      container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
      container.getExecuter().getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDraw(SkillContainer container) {
      if (!(container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof TrueBerserkSkill)) {
         return false;
      } else {
         return ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING.get()) || (Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get()) || (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.SAVED_CHARGE.get()) > 0) && !(Boolean)container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.ACTIVE.get());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      poseStack.translate(0.0F, (float)gui.getSlidingProgression(), 0.0F);
      RenderSystem.setShaderTexture(0, WOMSkills.TRUE_BERSERK.getSkillTexture());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.blit(WOMSkills.TRUE_BERSERK.getSkillTexture(), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
      int charge;
      if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.SAVED_CHARGE.get()) > 0) {
         charge = ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.SAVED_CHARGE.get()) + 10) / 30;
      } else {
         charge = ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) + 10) / 30;
      }

      if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get())) {
         guiGraphics.drawString(gui.font, String.valueOf(charge), x + 10.0F, y + 4.0F, 16777215, true);
         guiGraphics.drawString(gui.font, "x3", x + 7.0F, y + 13.0F, 16777215, true);
      } else {
         guiGraphics.drawString(gui.font, String.valueOf(charge), x + 10.0F, y + 6.0F, 16777215, true);
      }

      poseStack.popPose();
   }

   public void updateContainer(SkillContainer container) {
      PlayerPatch entitypatch;
      float stamina;
      float maxStamina;
      float staminaRegen;
      if (container.getExecuter().isLogicalClient() && (container.getExecuter().getCurrentLivingMotion() == LivingMotions.WALK || container.getExecuter().getCurrentLivingMotion() == LivingMotions.RUN) && !((Player)container.getExecuter().getOriginal()).isUsingItem()) {
         entitypatch = container.getExecuter();
         float interpolation = 0.0F;
         OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getAnimator().getPose(interpolation), Armatures.BIPED.toolR);
         transformMatrix.translate(new Vec3f(0.0F, -0.0F, -1.2F));
         OpenMatrix4f.mul((new OpenMatrix4f()).rotate(-((float)Math.toRadians((double)(((Player)entitypatch.getOriginal()).yBodyRotO + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)), transformMatrix, transformMatrix);
         transformMatrix.translate(new Vec3f(0.0F, 0.0F, -((new Random()).nextFloat() * 1.0F)));
         stamina = transformMatrix.m30 + (float)((Player)entitypatch.getOriginal()).getX();
         maxStamina = transformMatrix.m31 + (float)((Player)entitypatch.getOriginal()).getY();
         staminaRegen = transformMatrix.m32 + (float)((Player)entitypatch.getOriginal()).getZ();
         BlockState blockstate = ((Player)entitypatch.getOriginal()).level().getBlockState(new BlockPos.MutableBlockPos((double)stamina, (double)maxStamina, (double)staminaRegen));
         new BlockPos.MutableBlockPos((double)stamina, (double)maxStamina, (double)staminaRegen);

         while((blockstate.getBlock() instanceof BushBlock || blockstate.isAir()) && !blockstate.is(Blocks.VOID_AIR)) {
            --maxStamina;
            blockstate = ((Player)entitypatch.getOriginal()).level().getBlockState(new BlockPos.MutableBlockPos((double)stamina, (double)maxStamina, (double)staminaRegen));
         }

         while(blockstate instanceof FractureBlockState) {
            BlockPos blockpos = new BlockPos.MutableBlockPos((double)stamina, (double)(maxStamina--), (double)staminaRegen);
            blockstate = ((Player)entitypatch.getOriginal()).level().getBlockState(blockpos.below());
         }

         if ((double)transformMatrix.m31 + ((Player)entitypatch.getOriginal()).getY() < (double)(maxStamina + 1.5F)) {
            for(int i = 0; i < 2; ++i) {
               ((Player)entitypatch.getOriginal()).level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), (double)transformMatrix.m30 + ((Player)entitypatch.getOriginal()).getX(), (double)transformMatrix.m31 + ((Player)entitypatch.getOriginal()).getY() - 0.20000000298023224, (double)transformMatrix.m32 + ((Player)entitypatch.getOriginal()).getZ(), (double)(((new Random()).nextFloat() - 0.5F) * 0.005F), (double)((new Random()).nextFloat() * 0.02F), (double)(((new Random()).nextFloat() - 0.5F) * 0.005F));
            }
         }
      }

      if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get())) {
         entitypatch = container.getExecuter();
         int numberOf = 2;
         float partialScale = 1.0F / (float)(numberOf - 1);
         stamina = 0.0F;

         for(int i = 0; i < numberOf; ++i) {
            OpenMatrix4f transformMatrix = entitypatch.getArmature().getBindedTransformFor(entitypatch.getAnimator().getPose(stamina), Armatures.BIPED.toolR);
            transformMatrix.translate(new Vec3f(0.0F, 0.0F, -1.0F));
            OpenMatrix4f.mul((new OpenMatrix4f()).rotate(-((float)Math.toRadians((double)(((Player)entitypatch.getOriginal()).yBodyRotO + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)), transformMatrix, transformMatrix);
            transformMatrix.translate(new Vec3f(0.0F, 0.0F, -((new Random()).nextFloat() * 1.0F)));
            ((Player)entitypatch.getOriginal()).level().addParticle(new DustParticleOptions(new Vector3f(0.8F, 0.6F, 0.0F), 1.0F), (double)transformMatrix.m30 + ((Player)entitypatch.getOriginal()).getX() + (double)(((new Random()).nextFloat() - 0.5F) * 0.55F), (double)transformMatrix.m31 + ((Player)entitypatch.getOriginal()).getY() + (double)(((new Random()).nextFloat() - 0.5F) * 0.55F), (double)transformMatrix.m32 + ((Player)entitypatch.getOriginal()).getZ() + (double)(((new Random()).nextFloat() - 0.5F) * 0.55F), 0.0, 0.0, 0.0);
            ((Player)entitypatch.getOriginal()).level().addParticle(ParticleTypes.FLAME, (double)transformMatrix.m30 + ((Player)entitypatch.getOriginal()).getX() + (double)(((new Random()).nextFloat() - 0.5F) * 0.75F), (double)transformMatrix.m31 + ((Player)entitypatch.getOriginal()).getY() + (double)(((new Random()).nextFloat() - 0.5F) * 0.75F), (double)transformMatrix.m32 + ((Player)entitypatch.getOriginal()).getZ() + (double)(((new Random()).nextFloat() - 0.5F) * 0.75F), 0.0, 0.0, 0.0);
            stamina += partialScale;
         }
      }

      if (!container.getExecuter().isLogicalClient()) {
         AttributeModifier charging_Movementspeed = new AttributeModifier(EVENT_UUID, "torment.charging_movespeed", 3.0, Operation.MULTIPLY_TOTAL);
         ServerPlayerPatch executer = (ServerPlayerPatch)container.getExecuter();
         if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING.get()) && !((Player)container.getExecuter().getOriginal()).isUsingItem() && container.getExecuter().getEntityState().canBasicAttack()) {
            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.MOVESPEED.get(), true, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
            int animation_timer = (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get());
            if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) < 20 && (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.SAVED_CHARGE.get()) >= 20) {
               animation_timer = (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.SAVED_CHARGE.get());
            }

            if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) >= 110) {
               ((Player)container.getExecuter().getOriginal()).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 4, 2, true, false, false));
               ((Player)container.getExecuter().getOriginal()).level().playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY(), ((Player)container.getExecuter().getOriginal()).getZ(), (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
               if (!((Player)container.getExecuter().getOriginal()).isCreative()) {
                  executer.consumeForSkill(this, Resource.STAMINA, 3.0F);
               }
            } else if (animation_timer >= 80) {
               container.getExecuter().playAnimationSynchronized(WOMAnimations.TORMENT_CHARGED_ATTACK_3, 0.0F);
            } else if (animation_timer >= 50) {
               container.getExecuter().playAnimationSynchronized(WOMAnimations.TORMENT_CHARGED_ATTACK_2, 0.0F);
            } else if (animation_timer >= 20) {
               container.getExecuter().playAnimationSynchronized(WOMAnimations.TORMENT_CHARGED_ATTACK_1, 0.0F);
            } else if (!(Boolean)container.getExecuter().getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.ACTIVE.get())) {
               ((Player)container.getExecuter().getOriginal()).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 8, 1, true, false, false));
               ((Player)container.getExecuter().getOriginal()).level().playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY(), ((Player)container.getExecuter().getOriginal()).getZ(), (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
               if (!((Player)container.getExecuter().getOriginal()).isCreative()) {
                  executer.consumeForSkill(this, Resource.STAMINA, 3.0F);
               }
            } else {
               ((Player)container.getExecuter().getOriginal()).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 14, 3, true, false, false));
               ((Player)container.getExecuter().getOriginal()).level().playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY(), ((Player)container.getExecuter().getOriginal()).getZ(), (SoundEvent)EpicFightSounds.WHOOSH_BIG.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
               stamina = container.getExecuter().getStamina();
               maxStamina = container.getExecuter().getMaxStamina();
               staminaRegen = (float)((Player)container.getExecuter().getOriginal()).getAttributeValue((Attribute)EpicFightAttributes.STAMINA_REGEN.get());
               int regenStandbyTime = 900 / (int)(30.0F * staminaRegen);
               if (container.getExecuter().getTickSinceLastAction() > regenStandbyTime) {
                  if (!((Player)container.getExecuter().getOriginal()).isCreative()) {
                     float staminaFactor = 1.0F + (float)Math.pow((double)(stamina / (maxStamina - stamina * 0.5F)), 2.0);
                     executer.consumeForSkill(this, Resource.STAMINA, 2.0F + maxStamina * 0.05F * staminaFactor * staminaRegen);
                  }
               } else if (!((Player)container.getExecuter().getOriginal()).isCreative()) {
                  executer.consumeForSkill(this, Resource.STAMINA, 2.0F);
               }

               container.getExecuter().playAnimationSynchronized(WOMAnimations.TORMENT_CHARGED_ATTACK_2, 0.0F);
            }

            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGING.get(), false, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get(), 0, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.SAVED_CHARGE.get(), 0, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
            ((Player)container.getExecuter().getOriginal()).getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(charging_Movementspeed);
         }

         if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING.get())) {
            ((Player)container.getExecuter().getOriginal()).addEffect(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 5, 0, true, false, false));
            if (((Player)container.getExecuter().getOriginal()).getAttribute(Attributes.MOVEMENT_SPEED).getModifier(EVENT_UUID) == null && (Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.MOVESPEED.get())) {
               ((Player)container.getExecuter().getOriginal()).getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(charging_Movementspeed);
            }

            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get(), (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) + 1, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
            if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) <= 130) {
               if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) == 20) {
                  container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.SAVED_CHARGE.get(), 0, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
                  ((Player)container.getExecuter().getOriginal()).level().playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY(), ((Player)container.getExecuter().getOriginal()).getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 0.6F);
                  this.consume_stamina(container);
               }

               if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) == 50) {
                  ((Player)container.getExecuter().getOriginal()).level().playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY(), ((Player)container.getExecuter().getOriginal()).getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 0.65F);
                  this.consume_stamina(container);
               }

               if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) == 80) {
                  ((Player)container.getExecuter().getOriginal()).level().playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY(), ((Player)container.getExecuter().getOriginal()).getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 0.7F);
                  this.consume_stamina(container);
               }

               if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) == 110) {
                  container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGED_ATTACK.get(), true, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
                  ((Player)container.getExecuter().getOriginal()).level().playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY(), ((Player)container.getExecuter().getOriginal()).getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 0.5F);
                  ((Player)container.getExecuter().getOriginal()).level().playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY(), ((Player)container.getExecuter().getOriginal()).getZ(), SoundEvents.BELL_BLOCK, SoundSource.MASTER, 2.5F, 0.5F);
                  this.consume_stamina(container);
               }

               if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get()) == 130) {
                  container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.CHARGING_TIME.get(), 0, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
                  ((Player)container.getExecuter().getOriginal()).getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(charging_Movementspeed);
               }
            }
         } else {
            ((Player)container.getExecuter().getOriginal()).getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(charging_Movementspeed);
         }
      }

   }

   public void consume_stamina(SkillContainer container) {
      if (!((Player)container.getExecuter().getOriginal()).isCreative()) {
         if (container.getExecuter().getStamina() <= 0.0F) {
            ((Player)container.getExecuter().getOriginal()).stopUsingItem();
         }

         if (!container.getExecuter().consumeForSkill(this, Resource.STAMINA, 3.0F, true)) {
            ((Player)container.getExecuter().getOriginal()).level().playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY(), ((Player)container.getExecuter().getOriginal()).getZ(), SoundEvents.LAVA_EXTINGUISH, ((Player)container.getExecuter().getOriginal()).getSoundSource(), 1.0F, 2.0F);
         }
      }

   }
}
