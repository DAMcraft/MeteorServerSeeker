package meteordevelopment.addons.example.modules;

import meteordevelopment.addons.example.ExampleAddon;
import minegame159.meteorclient.systems.modules.Module;

public class ExampleModule extends Module {
    public ExampleModule() {
        super(ExampleAddon.CATEGORY, "example", "This is an example module inside a custom category.");
    }
}