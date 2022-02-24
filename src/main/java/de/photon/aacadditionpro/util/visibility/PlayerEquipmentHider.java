package de.photon.aacadditionpro.util.visibility;

import com.comphenix.protocol.PacketType;
import de.photon.aacadditionpro.AACAdditionPro;
import de.photon.aacadditionpro.ServerVersion;
import de.photon.aacadditionpro.protocol.packetwrappers.sentbyserver.equipment.IWrapperPlayEquipment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

class PlayerEquipmentHider extends PlayerInformationHider
{
    public PlayerEquipmentHider()
    {
        super(PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    protected void onHide(@NotNull Player observer, @NotNull Collection<Entity> toHide)
    {
        Bukkit.getScheduler().runTask(AACAdditionPro.getInstance(), () -> toHide.stream().map(Entity::getEntityId).forEach(id -> IWrapperPlayEquipment.clearAllSlots(id, observer)));
    }

    @Override
    protected Set<ServerVersion> getSupportedVersions()
    {
        return ServerVersion.NON_188_VERSIONS;
    }
}