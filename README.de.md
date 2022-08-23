# <img src="https://wasabicodes.xyz/cdn/15852266ddef4696b804677658f11651/xc_logo.png" alt="XClaim" title="XClaim" style="width: 12em">
<h2>
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.md" title="Amerikanisches Englisch">🇬🇧</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.de.md" title="Deutsch">🇩🇪</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.zh.md" title="Vereinfachtes Chinesisch">🇨🇳</a>
</h2>

[![Java CI with Maven](https://github.com/WasabiThumb/xclaim/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/WasabiThumb/xclaim/actions/workflows/maven.yml)\
Ein besseres Chunk-anspruch Plugin für Paper Server.\
*Verstanden? Es klingt wie exclaim...*\
\
| [Installation](#Installation) | [Features](#Features) | [Konfiguration](#Konfiguration) | [Berechtigungen](#Berechtigungen) | [Befehle](#Befehle) | [Unterstützte Versionen](<#unterstützte-versionen>) | [Entwicklungsplan](#Entwicklungsplan) |

## Installation
Sie können ein Build vom ["Releases"-tab](https://github.com/WasabiThumb/xclaim/releases) auf der rechten Seite oder [selbst das Plugin builden](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#build-the-project), wenn Sie wollen. Platzieren Sie danach die .jar Datei in Ihren "plugins" Ordner. Nutzen Sie nicht die .jar Datei namens "original", außer Sie wissen, was Sie da tun!

## Features
Der Hauptbefehl ist "/xclaim" (alias "/xc"). Dieser erlaubt es Spielern Gebiete zu beanspruchen und zu verwalten. In diesen Gebieten können Berechtigungen umgestellt werden, entweder nach allgemeinen Gruppen (Niemand, vertrauenswürdige Spieler, Veteranen, jeder) oder individuell. Das GUI erlaubt es zudem Spieler der Liste an vertrauenswürdigen Spielern hinzuzufügen oder zu entfernen.

### Dynmap-Integration
Die Dynmap-Integration sollte "out-of-the-box" funktionieren, solange alles [ordnungsgemäß konfiguriert](#Konfiguration) ist. Ist dies nicht der Fall, melden Sie das Problem bitte auf der [Issues-Seite](https://github.com/WasabiThumb/xclaim/issues).\
Sehen Sie sich als Beispiel die Dynmap vom [HL21st Minecraft Server](https://www.planetminecraft.com/server/half-life-21st-century-humor-official/) live an: [hier](http://hl21st.com:8104/).\
\
<img src="https://wasabicodes.xyz/cdn/e536fc60213f22701f2e55858f8f87f9/dynmap.png" alt="Funktionsbeispiel der Dynmap Integration auf dem HL21st Minecraft Server, aufgenommen am 5/13/22" title="HL21st Minecraft Server" style="width: 30em">

### Importieren von ClaimChunk
Dieser Prozess sollte ohne Spieler online durchgeführt werden. Der Server sollte ClaimChunk **UND** XClaim zur selben Zeit geladen haben. Es besteht die Möglichkeit, dass das Plugin PlaceholderAPI ebenfalls vonnöten ist (unbestätigt), aber danach weder ClaimChunk noch PlaceholderAPI, damit XClaim normal funktioniert. Sobald alle Konditionen erfüllt sind, führen Sie den Befehl /importclaims aus. Dies könnte eine Weile dauern und ressourcenintensiv sein (dieses Feature wurde auf Skalierbarkeit überprüft), da versucht wird angrenzende Ansprüche zusammenzufassen.

### Sprachen
Seit der Version 1.6.x werden mehrere Sprachen unterstützt. Wenn das Plugin startet, werden die standard Sprach-Packete in ``/plugins/XClaim/lang`` geladen. In der folgenden Liste sind alle standard Sprach-Packte:
- en-US (Amerikanisches Englisch)
- de (Deutsch)
- zh (Vereinfachtes Chinesisch)

Das Plugin benutzt die Sprache, welche in der "language" Option in der [Konfiguration](#Konfiguration) spezifiziert ist.
\
\
Wollen Sie Ihr eigenes Sprach-Packet erstellen, so kopieren Sie ein vorhandenes (z.B. ``/plugins/XClaim/lang/en-US.json``) und benennen es [entsprechend](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) um (e.g. ``fr.json``). Nun können Sie den Inhalt dieser Datei übersetzen. Wissen über [JSON](https://en.wikipedia.org/wiki/JSON#Syntax) und [MiniMessage](https://docs.adventure.kyori.net/minimessage/index.html) sind dringlich empfohlen. Übersetzen Sie nicht die Schlüssel, sondern nur die Werte! Sprach-Packete können nach der Kodierung für Menschen schwieriger lesbar werden, deshalb ist es empfohlen sich eines von [der Quelle](https://github.com/WasabiThumb/xclaim/tree/master/src/main/resources/lang) zu holen. In manchen Fällen werden Symbole wie ``$1``, ``$2`` u.s.w. ... benutzt. Dies bedeutet, dass dort eine Variable verwendet werden kann. Zum Beispiel ``Hallo $1!`` kann im Spiel zu ``Hallo [Nutzername]!`` werden.

### Wirtschaft
Wirtschaftsfeatures sind standardmäßig aus. Um sie zu aktivieren, setzen Sie "use-economy" in der Konfiguration auf "true".\
Wenn "use-economy" aktiv ist, wird XClaim versuchen sich in die folgenden Wirtschafts-Plugins einzuhacken (also hacken und nicht hacken versteht sich 🙃):
- Vault
- EssentialsX

Spieler haben dann je nach Gruppe und Einstellungen für Ansprüche zu zahlen (Siehe [hier](#Berechtigungen)).\
Zum Beispiel, wenn Sie den Standard-Preis pro zu beanspruchenden Chunk auf 2.25 setzen wollen, dann können Sie das unter ``limits.default.claim-price`` auf ``2.25`` setzen.\
Für alle Optionen siehe: [Konfiguration Abschnitt](#Konfiguration).

## Konfiguration
| Name | Beschreibung | Standardwert |
| --: | :-: | :-- |
| language | Die zu benutzende Frage. Muss ein valides Sprachen-Packet aus ``/plugins/XClaim/lang`` sein, andernfalls wird das en-US Packet verwendet. | en-US |
| veteran-time | Die Zeit in Sekunden, die es an Spielzeit braucht, bevor ein Spieler der Veteranen-Gruppe zugeteilt wird. | 604800 (1 Woche) |
| stop-editing-on-shutdown | Ob Spieler beim Herunterfahren des Servers aus dem Chunk-Editor geworfen werden sollen. | false |
| stop-editing-on-leave | Ob Spieler aus dem Chunk-Editor geworfen werden sollen, wenn sie das Spiel freiwillig verlassen. | true |
| exempt-claim-owner-from-permission-rules | Ob Chunk-Besitzer von den Berechtigungsregeln auf ihrem Gebiet implizit ausgenommen werden sollen. Sie sollten dies nicht verändern, da dies hauptsächlich für debugging Zwecke verwendet wird. | true |
| enforce-adjacent-claim-chunks | Ob Chunks in einem beanspruchten Gebiet zusammenhängend seien müssen. | true |
| allow-diagonal-claim-chunks | Falls "enforce-adjacent-claim-chunks" auf "true" ist, werden Chunks, welche diagonal voneinander sind, als "nebeneinander" behandelt. Andernfalls macht diese Option nichts. | true |
| enter-chunk-editor-on-create | Wenn auf "true", werden Spieler, die einen neues Gebiet beanspruchen, automatisch in den Chunk-Editor versetzt. | true |
| use-economy | Ob Wirtschaft-Features genutzt werden sollen oder nicht. | false |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-chunks | Bestimmt die maximale Anzahl an beanspruchbaren Chunks für die Gruppe. Siehe [Berechtigungen](#Berechtigungen) für mehr Infos. | |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims | Bestimmt die maximale Anzahl an Gebieten für die Gruppe. See Permissions for more info. Siehe [Berechtigungen](#Berechtigungen) für mehr Infos. | |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.give-after | Die Spielzeit in Sekunden, nach der Spieler dieser Gruppe zugeteilt werden. Werte niedriger als 0 bedeuten "nie". | -1 |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price | Bestimmt den Preis für einen Chunk, falls Wirtschaft-Features aktiv sind. | 20 |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.unclaim-reward | Bestimmt die Anzahl an Währung, die ein Spieler der Gruppe, beim Chunk freigeben, erstattet bekommt, falls Wirtschaft-Features aktiv sind. | 0 |
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.free-chunks | Bestimmt die Anzahl an kostenfreien Chunks, bevor Spieler den Preis, welcher in ``limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price`` spezifiziert ist, zahlen müssen, falls Wirtschaft-Features aktiv sind. | 4 |
| dynmap-integration.enabled | Wenn "true", wird die Dynmap-Integration aktiviert. | true |
| dynmap-integration.use-old-outline-style | Wenn "true", wird Dynmap die alten Konvex-Hüllen-Umrandungen für Anspruchsgebiete verwenden. Dies ist hauptsächlich zum debuggen, da das neue Umrandungssystem experimentell ist. | false |

## Berechtigungen
Keine Sorge, es gibt nicht viele.
| Name | Beschreibung |
| --: | :-- |
| xclaim.override | Erlaubt es Chunks zu überschreiben. |
| xclaim.admin | Erlaubt es jeglichen Anspruch zu bearbeiten/löschen. |
| xclaim.import | Erlaubt es Ansprüche von ClaimChunk zu importieren. |
| xclaim.update | Erlaubt es automatisch Updates zu installieren. |
| xclaim.restart | Erlaubt es XClaim neu zu starten. |
| xclaim.clear | Erlaubt es alle Gebiete eines Spielers mit "/xclaim clear" freizugeben |
| xclaim.group.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦 | Wenn ein Spieler diese Berechtigung hat, ist dieser Teil der Gruppe. Spieler vererben die "besten" Werte aus all ihren Gruppen. Implizit sind alle Spieler in der "default" Gruppe. |

## Befehle
| Name | Beschreibung |
| --: | :-- |
| xclaim | Ist der XClaim Hauptbefehl. Ohne zusätzliche Argumente ist er äquivalent zu "/xclaim gui". |
| xclaim help | Listet die verfügbaren Unterbefehle auf. |
| xclaim info | Liefert grundlegende Informationen über XClaim. |
| xclaim gui | Öffnet ein leicht nutzbares GUI, welches die meisten wichtigen Features von XClaim's abdeckt. |
| xclaim update | Sucht nach neuen Versionen von XClaim und installiert diese auf Wunsch automatisch. |
| xclaim chunks \[claim_name] | Öffnet den Chunk Editor für das spezifizierte Gebiet oder, falls dies nicht gegeben ist, das aktuell befindliche. |
| xclaim current | Liefert Informationen über das aktuell befindliche Gebiet. |
| xclaim restart | Startet XClaim neu, ohne dabei den Server neu starten zu müssen. (experimentell) |
| xclaim clear | Gibt alle Gebiete eines Spielers frei. |
| xclaim list | Listet alle Gebiete eines Spielers auf. |
| importclaims | Importiert beanspruchte Gebiete von ClaimChunk. |

## Unterstützte Versionen
|         | 1.8 - 1.11 | 1.12 - 1.13 | 1.14 - 1.16 | 1.17 | 1.18 | 1.19 | Paper | Spigot |
| --:     | :-:  | :-:  | :-:  | :-:  | :-:  | :-:  | :-:   | :-:    |
| < 1.3.1 | ❌   | ❌   | ❌   | ❔    | ✔    | ❌   | ✔     | ❌     |
| 1.3.1   | ❌   | ❌   | ❌   | ❔    | ✔    | ✔    | ✔     | ❌     |
| 1.4.0   | ❌   | ❌   | ❌   | ✔    | ✔    | ✔    | ✔     | ❌     |
| 1.5.0   | ❌   | ❌   | ❌   | ✔    | ✔    | ✔    | ✔     | ✔      |
| 1.8.0   | ❌   | ❌   | ✔    | ✔    | ✔    | ✔    | ✔     | ✔      |
| 1.9.0   | ❌   | ✔   | ✔    | ✔    | ✔    | ✔    | ✔     | ✔      |
| 1.9.1+  | ✔   | ✔   | ✔    | ✔    | ✔    | ✔    | ✔     | ✔      |

Versionen vor 1.5.0 werden nicht länger unterstützt!

## Entwicklungsplan
* Mehr Management-Befehle hinzufügen.
