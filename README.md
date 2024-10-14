<h1 align="center">
<img src="doc/banner.png" alt="XClaim" title="XClaim" style="height: 6em">
</h1>

<div align="center">
<img alt="Build Status" src="https://img.shields.io/github/actions/workflow/status/WasabiThumb/xclaim/maven.yml">
<img src="https://img.shields.io/badge/dynamic/json?color=informational&label=version&query=%24.title&url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F102843%2Fupdates%2Flatest" alt="Version">
<img src="https://img.shields.io/badge/dynamic/json?color=informational&label=active%20servers&query=%24%5B0%5D%5B1%5D&url=https%3A%2F%2Fbstats.org%2Fapi%2Fv1%2Fplugins%2F16129%2Fcharts%2Fservers%2Fdata%2F%3FmaxElements%3D1" alt="Active Servers">
<img src="https://img.shields.io/badge/dynamic/json?color=informational&label=active%20players&query=%24[0][1]&url=https%3A%2F%2Fbstats.org%2Fapi%2Fv1%2Fplugins%2F16129%2Fcharts%2Fplayers%2Fdata%2F%3FmaxElements%3D1" alt="Active Players">
<img src="https://img.shields.io/endpoint?url=https%3A%2F%2F8f2bde531f0d7a.lhr.life%2F%3Fmetric%3Dstars&label=stars&cacheSeconds=3600" alt="Stars">
<img src="https://img.shields.io/endpoint?url=https%3A%2F%2F8f2bde531f0d7a.lhr.life%2F%3Fmetric%3Ddownloads&label=downloads&cacheSeconds=3600" alt="Downloads">
</div>

<div align="center">
<h2>
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.md" title="American English">🇬🇧</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.de.md" title="German">🇩🇪</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.zh.md" title="Chinese (Simplified)">🇨🇳</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.tr.md" title="Turkish">🇹🇷</a>
</h2>
</div>

<div align="center">
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="#installation">Installation</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="#features">Features</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="#config">Config</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="#permissions">Permissions</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="#commands">Commands</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="#support">Support</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="#roadmap">Roadmap</a>
</div>

