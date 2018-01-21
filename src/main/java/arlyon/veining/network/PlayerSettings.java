package arlyon.veining.network;

/**
 * A simple class that handles a player's settings on the server.
 */
public class PlayerSettings {
    public final boolean disableWhenCrouched;
    public final boolean disableWhenStanding;

    public PlayerSettings(boolean disableWhenCrouched, boolean disableWhenStanding) {
        this.disableWhenCrouched = disableWhenCrouched;
        this.disableWhenStanding = disableWhenStanding;
    }
}