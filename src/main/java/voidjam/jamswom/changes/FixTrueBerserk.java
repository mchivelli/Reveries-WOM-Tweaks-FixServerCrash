package voidjam.jamswom.changes;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.skill.WOMSkillDataKeys;
import voidjam.jamswom.changes.WOMRSkillDataKeys;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class FixTrueBerserk extends WeaponInnateSkill {
   public FixTrueBerserk(Skill.Builder<?> builder) {
      super(builder.setActivateType(ActivateType.TOGGLE));
   }

   public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
      if ((Boolean)executer.getSkill(this).getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.ACTIVE.get())) {
         super.cancelOnServer(executer, args);
         executer.getSkill(this).getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.ACTIVE.get(), false, (ServerPlayer)executer.getOriginal());
         executer.getSkill(this).setResource(0.0F);
         this.setDurationSynchronize(executer, 0);
         this.setStackSynchronize(executer, 0);
         executer.getSkill(this).deactivate();
         executer.modifyLivingMotionByCurrentItem(false);
      } else {
         executer.playAnimationSynchronized(WOMAnimations.TORMENT_BERSERK_CONVERT, 0.0F);
         ((ServerPlayer)executer.getOriginal()).level().playSound((Player)null, ((ServerPlayer)executer.getOriginal()).xo, ((ServerPlayer)executer.getOriginal()).yo, ((ServerPlayer)executer.getOriginal()).zo, SoundEvents.DRAGON_FIREBALL_EXPLODE, ((ServerPlayer)executer.getOriginal()).getSoundSource(), 1.0F, 0.5F);
         ((ServerLevel)((ServerPlayer)executer.getOriginal()).level()).sendParticles(ParticleTypes.LARGE_SMOKE, ((ServerPlayer)executer.getOriginal()).getX() - 0.15, ((ServerPlayer)executer.getOriginal()).getY() + 1.2, ((ServerPlayer)executer.getOriginal()).getZ() - 0.15, 150, 0.3, 0.6, 0.3, 0.1);
         executer.getSkill(this).getDataManager().setData((SkillDataKey)WOMRSkillDataKeys.TIMER.get(), 2);
         executer.getSkill(this).getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.ACTIVE.get(), true, (ServerPlayer)executer.getOriginal());
         this.setMaxDurationSynchronize(executer, this.maxDuration + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, (LivingEntity)executer.getOriginal()));
         this.setDurationSynchronize(executer, (Integer)executer.getSkill(this).getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.TIMER.get()));
         this.setStackSynchronize(executer, 1);
         executer.getSkill(this).activate();
         executer.modifyLivingMotionByCurrentItem(false);
      }

   }

   public WeaponInnateSkill registerPropertiesToAnimation() {
      return this;
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
      List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
      this.generateTooltipforPhase(list, itemStack, cap, playerCap, (Map)this.properties.get(0), "Auto attack :");
      this.generateTooltipforPhase(list, itemStack, cap, playerCap, (Map)this.properties.get(1), "Dash attack :");
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public void onScreen(LocalPlayerPatch playerpatch, float resolutionX, float resolutionY) {
    if ((Boolean)playerpatch.getSkill(this).getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.ACTIVE.get())) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, new ResourceLocation("wom", "textures/gui/overlay/true_berserk.png"));
         GlStateManager._enableBlend();
         GlStateManager._disableDepthTest();
         GlStateManager._blendFunc(770, 771);
         Tesselator tessellator = Tesselator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuilder();
         bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         bufferbuilder.vertex(0.0, 0.0, 1.0).uv(0.0F, 0.0F).endVertex();
         bufferbuilder.vertex(0.0, (double)resolutionY, 1.0).uv(0.0F, 1.0F).endVertex();
         bufferbuilder.vertex((double)resolutionX, (double)resolutionY, 1.0).uv(1.0F, 1.0F).endVertex();
         bufferbuilder.vertex((double)resolutionX, 0.0, 1.0).uv(1.0F, 0.0F).endVertex();
         tessellator.end();
      }

   }

   public void updateContainer(SkillContainer container) {
      if (container.isActivated()) {
         if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.ACTIVE.get())) {
            ((Player)container.getExecuter().getOriginal()).addEffect(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 2, 0, true, false, false));
            if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.TIMER.get()) > 0) {
               container.getDataManager().setData((SkillDataKey)WOMRSkillDataKeys.TIMER.get(), (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMRSkillDataKeys.TIMER.get()) - 1);
            } else {
               container.getDataManager().setData((SkillDataKey)WOMRSkillDataKeys.TIMER.get(), 3);
               if (container.getRemainDuration() > 1) {
                  if (!container.getExecuter().isLogicalClient()) {
                     this.setDurationSynchronize((ServerPlayerPatch)container.getExecuter(), container.getRemainDuration() - 1);
                  }
               } else if (!(((Player)container.getExecuter().getOriginal()).getHealth() - 2 > 0.0F) && !((Player)container.getExecuter().getOriginal()).isCreative()) {
                  if (!container.getExecuter().isLogicalClient()) {
                     container.getSkill().cancelOnServer((ServerPlayerPatch)container.getExecuter(), (FriendlyByteBuf)null);
                     container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.ACTIVE.get(), false, (ServerPlayer)container.getExecuter().getOriginal());
                     container.setResource(0.0F);
                     this.setDurationSynchronize((ServerPlayerPatch)container.getExecuter(), 0);
                     this.setStackSynchronize((ServerPlayerPatch)container.getExecuter(), 0);
                     container.getExecuter().getSkill(this).deactivate();
                     ((ServerPlayerPatch)container.getExecuter()).modifyLivingMotionByCurrentItem(false);
                  }

                  container.deactivate();
               } else {
                  container.getExecuter().getOriginal().setHealth(((Player)container.getExecuter().getOriginal()).getHealth() - 1);
                  //EpicFightDamageSource selfdamage = new EpicFightDamageSource(((Player)container.getExecuter().getOriginal()).level().damageSources().magic());
                  //selfdamage.setStunType(StunType.NONE);
                  //((Player)container.getExecuter().getOriginal()).hurt(selfdamage, ((Player)container.getExecuter().getOriginal()).getMaxHealth() * 0.04F);
                  if (!container.getExecuter().isLogicalClient()) {
                     if (!((Player)container.getExecuter().getOriginal()).isCreative()) {
                        ((ServerLevel)((Player)container.getExecuter().getOriginal()).level()).sendParticles(ParticleTypes.SMOKE, ((Player)container.getExecuter().getOriginal()).getX() - 0.2, ((Player)container.getExecuter().getOriginal()).getY() + 1.3, ((Player)container.getExecuter().getOriginal()).getZ() - 0.2, 40, 0.6, 0.8, 0.6, 0.05);
                     }

                     this.setDurationSynchronize((ServerPlayerPatch)container.getExecuter(), this.maxDuration + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, (LivingEntity)container.getExecuter().getOriginal()));
                  }
               }
            }
         } else if (!container.getExecuter().isLogicalClient()) {
            container.getSkill().cancelOnServer((ServerPlayerPatch)container.getExecuter(), (FriendlyByteBuf)null);
            container.getDataManager().setDataSync((SkillDataKey)WOMRSkillDataKeys.ACTIVE.get(), false, (ServerPlayer)container.getExecuter().getOriginal());
            container.setResource(0.0F);
            this.setDurationSynchronize((ServerPlayerPatch)container.getExecuter(), 0);
            this.setStackSynchronize((ServerPlayerPatch)container.getExecuter(), 0);
            container.getExecuter().getSkill(this).deactivate();
            ((ServerPlayerPatch)container.getExecuter()).modifyLivingMotionByCurrentItem(false);
         }
      }

   }
}

