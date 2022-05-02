# XClaim
[![Java CI with Maven](https://github.com/WasabiThumb/xclaim/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/WasabiThumb/xclaim/actions/workflows/maven.yml)
A better chunk claim system for Paper servers

## Installation
You can download a build from the [releases tab](https://github.com/WasabiThumb/xclaim/releases) on the right, or [build the plugin yourself](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#build-the-project) if you want. Then, place the JAR into your plugins folder. Don't use the JAR labelled "original" unless you know what you are doing.

## Features
The main command is /claimgui (aliases are /claims and /cg). This allows players to create and manage their claims. Claims have several permissions that can be toggled, either by general groups (nobody, trusted players, veteran players, all players) or by individuals. The GUI also allows players to add/remove players from their trusted list.

### Importing from ClaimChunk
This process should be done without any players online. The server should have ClaimChunk AND XClaim loaded at the same time. It's possible that you need PlaceholderAPI on the server as well while doing this (unverified), but you definitely don't need either ClaimChunk nor PlaceholderAPI for XClaim to work normally. Once all of those conditions are met, run /importclaims. This may take a while or be resource intensive (hasn't been tested at scale) since it will attempt to turn adjacent claimed chunks into one group.

### Config
| Name | Description | Default Value |
| --: | :-: | :-- |
| veteran-time | The time in seconds it takes for a player to be on the server in order for Veteran status to take effect | 604800 (1 week) |
| stop-editing-on-shutdown | Whether or not players should be booted out of the chunk editor on shutdown | false |
| stop-editing-on-leave | Whether or not players should be booted out of the chunk editor when they leave voluntarily | true |
| exempt-claim-owner-from-permission-rules | If claim owners should have access to all permissions on the claim implicitly. You shouldn't change this, it's mainly for debugging | true |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-chunks | Sets the max chunks for a group. See Permissions for more info. | |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims | Sets the max claims for a group. See Permissions for more info. | |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.give-after | The time in seconds it takes for a player to play until they are automatically entered into this group. Values less than 0 signify "never". | -1 |

### Permissions
Don't worry, there aren't that many.
| Name | Description |
| --: | :-- |
| xclaim.override | Allows you to overwrite claimed chunks |
| xclaim.admin | Allows you to modify/delete any claim |
| xclaim.import | Allows you to import claims from the ClaimChunk plugin |
| xclaim.group.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦 | If a player has this permission, they are a part of this group. Players will inherit the maximum values from each group they are in. If the group is named "default", all players are in this group implicitly. |

### Commands
| Name | Description |
| --: | :-- |
| claimgui | Simple GUI for XClaim |
| importclaims | Import claims from ClaimChunk |

### Roadmap
* Dynmap Integration
