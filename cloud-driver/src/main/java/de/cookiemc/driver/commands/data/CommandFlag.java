package de.cookiemc.driver.commands.data;

import lombok.Getter;
import de.cookiemc.driver.commands.parameter.AbstractBundledParameters;

import java.util.ArrayList;

@Getter
public class CommandFlag extends AbstractBundledParameters {

    /**
     * Specifies which argument from a command is a flag and which not
     */
    public static final String SPECIFIER = "-";

    /**
     * Label of the flag (full syntax would be: -label)
     *
     * @see #SPECIFIER
     */
    private final String label;

    public CommandFlag(String label) {
        super(new ArrayList<>());
        this.label = label;
    }

}
