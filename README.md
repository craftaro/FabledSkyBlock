FabledSkyBlock LaborPatch
===========
Forked from [FabledSkyBlock](https://gitlab.com/Songoda/fabledskyblock)

This fork contains bug fixes, features and improvements:
- Added option to check location security using `/is visit`
- Added option to remove water from Island Spawn
- Added an option to toggle fall damage in certain conditions like when using `/is home`
- Added per-world generator (editable only from generators.yml, GUI not supported yet)
- Added a "subtract" value to levels in order to have all the new islands to level 0
- Added option to set default WorldBorder status
- Added permissions for WorldBorder colors
- Added permission to bypass `/is kick`
- Added water in Nether mechanics!
- Added option to let slime splitting bypass limits.yml
- Fixed bugs in Challenges that didn't remove all the items
- Fixed WorldBorder size not reflecting real island size
- Now you can use `/is chat <message>` to send messages to island chat
- Now Challenges can be per-island too
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

