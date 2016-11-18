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

    protected Log log;
    protected CarbonPlugin inst;

    public void onEnable() {

        CarbonLib.addPlugin(this);
        inst = this;
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                try {
                    saveDefaultConfig();
                    log = new Log(inst, getDebugPath());
                    CarbonException.setGlobalPluginScope(inst, "net.teamcarbon");

                    enablePlugin();

                    CarbonLib.notifyHook(inst);

                } catch (Exception e) {
                    log.severe("===[ An exception occurred while trying to enable " + getDescription().getName() + " ]===");
                    (new CarbonException(inst, e)).printStackTrace();
                    log.severe("=====================================");
                }
            }
        }, 1L);

    }

    // ABSTRACT METHODS

    public abstract void enablePlugin();
    public abstract String getDebugPath();

    // INSTANCE ACCESSORS

    public CarbonPlugin getPlugin() { return inst; }
    public FileConfiguration getConf() { return getConfig(); }
    public void saveConf() { saveConfig(); }
    public void reloadConf() { reloadConfig(); }

    public void logMsg(String msg) { log.log(msg); }
    public void logInfo(String msg) { log.info(msg); }
    public void logWarn(String msg) { log.warn(msg); }
    public void logSevere(String msg) { log.severe(msg); }
    public void logDebug(String msg) { log.debug(msg); }

    // STATIC ACCESSORS

    public static Permission perm() { return CarbonLib.fetchPerm(); }
    public static Economy econ() { return CarbonLib.fetchEcon(); }
    public static Chat chat() { return CarbonLib.fetchChat(); }
    public static PluginManager pm() { return server().getPluginManager(); }
    public static Server server() { return Bukkit.getServer(); }
    public static CarbonPlugin getPlugin(String name) {
        for (CarbonPlugin cp : CarbonLib.getCarbonPlugins()) {
            if (cp.getDescription().getName().equalsIgnoreCase(name)) {
                return cp;
            }
        }
        return null;
    }

    public String namever() { return getDescription().getName() + " v" + getDescription().getVersion(); }

}
