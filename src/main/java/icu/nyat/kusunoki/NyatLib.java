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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public final class NyatLib extends JavaPlugin {
    public static final String BRAND = "minecraft:brand";
    private String PluginVersion = this.getDescription().getVersion();
    public static NyatLibCore brandUpdater;
    public static String BrandName;
    public static String BrandVersion;
    public static int BrandProtocolVersion;
    public static ArrayList<Integer> ServerSupportedProtocolVersion;
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
       NyatLibOnEnable EnableStep = new NyatLibOnEnable();
       ConfigReader.Read(getConfig());
       try {
           EnableStep.Check();
           NyatLibLogger.logINFO("§3Current NyatWork Version is: " + NyatLib.BrandVersion  + "§f");
       }catch(Exception e){
           NyatLibLogger.logERROR(e.toString());
           onDisable();
       }
       ProtocolManager manager = ProtocolLibrary.getProtocolManager();

       try{
           if(isBroadcastEnabled){
               brandUpdater = new NyatLibCore(Collections.singletonList(BrandName + " " + BrandVersion + "§f"),100,manager);
           }
       } catch (Exception e) {
           NyatLibLogger.logERROR(e.getMessage());
           NyatLibLogger.logERROR(e.toString());
           e.printStackTrace();
           onDisable();
       }

       manager.addPacketListener(new PacketListener(this, brandUpdater));
       try {
           Objects.requireNonNull(this.getCommand("nlreload")).setExecutor(new ReloadCmd(this));
       }catch (Exception ignored){}

       new PlayerListener(this, brandUpdater).register();
       if (brandUpdater.size() > 0) brandUpdater.broadcast();
       if (brandUpdater.size() > 1) brandUpdater.start();
       NyatLib plugin = NyatLib.getPlugin(NyatLib.class);
       if (PaperLib.isPaper()) {
           getServer().getPluginManager().registerEvents(new PingEventPaper(plugin), this);
       } else {
           PaperLib.suggestPaper(this);
           getServer().getPluginManager().registerEvents(new PingEventSpigot(plugin), this);
       }


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
