/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.listener;

import java.util.Objects;

public class DataLoadListenerImpl<T, U> implements DataLoadListener<T, U> {
    @FunctionalInterface
    public interface ListenerFunction<T> {
        T handle(T result);
    }
    private final ListenerFunction<T> loadHandler;
    private final ListenerFunction<U> errorHandler;
    private final DataLoadListener<T, U> nextListener;

    public DataLoadListenerImpl(ListenerFunction<T> loadHandler)
    {
        this(Objects.requireNonNull(loadHandler), null, null);
    }

    public DataLoadListenerImpl(ListenerFunction<T> loadHandler, ListenerFunction<U> errorHandler)
    {
        this(Objects.requireNonNull(loadHandler), Objects.requireNonNull(errorHandler), null);
    }

    public DataLoadListenerImpl(ListenerFunction<T> loadHandler, DataLoadListener<T, U> nextListener)
    {
        this(Objects.requireNonNull(loadHandler), null, Objects.requireNonNull(nextListener));
    }

    public DataLoadListenerImpl(DataLoadListener<T, U> nextListener)
    {
        this(null, null, Objects.requireNonNull(nextListener));
    }

    public DataLoadListenerImpl(ListenerFunction<T> loadHandler, ListenerFunction<U> errorHandler, DataLoadListener<T, U> nextListener)
    {
        this.loadHandler = loadHandler;
        this.errorHandler = errorHandler;
        this.nextListener = nextListener;
    }

    @Override
    public void onDataLoad(T result) {
        T updatedResult = null;
        if (loadHandler != null) {
            updatedResult = loadHandler.handle(result);
        }
        if (nextListener == null) {
            return;
        }
        nextListener.onDataLoad((updatedResult == null) ? result : updatedResult);
    }

    @Override
    public void onError(U errorMessage) {
        U updatedErrorMessage = null;
        if (errorHandler != null) {
            updatedErrorMessage = errorHandler.handle(errorMessage);
        }
        if (nextListener == null) {
            return;
        }
        nextListener.onError((updatedErrorMessage == null) ? errorMessage : updatedErrorMessage);
    }
}