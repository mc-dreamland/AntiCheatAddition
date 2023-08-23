package de.photon.anticheataddition.modules.checks.packetanalysis;

import de.photon.anticheataddition.modules.ViolationModule;
import de.photon.anticheataddition.user.User;
import de.photon.anticheataddition.util.mathematics.MathUtil;
import de.photon.anticheataddition.util.messaging.Log;
import de.photon.anticheataddition.util.violationlevels.Flag;
import de.photon.anticheataddition.util.violationlevels.ViolationLevelManagement;
import de.photon.anticheataddition.util.violationlevels.ViolationManagement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;

public class PacketAnalysisPerfectRotation extends ViolationModule implements Listener
{
    public static final PacketAnalysisPerfectRotation INSTANCE = new PacketAnalysisPerfectRotation();

    private PacketAnalysisPerfectRotation()
    {
        super("PacketAnalysis.parts.PerfectRotation");
    }

    private static final double EQUALITY_EPSILON = 0.000000001;
    private static final double[] MULTIPLE_PATTERNS = {0.1, 0.25};

    private static boolean isEqual(double reference, double d)
    {
        return MathUtil.absDiff(reference, d) <= EQUALITY_EPSILON;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        final var user = User.getUser(event.getPlayer());
        if (User.isUserInvalid(user, this) || event.getTo() == null) return;

        final double[] diffs = {MathUtil.absDiff(event.getTo().getYaw(), event.getFrom().getYaw()),
                                MathUtil.absDiff(event.getTo().getPitch(), event.getFrom().getPitch())};

        for (double d : diffs) {
            // Ignore 0 and 360 degrees as they represent no change in rotation.
            if (isEqual(0, d) || isEqual(360, d)) continue;

            // Check if the angle change is valid (not infinite or NaN).
            if (Double.isInfinite(d) || Double.isNaN(d))
                getManagement().flag(Flag.of(user).setAddedVl(10).setDebug(() -> "PacketAnalysisData-Debug | Player: " + user.getPlayer().getName() + " sent infinite rotation diffs."));

            Log.finest(() -> "PacketAnalysisData-Debug | Player: " + user.getPlayer().getName() + " sent rotation diffs: " + Arrays.toString(diffs));

            // Check if the angle change is a multiple of any pattern like 0.1 or 0.25.
            for (double pattern : MULTIPLE_PATTERNS) {
                final double potentialMultiple = d / pattern;

                if (isEqual(potentialMultiple, Math.rint(potentialMultiple)))
                    getManagement().flag(Flag.of(user).setAddedVl(10).setDebug(() -> "PacketAnalysisData-Debug | Player: " + user.getPlayer().getName() + " sent suspicious rotation diffs (" + Arrays.toString(diffs) + ")."));
            }
        }
    }


    @Override
    protected ViolationManagement createViolationManagement()
    {
        return ViolationLevelManagement.builder(this)
                                       .emptyThresholdManagement()
                                       .withDecay(200, 1).build();
    }
}