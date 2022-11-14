package de.cookiemc.context.definition.reader;

import de.cookiemc.context.definition.IDefinition;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CompositeDefinitionReader implements IDefinitionReader {
    private final List<IDefinitionReader> iDefinitionReaders;

    @Override
    public Set<IDefinition> getDefinitions() {
        return iDefinitionReaders
                .stream()
                .map(IDefinitionReader::getDefinitions)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
