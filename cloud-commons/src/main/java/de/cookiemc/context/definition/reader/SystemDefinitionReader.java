package de.cookiemc.context.definition.reader;

import de.cookiemc.context.definition.IDefinition;
import de.cookiemc.context.definition.ConstructorDefinition;
import de.cookiemc.context.postprocess.ContextProcessorCache;
import de.cookiemc.context.postprocess.ContextProcessorJson;
import de.cookiemc.context.postprocess.PostConstructProcessor;

import java.util.HashSet;
import java.util.Set;

public class SystemDefinitionReader implements IDefinitionReader {
    @Override
    public Set<IDefinition> getDefinitions() {

        Set<IDefinition> postProcessors = new HashSet<>();
        postProcessors.add(new ConstructorDefinition(ContextProcessorCache.class));
        postProcessors.add(new ConstructorDefinition(ContextProcessorJson.class));
        postProcessors.add(new ConstructorDefinition(PostConstructProcessor.class));

        return postProcessors;
    }
}
