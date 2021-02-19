package meteordevelopment.exampleaddon.modules;

import meteordevelopment.exampleaddon.ExampleAddon;
import minegame159.meteorclient.modules.Module;

public class ExampleModule extends Module {
    public ExampleModule() {
        super(ExampleAddon.CATEGORY, "example-module", "This is an example module inside an example category.");
    }
}
