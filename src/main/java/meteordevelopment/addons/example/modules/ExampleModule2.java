package meteordevelopment.addons.example.modules;

import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;

public class ExampleModule2 extends Module {
    public ExampleModule2() {
        super(Categories.Player, "example-2", "This is an example module that is in an existing category.");
    }
}