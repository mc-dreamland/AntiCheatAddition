package de.photon.anticheataddition.modules.checks.scaffold;

import de.photon.anticheataddition.modules.Module;
import de.photon.anticheataddition.user.User;
import de.photon.anticheataddition.user.data.TimeKey;
import de.photon.anticheataddition.util.messaging.Log;

/**
 * This Scaffold part identifies suspicious rotation patterns sudden, large angle changes, as well as some very random rotations.
 */
public class ScaffoldRotation extends Module
{
    public static final ScaffoldRotation INSTANCE = new ScaffoldRotation();

    private static final double ANGLE_CHANGE_SUM_THRESHOLD = 7D;
    private static final double ANGLE_OFFSET_SUM_THRESHOLD = 5.2D;

    private ScaffoldRotation()
    {
        super("Scaffold.parts.Rotation");
    }

    public int getVl(User user)
    {
        if (!this.isEnabled()) return 0;
        int vl = 0;

        final var scaffoldAngleInfo = user.getLookPacketData().getAngleInformation();

        // Detect sudden changes in the last two ticks.
        if (user.getTimeMap().at(TimeKey.SCAFFOLD_SIGNIFICANT_ROTATION_CHANGE).recentlyUpdated(125)) {
            Log.fine(() -> "Scaffold-Debug | Player: " + user.getPlayer().getName() + " placed a block after a large, sudden rotation change.");
            vl += 15;
        }

        // Detects an excessive amount of large rotation changes in general.
        if (scaffoldAngleInfo.changeSum() > ANGLE_CHANGE_SUM_THRESHOLD) {
            Log.fine(() -> "Scaffold-Debug | Player: " + user.getPlayer().getName() + " sent suspicious rotation changes.");
            vl += 10;
        }

        // This detects some very random rotations that some scaffold cheats might use.
        if (scaffoldAngleInfo.offsetSum() > ANGLE_OFFSET_SUM_THRESHOLD) {
            Log.fine(() -> "Scaffold-Debug | Player: " + user.getPlayer().getName() + " sent suspiciously random rotations.");
            vl += 5;
        }

        return vl;
    }
}