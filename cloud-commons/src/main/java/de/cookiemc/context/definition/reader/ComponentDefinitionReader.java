package de.cookiemc.context.definition.reader;

import de.cookiemc.context.annotations.ApplicationParticipant;
import de.cookiemc.context.definition.ConstructorDefinition;
import de.cookiemc.context.definition.IDefinition;
import de.cookiemc.context.utils.PackageScanner;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ComponentDefinitionReader implements IDefinitionReader {
    private final PackageScanner packageScanner;
    private final String packageName;

    @Override
    public Set<IDefinition> getDefinitions() {
        return packageScanner.findClasses(packageName)
                .stream()
                .filter(clazz -> {
                    boolean result = clazz.isAnnotationPresent(ApplicationParticipant.class);
                    if (!result) {
                        for (Annotation classAnnotations : clazz.getAnnotations()) {
                            Annotation[] annotation2annotation = classAnnotations.annotationType().getAnnotations();
                            for (Annotation an : annotation2annotation) {
                                if (an.annotationType().isAssignableFrom(ApplicationParticipant.class))
                                    return true;
                            }
                        }
                    }
                    return result;
                })
                .map(ConstructorDefinition::new)
                .collect(Collectors.toSet());
    }
}
