/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.healthcheck;

import java.util.Observable;

/**
 * @author unavani
 *
 */
public class HeartBeat extends Observable implements Runnable {

    /** The smallest sleep duration */
    private static final long DURATION = 200;

    /** The excitement level of the heart */
    private long excitementLevel = 5;

    /** The number of beats counter */
    private long numberOfBeats = 0;

    /** The state of the model */
    private boolean running;

    /** An empty constructor */
    public HeartBeat() {
        running = true;
    }

    /**
     * The "heart" of the HeartBeat model.
     * The loop increments the beat counter, then sends updates
     * to whoever subscribed, then goes to sleep for a
     * certain amount of time. The amount of time depends
     * on the current excitementLevel of the model.
     *
     */
    @Override
    public void run() {
        while (running) {
            numberOfBeats++;
            updateObservers();
            try {
                Thread.sleep(DURATION * excitementLevel);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * Set the changed flag in the Observable object
     * and then notify the observers with the current
     * beat count.
     *
     * Each model might have it's own process for updating
     * observers - for instance, they might need to build new
     * data objects before sending the update - this is a good
     * reason for separating the update method in the model.
     * Also, if there is more than one way to trigger the update,
     * a single update method is the smart way to go.
     * Also, if there is more than one update in one model,
     * it might benefit from having separate methods as well.
     */
    private void updateObservers() {
        setChanged();
        notifyObservers(Long.valueOf(numberOfBeats));
    }

    /**
     * Adjusts the excitementLevel of the model by adding the
     * delta value to the current  <code>excitementLevel</code>.  It also
     * insures that the <code>excitementLevel</code> doesn't go below
     * -1.
     *
     *@param delta The amount to change the excementLevel by.
     */
    public void adjustExcitementLevel(long delta) {
        excitementLevel += delta;
        if (excitementLevel < 1) {
            excitementLevel = 1;
        }

    }

    /**
     * Halts the heart...with no way to start it up again...
     */
    public void stopHeart() {
        running = false;
    }

    /**
     * Start the heart in a thread
     */
    public void startHeart() {
        running = true;
        new Thread(this).start();
    }

}

