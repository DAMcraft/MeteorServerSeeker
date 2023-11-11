package de.damcraft.serverseeker.country;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;

public class CountrySettingScreen extends WindowScreen {
    private final CountrySetting setting;

    private WTable table;

    private WTextBox filter;
    private String filterText = "";

    public CountrySettingScreen(GuiTheme theme, CountrySetting setting) {
        super(theme, "Select Country");

        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        filter = add(theme.textBox("")).minWidth(400).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            initTable();
        };

        table = add(theme.table()).expandX().widget();

        initTable();
    }

    private void initTable() {
        Collection<Country> countries = ServerSeeker.COUNTRY_MAP.values();
        // Sort alphabetically. Save to array to avoid concurrent modification.
        Country[] countryArray = countries.toArray(new Country[0]);
        Arrays.sort(countryArray);

        for (Country country : countryArray) {
            if (setting.filter != null && !setting.filter.test(country)) continue;
            boolean isSelected = country == setting.get();
            if (!filterText.isEmpty() && (
                !StringUtils.containsIgnoreCase(country.name, filterText) && !StringUtils.containsIgnoreCase(country.code, filterText)
            )) continue;
            table.add(new WCountry(country)).widget();

            WLabel label = table.add(theme.label(country.name)).widget();
            if (isSelected) label.color = Color.GREEN;

            WButton select = table.add(theme.button("Select")).expandCellX().right().widget();
            select.action = () -> {
                setting.set(country);
                close();
            };

            table.row();
        }
    }
}
