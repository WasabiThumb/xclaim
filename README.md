# <img src="https://wasabicodes.xyz/cdn/15852266ddef4696b804677658f11651/xc_logo.png" alt="XClaim" title="XClaim" style="width: 12em">

[![Java CI with Maven](https://github.com/WasabiThumb/xclaim/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/WasabiThumb/xclaim/actions/workflows/maven.yml)\
A better chunk claim system for Paper servers\
*Get it? It sounds like exclaim...*\
\
| [Installation](#installation) | [Features](#features) | [Config](#config) | [Permissions](#permissions) | [Commands](#commands) | [Support](#support) | [Roadmap](#roadmap) |

## Installation
You can download a build from the [releases tab](https://github.com/WasabiThumb/xclaim/releases) on the right, or [build the plugin yourself](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#build-the-project) if you want. Then, place the JAR into your plugins folder. Don't use the JAR labelled "original" unless you know what you are doing.

## Features
The main command is /xclaim (alias /xc). This allows players to create and manage their claims. Claims have several permissions that can be toggled, either by general groups (nobody, trusted players, veteran players, all players) or by individuals. The GUI also allows players to add/remove players from their trusted list.

### Dynmap Integration
Dynmap integration should work out-of-the-box as long as it is [configured properly](#config). If it does not, please create an Issue on the [Issues page](https://github.com/WasabiThumb/xclaim/issues).\
View the Dynmap on the [HL21st Minecraft Server](https://www.planetminecraft.com/server/half-life-21st-century-humor-official/) live by clicking [here](http://hl21st.com:8104/).\
\
<img src="https://wasabicodes.xyz/cdn/e536fc60213f22701f2e55858f8f87f9/dynmap.png" alt="Dynmap integration working on the HL21st Minecraft Server, captured on 5/13/22" title="HL21st Minecraft Server" style="width: 30em">

### Importing from ClaimChunk
This process should be done without any players online. The server should have ClaimChunk AND XClaim loaded at the same time. It's possible that you need PlaceholderAPI on the server as well while doing this (unverified), but you definitely don't need either ClaimChunk nor PlaceholderAPI for XClaim to work normally. Once all of those conditions are met, run /importclaims. This may take a while or be resource intensive (hasn't been tested at scale) since it will attempt to turn adjacent claimed chunks into one group.

### Languages
As of version 1.6.x, multiple languages are supported. When the plugin starts, default language packs are loaded into ``/plugins/XClaim/lang``. Below are a list of default language packs:
- en-US (American English)

The plugin decides what language to use based on the "language" option in the [config](#config).
\
\
If you want to make your own language pack, copy an existing one as an example (e.g. ``/plugins/XClaim/lang/en-US.json``) and rename it [accordingly](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) (e.g. ``fr.json``). You can then translate the contents of that file. Knowledge of [JSON](https://en.wikipedia.org/wiki/JSON#Syntax) and [MiniMessage](https://docs.adventure.kyori.net/minimessage/index.html) is highly suggested. Do not translate the keys, only the values. Language packs may become less human-readable after encoding, so it is suggested to get your language pack base from [the source](https://github.com/WasabiThumb/xclaim/tree/master/src/main/resources/lang).

## Config
| Name | Description | Default Value |
| --: | :-: | :-- |
| language | The language to use, must be a valid language pack from ``/plugins/XClaim/lang`` otherwise falls back to en-US | en-US |
| veteran-time | The time in seconds it takes for a player to be on the server in order for Veteran status to take effect | 604800 (1 week) |
| stop-editing-on-shutdown | Whether or not players should be booted out of the chunk editor on shutdown | false |
| stop-editing-on-leave | Whether or not players should be booted out of the chunk editor when they leave voluntarily | true |
| exempt-claim-owner-from-permission-rules | If claim owners should have access to all permissions on the claim implicitly. You shouldn't change this, it's mainly for debugging | true |
| enforce-adjacent-claim-chunks | Whether or not chunks in a claim must be next to each other | true |
| allow-diagonal-claim-chunks | If enforce-adjacent-claim-chunks is true, this sets if chunks diagonal from each other are considered as "next to" each other. Otherwise, does nothing. | true |
| enter-chunk-editor-on-create | If true, then players will enter the chunk editor when they make a new claim | true |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-chunks | Sets the max chunks for a group. See Permissions for more info. | |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims | Sets the max claims for a group. See Permissions for more info. | |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.give-after | The time in seconds it takes for a player to play until they are automatically entered into this group. Values less than 0 signify "never". | -1 |
| dynmap-integration.enabled | If true, XClaim will look for dynmap on startup and hook into it. Mild speedup if turned off. | true |
| dynmap-integration.use-old-outline-style | If true, the dynmap will use the old convex hull outlines on claims. This is mainly for debugging, as the new outline system is experimental. | false |

## Permissions
Don't worry, there aren't that many.
| Name | Description |
| --: | :-- |
| xclaim.override | Allows you to overwrite claimed chunks |
| xclaim.admin | Allows you to modify/delete any claim |
| xclaim.import | Allows you to import claims from the ClaimChunk plugin |
| xclaim.update | Allows you to use the auto-updater |
| xclaim.restart | Allows you to restart xclaim |
| xclaim.clear | Allows clearing claims from players with /xclaim clear |
| xclaim.group.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦 | If a player has this permission, they are a part of this group. Players will inherit the maximum values from each group they are in. If the group is named "default", all players are in this group implicitly. |

## Commands
| Name | Description |
| --: | :-- |
| xclaim | XClaim main command. Without any extra arguments, is the same as /xclaim gui |
| xclaim help | List the available subcommands |
| xclaim info | Provides basic info about XClaim |
| xclaim gui | Opens an easy-to-use GUI that covers most of XClaim's important features |
| xclaim update | Scans for new versions of XClaim and, if desired, runs the auto-updater |
| xclaim chunks \[claim_name] | Opens the chunk editor for the specified claim or, if absent, the current residing claim |
| xclaim current | Gets info about the current claim you are in |
| xclaim restart | Restart XClaim without restarting the server (experimental) |
| xclaim clear | Clear all claims from a player |
| xclaim list | Lists all claims a player owns |
| importclaims | Import claims from ClaimChunk |

## Support
|         | 1.17 | 1.18 | 1.19 | Paper | Spigot |
| --:     | :-:  | :-:  | :-:  | :-:   | :-:    |
| < 1.3.1 |      | ✔    |      | ✔     |        |
| 1.3.1   |      | ✔    | ✔    | ✔     |        |
| 1.4.x   | ✔    | ✔    | ✔    | ✔     |        |
| 1.5.x+  | ✔    | ✔    | ✔    | ✔     | ✔      |


## Roadmap
* Add more management commands
