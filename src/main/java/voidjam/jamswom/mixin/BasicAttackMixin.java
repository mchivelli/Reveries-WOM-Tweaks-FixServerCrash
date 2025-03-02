package voidjam.jamswom.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.FriendlyByteBuf;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

@Mixin(BasicAttack.class)
public class BasicAttackMixin {
   private static final UUID EVENT_UUID = UUID.fromString("a42e0198-fdbc-11eb-9a03-0242ac130003");

    @Inject(method = "onInitiate", at = @At("HEAD"), remap = false)
    public void onInitiate(SkillContainer container, CallbackInfo ci) {
      container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_CLIENT, EVENT_UUID, (event) -> {
         event.getAnimation().addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
            if ((container.getExecuter().getStamina() - 1.6F) >= 0.0F) {
               return 1.0F * speed;
              } else {
               return 0.75F * speed;
            }
         });
      });
	}
   @Inject(method = "executeOnServer", at = @At("HEAD"), remap = false)
   public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args, CallbackInfo ci) {
      if ((executer.getStamina() - 1.6F) >= 0.0F) {
         executer.setStamina(executer.getStamina() - 1.6F);
        } else {
         executer.setStamina(0.0F);
      }
   }

   @Inject(method = "onRemoved", at = @At("HEAD"), remap = false)
    public void onRemoved(SkillContainer container, CallbackInfo ci) {
      container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_CLIENT, EVENT_UUID);
	}
}
