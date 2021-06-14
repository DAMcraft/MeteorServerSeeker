package meteordevelopment.addons.template.modules;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class AnotherExample extends Module {
    public AnotherExample() {
        super(Categories.Player, "example-2", "An example module in an existing category.");
    }
}