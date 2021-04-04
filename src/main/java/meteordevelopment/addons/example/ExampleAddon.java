package meteordevelopment.addons.example;

import meteordevelopment.addons.example.modules.ExampleModule;
import meteordevelopment.addons.example.modules.ExampleModule2;
import minegame159.meteorclient.MeteorAddon;
import minegame159.meteorclient.systems.modules.Category;
import minegame159.meteorclient.systems.modules.Modules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleAddon extends MeteorAddon {

	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("Example");

	@Override
	public void onInitialize() {
		LOG.info("Initializing Meteor Addon");
		Modules.get().add(new ExampleModule());
		Modules.get().add(new ExampleModule2());
	}

	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}

}