import java.io.Serializable;

import android.os.SystemClock;
import android.util.Log;

/**
 * A revolving queue of boolean values. the values can only be set true or false. a newly constructed cycle contains null values.
 */
public class CircularArray implements Serializable {

    // data structure
    private long[] times;
    private int[] detections;
    private boolean[] netStates;

    // the three states that detections[] can hold
    private static final int NO_ENTRY = -1;
    public static final int STATIONARY = 0;
    public static final int MOVING = 1;

    // to index into data structure arrays
    private int counter;
    private final int states;

    /**
     * a CircularArray with a certain number of states. the number of states given determines the level of 'certainty' required before the cycle will be 'allTrue' -- note however that this will also directly impact the time taken to fill the detection cycle with states.
     *
     * @param equivalent to array size
     */
    public CircularArray(int states) {
        this.states = states;
        initialise();
    }

    /**
     * Reset and initialise the internal data
     */
    public void initialise() {
        counter = 0;

        times = new long[states];
        detections = new int[states];
        netStates = new boolean[states];

        for (int i = 0; i < states; i++) {
            times[i] = 0L;
            detections[i] = NO_ENTRY;
            netStates[i] = true;
        }
    }

    /**
     * Places relevant values to the circular array, and timestamps the entry.
     *
     * @param motion result from a motion detector
     * @param netState result from Commons.isDeviceNetworkCapable()
     */
    public void add(boolean motion, boolean netState) {
        int position = counter++ % states;
        long time = SystemClock.elapsedRealtime();

        detections[position] = motion ? MOVING : STATIONARY;
        times[position] = time;
        netStates[position] = netState;
    }

    /**
     * looks back through entries, and returns a boolean result confirming whether there has been
     * any event of interest during the requested period.
     *
     * @param durationMinutes duration in minutes to query back through detection history
     * @param ignoreNetStates user preference indicating whether to acknowledge connectivity
     * @return whether any movement has occurred during query duration
     */
    public boolean interactionDetected(int durationMinutes, boolean ignoreNetStates) {
        // entry time threshold in milliseconds
        final long earliestTimeLimit = SystemClock.elapsedRealtime() - ((long) durationMinutes * 60 * 1000);

        // condition to allow one step back over time threshold to produce expected trigger time behaviour
        boolean reachedFinalIteration = false;

        for (int state = states, count = counter - 1; count >= 0 && state > 0 && !reachedFinalIteration; state--, count--) {
            int index = count % states;

            long time = times[index];
            int detection = detections[index];
            boolean netCapable = netStates[index];

            // for troubleshooting
//            log(time, detection, netCapable);

            if (time == 0L) return true;
            if (time < earliestTimeLimit) reachedFinalIteration = true;

            if (detection != STATIONARY) return true;
            if (netCapable && !ignoreNetStates) return true;
        }

        // return whether an insufficient number of entries were tested
        return !reachedFinalIteration;
    }

    private void log(long time, int detection, boolean netCapable) {
        String msg = "time: " + time + ", detection: "
                + detection + ", net: " + netCapable;

        Log.i("DetectionArray", msg);
    }

    @Override
    public String toString() {
        int position = ((counter - 1) % states) + 1;

        StringBuilder out = new StringBuilder("position " + position + ":\n[");

        for (int i : detections)
            out.append(i).append(", ");
        out.replace(out.length() - 2, out.length(), "]");

        out.append("\n[");
        for (boolean b : netStates)
            out.append(b ? 1 : 0).append(", ");
        out.replace(out.length() - 2, out.length(), "]");

        return out.toString();
    }

}

