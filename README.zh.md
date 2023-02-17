# <img src="https://wasabicodes.xyz/cdn/15852266ddef4696b804677658f11651/xc_logo.png" alt="XClaim" title="XClaim" style="width: 12em">
<h2>
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.md" title="美式英语">🇬🇧</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.de.md" title="德语">🇩🇪</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.zh.md" title="简体中文">🇨🇳</a>
</h2>

[![Java CI with Maven](https://github.com/WasabiThumb/xclaim/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/WasabiThumb/xclaim/actions/workflows/maven.yml)
<img src="https://img.shields.io/badge/dynamic/json?color=informational&label=Version&query=%24.title&url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F102843%2Fupdates%2Flatest" alt="版本">
<img src="https://img.shields.io/badge/dynamic/json?color=informational&label=Active%20Servers&query=%24%5B0%5D%5B1%5D&url=https%3A%2F%2Fbstats.org%2Fapi%2Fv1%2Fplugins%2F16129%2Fcharts%2Fservers%2Fdata%2F%3FmaxElements%3D1" alt="使用数">
<img src="https://img.shields.io/badge/dynamic/json?color=informational&label=Active%20Players&query=%24[0][1]&url=https%3A%2F%2Fbstats.org%2Fapi%2Fv1%2Fplugins%2F16129%2Fcharts%2Fplayers%2Fdata%2F%3FmaxElements%3D1" alt="使用玩家">
<img src="https://img.shields.io/badge/dynamic/json?color=informational&label=Downloads&query=%24.downloads&url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F102843" alt="下载">
<img src="https://shields.io/spiget/stars/102843" alt="评分">

专为 Paper 服务器打造的区块领地插件\
*明白吗? 它听起来像是 exclaim...*\
\
| [安装教程](#installation) | [插件特色](#features) | [配置文件](#config) | [插件权限](#permissions) | [插件命令](#commands) | [联系支持](#support) | [未来计划](#roadmap) |

## 安装教程
你可以在 [发布页](https://github.com/WasabiThumb/xclaim/releases) 右侧下载插件本体, 如果你想的话, 也可以通过源代码 [自行构建本插件](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#build-the-project). 这之后, 将 JAR 文件置入你的服务器文件夹 plugins 下. 但注意请不要将文件名中带有 "original" 的插件加入服务器, 除非你已经知道你在干什么, 否则后果自负.

## 插件特色
本插件的主命令是 /xclaim (可缩写为 /xc). 这允许玩家通过该命令创建并管理他们的领地. 领地有一些可被切换的权限, 同时也可对全局用户组生效 (例如 nobody 无玩家, trusted players 受信任用户组, veteran players 驻留用户组, all players 全体玩家) ,也可对单独的玩家生效. 本插件自带的 GUI 也可以编辑全局或单独玩家的相关权限.

### Dynmap 集成
若 [正确地设定插件的配置文件](#config), 本插件应当支持在 Dynmap 地图上显示已领取的领地及其所属区块. 若它没有正常运作, 在你确认这不是的配置问题后在本插件的开源页面的 [Issues 区域](https://github.com/WasabiThumb/xclaim/issues) 提交相关问题.\
若要预览本插件的效果, 你可以在 [HL21st 服务器](https://www.planetminecraft.com/server/half-life-21st-century-humor-official/) 的 [此处](http://hl21st.com:8104/) 来浏览本插件的效果.\
\
<img src="https://wasabicodes.xyz/cdn/e536fc60213f22701f2e55858f8f87f9/dynmap.png" alt="集成了 Dynmap 功能的 HL21st 服务器, captured on 5/13/22" title="HL21st 服务器" style="width: 30em">

### 从 ClaimChunk 插件导入数据
该过程建议在全体玩家均不在线时进行. 服务器必须同时安装 ClaimChunk 与 XClaim. 同时你可能也需要安装 PlaceholderAPI 以使这项功能正常运站 (需要验证), 但你可以在数据转化完毕后卸载 ClaimChunk 或 PlaceholderAPI , 这不会干扰本插件的正常运行 (译者注: 有必要保留后者). 当一切准备就绪时, 输入命令 /importclaims 即可开始. 这可能需要一段时间, 并且可能会消耗大量资源(注意: 本插件尚未进行过较大规模转化的测试), 因为本插件会将相邻的已认领区块合并至一个领地下.

### Languages
在 1.6.x 之后的插件, 支持了多种语言. 当插件初次运行时, 默认的语言包会被载入至 ``/plugins/XClaim/lang`` 下. 下列是默认支持的语言包:
- en-US (美式英语)
- de (德语) by eingruenesbeb
- zh (简体中文) by SnowCutieOwO

[配置文本](#config) 中的 "language" 选项可决定插件使用哪种语言.
\
\
若你想要创建新的语言包, 将已有的语言文本复制一份用作模板 (例如 ``/plugins/XClaim/lang/en-US.json``) 并将其重命名, 格式需按照 [你的语言缩写](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) (例如 ``fr.json`` 即为 法语语言包). 这样你就可以开始语言文件的翻译工作了. 你可能需要用到 [JSON 教程](https://en.wikipedia.org/wiki/JSON#Syntax) 以及 [MiniMessage 教程](https://docs.adventure.kyori.net/minimessage/index.html). 不可以翻译这些特殊的代码, 只可以翻译语言文本. 编码后的语言文件可读性会变差, 所以最好从 [源](https://github.com/WasabiThumb/xclaim/tree/master/src/main/resources/lang) 获取你需要的语言文件 (译者注: 部分地区网络连接可能不佳). 当然也有一些诸如 ``$1``, ``$2`` 的保留字被使用. 这些字符的意识是 "有些东西在此处会被替换" (译者注: 即内建变量), 例如 ``Hello $1!`` 会在游戏中实际显示为 ``Hello Username!``.

### 经济支持
经济支持默认关闭. 若要启用, 在配置文件中将选项 "use-economy" 设置为 true 即可.\
若该项启用, XClaim 将会尝试对接到下列插件中以正确启用经济:
- Vault
- EssentialsX

这会使玩家在不同组时付出不同购买区块的价格 (见 [此处](#permissions)).\
例如, 若你要将购买区块的默认价格设置为 2.25, 那么你需要在配置文本中设置 ``limits.default.claim-price`` 的值为 ``2.25``.\
在 [配置文件](#config) 中浏览所有可配置的选项.

## 配置文件
| 名称 | 描述 | 默认值 |
| --: | :-: | :-- |
| language | 插件使用的语言, 对应的语言文件应当存在于 ``/plugins/XClaim/lang``否则它将会重置回默认值， 即 en-US | en-US |
| veteran-time | 玩家自动进入驻留用户组所需时间 | 604800 (1 星期) |
| stop-editing-on-shutdown | 服务器关闭时是否阻止玩家打开编辑器/关闭现有正在编辑的GUI | false |
| stop-editing-on-leave | 当玩家自行离开时是否将其的领地编辑器强制关闭 | true |
| exempt-claim-owner-from-permission-rules | 领地拥有者是否默认拥有全部权限. 该项不应被改动, 它主要用于调试 | true |
| enforce-adjacent-claim-chunks | 领地内的所有区块是否必须相连 | true |
| allow-diagonal-claim-chunks | 若 enforce-adjacent-claim-chunks 项为 true, 该设置将决定处在两个对角上的区块是否算作相邻区块 | true |
| enter-chunk-editor-on-create | 若设置为 true, 每当玩家创建新领地时便会自动进入编辑模式 | true |
| use-economy | 是否使用经济支持 | false |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-chunks | 设置用户组的最大可认领区块数. 详细信息见权限列表. | |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims | 设置用户组的最大可创建领地数. 详细信息见权限列表. | |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.give-after | 玩家延迟进入小组的时间, 设置为 -1 即表示禁用. | -1 |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price | 若经济支持启用, 该项可决定认领一个区块需要消耗多少游戏币. | 20 |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.unclaim-reward | 若经济支持启用, 该项可决定解除认领一个区块可退还多少游戏币. | 0 |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.free-chunks | 若经济支持启用, 该项可设置玩家在一个领地内可免费领取的区块数, 意味着若超出该数字则需要按照 ``limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price`` 选项来付费. | 4 |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims-in-world | 单个玩家世界内最大可创建领地数量. 设置为小于 1 的值表示为禁用. | -1 |
| dynmap-integration.enabled | 若开启, 本插件将会尝试对接到 Dynmap 并提供联动功能. 请在出现问题时禁用本项 | true |
| dynmap-integration.use-old-outline-style | 若设置为 true 本插件将会使用旧版的描边轮廓. 该项主要用于调试. | false |
| disable-paper-warning | 在非 Paper 服务器上启用本插件时是否关闭警告信息 | false |
| worlds.use-whitelist | 是否启用分世界黑名单 | false |
| worlds.use-blacklist | 是否启用分世界白名单| false |
| worlds.case-sensitive | 是否在黑/白名单中检查世界的大小写 | true |
| worlds.whitelist | 本插件将会启用的世界 | 列表 |
| worlds.blacklist | 本插件不会启用的世界 | 列表 |
| worlds.grace-time | 若领地在一个不被允许的世界创建, 玩家有多少时间来删除他们的领地并打包走人 | 604800 (1 星期) |

## 插件权限
不用担心, 这里并没有你想的那么可怕.
| 权限名称 | 权限描述 |
| --: | :-- |
| xclaim.override | 允许覆写已存在的领地 |
| xclaim.admin | 允许你修改或删除任意领地 |
| xclaim.import | 允许从 ClaimChunks 导入领地数据 |
| xclaim.update | 允许你使用自动更新检查器 |
| xclaim.restart | 允许你重启本插件 |
| xclaim.clear | 允许通过 /xclaim clear 命令清除其他玩家的领地 |
| xclaim.group.组名称 | 若玩家有该权限, 那么他们就是这个组的一部分. 玩家将优先位于权重值最高的组内. 若组名为 "default", 那么所有玩家都将默认包括在该组. |

## 插件命令
| 命令名称 | 命令描述 |
| --: | :-- |
| xclaim | XClaim 的主命令. 不带任何参数则与 /xclaim gui 等价 |
| xclaim help | 列出可用命令 |
| xclaim info | 显示 XClaim 的基本信息 |
| xclaim gui | 打开插件的 GUI, 可使用许多重要的功能 |
| xclaim update | 检查更新, 若在配置文本中预先设置, 则会直接下载更新 |
| xclaim chunks \[领地名称] | 编辑特定的领地, 留空则编辑已拥有的领地 |
| xclaim current | 获取你所处领地的相关信息 |
| xclaim restart | 在不重启服务器的情况下重启本插件 (测试功能) |
| xclaim clear | 清除玩家的所有领地 |
| xclaim list | 列出玩家所有的领地 |
| importclaims | 从 ClaimChunk 插件导入领地数据 |

## Support
|         | 1.8 - 1.11 | 1.12 - 1.13 | 1.14 - 1.16 | 1.17 | 1.18 | 1.19 | 1.19.3 | Paper | Spigot |
| --:     | :-:  | :-:  | :-:  | :-:  | :-:  | :-:  | :-:  | :-:   | :-:    |
| < 1.3.1 | ❌   | ❌   | ❌   | ❔    | ✔    | ❌   | ❔    | ✔     | ❌     |
| 1.3.1   | ❌   | ❌   | ❌   | ❔    | ✔    | ✔    | ❔    | ✔     | ❌     |
| 1.4.0   | ❌   | ❌   | ❌   | ✔    | ✔    | ✔    | ❔    | ✔     | ❌     |
| 1.5.0   | ❌   | ❌   | ❌   | ✔    | ✔    | ✔    | ❔    | ✔     | ✔      |
| 1.8.0   | ❌   | ❌   | ✔    | ✔    | ✔    | ✔    | ❔    | ✔     | ✔      |
| 1.9.0   | ❌   | ✔   | ✔    | ✔    | ✔    | ✔    | ❔    | ✔     | ✔      |
| 1.9.1  | ✔   | ✔   | ✔    | ✔    | ✔    | ✔    | ❔    | ✔     | ✔      |
| 1.10.0  | ✔   | ✔   | ✔    | ✔    | ✔    | ✔    | ✔    | ✔     | ✔      |

在 1.5.0 之前的版本将不再受到支持

## 未来计划
* 添加更多管理命令