package de.cookiemc.common.function;

public interface BiSupplier<V, E> {

    E supply(V v);
}
