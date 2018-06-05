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
        void handle(T result);
    }
    private final ListenerFunction<T> loadHandler;
    private final ListenerFunction<U> errorHandler;
    private final DataLoadListener<T, U> nextListener;

    public DataLoadListenerImpl(ListenerFunction<T> loadHandler)
    {
        this(loadHandler, null, null);
    }

    public DataLoadListenerImpl(ListenerFunction<T> loadHandler, ListenerFunction<U> errorHandler)
    {
        this(loadHandler, errorHandler, null);
    }

    public DataLoadListenerImpl(ListenerFunction<T> loadHandler, DataLoadListener<T, U> nextListener)
    {
        this(loadHandler, null, nextListener);
    }

    public DataLoadListenerImpl(ListenerFunction<T> loadHandler, ListenerFunction<U> errorHandler, DataLoadListener<T, U> nextListener)
    {
        this.loadHandler = Objects.requireNonNull(loadHandler);
        this.errorHandler = errorHandler;
        this.nextListener = nextListener;
    }

    @Override
    public void onDataLoad(T result) {
        loadHandler.handle(result);
        if (nextListener == null) {
            return;
        }
        nextListener.onDataLoad(result);
    }

    @Override
    public void onError(U errorMessage) {
        if (errorHandler != null) {
            errorHandler.handle(errorMessage);
        }
        if (nextListener == null) {
            return;
        }
        nextListener.onError(errorMessage);
    }
}
