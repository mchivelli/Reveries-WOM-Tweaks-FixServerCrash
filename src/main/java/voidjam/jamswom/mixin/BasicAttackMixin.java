package voidjam.jamswom.mixin;

import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import voidjam.jamswom.main.WOMRConfigs;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

@Mixin(BasicAttack.class)
public class BasicAttackMixin {
   private static final UUID ATTACK_UUID = UUID.fromString("afbfef3f-8233-4fb3-815b-2053e329b0eb");
   private static final AttributeModifier ATTACK_SPEED_MODIFIER = new AttributeModifier(
           ATTACK_UUID,
           "Attack Speed Slowdown",
           -0.45,
           AttributeModifier.Operation.MULTIPLY_TOTAL
   );

   @Inject(
           method = {"onInitiate"},
           at = {@At("HEAD")},
           remap = false
   )
   public void onInitiate(SkillContainer container, CallbackInfo ci) {
      container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, ATTACK_UUID, (event) -> {
         AttributeInstance attr = container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED);
         if (attr.hasModifier(ATTACK_SPEED_MODIFIER) &&
                 (!WOMRConfigs.DO_STAMINA_PENALTY.get() ||
                         WOMRConfigs.BA_STAMINA_CONSUMPTION.get().floatValue() == 0.0F ||
                         container.getExecuter().getStamina() - WOMRConfigs.BA_STAMINA_CONSUMPTION.get().floatValue() > 0.0F)) {
            attr.removeModifier(ATTACK_UUID);
         }
      });
   }

   @Inject(
           method = {"executeOnServer"},
           at = {@At("HEAD")},
           remap = false
   )
   public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args, CallbackInfo ci) {
      ((ServerPlayer)executer.getOriginal()).getAttribute(Attributes.ATTACK_SPEED).removePermanentModifier(ATTACK_UUID);
      if (executer.getStamina() - WOMRConfigs.BA_STAMINA_CONSUMPTION.get().floatValue() > 0.0F) {
         executer.setStamina(executer.getStamina() - WOMRConfigs.BA_STAMINA_CONSUMPTION.get().floatValue());
         executer.modifyLivingMotionByCurrentItem(true);
      } else {
         executer.setStamina(0.0F);
         executer.getOriginal().getAttribute(Attributes.ATTACK_SPEED).addPermanentModifier(ATTACK_SPEED_MODIFIER);
      }
   }

   @Inject(
           method = {"onRemoved"},
           at = {@At("HEAD")},
           remap = false
   )
   public void onRemoved(SkillContainer container, CallbackInfo ci) {
      container.getExecuter().getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, ATTACK_UUID);
   }

   @Inject(
           method = {"updateContainer"},
           at = {@At("HEAD")},
           remap = false
   )
   public void updateContainer(SkillContainer container, CallbackInfo ci) {
      if (!container.getExecuter().isLogicalClient()) {
         AttributeInstance attr = container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED);
         if (attr.hasModifier(ATTACK_SPEED_MODIFIER) &&
                 (!WOMRConfigs.DO_STAMINA_PENALTY.get() ||
                         WOMRConfigs.BA_STAMINA_CONSUMPTION.get().floatValue() == 0.0F ||
                         container.getExecuter().getStamina() - WOMRConfigs.BA_STAMINA_CONSUMPTION.get().floatValue() > 0.0F)) {
            attr.removeModifier(ATTACK_UUID);
         }
      }
   }
}