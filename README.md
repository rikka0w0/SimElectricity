# SimElectricity
==============

[![Build Status](https://travis-ci.org/RoyalAliceAcademyOfSciences/SimElectricity.svg?branch=master)](https://travis-ci.org/RoyalAliceAcademyOfSciences/SimElectricity)

A minecraft mod aiming to bring a real world electrical system into the minecraft world

Presented by the Scientific MineCraft Mod Researching Institution of the People's Republic of China (SMRI)

## Addons:
BuildCraft Extension:
Install BuildCraft facade to SimElectricity cable
https://github.com/RoyalAliceAcademyOfSciences/SimElectricity_BuildCraft_Extension

# Compiling and Testing
1. Ensure that `Java` (found [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)), `Git` (found [here](http://git-scm.com/)) are installed correctly on your system.
1. Create a base directory for the repo
1. (On Windows) open Git CMD and navigate to the directory just created
1. `git clone https://github.com/RoyalAliceAcademyOfSciences/SimElectricity`
1. `git submodule init` and `git submodule update` to get LibRikka
1. `gradlew build` to build jars
1. `gradlew setupDecompWorkspace` to setup a complete development environment.
* On Windows: use `gradlew.bat` instead of `gradlew`

# Notes
1. If you are using Intellij Idea, the IDE will configure LibRikka automatically, so you don't need to worry about this
2. Obfuscated and deobfuscated jars needs LibRikka to work properly
3. Navigate to librikka directory and use `gradlew build` to build LibRikka
4. In standalone MineCraft, you need to have both `SimElectricity.jar` and `librikka.jar` in your `mods` folder
5. `SimElectricity-full.jar` includes LibRikka
* Advanced users only: There are two hidden gradlew tasks: `energyNetDevJar` and `energyNetJar`, which can generate Jars without essential mod.

## Testing The Source Code (Eclipse):
* You MUST set the compatibility level of Java compiler to 1.8. By default, Minecraft Forge(1.11.2 - 13.20.1.2386) sets the compatibility level to 1.6. To do this, modify `sourceCompatibility = targetCompatibility = "1.6"` in build.gradle to `sourceCompatibility = targetCompatibility = "1.8"`.
1. Deploy the forge MDK, detailed tutorial can be found on forge's website: https://mcforge.readthedocs.io/en/latest/gettingstarted/
2. Navigate to the forge MKD folder, Delete `/src/main/java/com/example/` .
3. Download the source code as a Zip file
4. Extract the `src` folder in the Zip file to the forge MDK directory
5. Launch Eclipse and then run Minecraft, all done

## Special Thanks To:

CSPARSEJ - A Java port of CSPARSE, CSPARSE is a sparse matrix utility library

Minecraft Forge - A Minecraft mod container and loader

LambdaLib - A modding library that aims at making modding fluent and enjoyable.

Also, inspired by BuildCraft and Immersive Engineering !zz
