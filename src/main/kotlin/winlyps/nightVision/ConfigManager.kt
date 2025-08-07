package winlyps.nightVision

import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.*
import kotlin.concurrent.thread

class ConfigManager(private val plugin: NightVision) {

    var onConfigReload: (() -> Unit)? = null
    private val dataFolder = plugin.dataFolder
    var requirePermission: Boolean = false
    var showParticles: Boolean = false
    var showIcon: Boolean = false
    var preventMilkRemoval: Boolean = true
    var applyOnJoin: Boolean = false
    lateinit var prefix: String
    lateinit var reloaded: String
    lateinit var nightVisionEnabled: String
    lateinit var nightVisionDisabled: String
    lateinit var noPermission: String
    lateinit var playerOnly: String

    init {
        createDefaultConfig()
        loadConfig()
    }

    fun loadConfig() {
        val configFile = File(dataFolder, "config.yml")
        val config = YamlConfiguration.loadConfiguration(configFile)

        applyOnJoin = config.getBoolean("apply-on-join", false)
        requirePermission = config.getBoolean("require-permission", false)
        showParticles = config.getBoolean("effects.show-particles", false)
        showIcon = config.getBoolean("effects.show-icon", false)
        preventMilkRemoval = config.getBoolean("effects.prevent-milk-removal", true)
        prefix = config.getString("messages.prefix", "&8[&bNightVision&8] &r")!!
        reloaded = config.getString("messages.reloaded", "&aConfiguration reloaded successfully.")!!
        nightVisionEnabled = config.getString("messages.night-vision-enabled", "&aNight Vision has been enabled.")!!
        nightVisionDisabled = config.getString("messages.night-vision-disabled", "&cNight Vision has been disabled.")!!
        noPermission = config.getString("messages.no-permission", "&cYou do not have permission to use this command.")!!
        playerOnly = config.getString("messages.player-only", "&cThis command can only be run by a player.")!!
    }

    fun getFormattedMessage(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', prefix + message)
    }

    private var watchKey: WatchKey? = null
    private var watcher: WatchService? = null

    fun startWatcher() {
        watcher = FileSystems.getDefault().newWatchService()
        watchKey = dataFolder.toPath().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY)

        thread(isDaemon = true) {
            while (true) {
                val key = watcher?.take() ?: break
                for (event in key.pollEvents()) {
                    val kind = event.kind()
                    if (kind == StandardWatchEventKinds.OVERFLOW) continue
                    @Suppress("UNCHECKED_CAST")
                    val ev = event as WatchEvent<Path>
                    val fileName = ev.context()
                    if (fileName.toString() == "config.yml") {
                        plugin.server.scheduler.runTask(plugin, Runnable {
                            loadConfig()
                            onConfigReload?.invoke()
                        })
                    }
                }
                if (!key.reset()) {
                    break
                }
            }
        }
    }

    fun stopWatcher() {
        watcher?.close()
    }

    private fun createDefaultConfig() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            configFile.writeText(
                """
                # NightVision Plugin Configuration

                # Set to true to require players to have the 'nightvision.use' permission.
                require-permission: false

                # Potion effect settings
                effects:
                  # If true, shows potion particles around the player.
                  show-particles: false
                  # If true, shows the Night Vision icon in the player's HUD.
                  show-icon: false
                  # If true, prevents players from removing the Night Vision effect by drinking milk.
                  prevent-milk-removal: true
 
                # If true, the plugin will apply the gamma night vision effect to players automatically on join.
                apply-on-join: false
                
                messages:
                  prefix: "&8[&bNightVision&8] &r"
                  reloaded: "&aConfiguration reloaded successfully."
                  night-vision-enabled: "&aNight Vision has been enabled."
                  night-vision-disabled: "&cNight Vision has been disabled."
                  no-permission: "&cYou do not have permission to use this command."
                  player-only: "&cThis command can only be run by a player."
                """.trimIndent()
            )
        }
    }
}