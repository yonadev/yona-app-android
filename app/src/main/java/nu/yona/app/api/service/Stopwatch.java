/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.service;

/**
 * The type Stopwatch.
 */
class Stopwatch {
    private long startTime = 0;
    private boolean running = false;
    private long currentTime = 0;

    /**
     * Start.
     */
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    /**
     * Is started boolean.
     *
     * @return the boolean
     */
    public boolean isStarted() {
        return this.running;
    }

    /**
     * Stop.
     */
    public void stop() {
        this.running = false;
    }

    /**
     * Pause.
     */
    public void pause() {
        this.running = false;
        currentTime = System.currentTimeMillis() - startTime;
    }

    /**
     * Resume.
     */
    public void resume() {
        this.running = true;
        this.startTime = System.currentTimeMillis() - currentTime;
    }

    //elaspsed time in milliseconds
    private long getElapsedTimeMili() {
        long elapsed = 0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 100) % 1000;
        }
        return elapsed;
    }

    /**
     * Gets elapsed time secs.
     *
     * @return the elapsed time secs
     */
//elaspsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed = 0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return elapsed;
    }

    /**
     * Gets elapsed time min.
     *
     * @return the elapsed time min
     */
//elaspsed time in minutes
    public long getElapsedTimeMin() {
        long elapsed = 0;
        if (running) {
            elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60) % 60;
        }
        return elapsed;
    }

    //elaspsed time in hours
    private long getElapsedTimeHour() {
        long elapsed = 0;
        if (running) {
            elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60) / 60);
        }
        return elapsed;
    }

    public String toString() {
        return getElapsedTimeHour() + ":" + getElapsedTimeMin() + ":"
                + getElapsedTimeSecs() + "." + getElapsedTimeMili();
    }

    /**
     * Gets strat time.
     *
     * @return the strat time
     */
    public long getStratTime() {
        return startTime;
    }
}