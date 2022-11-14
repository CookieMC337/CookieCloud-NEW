package de.cookiemc.driver.event;


public interface Cancelable {

	boolean isCancelled();

	void setCancelled(boolean cancelled);

}
