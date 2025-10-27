package icu.nyat.kusunoki.Command;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import icu.nyat.kusunoki.NyatLib;
import icu.nyat.kusunoki.NyatLibCore;
import icu.nyat.kusunoki.Packet.PacketListener;
import icu.nyat.kusunoki.Utils.ConfigReader;
import icu.nyat.kusunoki.Utils.NyatLibLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class ReloadCmd implements CommandExecutor {
    private final Plugin plugin;

    public ReloadCmd(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        NyatLibLogger.logINFO("Reloading NyatLib configuration...");

        // Stop existing broadcaster and remove ProtocolLib listeners
        try {
            if (NyatLib.brandUpdater != null) {
                NyatLib.brandUpdater.stop();
                NyatLib.brandUpdater = null;
            }
            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            manager.removePacketListeners(plugin);
        } catch (Throwable ignored) { }

        // Reload config and reinitialize
        plugin.reloadConfig();
        ConfigReader.Read(plugin.getConfig());

        try {
            if (NyatLib.isBroadcastEnabled) {
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                NyatLib.brandUpdater = new NyatLibCore(Collections.singletonList(NyatLib.BrandName + " " + NyatLib.BrandVersion + "§f"), 100, manager);
                manager.addPacketListener(new PacketListener((NyatLib) plugin));
                if (NyatLib.brandUpdater.size() > 0) NyatLib.brandUpdater.broadcast();
                if (NyatLib.brandUpdater.size() > 1) NyatLib.brandUpdater.start((JavaPlugin) plugin);
            }
            NyatLibLogger.logINFO("NyatLib reloaded.");
        } catch (Exception e) {
            NyatLibLogger.logERROR("Reload failed: " + e.getMessage());
        }
        return true;
    }
}
