package de.cookiemc.node.module.updater;

import de.cookiemc.common.VersionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModuleInfo {

    private final String name;

    private final String url;

    private final VersionInfo version;
}