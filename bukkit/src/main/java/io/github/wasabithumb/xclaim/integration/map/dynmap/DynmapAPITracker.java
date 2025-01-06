package io.github.wasabithumb.xclaim.integration.map.dynmap;

import io.github.wasabithumb.xclaim.util.ForeignLoadable;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.jetbrains.annotations.NotNull;

class DynmapAPITracker extends ForeignLoadable<DynmapCommonAPI> {

    private final Listener listener;
    public DynmapAPITracker() {
        super();
        Listener l = new Listener(this);
        DynmapCommonAPIListener.register(l);
        this.listener = l;
    }

    public void cleanup() {
        DynmapCommonAPIListener.unregister(this.listener);
    }

    //

    private static final class Listener extends DynmapCommonAPIListener {

        private final DynmapAPITracker tracker;
        Listener(@NotNull DynmapAPITracker tracker) {
            this.tracker = tracker;
        }

        @Override
        public void apiEnabled(DynmapCommonAPI value) {
            this.tracker.setEnabled(value);
        }

        @Override
        public void apiDisabled(DynmapCommonAPI value) {
            this.tracker.setDisabled(value);
        }

    }

}
