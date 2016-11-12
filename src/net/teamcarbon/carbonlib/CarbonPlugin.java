package net.teamcarbon.carbonlib;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.teamcarbon.carbonlib.Misc.CarbonException;
import net.teamcarbon.carbonlib.Misc.Log;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public abstract class CarbonPlugin extends JavaPlugin {

    public static Log log;
    public static CarbonPlugin inst;

    public void onEnable() {

        CarbonLib.addPlugin(this);
        inst = this;
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                try {
                    saveDefaultConfig();
                    log = new Log(inst, "enable-debug-messages");
                    CarbonException.setGlobalPluginScope(inst, "net.teamcarbon");
                    CarbonLib.notifyHook(inst);

                    enablePlugin();

                } catch (Exception e) {
                    log.severe("===[ An exception occurred while trying to enable " + getDescription().getName() + " ]===");
                    (new CarbonException(inst, e)).printStackTrace();
                    log.severe("=====================================");
                }
                enablePlugin();
            }
        }, 1L);

    }

    public abstract void enablePlugin();

    public static Permission perm() { return CarbonLib.fetchPerm(); }
    public static Economy econ() { return CarbonLib.fetchEcon(); }
    public static Chat chat() { return CarbonLib.fetchChat(); }
    public static PluginManager pm() { return server().getPluginManager(); }
    public static Server server() { return inst.getServer(); }

    public static FileConfiguration getConf() { return inst.getConfig(); }
    public static void saveConf() { inst.saveConfig(); }
    public static void reloadConf() { inst.reloadConfig(); }

    public static String namever() { return inst.getDescription().getName() + " v" + inst.getDescription().getVersion(); }

}
