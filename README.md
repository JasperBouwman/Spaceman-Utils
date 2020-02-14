# Spaceman Utils

This util is created to help you create Bukkit plugins.

When using this util you can download the this util as a whole, or separate classes to use.
But make sure that the path is different in all the plugins<br>
Example: com.spaceman.example.util.ColorFormatter.

When 2 (or more) plugins are registering which contains class names in the same package you will run into problems. Example:<br>
Plugin1 has ColorFormatter in the package 'com.spaceman.utils.ColorFormatter'.<br>
Plugin2 has ColorFormatter in the package 'com.spaceman.utils.ColorFormatter'.<br>
The Classloader can't register the ColorFormatter in Plugin2...

Every package after com.spaceman is a separate util (and every class is its own util/template).<br>
In the main file of the util is described which utils is needed to use it

For more questions feel fee to ask me

Main files:  
colorFormatter.ColorFormatter  
colorFormatter.ColorTheme  
commandHandler.CommandTemplate  
cooldown.CooldownManager  
cooldown.CooldownSubCommand  
fancyMessage.Message  
fancyMessage.Book  
fileHandler.Files  
keyValueHelper.KeyValueHelper  
mapsUtil.MapTool (no documentation available, new version will come)  
playerUUID.PlayerUUID  
Main  
Pair  
Permissions  
Glow  