package de.cookiemc.context.definition.registry;

import de.cookiemc.context.definition.IDefinition;

import java.util.Collection;
import java.util.Set;

public interface DefinitionRegistry {

    void registerDefinition(IDefinition iDefinition);

    Collection<IDefinition> getDefinitions(Class<?> clazz);

    Collection<IDefinition> getDefinitions();

    IDefinition getDefinition(String name);

    boolean containsDefinition(IDefinition iDefinition);

    Set<String> getDefinitionNames();
}
