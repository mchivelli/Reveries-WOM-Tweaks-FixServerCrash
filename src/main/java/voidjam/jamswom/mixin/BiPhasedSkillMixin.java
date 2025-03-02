package voidjam.jamswom.mixin;

import java.util.UUID;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import reascer.wom.skill.identity.BiPhasedSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.skill.SkillDataKey;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.WOMSkillDataKeys;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.client.gui.BattleModeGui;

@Mixin(BiPhasedSkill.class)
public class BiPhasedSkillMixin {
    
    private static final UUID EVENT_UUID = UUID.fromString("05e61b67-4531-4127-b973-9f572acede5c");

    @Inject(method = "onInitiate", at = @At("HEAD"), cancellable = true, remap = false)
    private void replaceOnInitiate(SkillContainer container, CallbackInfo ci) {
        container.getExecuter().getEventListener().addEventListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event) -> {
            if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.CHARGE.get()) < 7) {
                container.getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.CHARGE.get(), (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.CHARGE.get()) + 1, (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal());
                System.out.println(container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.CHARGE.get()));
                if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.CHARGE.get()) == 7) {
                   container.getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.TIMER.get(), 200, (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal());
                }
             }
        });

        container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
            if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.CHARGE.get()) == 7 && ((DamageSource)event.getDamageSource()).getEntity() != null) {
                event.setCanceled(true);
                event.setResult(ResultType.MISSED);
             }
        });
        ci.cancel();
    }

    @Inject(method = "drawOnGui", at = @At("HEAD"), cancellable = true, remap = false)
    public void replaceDrawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, CallbackInfo ci) {
        ci.cancel();
        
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0.0F, (float) gui.getSlidingProgression(), 0.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        String text = "";
        int charge = container.getDataManager().getDataValue(WOMSkillDataKeys.CHARGE.get());

        if (charge == 8) {
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.5F);
            guiGraphics.blit(new ResourceLocation("wom", "textures/gui/skills/bi_phased.png"), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
            text = String.valueOf(container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) / 20);
        } else if (charge < 7) {
            text = String.valueOf(charge);
            guiGraphics.blit(new ResourceLocation("wom", "textures/gui/skills/bi_phased_blue.png"), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        } else {
            text = String.valueOf(container.getDataManager().getDataValue(WOMSkillDataKeys.TIMER.get()) / 20);
            guiGraphics.blit(new ResourceLocation("wom", "textures/gui/skills/bi_phased_red.png"), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.drawString(gui.font, text, x + 13.0F - (float) (gui.font.width(text) / 2), y + 10.0F, 16777215, true);
        poseStack.popPose();
    }

    @Inject(method = "updateContainer", at = @At("HEAD"), cancellable = true, remap = false)
    private void replaceUpdateContainer(SkillContainer container, CallbackInfo ci) {
         if (!container.getExecuter().isLogicalClient()) {
         if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) == 1 && (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.CHARGE.get()) == 7) {
            ((ServerLevel)((Player)container.getExecuter().getOriginal()).level()).playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY() + (double)(((Player)container.getExecuter().getOriginal()).getBbHeight() / 2.0F), ((Player)container.getExecuter().getOriginal()).getZ(), SoundEvents.GHAST_SCREAM, ((Player)container.getExecuter().getOriginal()).getSoundSource(), 1.0F, 0.5F);
         }

         if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) == 1 && (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.CHARGE.get()) == 8) {
            ((ServerLevel)((Player)container.getExecuter().getOriginal()).level()).sendParticles(new DustParticleOptions(new Vector3f(0.0F, 0.0F, 1.0F), 1.0F), ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY() + (double)(((Player)container.getExecuter().getOriginal()).getBbHeight() / 2.0F), ((Player)container.getExecuter().getOriginal()).getZ(), 20, 0.5, 0.8, 0.5, 0.2);
         }

         if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) == 200 && (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.CHARGE.get()) == 7) {
            ((ServerLevel)((Player)container.getExecuter().getOriginal()).level()).playSound((Player)null, ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY() + (double)(((Player)container.getExecuter().getOriginal()).getBbHeight() / 2.0F), ((Player)container.getExecuter().getOriginal()).getZ(), SoundEvents.GHAST_SCREAM, ((Player)container.getExecuter().getOriginal()).getSoundSource(), 1.0F, 1.0F);
            ((ServerLevel)((Player)container.getExecuter().getOriginal()).level()).sendParticles(new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F), ((Player)container.getExecuter().getOriginal()).getX(), ((Player)container.getExecuter().getOriginal()).getY() + (double)(((Player)container.getExecuter().getOriginal()).getBbHeight() / 2.0F), ((Player)container.getExecuter().getOriginal()).getZ(), 20, 0.5, 0.8, 0.5, 0.2);
         }

         if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) > 0) {
            container.getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.TIMER.get(), (Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) - 1, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
            if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.TIMER.get()) == 0) {
               container.getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.TIMER.get(), 200, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
               if ((Integer)container.getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.CHARGE.get()) == 7) {
                  container.getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.CHARGE.get(), 8, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
               } else {
                  container.getDataManager().setDataSync((SkillDataKey)WOMSkillDataKeys.CHARGE.get(), 0, (ServerPlayer)((ServerPlayerPatch)container.getExecuter()).getOriginal());
               }
            }
         }
      }
      ci.cancel();
    }
}
