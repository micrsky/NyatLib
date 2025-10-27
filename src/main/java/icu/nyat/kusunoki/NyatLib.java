package icu.nyat.kusunoki;

import icu.nyat.kusunoki.Action.NyatLibOnDisable;
import icu.nyat.kusunoki.Action.NyatLibOnEnable;
import icu.nyat.kusunoki.Utils.ConfigReader;
import icu.nyat.kusunoki.Utils.NyatLibLogger;
import icu.nyat.kusunoki.Command.ReloadCmd;
import icu.nyat.kusunoki.Packet.*;
import icu.nyat.kusunoki.motd.PingEventPaper;
import icu.nyat.kusunoki.motd.PingEventSpigot;
import io.papermc.lib.PaperLib;
import org.bukkit.plugin.java.JavaPlugin;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import java.util.*;

public final class NyatLib extends JavaPlugin {
    public static final String BRAND = "minecraft:brand";
    private final String PluginVersion = this.getDescription().getVersion();
    public static NyatLibCore brandUpdater;
    public static String BrandName;
    public static String BrandVersion;
    public static int BrandProtocolVersion;
    public static Set<Integer> ServerSupportedProtocolVersion;
    public static boolean isBroadcastEnabled;

    @Override
    public void onLoad(){
        try {
            initial();
            NyatLibLogger.logLoader("NyatLib Version:" + PluginVersion);
        }catch (Exception ex){
            NyatLibLogger.logERROR(ex.toString());
        }
   }

   @Override
    public void onEnable(){
       ConfigReader.Read(getConfig());

       // Ensure ProtocolLib is installed and enabled before proceeding
       if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
           NyatLibLogger.logERROR("ProtocolLib not found or not enabled. Disabling NyatLib.");
           getServer().getPluginManager().disablePlugin(this);
           return;
       }

       NyatLibOnEnable EnableStep = new NyatLibOnEnable();
       try {
           EnableStep.Check();
           NyatLibLogger.logINFO("§3Current NyatWork Version is: " + NyatLib.BrandVersion  + "§f");
       }catch(Exception e){
           NyatLibLogger.logERROR(e.toString());
           onDisable();
           return;
       }

       try{
           if(isBroadcastEnabled){
               ProtocolManager manager = ProtocolLibrary.getProtocolManager();
               brandUpdater = new NyatLibCore(Collections.singletonList(BrandName + " " + BrandVersion + "§f"),100,manager);
               // Register packet listener only when updater is available
               manager.addPacketListener(new PacketListener(this));
           }
       } catch (Exception e) {
           NyatLibLogger.logERROR(e.getMessage());
           NyatLibLogger.logERROR(e.toString());
           NyatLibLogger.logERROR("Startup failed with exception above; disabling plugin.");
           onDisable();
           return;
       }

       // Register player listener only when broadcasting is enabled and updater is ready
       if (brandUpdater != null) {
           new PlayerListener(this, brandUpdater).register();

           if (brandUpdater.size() > 0) brandUpdater.broadcast();
           if (brandUpdater.size() > 1) brandUpdater.start(this);
       }

       NyatLib plugin = NyatLib.getPlugin(NyatLib.class);
       if (PaperLib.isPaper()) {
           getServer().getPluginManager().registerEvents(new PingEventPaper(plugin), this);
       } else {
           PaperLib.suggestPaper(this);
           getServer().getPluginManager().registerEvents(new PingEventSpigot(plugin), this);
       }

       try {
           Objects.requireNonNull(this.getCommand("nlreload")).setExecutor(new ReloadCmd(this));
       } catch (Exception ignored) {}

   }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        NyatLibOnDisable.DisableStep();
    }
    public static void initial(){
        NyatLibLogger.logINFO("==========================================================================================");
        NyatLibLogger.logINFO("");
        NyatLibLogger.logINFO("@@@@@@^    @@@@^                              =@@@@^    =@@@@^        @@@@@  =@@@@");
        NyatLibLogger.logINFO("@@@@@@@\\   @@@@^                              =@@@@^    =@@@@^               =@@@@");
        NyatLibLogger.logINFO("@@@@@@@@\\  @@@@^.@@@@\\    @@@@/ O@@@@@@@@@`  @@@@@@@@@@.=@@@@^        O@@@O  =@@@@/@@@@@@`");
        NyatLibLogger.logINFO("@@@@^@@@@@.@@@@^ ,@@@@^  =@@@@. O@@[[[\\@@@@^ \\@@@@@@OOO.=@@@@^        O@@@O  =@@@@@@@@@@@@`");
        NyatLibLogger.logINFO("@@@@^.@@@@@@@@@^  =@@@@`.@@@@^   .,]/@@@@@@@  =@@@@^    =@@@@^        O@@@O  =@@@@   .@@@@^");
        NyatLibLogger.logINFO("@@@@^  \\@@@@@@@^   \\@@@@/@@@^  ,@@@@@@@@@@@@  =@@@@^    =@@@@^        O@@@O  =@@@@    @@@@O");
        NyatLibLogger.logINFO("@@@@^   \\@@@@@@^   .@@@@@@@@   @@@@@   =@@@@  =@@@@^    =@@@@^        O@@@O  =@@@@   ,@@@@^");
        NyatLibLogger.logINFO("@@@@^    =@@@@@^    ,@@@@@@.   =@@@@@@@@@@@@  .@@@@@@@@.=@@@@@@@@@@@O O@@@O  =@@@@@@@@@@@/");
        NyatLibLogger.logINFO("@@@@^     =@@@@^     =@@@@^     ,@@@@@/=@@@@   .\\@@@@@@.=@@@@@@@@@@@O O@@@O  =@@@@\\@@@@/`");
        NyatLibLogger.logINFO("                    ,@@@@/");
        NyatLibLogger.logINFO("                   .@@@@@");
        NyatLibLogger.logINFO("");
        NyatLibLogger.logINFO("==========================================================================================");

    }
}
