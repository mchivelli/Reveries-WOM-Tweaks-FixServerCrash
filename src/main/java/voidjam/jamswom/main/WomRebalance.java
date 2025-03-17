package voidjam.jamswom.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import voidjam.jamswom.changes.WOMRSkillDataKeys;
import voidjam.jamswom.gameassets.WOMRAnimations;
// The value here should match an entry in the META-INF/mods.toml file
@Mod("jamswom")
public class WOMRebalance
{
	public static final String MODID = "jamswom";
	public static final String CONFIG_FILE_PATH = WOMRebalance.MODID + ".toml";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	private static WOMRebalance instance;
	
	public static WOMRebalance getInstance() {
		return instance;
	}
	
    public WOMRebalance() {
    	instance = this;
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	MinecraftForge.EVENT_BUS.register(this);
		bus.addListener(WOMRAnimations::registerAnimations);
		WOMRSkillDataKeys.DATA_KEYS.register(bus);

        //ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(IngameConfigurationScreen::new));
    }
}