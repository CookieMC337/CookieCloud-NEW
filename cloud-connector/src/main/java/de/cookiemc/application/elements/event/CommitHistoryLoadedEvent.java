package de.cookiemc.application.elements.event;

import de.cookiemc.driver.event.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kohsuke.github.GHCommit;

import java.util.Collection;

@AllArgsConstructor
@Getter
public class CommitHistoryLoadedEvent implements CloudEvent {


    private final Collection<GHCommit> loadedCommits;
}
