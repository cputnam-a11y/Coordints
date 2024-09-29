package coordints;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Coordints implements ModInitializer {
	public static final String MOD_ID = "coordints";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		NeoForgeConfigRegistry.INSTANCE.register("coordints", ModConfig.Type.COMMON, Config.SPEC);
		NeoForgeModConfigEvents.loading("coordints").register(Config::onLoad);
		NeoForgeModConfigEvents.reloading("coordints").register(Config::onLoad);
		Events.init();
	}
}