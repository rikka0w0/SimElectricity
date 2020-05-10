# SimElectricity 

[![Build Status](https://travis-ci.org/RoyalAliceAcademyOfSciences/SimElectricity.svg?branch=master)](https://travis-ci.org/RoyalAliceAcademyOfSciences/SimElectricity)

A Minecraft mod aiming to bring real world electrical systems into the Minecraft world

Presented by the Chinese Institution of Scientific Minecraft Mod (CISM)

Current Supported Version: Minecraft Forge 1.15.2

![Image](/documentation/grid.jpg)

# Addons: ![Image](/src/main/resources/assets/sime_essential/textures/item/fute_lemon_tea.png)
1. BuildCraft Extension:
Install BuildCraft facades to SimElectricity cable, already included in the SimElectricity Essential Mod.
1. ThermalExpension Extension: Install ThermalDynamics facades to SimElectricity cable, already included in the SimElectricity Essential Mod.

# Setup the Environment ![Image](/src/main/resources/assets/sime_essential/textures/item/tool_multimeter.png)
1. Ensure `Java` (found [here](https://www.java.com/en/download/manual.jsp)) and `Git` (found [here](http://git-scm.com/)) are properly installed on your system.
1. Create a base directory for the repo (anywhere you like)
1. On Windows, open either 'CMD' or Windows PowerShell, on Linux and MacOS, 
launch a terminal, then navigate to the directory just created,
and type the following commands:
1. `git clone https://github.com/RoyalAliceAcademyOfSciences/SimElectricity`
1. `git submodule init` and `git submodule update` to get LibRikka
* On Windows: use `gradlew.bat` instead of `gradlew`

# Build and Test ![Image](/src/main/resources/assets/sime_essential/textures/item/tool_crowbar.png)
1. Complete the steps in "Setup the Environment" section.
1. In the repo root folder, execute `gradlew runData` to launch the data generator, generated resource files will be located at "/src/generated"
1. If you just want to try this mod, run `gradlew runClient` to launch the game, otherwise skip this step.
1. Execute `gradlew build` to build SimElectricity.
1. Switch to the librikka directory under the repo root: `cd librikka`
1. Execute `gradlew build` again to build LibRikka jars
1. Jars files are in `SimElectricity/build/libs` and `SimElectricity\librikka\build\libs`
*  The suffix of deobfuscated jars is "dev".
*  __Obfuscated jars don't have any suffix, these jars are supposed to be used in normal minecraft games, copy them toyour `.minecraft\mods` directory __
*  `SimElectricity-full.jar` includes LibRikka

# For Developers ![Image](/src/main/resources/assets/sime_essential/textures/item/tool_wrench.png)
For API Usage, Please refer to the [SimElectricity Github Wiki Pages](https://github.com/RoyalAliceAcademyOfSciences/SimElectricity/wiki)
and comments in the API source code (`package simelectricity.api`).

## Eclipse
1. Setup the environment first (See section "Setup Environment")
1. In the SimElectricity directory execute `gradlew genEclipseRuns` to generate the workspace and launch configuration
1. Launch Eclipse, then `Import > Existing Gradle Project > Select Folder`
## IntelliJ IDEA
1. Setup the environment first (See section "Setup Environment")
1. Execute `gradlew genIntellijRuns`
1. In IDEA, import build.gradle as a gradle project
## Notes
1. Advanced users only: There are two hidden gradlew tasks: `energyNetDevJar` and `energyNetJar`, 
 which can generate Jars without the essential mod.
1. Since 1.15.2, Mojang introduces "DataGenerators", they generate json resource files such as blockstates and models.
They are invoked separately. Before building or testing this Mod, you have to execute the gradle task "runData". 
Checkout: [https://minecraft.gamepedia.com/Tutorials/Running_the_Data_Generator](https://minecraft.gamepedia.com/Tutorials/Running_the_Data_Generator)

## Special Thanks To: ![Image](/src/main/resources/assets/sime_essential/textures/items/tool_glove.png)
* [LibRikka](https://github.com/rikka0w0/librikka) - A code pack designed to simplify Minecraft Modding
* [CSPARSEJ](https://github.com/rwl/CSparseJ) - CSparseJ is a Java port of CSparse, a Concise Sparse matrix package.
* [Minecraft Forge](https://github.com/MinecraftForge/MinecraftForge) - A Minecraft mod container and loader
* [LambdaLib](https://github.com/LambdaInnovation/LambdaLib) - A modding library that aims at making modding fluent and enjoyable.
* Also, inspired by [BuildCraft](https://github.com/BuildCraft/BuildCraft) and
 [Immersive Engineering](https://github.com/BluSunrize/ImmersiveEngineering) !
