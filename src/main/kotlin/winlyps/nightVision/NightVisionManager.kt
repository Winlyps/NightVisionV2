package winlyps.nightVision

import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID

class NightVisionManager(
    private val databaseManager: DatabaseManager,
    private val configManager: ConfigManager
) {

    private val activeUsers = mutableSetOf<UUID>()

    fun loadInitialUsers() {
        activeUsers.clear()
        activeUsers.addAll(databaseManager.getNightVisionUsers())
    }

    fun toggleNightVision(player: Player, showIcon: Boolean): Boolean {
        val uuid = player.uniqueId
        return if (activeUsers.contains(uuid)) {
            activeUsers.remove(uuid)
            removeEffect(player)
            databaseManager.removeNightVisionUser(uuid)
            false
        } else {
            activeUsers.add(uuid)
            applyEffect(player, showIcon)
            databaseManager.addNightVisionUser(uuid)
            true
        }
    }

    fun applyEffect(player: Player, showIcon: Boolean) {
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            return
        }
        
        val effect = PotionEffect(
            PotionEffectType.NIGHT_VISION,
            Integer.MAX_VALUE,
            0,
            false,
            configManager.showParticles,
            showIcon
        )
        player.addPotionEffect(effect)
    }

    fun enableEffectOnJoin(player: Player) {
        activeUsers.add(player.uniqueId)
        applyEffect(player, configManager.showIcon)
    }

    fun removeEffect(player: Player) {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION)
    }

    fun isNightVisionActive(uuid: UUID): Boolean {
        return activeUsers.contains(uuid)
    }
    fun removeUser(uuid: UUID) {
        activeUsers.remove(uuid)
        databaseManager.removeNightVisionUser(uuid)
    }
}