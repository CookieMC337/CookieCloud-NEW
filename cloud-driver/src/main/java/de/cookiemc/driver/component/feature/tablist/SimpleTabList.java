package de.cookiemc.driver.component.feature.tablist;

import de.cookiemc.driver.component.Component;
import de.cookiemc.driver.component.base.SimpleTextBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @AllArgsConstructor @Setter
public class SimpleTabList extends SimpleTextBase<TabList> implements TabList {

    /**
     * The header of this tab
     */
    private String header;

    /**
     * The footer of this tab
     */
    private String footer;

    public SimpleTabList() {
        this.addHandler(option -> {
            if (option.getName().equalsIgnoreCase("header")) {
                header = option.getValue(Component.class, Component.empty()).getContent();
            } else if (option.getName().equalsIgnoreCase("footer")) {
                footer = option.getValue(Component.class, Component.empty()).getContent();
            }
        });
    }
}
