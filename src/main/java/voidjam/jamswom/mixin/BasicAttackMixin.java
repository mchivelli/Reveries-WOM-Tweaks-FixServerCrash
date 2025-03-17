package voidjam.jamswom.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

@Mixin(BasicAttack.class)
public class BasicAttackMixin {
   private static final UUID EVENT_UUID = UUID.fromString("0195797b-0b7b-76b5-8b95-7b4cdc7dc470");
   private static final UUID ATTACK_UUID = UUID.fromString("afbfef3f-8233-4fb3-815b-2053e329b0eb");
   private static final AttributeModifier ATTACK_SPEED_MODIFIER = new AttributeModifier(
            ATTACK_UUID,
            "Attack Speed Slowdown",
            -0.45,
            AttributeModifier.Operation.MULTIPLY_TOTAL
    );
   public BasicAttackMixin() {
   }

   @Inject(
      method = {"onInitiate"},
      at = {@At("HEAD")},
      remap = false
   )
   public void onInitiate(SkillContainer container, CallbackInfo ci) {
      container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, ATTACK_UUID, (event) -> {
         if (container.getExecuter().getStamina() - 2F <= 0.0F) {
            container.getExecuter().getOriginal().getAttribute(Attributes.ATTACK_SPEED).removeModifier(ATTACK_UUID);
         }
      });
   }

   @Inject(
      method = {"executeOnServer"},
      at = {@At("HEAD")},
      remap = false
   )
   
   public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args, CallbackInfo ci) {
      executer.getOriginal().getAttribute(Attributes.ATTACK_SPEED).removePermanentModifier(ATTACK_UUID);
      if (executer.getStamina() - 2F > 0.0F) {
         executer.setStamina(executer.getStamina() - 2F);
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
}
