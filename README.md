# SimElectricity 

[![Build Status](https://travis-ci.org/RoyalAliceAcademyOfSciences/SimElectricity.svg?branch=master)](https://travis-ci.org/RoyalAliceAcademyOfSciences/SimElectricity)

A Minecraft mod aiming to bring real world electrical systems into the Minecraft world

Presented by the Chinese Institution of Scientific Minecraft Mod (CISM)

![Image](/documentation/hvpole.png)

# Addons: ![Image](/src/main/resources/assets/sime_essential/textures/items/cell_vita.png)
[BuildCraft Extension](https://github.com/RoyalAliceAcademyOfSciences/SimElectricity_BuildCraft_Extension):
Install BuildCraft facade to SimElectricity cable

# Setup Environment ![Image](/src/main/resources/assets/sime_essential/textures/items/tool_multimeter.png)
1. Ensure `Java` (found [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)) and `Git` (found [here](http://git-scm.com/)) are properly installed on your system.
1. Create a base directory for the repo (anywhere you like)
1. On Windows, open either 'CMD' or Windows PowerShell, on Linux and MacOS, 
launch a terminal, then navigate to the directory just created,
and type the following commands:
1. `git clone https://github.com/RoyalAliceAcademyOfSciences/SimElectricity`
1. `git submodule init` and `git submodule update` to get LibRikka
1. `gradlew setupDecompWorkspace` to setup a complete development environment.
* On Windows: use `gradlew.bat` instead of `gradlew`
* If you don't need the source code of Minecraft and Minecraft Forge, or just want to build the code, use 
`gradlew setupDeVWorkspace` instead.

# Compile and Test ![Image](/src/main/resources/assets/sime_essential/textures/items/tool_crowbar.png)
1. On Windows, open either 'CMD' or Windows PowerShell, on Linux and MacOS, 
   launch a terminal, then navigate to the SimElectricity directory (the one contains README.MD and Jenkinsfile)
1. Execute `gradlew build` to generate SimElectricity jars
1. Switch to the librikka directory under the SimElectricity directory
1. Execute `gradlew build` again to build LibRikka jars
1. Jars files are in `SimElectricity/build/libs` and `SimElectricity\librikka\build\libs`
1. Copy `SimElectricity-xx.xx.xx.jar` and `LibRikka-1.0.0.jar` to the `mods` folder under your `.minecraft` directory
1. Launch your Minecraft and enjoy
* __Both Obfuscated and deobfuscated jars needs corresponding version of LibRikka to work properly__
* `SimElectricity-full.jar` includes LibRikka

# For Developers ![Image](/src/main/resources/assets/sime_essential/textures/items/tool_wrench.png)
## Eclipse
1. Setup the environment first (See section "Setup Environment")
1. In the SimElectricity directory execute `gradlew eclipse` to generate the workspace and launch configuration
1. Launch Eclipse and set your workspace to `.eclipse` folder under the SimElectricity directory
1. Open any source file and hit the debug button to test if it works
##IntelliJ IDEA
1. Launch IDEA and clone the SimElectricity git repository to your local disk
1. IDEA should prompt you for importing Gradle project, choose YES
1. After IDEA finishes setting up Gradle, open Gradle panel
1. Double click `setupDecompWorkspace` under `Tasks->forgegradle`
1. After finish this, double click `genIntellijRuns`
1. You are ready to go
## Notes
1. You MUST set the compatibility level of Java compiler to 1.8. By default, 
Minecraft Forge(1.11.2 - 13.20.1.2386) sets the compatibility level to 1.6. 
To do this, change `sourceCompatibility = targetCompatibility = "1.6"` in `build.gradle`
 to `sourceCompatibility = targetCompatibility = "1.8"`.
1. Advanced users only: There are two hidden gradlew tasks: `energyNetDevJar` and `energyNetJar`, 
 which can generate Jars without the essential mod.

## Special Thanks To: ![Image](/src/main/resources/assets/sime_essential/textures/items/tool_glove.png)
* [LibRikka](https://github.com/rikka0w0/librikka) - A code pack designed to simplify Minecraft Modding
* [CSPARSEJ](https://github.com/rwl/CSparseJ) - CSparseJ is a Java port of CSparse, a Concise Sparse matrix package.
* [Minecraft Forge](https://github.com/MinecraftForge/MinecraftForge) - A Minecraft mod container and loader
* [LambdaLib](https://github.com/LambdaInnovation/LambdaLib) - A modding library that aims at making modding fluent and enjoyable.
* Also, inspired by [BuildCraft](https://github.com/BuildCraft/BuildCraft) and
 [Immersive Engineering](https://github.com/BluSunrize/ImmersiveEngineering) !
