package voidjam.jamswom.main;

import net.minecraftforge.common.ForgeConfigSpec;

public class WOMRConfigs {
    public static final ForgeConfigSpec.DoubleValue BA_STAMINA_CONSUMPTION;
    public static final ForgeConfigSpec.DoubleValue BASE_STAMINA_INCREASE;
    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Stamina Configurations");
        BA_STAMINA_CONSUMPTION = createDouble(builder, "basic_attack_stamina_consumption", 2d, 0d, "(Requires Respawn) Amount of Stamina that Basic Attack consumes");
        BASE_STAMINA_INCREASE = createDouble(builder, "base_stamina_increase", 30d, 0d, "Amount of additional starting stamina");
        builder.pop();

        SPEC = builder.build();
    }
    private static ForgeConfigSpec.DoubleValue createDouble(ForgeConfigSpec.Builder builder, String key, double defaultValue, double min, String... comment) {
        return builder
                .translation("config."+ WOMRebalance.MODID+".common."+key)
                .comment(comment)
                .defineInRange(key, defaultValue, min, Double.MAX_VALUE);
    }
}