## Installation
You can download a build from the [releases tab](https://github.com/WasabiThumb/xclaim/releases) on the right, or [build the plugin yourself](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#build-the-project) if you want. Then, place the JAR into your plugins folder. Don't use the JAR labelled "original" unless you know what you are doing.

## Features
The main command is /xclaim (alias /xc). This allows players to create and manage their claims. Claims have several permissions that can be toggled, either by general groups (nobody, trusted players, veteran players, all players) or by individuals. The GUI also allows players to add/remove players from their trusted list.

### Map Integration
- Map integration should work out-of-the-box as long as it is [configured properly](#config). If it does not, please create an Issue on the [Issues page](https://github.com/WasabiThumb/xclaim/issues).
- BlueMap integration has also been supported since version 1.10.0.

### Importing from ClaimChunk
This process should be done without any players online. The server should have ClaimChunk AND XClaim loaded at the same time. It's possible that you need PlaceholderAPI on the server as well while doing this, but you definitely don't need either ClaimChunk nor PlaceholderAPI for XClaim to work normally. Once all of those conditions are met, run /importclaims. This may take a while or be resource intensive since it will attempt to turn adjacent claimed chunks into one group.

### Languages
As of version 1.6.x, multiple languages are supported. When the plugin starts, default language packs are loaded into ``/plugins/XClaim/lang``. Below are a list of default language packs:
- en-US (American English)
- de (German) by eingruenesbeb
- zh (Simplified Chinese) by SnowCutieOwO
- tr (Turkish) by Krayir5

The plugin decides what language to use based on the "language" option in the [config](#config).
\
\
If you want to make your own language pack, copy an existing one as an example (e.g. ``/plugins/XClaim/lang/en-US.json``) and rename it [accordingly](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) (e.g. ``fr.json``). You can then translate the contents of that file. Knowledge of [JSON](https://en.wikipedia.org/wiki/JSON#Syntax) and [MiniMessage](https://docs.adventure.kyori.net/minimessage/index.html) is highly suggested. Do not translate the keys, only the values. Language packs may become less human-readable after encoding, so it is suggested to get your language pack base from [the source](https://github.com/WasabiThumb/xclaim/tree/master/src/main/resources/lang). There are some cases where the symbols ``$1``, ``$2``, etc. are used. This means that "something is inserted here", for example ``Hello $1!`` may resolve to ``Hello Username!`` ingame.

### Economy
By default, economy features are disabled. To enable them, set "use-economy" in the config to true.\
If use-economy is enabled, XClaim will attempt to hook into the following economy plugins if present:
- Vault
- EssentialsX

Players will then pay depending on the permission group the player is in (see [here](#permissions)).\
For instance, if you wanted to set the default price for a claim to 2.25, then you would set ``limits.default.claim-price`` to ``2.25``.\
See all options in the [config section](#config).

## Config
Configuration is now handled by [``config.toml``](https://github.com/WasabiThumb/xclaim/blob/master/src/main/resources/config.toml), which is fairly self-explanatory.
Support for the [legacy YAML config](https://github.com/WasabiThumb/xclaim/blob/00823def93261519b8ca836a1a774a5a1f81ce65/README.md#config) may be removed in the future.

**If both formats are present, [``config.yml``](https://github.com/WasabiThumb/xclaim/blob/00823def93261519b8ca836a1a774a5a1f81ce65/src/main/resources/config.yml) will be used.**

### Config (GUI Layouts)
**This only applies for ``config.toml`` with ``gui.version`` set to 2.**

After running once, the ``layouts`` directory will appear in the XClaim configuration root. This will give access to GUI
layout files (e.g. ``layouts/main.xml``). A typical application for editing the layouts would be to remove a button from the GUI. For instance, if you wanted to remove the ability to modify the ``ENTER`` permission, then change ``layouts/permission-list.xml``:

```diff
    <slot id="2"/>  <!-- BREAK -->
-   <slot id="3"/>  <!-- ENTER -->
+   <!-- <slot id="3"/> --> <!-- ENTER -->
    <slot id="4"/>  <!-- INTERACT -->
```

The format is not very friendly, but an attempt will be made to document it here:

|          Tag |              Allowed Properties               | Description                                                                                                                                                                                                  |
|-------------:|:---------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ``<layout>`` |                   - none -                    | The document root. No other tags should be placed at top-level, including metadata.                                                                                                                          |
|    ``<row>`` |    ``id``, ``x``, ``y``, ``w``, ``basis``     | Automatically adjusts the X position of each child element according to either the ``basis`` set in the config or the ``basis`` set on the tag. If an ``id`` is specified, it should have no child elements. |
|   ``<slot>`` |             ``id``, ``x``, ``y``              | Marks a location where XClaim can insert an item. Must have an ``id`` and must have no child elements.                                                                                                       |
|   ``<area>`` | ``id``, ``x``, ``y``, ``w``, ``h``, ``basis`` | Marks a location where XClaim can insert multiple (in excess of 9) items. Must have an ``id`` and must have no child elements. Mainly used for paginated content.                                            |


|  Property | Description                                                                                                                                                                                                                                                                              |
|----------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|    ``id`` | Binds the element to a slot in the code. If the spec wishes to place an item at ID ``0``, it will end up located at the ``x`` and ``y`` position of the element with ``id="0"``.                                                                                                         |
|     ``x`` | Sets the ``x`` position of the element. Must be between ``0`` and ``CONTAINER_WIDTH - 1`` (denoted as ``~``[†](#tilda-syntax)). If not specified, the element inherits the ``x`` position of its container.                                                                              |
|     ``y`` | Sets the ``y`` position of the element. Must be between ``0`` and ``CONTAINER_HEIGHT - 1`` (denoted as ``~``[†](#tilda-syntax)). If not specified, the element inherits the ``y`` position of its container.                                                                             |
|     ``w`` | Sets the width of the element. Must be between ``1`` and ``CONTAINER_WIDTH`` (denoted as ``~``[†](#tilda-syntax)). If not specified, the width is the default width for that element. For instance, ``<row>`` is width ``~`` by default, and ``<slot>`` is width ``1`` by default.       |
|     ``h`` | Sets the width of the element. Must be between ``1`` and ``CONTAINER_HEIGHT`` (denoted as ``~``[†](#tilda-syntax)). If not specified, the height is the default height for that element. For instance, ``<area>`` is height ``~`` by default, and ``<slot>`` is height ``1`` by default. |
| ``basis`` | The default horizontal alignment of slots within this element. Must be one of ``LEFT``, ``RIGHT``, ``CENTER`` or ``EVEN``.                                                                                                                                                               |

#### Tilda Syntax
The symbol ``~`` when applied to a numeric value indicates the maximum value that is within bounds. A number placed
after the symbol subtracts from the maximum, for instance ``~1`` is one less than the maximum and ``~2`` is two less than the maximum.


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
| xclaim.group.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦 | If a player has this permission, they are a part of this group. Players will inherit the "best" values from each group they are in. If the group is named "default", all players are in this group implicitly. |

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

## Placeholders
PlaceholderAPI integration was added in plugin version 1.13
| Name | Description |
| --: | :-- |
| xclaim_claim_count | Number of claims a player owns |
| xclaim_claim_count_in_*world* | Number of claims a player owns in *world* |
| xclaim_claim_max | Maximum number of claims a player could own |
| xclaim_chunk_count | Aggregate number of chunks a player owns |
| xclaim_chunk_count_in_*world* | Aggregate number of chunks a player owns in *world* |
| xclaim_chunk_max | Maximum number of chunks a player can have in **one claim** |
| xclaim_chunk_max_abs | Maximum number of chunks a player could own, if the player had as many claims as they possibly could and each claim had as many chunks as they possibly could |

## Support
|         | 1.8 - 1.11 | 1.12 - 1.13 | 1.14 - 1.16 | 1.17 - 1.19 | 1.20 | Folia | Paper & Spigot |
| --:     | :-:  | :-:  | :-:  | :-:  | :-:  | :-:  | :-:  |
| 1.5.0   | ❌   | ❌   | ❌   | ✔    | ❌    | ❌   | ✔     | 
| 1.8.0   | ❌   | ❌   | ✔    | ✔    | ❌    | ❌    | ✔     | 
| 1.9.0   | ❌   | ✔   | ✔    | ✔    |❌    | ❌    | ✔     |
| 1.9.1  | ✔   | ✔   | ✔    | ✔    | ❌    | ❌    | ✔     | 
| 1.10.0  | ✔   | ✔   | ✔    | ✔    | ✔    | ❌    | ✔     | 
| 1.10.2  | ✔   | ✔   | ✔    | ✔    | ✔    | ✔    | ✔     |
| 1.12.0  | ✔   | ✔   | ✔    | ✔    | ✔    | ✔    | ✔     | 

Versions before 1.5.0 are no longer supported

## Roadmap
* Add more management commands
