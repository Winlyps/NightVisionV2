package winlyps.nightVision

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GammaCommand(
    private val nightVisionManager: NightVisionManager,
    private val configManager: ConfigManager
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            Bukkit.getConsoleSender().sendMessage(configManager.getFormattedMessage(configManager.playerOnly))
            return true
        }

        if (configManager.requirePermission && !sender.hasPermission("nightvision.use")) {
            sender.sendMessage(configManager.getFormattedMessage(configManager.noPermission))
            return true
        }

        val nightVisionActive = nightVisionManager.isNightVisionActive(sender.uniqueId)
        val hasEffect = sender.hasPotionEffect(org.bukkit.potion.PotionEffectType.NIGHT_VISION)

        // If the effect is not active according to the plugin, enable it.
        if (!nightVisionActive) {
            nightVisionManager.toggleNightVision(sender, configManager.showIcon)
            sender.sendMessage(configManager.getFormattedMessage(configManager.nightVisionEnabled))
        }
        // If the effect IS active, but the player somehow doesn't have it, apply it.
        else if (nightVisionActive && !hasEffect) {
            nightVisionManager.applyEffect(sender, configManager.showIcon)
            sender.sendMessage(configManager.getFormattedMessage(configManager.nightVisionEnabled))
        }
        // If the effect is active and the player has it, disable it.
        else {
            nightVisionManager.toggleNightVision(sender, configManager.showIcon)
            sender.sendMessage(configManager.getFormattedMessage(configManager.nightVisionDisabled))
        }

        return true
    }
}