package de.cookiemc.context.definition.reader;

import de.cookiemc.context.annotations.Configuration;
import de.cookiemc.context.annotations.DefinedMethod;
import de.cookiemc.context.definition.IDefinition;
import de.cookiemc.context.definition.MethodDefinition;
import de.cookiemc.context.utils.PackageScanner;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ConfigurationDefinitionReader implements IDefinitionReader {

    private final PackageScanner packageScanner;
    private final String packageName;

    @Override
    public Set<IDefinition> getDefinitions() {
        return packageScanner.findClasses(packageName)
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(Configuration.class))
                .map(Class::getDeclaredMethods)
                .flatMap(Arrays::stream)
                .filter(method -> method.isAnnotationPresent(DefinedMethod.class))
                .map(MethodDefinition::new)
                .collect(Collectors.toSet());
    }
}
