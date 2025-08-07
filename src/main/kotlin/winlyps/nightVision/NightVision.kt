package winlyps.nightVision

import org.bukkit.plugin.java.JavaPlugin

class NightVision : JavaPlugin() {
    private lateinit var configManager: ConfigManager
    private lateinit var databaseManager: DatabaseManager
    private lateinit var nightVisionManager: NightVisionManager

    override fun onEnable() {
        try {
            configManager = ConfigManager(this)
            configManager.onConfigReload = ::reloadPlayerEffects
            configManager.startWatcher()
            databaseManager = DatabaseManager(dataFolder)
            nightVisionManager = NightVisionManager(databaseManager, configManager)
            nightVisionManager.loadInitialUsers()

            server.pluginManager.registerEvents(PlayerListener(this, nightVisionManager, configManager), this)
            getCommand("gamma")?.setExecutor(GammaCommand(nightVisionManager, configManager))

            for (player in server.onlinePlayers) {
                if (nightVisionManager.isNightVisionActive(player.uniqueId)) {
                    if (configManager.requirePermission && !player.hasPermission("nightvision.use")) {
                        nightVisionManager.removeUser(player.uniqueId)
                        continue
                    }
                    nightVisionManager.applyEffect(player, configManager.showIcon)
                }
            }
        } catch (e: Exception) {
            logger.severe("NightVision failed to enable. A critical error occurred during setup.")
            logger.severe("Error details: ${e.message}")
            e.printStackTrace()
            server.pluginManager.disablePlugin(this)
        }
    }

    override fun onDisable() {
        if (this::nightVisionManager.isInitialized) {
            for (player in server.onlinePlayers) {
                if (nightVisionManager.isNightVisionActive(player.uniqueId)) {
                    nightVisionManager.removeEffect(player)
                }
            }
        }
        if (this::configManager.isInitialized) {
            configManager.stopWatcher()
        }
        if (this::databaseManager.isInitialized) {
            databaseManager.closeConnection()
        }
    }

    private fun reloadPlayerEffects() {
        for (player in server.onlinePlayers) {
            if (nightVisionManager.isNightVisionActive(player.uniqueId)) {
                nightVisionManager.removeEffect(player)
                nightVisionManager.applyEffect(player, configManager.showIcon)
            }
        }
    }
}
