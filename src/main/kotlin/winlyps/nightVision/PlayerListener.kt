package winlyps.nightVision

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffectType

class PlayerListener(
    private val plugin: NightVision,
    private val nightVisionManager: NightVisionManager,
    private val configManager: ConfigManager
) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val shouldHaveNightVisionOnJoin = configManager.applyOnJoin || nightVisionManager.isNightVisionActive(player.uniqueId)

        if (!shouldHaveNightVisionOnJoin) {
            return
        }

        if (configManager.requirePermission && !player.hasPermission("nightvision.use")) {
            if (nightVisionManager.isNightVisionActive(player.uniqueId)) {
                nightVisionManager.removeUser(player.uniqueId)
            }
            return
        }
        
        if (configManager.applyOnJoin) {
            nightVisionManager.enableEffectOnJoin(player)
        } else {
            nightVisionManager.applyEffect(player, configManager.showIcon)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: org.bukkit.event.player.PlayerQuitEvent) {
        if(nightVisionManager.isNightVisionActive(event.player.uniqueId)) {
            nightVisionManager.removeEffect(event.player)
        }
    }

    @EventHandler
    fun onPlayerConsume(event: PlayerItemConsumeEvent) {
        if (configManager.preventMilkRemoval &&
            event.item.type == Material.MILK_BUCKET &&
            nightVisionManager.isNightVisionActive(event.player.uniqueId)) {
            event.isCancelled = true
            event.player.activePotionEffects.forEach { effect ->
                if (effect.type != PotionEffectType.NIGHT_VISION) {
                    event.player.removePotionEffect(effect.type)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        if (nightVisionManager.isNightVisionActive(event.player.uniqueId)) {
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                nightVisionManager.applyEffect(event.player, configManager.showIcon)
            }, 1L)
        }
    }
}