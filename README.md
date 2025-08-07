# NightVision Plugin ( UPDATED to V2 )   
Required Plugin: https://modrinth.com/plugin/kotlinplugins
- Provides Night Vision effect to ensure you never have to struggle with Brightness again!
 
- Usage: ```/gamma```   or   ```/nv``` ( use the command to enable/disable )
- Required permission: ```nightvision.use``` ( by default it's disabled ) 
- Plugin generates configurable config.yml file inside NightVision folder.
- You don't have to reload the plugin.  After changing and saving config.yml - updates automatically!

```
                # NightVision Plugin Configuration

                # Set to true to require players to have the 'nightvision.use' permission.
                require-permission: false

                # Potion effect settings
                effects:
                  # If true, shows Night Vision particles around the player.
                  show-particles: false
                  # If true, shows the Night Vision icon in the player's HUD.
                  show-icon: false
                  # If true, prevents players from removing the Night Vision effect by drinking milk.
                  prevent-milk-removal: true
 
                # If true, the plugin will apply the gamma night vision effect to players automatically on join.
                apply-on-join: false
                
                messages:
                  prefix: "&8[&bNightVision&8] &r"
                  night-vision-enabled: "&aGamma has been enabled."
                  night-vision-disabled: "&cGamma has been disabled."
                  no-permission: "&cYou do not have permission to use this command."
                  player-only: "&cThis command can only be run by a player." 

```
- If player dies with gamma/nv enabled, the night vision effect will persist!!!
- Information about enabled gamma/nv is stored inside generated database file.

Check out other plugins: https://modrinth.com/user/Winlyps
