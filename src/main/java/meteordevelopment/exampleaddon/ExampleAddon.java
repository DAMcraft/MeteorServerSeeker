package meteordevelopment.exampleaddon;

import meteordevelopment.exampleaddon.modules.ExampleModule;
import meteordevelopment.exampleaddon.modules.ExampleModule2;
import minegame159.meteorclient.MeteorAddon;
import minegame159.meteorclient.modules.Category;
import minegame159.meteorclient.modules.Modules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleAddon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("Example");

	@Override
	public void onInitialize() {
		LOG.info("Initializing Meteor Addon");
		Modules.get().addModule(new ExampleModule());
		Modules.get().addModule(new ExampleModule2());
	}

	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}
}