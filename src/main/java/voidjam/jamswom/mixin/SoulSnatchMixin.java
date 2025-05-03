package voidjam.jamswom.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.skill.weaponinnate.SoulSnatchSkill;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Mixin(SoulSnatchSkill.class)
public abstract class SoulSnatchMixin {

    @Inject(
            method = "executeOnServer",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args, CallbackInfo ci) {
        ci.cancel(); // Cancel the original method execution
        SoulSnatchSkill skill = (SoulSnatchSkill)(Object)this; // Explicitly cast this to SoulSnatchSkill
        SkillContainer skillContainer = executer.getSkill(skill); // Get the SkillContainer
        ServerPlayer player = executer.getOriginal();

        if (!player.onGround() && !player.isInWater() && player.fallDistance < 0.1F
                && (player.level().isEmptyBlock(player.blockPosition().below())
                || player.yo - (double)player.blockPosition().getY() > 0.2)) {
            executer.playAnimationSynchronized(WOMAnimations.RUINE_REDEMPTION, 0.0F);
            skillContainer.getDataManager().setDataSync(WOMSkillDataKeys.REDEMPTION.get(), true, player);
            if (skillContainer.getStack() <= 1) {
                if (executer.getSkill(EpicFightSkills.FORBIDDEN_STRENGTH) != null) {
                    executer.consumeForSkill(skill, Skill.Resource.STAMINA,
                            (24.0F - skillContainer.getResource()) * 0.5F);
                    skillContainer.getSkill().setConsumptionSynchronize(executer, 0.0F); // Use static method
                } else {
                    executer.consumeForSkill(skill, Skill.Resource.STAMINA,
                            (executer.getMaxStamina()) * 0.5F);
                    skillContainer.getDataManager().setDataSync(WOMSkillDataKeys.EXPIATION.get(), false, player);
                }
            }
        } else if (player.isSprinting()) {
            executer.playAnimationSynchronized(WOMAnimations.RUINE_EXPIATION, 0.0F);
            skillContainer.getDataManager().setDataSync(WOMSkillDataKeys.EXPIATION.get(), true, player);
            if (skillContainer.getStack() <= 1) {
                if (executer.getSkill(EpicFightSkills.FORBIDDEN_STRENGTH) != null) {
                    executer.consumeForSkill(skill, Skill.Resource.STAMINA,
                            (24.0F - skillContainer.getResource()) * 0.5F);
                    skillContainer.getSkill().setConsumptionSynchronize(executer, 0.0F); // Use static method
                } else {
                    executer.consumeForSkill(skill, Skill.Resource.STAMINA,
                            (executer.getMaxStamina()) * 0.5F);
                    skillContainer.getDataManager().setDataSync(WOMSkillDataKeys.EXPIATION.get(), false, player);
                }
            }
        } else if (skillContainer.getStack() == 9 || player.isCreative()) {
            skillContainer.getSkill().setStackSynchronize(executer, 0); // Use shadowed method
            executer.playAnimationSynchronized(WOMAnimations.RUINE_PLUNDER, 0.0F);
            skillContainer.getDataManager().setData(WOMSkillDataKeys.BUFFING.get(), true);
            skillContainer.getDataManager().setData(WOMSkillDataKeys.BUFFED.get(), false);
            skillContainer.getDataManager().setData(WOMSkillDataKeys.STRENGHT.get(), 0);
        }
    }
}