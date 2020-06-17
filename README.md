FabledSkyBlock LaborPatch
===========
Forked from [FabledSkyBlock](https://gitlab.com/Songoda/fabledskyblock)

This fork contains bug fixes, features and improvements:
- Added option to check location security when using `/is visit`
- Added option to remove water from Island Spawn
- Added option to have Members, Operators and Owners all responding to Members settings as a temporary fix to Operators and Owners settings not being editable via GUI
- Added option to toggle fall damage in certain conditions like when using `/is home`
- Added per-world generator (editable only from generators.yml, GUI not supported yet)
- Added a "subtract" value to levels in order to have all the new islands to level 0
- Added option to set default WorldBorder status
- Added permissions for WorldBorder colors
- Added permissions to bypass kick and ban
- Added water in Nether mechanics!
- Added option to let slime splitting bypass limits.yml
- Added option to define distance between islands
- Added option to clear inventory and/or enderchest on island delete (working only with online players)
- Added deletion cooldown
- Fixed bugs in Challenges that didn't remove all the items
- Fixed WorldBorder size not reflecting real island size
- Fixed bugs in island settings that prevented the from loading correctly
- Fixed mob grief setting
- Fixed explosion setting
- Fixed damage setting
- Fixed use portal setting
- Fixed bank that couldn't be opened from Members
- Fixed message telling that island disappeared on login
- Fixed GUI menus that had used the same page variable
- Fixed stackable bypassing break setting
- Now you can use `/is chat <message>` to send messages to island chat
- Now Challenges can be per-island too
- Now hunger setting works as intended
- Hide options in control panel if missing the permission
- Hide vanished players from visitors list
- Hide bank from leaderboard if disabled
- Minor bug fixes
- Little optimizations

Use this fork at your own risk. No support provided.

Compile
------
To compile this fork, clone it and run the following command
```
mvn clean package
```
You will find the jar file in the target/ directory.

