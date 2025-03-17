package voidjam.jamswom.changes.player;

import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@EventBusSubscriber(
    modid = "jamswom",
    bus = Bus.FORGE
)
public class StaminaChange {
    private static final UUID ATTRIBUTE_MAX_UUID = UUID.fromString("01957962-acf8-727d-b63b-3ab4b3471bc0");

    @SubscribeEvent
    public static void onSpawnEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getAttribute(EpicFightAttributes.MAX_STAMINA.get()).addTransientModifier(new AttributeModifier(ATTRIBUTE_MAX_UUID, "max_stamina_increase", 2F, Operation.MULTIPLY_BASE));
        }
    }
}
