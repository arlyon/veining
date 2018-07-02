/*
 * veining (c) by Alexander Lyon
 *
 * veining is licensed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nc-sa/4.0/>
 */

/*
 * veining (c) by arlyon
 *
 * veining is licensed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nc-sa/4.0/>
 */

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