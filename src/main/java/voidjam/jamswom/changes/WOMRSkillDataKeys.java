package voidjam.jamswom.changes;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import reascer.wom.skill.dodges.RavangerForceSkill;
import reascer.wom.skill.weaponinnate.RegierungSkill;
import reascer.wom.skill.weaponinnate.SolarArcanaSkill;
import reascer.wom.skill.weaponinnate.TrueBerserkSkill;
import reascer.wom.skill.weaponpassive.SolarPassiveSkill;
import reascer.wom.skill.weaponpassive.TormentPassiveSkill;
import yesman.epicfight.skill.SkillDataKey;

public class WOMRSkillDataKeys {
    public static final DeferredRegister<SkillDataKey<?>> DATA_KEYS = DeferredRegister.create(new ResourceLocation("epicfight", "skill_data_keys"), "jamswom");

    public static final RegistryObject<SkillDataKey<Boolean>> ACTIVE;

    public static final RegistryObject<SkillDataKey<Integer>> TIMER;

    public static final RegistryObject<SkillDataKey<Integer>> CHARGE;

    public static final RegistryObject<SkillDataKey<Integer>> CHARGING_TIME;

    public static final RegistryObject<SkillDataKey<Integer>> SAVED_CHARGE;

    public static final RegistryObject<SkillDataKey<Boolean>> CHARGING;

    public static final RegistryObject<SkillDataKey<Boolean>> CHARGED;
    
    public static final RegistryObject<SkillDataKey<Boolean>> CHARGED_ATTACK;
    
    public static final RegistryObject<SkillDataKey<Boolean>> SUPER_ARMOR;

    public static final RegistryObject<SkillDataKey<Boolean>> MOVESPEED;

    static {
        ACTIVE = DATA_KEYS.register("active", () ->
        SkillDataKey.createBooleanKey(false, false, new Class[]{
            FixTrueBerserk.class})
        );

        CHARGE = DATA_KEYS.register("charge", () -> 
            SkillDataKey.createIntKey(0, false, new Class[]{FixTormentPassive.class})
        );

        TIMER = DATA_KEYS.register("timer", () ->
        SkillDataKey.createIntKey(0, false, new Class[]{
            FixTrueBerserk.class})
        );

        CHARGING_TIME = DATA_KEYS.register("charging_time", () -> {
         return SkillDataKey.createIntKey(0, false, new Class[]{FixTormentPassive.class});
        });

        SAVED_CHARGE = DATA_KEYS.register("saved_charge", () -> {
            return SkillDataKey.createIntKey(0, false, new Class[]{FixTormentPassive.class});
        });

        CHARGING = DATA_KEYS.register("charging", () -> {
            return SkillDataKey.createBooleanKey(false, false, new Class[]{FixTormentPassive.class});
        });

        CHARGED = DATA_KEYS.register("charged", () -> {
            return SkillDataKey.createBooleanKey(false, false, new Class[]{FixTormentPassive.class});
        });

        CHARGED_ATTACK = DATA_KEYS.register("charged_attack", () -> {
            return SkillDataKey.createBooleanKey(false, false, new Class[]{FixTormentPassive.class});
        });

        SUPER_ARMOR = DATA_KEYS.register("super_armor", () -> {
         return SkillDataKey.createBooleanKey(false, false, new Class[]{FixTrueBerserk.class, FixTormentPassive.class});
         });
      
        MOVESPEED = DATA_KEYS.register("movespeed", () -> {
            return SkillDataKey.createBooleanKey(false, false, new Class[]{FixTormentPassive.class});
        });

    }
}
