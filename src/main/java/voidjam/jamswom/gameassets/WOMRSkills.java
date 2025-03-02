package voidjam.jamswom.gameassets;

import java.util.Set;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import reascer.wom.skill.weaponinnate.TrueBerserkSkill;
import reascer.wom.skill.weaponpassive.TormentPassiveSkill;
import voidjam.jamswom.changes.FixTormentPassive;
import voidjam.jamswom.changes.FixTrueBerserk;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.passive.PassiveSkill;

@EventBusSubscriber(
   modid = "jamswom",
   bus = Bus.MOD
)
public class WOMRSkills {
   public static Skill TRUE_BERSERK;
   public static Skill TORMENT_PASSIVE;

   public WOMRSkills() {
   }
   @SubscribeEvent
   public static void buildSkillEvent(SkillBuildEvent build) {
    SkillBuildEvent.ModRegistryWorker modRegistry = build.createRegistryWorker("jamswom");
    WeaponInnateSkill trueBerserkSkill = (WeaponInnateSkill)modRegistry.build("true_berserk", FixTrueBerserk::new, WeaponInnateSkill.createWeaponInnateBuilder());
      trueBerserkSkill.newProperty().addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.2F)).addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(8.0F)).addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0]))).newProperty().addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F)).addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(10.0F)).addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])));
      TRUE_BERSERK = trueBerserkSkill;

    TORMENT_PASSIVE = modRegistry.build("torment_passive", FixTormentPassive::new, Skill.createBuilder().setCategory(SkillCategories.WEAPON_PASSIVE));
   }
}