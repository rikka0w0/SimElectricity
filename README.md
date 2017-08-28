# SimElectricity
==============

[![Build Status](https://travis-ci.org/RoyalAliceAcademyOfSciences/SimElectricity.svg?branch=master)](https://travis-ci.org/RoyalAliceAcademyOfSciences/SimElectricity)

A minecraft mod aiming to bring a real world electrical system into the minecraft world

Presented by the Scientific MineCraft Mod Researching Institution of the People's Republic of China (SMRI)

## Addons:
BuildCraft Extension:
Install BuildCraft facade to SimElectricity cable
https://github.com/RoyalAliceAcademyOfSciences/SimElectricity_BuildCraft_Extension

## Test The Source Code (Eclipse):
### You MUST set the compatibility level of Java compiler to 1.8. By default, Minecraft Forge(1.11.2 - 13.20.1.2386) sets the compatibility level to 1.6. To do this, modify `sourceCompatibility = targetCompatibility = "1.6"` in build.gradle to `sourceCompatibility = targetCompatibility = "1.8"`.
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
