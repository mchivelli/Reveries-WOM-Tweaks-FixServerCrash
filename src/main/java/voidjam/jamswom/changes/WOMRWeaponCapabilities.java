package voidjam.jamswom.changes;

import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.gameasset.WOMWeaponColliders;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.world.capabilities.item.WOMWeaponCategories;
import voidjam.jamswom.gameassets.WOMRAnimations;
import voidjam.jamswom.gameassets.WOMRSkills;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

@EventBusSubscriber(
   modid = "jamswom",
   bus = Bus.MOD
)
public class WOMRWeaponCapabilities {
    public static final Function<Item, CapabilityItem.Builder> RUINE = (item) -> {
        CapabilityItem.Builder builder = WeaponCapability.builder()
        .category(WeaponCategories.LONGSWORD).styleProvider((entitypatch) -> {
            return entitypatch instanceof PlayerPatch && ((PlayerPatch)entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.BUFFED.get()) != null && (Boolean)((PlayerPatch)entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.BUFFED.get()) ? Styles.OCHS : Styles.TWO_HAND;
        })
        .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
        .collider(WOMWeaponColliders.RUINE)
        .canBePlacedOffhand(false)
        .newStyleCombo(Styles.TWO_HAND, new StaticAnimation[]{
            WOMAnimations.RUINE_AUTO_1, WOMAnimations.RUINE_AUTO_2, 
            WOMAnimations.RUINE_AUTO_3, WOMRAnimations.RUINE_AUTO_4, 
            WOMAnimations.RUINE_CHATIMENT, WOMAnimations.RUINE_COMET
        })
        .newStyleCombo(Styles.OCHS, new StaticAnimation[]{
            WOMAnimations.RUINE_AUTO_1, WOMAnimations.RUINE_AUTO_2, 
            WOMAnimations.RUINE_AUTO_3, WOMRAnimations.RUINE_AUTO_4, 
            WOMAnimations.RUINE_CHATIMENT, WOMAnimations.RUINE_COMET
        })
        .newStyleCombo(Styles.MOUNT, new StaticAnimation[]{Animations.SWORD_MOUNT_ATTACK})
        .innateSkill(Styles.TWO_HAND, (itemstack) -> {
            return WOMSkills.SOUL_SNATCH;
        }).innateSkill(Styles.OCHS, (itemstack) -> {
            return WOMSkills.SOUL_SNATCH;
        }).passiveSkill(WOMSkills.RUINE_PASSIVE)
        .comboCancel((style) -> {
            return false;
        })
        
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.RUINE_IDLE)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.RUINE_WALK)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.RUINE_RUN)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.RUINE_BLOCK)
        .livingMotionModifier(Styles.OCHS, LivingMotions.IDLE, WOMAnimations.RUINE_BOOSTED_IDLE)
        .livingMotionModifier(Styles.OCHS, LivingMotions.WALK, WOMAnimations.RUINE_BOOSTED_WALK)
        .livingMotionModifier(Styles.OCHS, LivingMotions.CHASE, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.OCHS, LivingMotions.RUN, WOMAnimations.RUINE_RUN)
        .livingMotionModifier(Styles.OCHS, LivingMotions.SNEAK, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.OCHS, LivingMotions.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.OCHS, LivingMotions.JUMP, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.OCHS, LivingMotions.SWIM, Animations.BIPED_HOLD_GREATSWORD)
        .livingMotionModifier(Styles.OCHS, LivingMotions.BLOCK, WOMAnimations.RUINE_BLOCK);
        return builder;
    };

    public static final Function<Item, CapabilityItem.Builder> MOONLESS = (item) -> {
        CapabilityItem.Builder builder = WeaponCapability.builder()
        .category(WeaponCategories.TACHI)
        .styleProvider((entitypatch) -> {
           if (entitypatch instanceof PlayerPatch<?> playerpatch) {
              if (playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().hasData((SkillDataKey)WOMSkillDataKeys.VERSO.get()) && (Boolean)playerpatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue((SkillDataKey)WOMSkillDataKeys.VERSO.get())) {
                 return Styles.OCHS;
              }
           }
           return Styles.TWO_HAND;
        })
        .collider(WOMWeaponColliders.MOONLESS)
        .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
        .comboCancel((style) -> {
           return false;
        })
        .canBePlacedOffhand(false)
        .newStyleCombo(Styles.TWO_HAND, new StaticAnimation[]{
            WOMAnimations.MOONLESS_AUTO_1, WOMAnimations.MOONLESS_AUTO_2, 
            WOMAnimations.MOONLESS_AUTO_3, WOMAnimations.MOONLESS_REVERSED_BYPASS, 
            WOMAnimations.MOONLESS_CRESCENT})
        .innateSkill(Styles.TWO_HAND, (itemstack) -> {
           return WOMSkills.lUNAR_ECLIPSE;
        })
        .newStyleCombo(Styles.OCHS, new StaticAnimation[]{
            WOMAnimations.MOONLESS_AUTO_1_VERSO, WOMAnimations.MOONLESS_AUTO_2_VERSO, 
            WOMAnimations.MOONLESS_AUTO_3_VERSO, WOMAnimations.MOONLESS_BYPASS, 
            WOMRAnimations.MOONLESS_FULLMOON})
        .innateSkill(Styles.OCHS, (itemstack) -> {
           return WOMSkills.lUNAR_ECLIPSE;
        }).newStyleCombo(Styles.MOUNT, new StaticAnimation[]{Animations.SWORD_MOUNT_ATTACK})
        .passiveSkill(WOMSkills.LUNAR_ECHO_PASSIVE)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.MOONLESS_IDLE)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.MOONLESS_WALK)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.MOONLESS_RUN)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.MOONLESS_RUN)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
        .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.MOONLESS_GUARD)
        .livingMotionModifier(Styles.OCHS, LivingMotions.IDLE, WOMAnimations.MOONLESS_IDLE)
        .livingMotionModifier(Styles.OCHS, LivingMotions.WALK, WOMAnimations.MOONLESS_WALK)
        .livingMotionModifier(Styles.OCHS, LivingMotions.CHASE, WOMAnimations.MOONLESS_RUN)
        .livingMotionModifier(Styles.OCHS, LivingMotions.RUN, WOMAnimations.MOONLESS_RUN)
        .livingMotionModifier(Styles.OCHS, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR)
        .livingMotionModifier(Styles.OCHS, LivingMotions.BLOCK, WOMAnimations.MOONLESS_GUARD);
        return builder;
     };

     public static final Function<Item, CapabilityItem.Builder> TORMENT = (item) -> {
      CapabilityItem.Builder builder = WeaponCapability.builder().category(WOMWeaponCategories.TORMENT).styleProvider((entitypatch) -> {
         return entitypatch instanceof PlayerPatch && ((PlayerPatch)entitypatch).getSkill(SkillSlots.WEAPON_INNATE).getRemainDuration() > 0 ? Styles.OCHS : Styles.TWO_HAND;
      }).collider(WOMWeaponColliders.TORMENT)
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(Styles.TWO_HAND, new StaticAnimation[]{WOMAnimations.TORMENT_AUTO_1, WOMAnimations.TORMENT_AUTO_2, WOMAnimations.TORMENT_AUTO_3, WOMAnimations.TORMENT_AUTO_4, WOMAnimations.TORMENT_DASH, WOMAnimations.TORMENT_AIRSLAM})
      .newStyleCombo(Styles.OCHS, new StaticAnimation[]{WOMAnimations.TORMENT_BERSERK_AUTO_1, WOMAnimations.TORMENT_BERSERK_AUTO_2, WOMAnimations.TORMENT_BERSERK_DASH, WOMAnimations.TORMENT_BERSERK_AIRSLAM})
      .newStyleCombo(Styles.MOUNT, new StaticAnimation[]{Animations.SWORD_MOUNT_ATTACK})
      .innateSkill(Styles.TWO_HAND, (itemstack) -> {
         return WOMRSkills.TRUE_BERSERK;
      }).innateSkill(Styles.OCHS, (itemstack) -> {
         return WOMRSkills.TRUE_BERSERK;
      }).passiveSkill(WOMRSkills.TORMENT_PASSIVE)
      .comboCancel((style) -> {
         return false;
      }).livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, WOMAnimations.TORMENT_IDLE).livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, WOMAnimations.TORMENT_WALK).livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, WOMAnimations.TORMENT_RUN).livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, WOMAnimations.TORMENT_RUN).livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR).livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, WOMAnimations.TORMENT_CHARGE).livingMotionModifier(Styles.OCHS, LivingMotions.IDLE, WOMAnimations.TORMENT_BERSERK_IDLE).livingMotionModifier(Styles.OCHS, LivingMotions.WALK, WOMAnimations.TORMENT_BERSERK_WALK).livingMotionModifier(Styles.OCHS, LivingMotions.CHASE, WOMAnimations.TORMENT_BERSERK_RUN).livingMotionModifier(Styles.OCHS, LivingMotions.RUN, WOMAnimations.TORMENT_BERSERK_RUN).livingMotionModifier(Styles.OCHS, LivingMotions.SWIM, Animations.BIPED_HOLD_SPEAR);
      return builder;
   };

    public WOMRWeaponCapabilities() {
    }
    
    @SubscribeEvent
       public static void register(WeaponCapabilityPresetRegistryEvent event) {
        event.getTypeEntry().put(new ResourceLocation("wom","ruine"), RUINE);
        event.getTypeEntry().put(new ResourceLocation("wom","moonless"), MOONLESS);
        event.getTypeEntry().put(new ResourceLocation("wom","torment"), TORMENT);
     }
}