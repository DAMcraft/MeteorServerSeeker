package meteordevelopment.addons.template;

import meteordevelopment.addons.template.commands.ExampleCommand;
import meteordevelopment.addons.template.modules.AnotherExample;
import meteordevelopment.addons.template.modules.Example;
import minegame159.meteorclient.MeteorAddon;
import minegame159.meteorclient.systems.commands.Commands;
import minegame159.meteorclient.systems.modules.Category;
import minegame159.meteorclient.systems.modules.Modules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateAddon extends MeteorAddon {

	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("Example");

	@Override
	public void onInitialize() {
		LOG.info("Initializing Meteor Addon Template");

		// Modules
		Modules.get().add(new Example());
		Modules.get().add(new AnotherExample());

		// Commands
		Commands.get().add(new ExampleCommand());
	}

	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}

}