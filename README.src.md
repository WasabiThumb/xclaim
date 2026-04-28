<!--@nrg.languages=en,de,tr,zh-->
<!--@nrg.defaultLanguage=en-->
<h1 align="center">
<img src="doc/banner.png" alt="XClaim" title="XClaim" style="height: 6em">
</h1>

<div align="center">
<img${en:'', tr:'', de:' style="margin:0.3em"', zh:' style="margin:0.3em"'} alt="Build Status" src="https://img.shields.io/github/actions/workflow/status/WasabiThumb/xclaim/maven.yml">
<img${en:'', tr:'', de:' style="margin:0.3em"', zh:' style="margin:0.3em"'} src="https://img.shields.io/badge/dynamic/json?color=informational&label=version&query=%24.title&url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F102843%2Fupdates%2Flatest" alt="Version">
<img${en:'', tr:'', de:' style="margin:0.3em"', zh:' style="margin:0.3em"'} src="https://img.shields.io/badge/dynamic/json?color=informational&label=active%20servers&query=%24%5B0%5D%5B1%5D&url=https%3A%2F%2Fbstats.org%2Fapi%2Fv1%2Fplugins%2F16129%2Fcharts%2Fservers%2Fdata%2F%3FmaxElements%3D1" alt="Active Servers">
<img${en:'', tr:'', de:' style="margin:0.3em"', zh:' style="margin:0.3em"'} src="https://img.shields.io/badge/dynamic/json?color=informational&label=active%20players&query=%24[0][1]&url=https%3A%2F%2Fbstats.org%2Fapi%2Fv1%2Fplugins%2F16129%2Fcharts%2Fplayers%2Fdata%2F%3FmaxElements%3D1" alt="Active Players">
<img${en:'', tr:'', de:' style="margin:0.3em"', zh:' style="margin:0.3em"'} src="https://img.shields.io/endpoint?url=https%3A%2F%2F8f2bde531f0d7a.lhr.life%2F%3Fmetric%3Dstars&label=stars&cacheSeconds=3600" alt="Stars">
<img${en:'', tr:'', de:' style="margin:0.3em"', zh:' style="margin:0.3em"'} src="https://img.shields.io/endpoint?url=https%3A%2F%2F8f2bde531f0d7a.lhr.life%2F%3Fmetric%3Ddownloads&label=downloads&cacheSeconds=3600" alt="Downloads">
</div>

<div align="center">
<h2>
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.md" title="${en:'American English', de:'Amerikanisches Englisch', tr:'American English', zh:'美式英语'}">🇬🇧</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.de.md" title="${en:'German', de:'Deutsch', tr:'German', zh:'德语'}">🇩🇪</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.zh.md" title="${en:'Chinese (Simplified)', de:'Vereinfachtes Chinesisch', tr:'Chinese (Simplified)', zh:'简体中文'}">🇨🇳</a>
&nbsp;
<a href="https://github.com/WasabiThumb/xclaim/blob/master/README.tr.md" title="${en:'Turkish', de:'Turkish', tr:'Turkish', zh:'Turkish'}">🇹🇷</a>
</h2>
</div>

<div align="center">
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="${en:'#installation', de:'#installation', tr:'#kurulum', zh:'#安装教程'}">${en:'Installation', de:'Installation', tr:'Kurulum', zh:'安装教程'}</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="${en:'#features', de:'#features', tr:'#özellikler', zh:'#插件特色'}">${en:'Features', de:'Features', tr:'Özellikler', zh:'插件特色'}</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="${en:'#config', de:'#konfiguration', tr:'#config', zh:'#配置文件'}">${en:'Config', de:'Konfiguration', tr:'Config', zh:'配置文件'}</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="${en:'#permissions', de:'#berechtigungen', tr:'#yetkiler', zh:'#插件权限'}">${en:'Permissions', de:'Berechtigungen', tr:'Yetkiler', zh:'插件权限'}</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="${en:'#commands', de:'#befehle', tr:'#komutlar', zh:'#插件命令'}">${en:'Commands', de:'Befehle', tr:'Komutlar', zh:'插件命令'}</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="${en:'#support', de:'#unterstützte-versionen', tr:'#desteklenen-sürümler', zh:'#support'}">${en:'Support', de:'Unterstützte Versionen', tr:'Desteklenen Sürümler', zh:'联系支持'}</a>
  <a style="margin:0.3rem;padding:0.5em;background-color:#303030;border-radius:0.5em" href="${en:'#roadmap', de:'#entwicklungsplan', tr:'#planlar', zh:'#未来计划'}">${en:'Roadmap', de:'Entwicklungsplan', tr:'Planlar', zh:'未来计划'}</a>
</div>${en:'', tr:'', de:'<br>', zh:'<br>'}

Ein besseres Chunk-Claiming Plugin für Paper Server.\<!--de-->
*Verstanden? Es klingt wie exclaim...*<!--de-->
专为 Paper 服务器打造的区块领地插件\<!--zh-->
*明白吗? 它听起来像是 exclaim...*<!--zh-->
<!--de--><!--zh-->
<!--de-->
## ${en:'Installation', de:'Installation', tr:'Kurulum', zh:'安装教程'}
You can download a build from the [releases tab](https://github.com/WasabiThumb/xclaim/releases) on the right, or [build the plugin yourself](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#build-the-project) if you want. Then, place the JAR into your plugins folder. Don't use the JAR labelled "original" unless you know what you are doing.<!--en-->
Sie können einen Build vom ["Releases"-Tab](https://github.com/WasabiThumb/xclaim/releases) auf der rechten Seite herunterladen oder [selbst das Plugin bauen](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#build-the-project), wenn Sie wollen. Platzieren Sie danach die .jar Datei in Ihren "plugins" Ordner. Nutzen Sie nicht die .jar Datei namens "original", außer Sie wissen, was Sie da tun!<!--de-->
[Sağdaki sürümler sekmesinden](https://github.com/WasabiThumb/xclaim/releases) hazır halini indirebilirsiniz ya da isterseniz [plugini kendiniz de oluşturabilirsiniz](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#build-the-project). Ardından JAR dosyasını plugin klasörüne atın. Ne yaptığınızı bilmiyorsanız "orijinal" etiketli JAR'ı kullanmayın. <!--tr-->
你可以在 [发布页](https://github.com/WasabiThumb/xclaim/releases) 右侧下载插件本体, 如果你想的话, 也可以通过源代码 [自行构建本插件](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#build-the-project). 这之后, 将 JAR 文件置入你的服务器文件夹 plugins 下. 但注意请不要将文件名中带有 "original" 的插件加入服务器, 除非你已经知道你在干什么, 否则后果自负.<!--zh-->

## ${en:'Features', de:'Features', tr:'Özellikler', zh:'插件特色'}
The main command is /xclaim (alias /xc). This allows players to create and manage their claims. Claims have several permissions that can be toggled, either by general groups (nobody, trusted players, veteran players, all players) or by individuals. The GUI also allows players to add/remove players from their trusted list.<!--en-->
Der Hauptbefehl ist "/xclaim" (alias "/xc"). Dieser erlaubt es Spielern Gebiete zu beanspruchen und zu verwalten. In diesen Gebieten können Berechtigungen vergeben werden, entweder nach allgemeinen Gruppen (Niemand, vertrauenswürdige Spieler, Veteranen, Jeder) oder individuell. Die Benutzeroberfläche erlaubt es zudem vertrauenswürdigen Spielern einem Gebiet hinzuzufügen oder zu entfernen.<!--de-->
Ana komut /xclaim'dir (takma ad /xc). Bu, oyuncuların alanlarını oluşturmasına ve yönetmesine olanak tanır. Alanların, genel gruplar (hiç kimse, güvenilir oyuncular, kıdemli oyuncular ve tüm oyuncular) veya bireyler tarafından değiştirilebilen çeşitli izinleri vardır. GUI ayrıca oyuncuların güvenilir listelerine oyuncu eklemesine/çıkarmasına da olanak tanır<!--tr-->
本插件的主命令是 /xclaim (可缩写为 /xc). 这允许玩家通过该命令创建并管理他们的领地. 领地有一些可被切换的权限, 同时也可对全局用户组生效 (例如 nobody 无玩家, trusted players 受信任用户组, veteran players 驻留用户组, all players 全体玩家) ,也可对单独的玩家生效. 本插件自带的 GUI 也可以编辑全局或单独玩家的相关权限.<!--zh-->

### ${en:'Map Integration', de:'Map-Integrationen', tr:'Harita Entegrasyonu', zh:'Dynmap 集成'}
- Map integration should work out-of-the-box as long as it is [configured properly](#config). If it does not, please create an Issue on the [Issues page](https://github.com/WasabiThumb/xclaim/issues).<!--en-->
- BlueMap integration has also been supported since version 1.10.0.<!--en-->
- Die Dynmap-Integration sollte standardmäßig funktionieren, solange alles [ordnungsgemäß konfiguriert](#Konfiguration) ist. Sollte es dennoch Probleme / Bugs geben, melden Sie diese bitte auf der [Issues-Seite](https://github.com/WasabiThumb/xclaim/issues).<!--de-->
- Eine BlueMap-Integration ist auch seit Version 1.10.0 standardmäßig verfügbar.<!--de-->
- Harita entegrasyonu, [düzgün şekilde yapılandırıldığı](#config) sürece kutudan çıktığı gibi çalışmalıdır. Aksi takdirde lütfen [Sorunlar sayfasında](https://github.com/WasabiThumb/xclaim/issues) bir sorun oluşturun.<!--tr-->
- BlueMap entegrasyonu da 1.10.0 sürümünden beri desteklenmektedir.<!--tr-->
若 [正确地设定插件的配置文件](#config), 本插件应当支持在 Dynmap 地图上显示已领取的领地及其所属区块. 若它没有正常运作, 在你确认这不是的配置问题后在本插件的开源页面的 [Issues 区域](https://github.com/WasabiThumb/xclaim/issues) 提交相关问题.\<!--zh-->
若要预览本插件的效果, 你可以在 [HL21st 服务器](https://www.planetminecraft.com/server/half-life-21st-century-humor-official/) 的 [此处](http://hl21st.com:8104/) 来浏览本插件的效果.\<!--zh-->
\<!--zh-->
<img src="https://wasabicodes.xyz/cdn/e536fc60213f22701f2e55858f8f87f9/dynmap.png" alt="集成了 Dynmap 功能的 HL21st 服务器, captured on 5/13/22" title="HL21st 服务器" style="width: 30em"><!--zh-->

### ${en:'Importing from ClaimChunk', de:'Importieren von ClaimChunk', tr:'ClaimChunk''tan bilgi aktarma', zh:'从 ClaimChunk 插件导入数据'}
This process should be done without any players online. The server should have ClaimChunk AND XClaim loaded at the same time. It's possible that you need PlaceholderAPI on the server as well while doing this, but you definitely don't need either ClaimChunk nor PlaceholderAPI for XClaim to work normally. Once all of those conditions are met, run /importclaims. This may take a while or be resource intensive since it will attempt to turn adjacent claimed chunks into one group.<!--en-->
**WICHTIG: Dieser Prozess sollte ohne jegliche angemeldete Spieler auf dem Server durchgeführt werden.** Der Server sollte ClaimChunk **UND** XClaim zur selben Zeit geladen haben. Es besteht die Möglichkeit, dass das Plugin PlaceholderAPI ebenfalls benötigt wird (unbestätigt). Nach dem Import wird allerdings weder ClaimChunk noch PlaceholderAPI benötigt, damit XClaim normal funktioniert. Sobald alles Bereit ist, führen Sie den Befehl /importclaims aus. Die Ausführung könnte eine Weile dauern und ressourcenintensiv sein (dieses Feature wurde auf Skalierbarkeit überprüft), da versucht wird angrenzende Ansprüche zusammenzufassen.<!--de-->
Bu işlem çevrimiçi herhangi bir oyuncu olmadan yapılmalıdır. Sunucuda ClaimChunk VE XClaim'in aynı anda yüklü olması gerekir. Bunu yaparken sunucuda PlaceholderAPI'ye de ihtiyacınız olması mümkündür, ancak XClaim'in normal çalışması için kesinlikle ne ClaimChunk'a ne de PlaceholderAPI'ye ihtiyacınız yoktur. Tüm bu koşullar yerine getirildikten sonra /importclaims komutunu çalıştırın. Bu işlem bitişik olan alanlardaki chunkları bir grup haline getirmeye çalışacağından biraz zaman alabilir veya yoğun kaynak gerektirebilir.<!--tr-->
该过程建议在全体玩家均不在线时进行. 服务器必须同时安装 ClaimChunk 与 XClaim. 同时你可能也需要安装 PlaceholderAPI 以使这项功能正常运站 (需要验证), 但你可以在数据转化完毕后卸载 ClaimChunk 或 PlaceholderAPI , 这不会干扰本插件的正常运行 (译者注: 有必要保留后者). 当一切准备就绪时, 输入命令 /importclaims 即可开始. 这可能需要一段时间, 并且可能会消耗大量资源(注意: 本插件尚未进行过较大规模转化的测试), 因为本插件会将相邻的已认领区块合并至一个领地下.<!--zh-->

<!--tr-->
### ${en:'Languages', de:'Sprachen', tr:'Diller', zh:'Languages'}
As of version 1.6.x, multiple languages are supported. When the plugin starts, default language packs are loaded into ``/plugins/XClaim/lang``. Below are a list of default language packs:<!--en-->
Seit der Version 1.6.x werden mehrere Sprachen unterstützt. Wenn das Plugin startet, werden die Standard-Sprachpakete in ``/plugins/XClaim/lang`` geladen. Diese beinhalten:<!--de-->
1.6.x sürümünden itibaren birden fazla dil desteklenmektedir. Eklenti başlatıldığında, varsayılan dil paketleri ``/plugins/XClaim/lang`` dosyasına yüklenir. Aşağıda varsayılan dil paketlerinin bir listesi bulunmaktadır:<!--tr-->
在 1.6.x 之后的插件, 支持了多种语言. 当插件初次运行时, 默认的语言包会被载入至 ``/plugins/XClaim/lang`` 下. 下列是默认支持的语言包:<!--zh-->
- en-US (American English)<!--en-->
- de (German) by eingruenesbeb<!--en-->
- zh (Simplified Chinese) by SnowCutieOwO<!--en-->
- tr (Turkish) by Krayir5<!--en-->
- en-US (Amerikanisches Englisch)<!--de-->
- de (Deutsch) von eingruenesbeb<!--de-->
- zh (Vereinfachtes Chinesisch) von SnowCutieOwO<!--de-->
- tr (Türkisch) von Krayir5<!--de-->
- en-US (Amerikan İngilizcesi)<!--tr-->
- de (Almanca) eingruenesbeb tarafından<!--tr-->
- zh (Basitleştirilmiş Çince) SnowCutieOwO tarafından<!--tr-->
- tr (Türkçe) Krayir5 tarafından<!--tr-->
- en-US (美式英语)<!--zh-->
- de (德语) by eingruenesbeb<!--zh-->
- zh (简体中文) by SnowCutieOwO<!--zh-->
- tr (土耳其) by Krayir5<!--zh-->

The plugin decides what language to use based on the "language" option in the [config](#config).<!--en-->
Das Plugin benutzt die Sprache, welche in der "language" Option in der [Konfiguration](#Konfiguration) spezifiziert ist.<!--de-->
Eklenti, [config](#config) dosyasındaki "dil" seçeneğine göre hangi dilin kullanılacağına karar verir.<!--tr-->
[配置文本](#config) 中的 "language" 选项可决定插件使用哪种语言.<!--zh-->
\
\
If you want to make your own language pack, copy an existing one as an example (e.g. ``/plugins/XClaim/lang/en-US.json``) and rename it [accordingly](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) (e.g. ``fr.json``). You can then translate the contents of that file. Knowledge of [JSON](https://en.wikipedia.org/wiki/JSON#Syntax) and [MiniMessage](https://docs.adventure.kyori.net/minimessage/index.html) is highly suggested. Do not translate the keys, only the values. Language packs may become less human-readable after encoding, so it is suggested to get your language pack base from [the source](https://github.com/WasabiThumb/xclaim/tree/master/src/main/resources/lang). There are some cases where the symbols ``$1``, ``$2``, etc. are used. This means that "something is inserted here", for example ``Hello $1!`` may resolve to ``Hello Username!`` ingame.<!--en-->
Wollen Sie Ihr eigenes Sprachpaket erstellen, so kopieren Sie ein vorhandenes (z.B. ``/plugins/XClaim/lang/en-US.json``) und benennen es [entsprechend](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) um (z.B. ``fr.json`` für Französisch). Nun können Sie den Inhalt dieser Datei übersetzen. Wissen über [JSON](https://en.wikipedia.org/wiki/JSON#Syntax) und [MiniMessage](https://docs.adventure.kyori.net/minimessage/index.html) sind dringlich empfohlen. Übersetzen Sie nicht die Schlüssel, sondern nur die Werte! Sprachpakete können nach der Kodierung für Menschen schwieriger lesbar werden, deshalb ist es empfohlen sich eines von [der Quelle](https://github.com/WasabiThumb/xclaim/tree/master/src/main/resources/lang) zu holen. In manchen Fällen werden Symbole wie ``$1``, ``$2`` u.s.w. ... benutzt. Dies bedeutet, dass dort eine Variable verwendet werden kann. Zum Beispiel ``Hallo $1!`` kann im Spiel zu ``Hallo [Nutzername]!`` werden.<!--de-->
Kendi dil paketinizi oluşturmak istiyorsanız örnek olarak mevcut bir paketi kopyalayın (ör. ``/plugins/XClaim/lang/tr.json``) ve onu [buna göre](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) yeniden adlandırın (örn. ``fr.json``). Daha sonra bu dosyanın içeriğini çevirebilirsiniz. [JSON](https://en.wikipedia.org/wiki/JSON#Syntax) ve [MiniMessage](https://docs.adventure.kyori.net/minimessage/index.html) bilgisi önemle tavsiye edilir. Anahtarları çevirmeyin, yalnızca değerleri çevirin. Dil paketleri, kodlamanın ardından insanlar tarafından daha az okunabilir hale gelebilir; bu nedenle, dil paketi tabanınızı [kaynaktan](https://github.com/WasabiThumb/xclaim/tree/master/src/main/resources/lang) almanız önerilir. ``$1``, ``$2`` gibi simgelerin kullanıldığı bazı durumlar vardır. Bu, "buraya bir şey eklenmiş" anlamına gelir; örneğin "Merhaba $1!", oyun içinde "Merhaba Kullanıcı Adı!" olarak çözülebilir.<!--tr-->
若你想要创建新的语言包, 将已有的语言文本复制一份用作模板 (例如 ``/plugins/XClaim/lang/en-US.json``) 并将其重命名, 格式需按照 [你的语言缩写](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) (例如 ``fr.json`` 即为 法语语言包). 这样你就可以开始语言文件的翻译工作了. 你可能需要用到 [JSON 教程](https://en.wikipedia.org/wiki/JSON#Syntax) 以及 [MiniMessage 教程](https://docs.adventure.kyori.net/minimessage/index.html). 不可以翻译这些特殊的代码, 只可以翻译语言文本. 编码后的语言文件可读性会变差, 所以最好从 [源](https://github.com/WasabiThumb/xclaim/tree/master/src/main/resources/lang) 获取你需要的语言文件 (译者注: 部分地区网络连接可能不佳). 当然也有一些诸如 ``$1``, ``$2`` 的保留字被使用. 这些字符的意识是 "有些东西在此处会被替换" (译者注: 即内建变量), 例如 ``Hello $1!`` 会在游戏中实际显示为 ``Hello Username!``.<!--zh-->

### ${en:'Economy', de:'Wirtschaft', tr:'Ekonomi', zh:'经济支持'}
By default, economy features are disabled. To enable them, set "use-economy" in the config to true.\<!--en-->
If use-economy is enabled, XClaim will attempt to hook into the following economy plugins if present:<!--en-->
Wirtschaftsfeatures sind standardmäßig deaktiviert. Um sie zu aktivieren, setzen Sie "use-economy" in der Konfiguration auf "true".\<!--de-->
Wenn "use-economy" aktiv ist, wird XClaim versuchen sich in die folgenden Wirtschafts-Plugins einzuhacken (also hacken und nicht hacken versteht sich 🙃):<!--de-->
Varsayılan olarak ekonomi özellikleri devre dışıdır. Bunları etkinleştirmek için yapılandırmadaki "use-economy" seçeneğini true olarak ayarlayın.\<!--tr-->
Ekonomi kullanımı etkinleştirilirse XClaim, eğer varsa aşağıdaki ekonomi eklentilerine bağlanmayı deneyecektir:<!--tr-->
经济支持默认关闭. 若要启用, 在配置文件中将选项 "use-economy" 设置为 true 即可.\<!--zh-->
若该项启用, XClaim 将会尝试对接到下列插件中以正确启用经济:<!--zh-->
- Vault
- EssentialsX

Players will then pay depending on the permission group the player is in (see [here](#permissions)).\<!--en-->
For instance, if you wanted to set the default price for a claim to 2.25, then you would set ``limits.default.claim-price`` to ``2.25``.\<!--en-->
See all options in the [config section](#config).<!--en-->
Spieler haben dann je nach Gruppe und Einstellungen für Ansprüche zu zahlen (Siehe [hier](#Berechtigungen)).\<!--de-->
Zum Beispiel, wenn Sie den Standardpreis pro zu beanspruchenden Chunk auf 2.25 setzen wollen, dann können Sie das unter ``limits.default.claim-price`` auf ``2.25`` setzen.\<!--de-->
Für alle Optionen siehe: [Konfiguration](#Konfiguration).<!--de-->
Oyuncular daha sonra oyuncunun bulunduğu izin grubuna bağlı olarak ödeme yapacaklardır (bkz. [buraya](#permissions)).\<!--tr-->
Örneğin, bir talebin varsayılan fiyatını 2,25 olarak ayarlamak isterseniz ``limits.default.claim-price`` değerini ``2,25`` olarak ayarlarsınız.\<!--tr-->
[config kısmında](#config) tüm seçeneklere bakabilirsiniz.<!--tr-->
这会使玩家在不同组时付出不同购买区块的价格 (见 [此处](#permissions)).\<!--zh-->
例如, 若你要将购买区块的默认价格设置为 2.25, 那么你需要在配置文本中设置 ``limits.default.claim-price`` 的值为 ``2.25``.\<!--zh-->
在 [配置文件](#config) 中浏览所有可配置的选项.<!--zh-->

## ${en:'Config', de:'Konfiguration', tr:'Config', zh:'配置文件'}
Configuration is now handled by [``config.toml``](https://github.com/WasabiThumb/xclaim/blob/master/src/main/resources/config.toml), which is fairly self-explanatory.<!--en-->
Support for the [legacy YAML config](https://github.com/WasabiThumb/xclaim/blob/00823def93261519b8ca836a1a774a5a1f81ce65/README.md#config) may be removed in the future.<!--en-->
<!--en-->
**If both formats are present, [``config.yml``](https://github.com/WasabiThumb/xclaim/blob/00823def93261519b8ca836a1a774a5a1f81ce65/src/main/resources/config.yml) will be used.**<!--en-->
<!--en-->
### Config (GUI Layouts)<!--en-->
**This only applies for ``config.toml`` with ``gui.version`` set to 2.**<!--en-->
<!--en-->
After running once, the ``layouts`` directory will appear in the XClaim configuration root. This will give access to GUI<!--en-->
layout files (e.g. ``layouts/main.xml``). A typical application for editing the layouts would be to remove a button from the GUI. For instance, if you wanted to remove the ability to modify the ``ENTER`` permission, then change ``layouts/permission-list.xml``:<!--en-->
<!--en-->
```diff<!--en-->
    <slot id="2"/>  <!-- BREAK --><!--en-->
-   <slot id="3"/>  <!-- ENTER --><!--en-->
+   <!-- <slot id="3"/> --> <!-- ENTER --><!--en-->
    <slot id="4"/>  <!-- INTERACT --><!--en-->
```<!--en-->
<!--en-->
The format is not very friendly, but an attempt will be made to document it here:<!--en-->
<!--en-->
|          Tag |              Allowed Properties               | Description                                                                                                                                                                                                  |<!--en-->
|-------------:|:---------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|<!--en-->
| ``<layout>`` |                   - none -                    | The document root. No other tags should be placed at top-level, including metadata.                                                                                                                          |<!--en-->
|    ``<row>`` |    ``id``, ``x``, ``y``, ``w``, ``basis``     | Automatically adjusts the X position of each child element according to either the ``basis`` set in the config or the ``basis`` set on the tag. If an ``id`` is specified, it should have no child elements. |<!--en-->
|   ``<slot>`` |             ``id``, ``x``, ``y``              | Marks a location where XClaim can insert an item. Must have an ``id`` and must have no child elements.                                                                                                       |<!--en-->
|   ``<area>`` | ``id``, ``x``, ``y``, ``w``, ``h``, ``basis`` | Marks a location where XClaim can insert multiple (in excess of 9) items. Must have an ``id`` and must have no child elements. Mainly used for paginated content.                                            |<!--en-->
<!--en-->
<!--en-->
|  Property | Description                                                                                                                                                                                                                                                                              |<!--en-->
|----------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|<!--en-->
|    ``id`` | Binds the element to a slot in the code. If the spec wishes to place an item at ID ``0``, it will end up located at the ``x`` and ``y`` position of the element with ``id="0"``.                                                                                                         |<!--en-->
|     ``x`` | Sets the ``x`` position of the element. Must be between ``0`` and ``CONTAINER_WIDTH - 1`` (denoted as ``~``[†](#tilda-syntax)). If not specified, the element inherits the ``x`` position of its container.                                                                              |<!--en-->
|     ``y`` | Sets the ``y`` position of the element. Must be between ``0`` and ``CONTAINER_HEIGHT - 1`` (denoted as ``~``[†](#tilda-syntax)). If not specified, the element inherits the ``y`` position of its container.                                                                             |<!--en-->
|     ``w`` | Sets the width of the element. Must be between ``1`` and ``CONTAINER_WIDTH`` (denoted as ``~``[†](#tilda-syntax)). If not specified, the width is the default width for that element. For instance, ``<row>`` is width ``~`` by default, and ``<slot>`` is width ``1`` by default.       |<!--en-->
|     ``h`` | Sets the width of the element. Must be between ``1`` and ``CONTAINER_HEIGHT`` (denoted as ``~``[†](#tilda-syntax)). If not specified, the height is the default height for that element. For instance, ``<area>`` is height ``~`` by default, and ``<slot>`` is height ``1`` by default. |<!--en-->
| ``basis`` | The default horizontal alignment of slots within this element. Must be one of ``LEFT``, ``RIGHT``, ``CENTER`` or ``EVEN``.                                                                                                                                                               |<!--en-->
<!--en-->
#### Tilda Syntax<!--en-->
The symbol ``~`` when applied to a numeric value indicates the maximum value that is within bounds. A number placed<!--en-->
after the symbol subtracts from the maximum, for instance ``~1`` is one less than the maximum and ``~2`` is two less than the maximum.<!--en-->
<!--en-->
| ${en:'.', de:'Name', tr:'İsim', zh:'名称'} | ${en:'.', de:'Beschreibung', tr:'Açıklaması', zh:'描述'} | ${en:'.', de:'Standardwert', tr:'Varsayılan Değer', zh:'默认值'} |<!--de--><!--tr--><!--zh-->
| --: | :-: | :-- |<!--de--><!--tr--><!--zh-->
| language | Die zu benutzende Sprache. Muss ein valides Sprachpaket aus ``/plugins/XClaim/lang`` sein, andernfalls wird das en-US Paket verwendet. | en-US |<!--de-->
| veteran-time | Die Zeit in Sekunden, die es an Spielzeit braucht, bevor ein Spieler als Veteran gilt. | 604800 (1 Woche) |<!--de-->
| stop-editing-on-shutdown | Ob Spieler beim Herunterfahren des Servers aus dem Chunk-Editor geworfen werden sollen. | false |<!--de-->
| stop-editing-on-leave | Ob Spieler aus dem Chunk-Editor geworfen werden sollen, wenn sie das Spiel freiwillig verlassen. | true |<!--de-->
| exempt-claim-owner-from-permission-rules | Ob Chunk-Besitzer von den Berechtigungsregeln auf ihrem Gebiet implizit ausgenommen werden sollen. Sie sollten dies nicht verändern, da dies hauptsächlich für debugging Zwecke verwendet wird. | true |<!--de-->
| enforce-adjacent-claim-chunks | Ob Chunks in einem beanspruchten Gebiet zusammenhängend seien müssen. | true |<!--de-->
| allow-diagonal-claim-chunks | Falls "enforce-adjacent-claim-chunks" auf "true" ist, werden Chunks, welche diagonal voneinander sind, als "nebeneinander" behandelt. Andernfalls macht diese Option nichts. | true |<!--de-->
| enter-chunk-editor-on-create | Wenn auf "true", werden Spieler, die ein neues Gebiet beanspruchen, automatisch in den Chunk-Editor versetzt. | true |<!--de-->
| use-economy | Ob Wirtschaft-Features genutzt werden sollen oder nicht. | false |<!--de-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-chunks | Bestimmt die maximale Anzahl an beanspruchbaren Chunks für die Gruppe. Siehe [Berechtigungen](#Berechtigungen) für mehr Infos. |  |<!--de-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims | Bestimmt die maximale Anzahl an Gebieten für die Gruppe. See Permissions for more info. Siehe [Berechtigungen](#Berechtigungen) für mehr Infos. |  |<!--de-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.give-after | Die Spielzeit in Sekunden, nach der Spieler dieser Gruppe zugeteilt werden. Werte niedriger als 0 bedeuten "nie". | -1 |<!--de-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price | Bestimmt den Preis für einen Chunk, falls Wirtschaft-Features aktiv sind. | 20 |<!--de-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.unclaim-reward | Bestimmt die Anzahl an Währung, die ein Spieler der Gruppe, beim Chunk freigeben, erstattet bekommt, falls Wirtschaft-Features aktiv sind. | 0 |<!--de-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.free-chunks | Bestimmt die Anzahl an kostenfreien Chunks, bevor Spieler den Preis, welcher in ``limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price`` spezifiziert ist, zahlen müssen, falls Wirtschaft-Features aktiv sind. | 4 |<!--de-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims-in-world | Die maximale Anzahl an gleichzeitig beanspruchten Gebieten pro Welt. Werte unter 1 setzen effektiv kein Limit. | -1 |<!--de-->
| dynmap-integration.enabled | Wenn "true", wird die Dynmap-Integration aktiviert. | true |<!--de-->
| dynmap-integration.use-old-outline-style | Wenn "true", wird Dynmap die alten Konvex-Hüllen-Umrandungen für Anspruchsgebiete verwenden. Dies ist hauptsächlich zum debuggen, da das neue Umrandungssystem experimentell ist. | false |<!--de-->
| disable-paper-warning | Deaktiviert die Warnung beim Serverstart, wenn das Plugin auf einem Spigot anstelle eines Paper Servers geladen wird. | false |<!--de-->
| worlds.use-whitelist | Ob worlds.whitelist beachtet werden soll | false |<!--de-->
| worlds.use-blacklist | Ob worlds.blacklist beachtet werden soll | false |<!--de-->
| worlds.case-sensitive | Ob Groß- und Kleinschrift für Welten in der Allow-/Denylist berücksichtigt werden soll | true |<!--de-->
| worlds.whitelist | Eine Liste, in der alle Welten aufgelistet werden müssen, damit XClaim in ihnen funktioniert. | Eine Beispielliste |<!--de-->
| worlds.blacklist | Eine Liste, in der alle Welten aufgelistet werden müssen, damit XClaim in ihnen NICHT funktioniert. | Eine Beispielliste |<!--de-->
| worlds.grace-time | Falls ein beanspruchtes Gebiet sich in einer Welt auf der Blacklist befindet, ist dies die Dauer in Sekunden, für welche das Gebiet bestehen bleibt. | 604800 (1 week) |<!--de-->
| language | Pluginin kullanılacağı dil, ``/plugins/XClaim/lang`` adresinden geçerli bir dil paketi olmalıdır, aksi takdirde en-US'ye geri döner | en-US |<!--tr-->
| veteran-time | "Veteran" statüsünün geçerli olması için bir oyuncunun sunucuda olması için gereken saniye cinsinden süre | 604800 (1 week) |<!--tr-->
| stop-editing-on-shutdown | Oyuncuların sunucu kapatıldığında chunk düzenleyicisinden çıkarılıp çıkarılmaması gerektiği | false |<!--tr-->
| stop-editing-on-leave | Oyuncuların gönüllü olarak ayrıldıklarında chunk düzenleyiciden çıkarılıp çıkarılmaması gerektiği | true |<!--tr-->
| exempt-claim-owner-from-permission-rules | Alan sahiplerinin alan üzerindeki tüm izinlere örtülü olarak erişmesi gerekiyorsa. Bunu değiştirmemelisiniz, esas olarak hata ayıklama amaçlıdır | true |<!--tr-->
| enforce-adjacent-claim-chunks | Bir hak talebindeki chunkların yan yana olup olması gerekip gerekmediği | true |<!--tr-->
| allow-diagonal-claim-chunks | Fenforce-adjacent-claim-chunks true olarak ayarlıysa bu, birbirinden köşegen olan chunkların birbirinin "yanında" olarak kabul edilip edilmeyeceğini belirler. Aksi takdirde hiçbir şey yapmaz. | true |<!--tr-->
| claim-min-distance | 0'dan büyükse, farklı oyuncular tarafından talep edilen parçalar arasındaki minimum mesafeyi belirler | 0 |<!--tr-->
| enter-chunk-editor-on-create | Eğer true değerindeyse, oyuncular yeni bir alan oluşturduklarında chunk düzenleyicisine girecekler | true |<!--tr-->
| use-economy | Ekonomi özelliklerinin kullanılıp kullanılmayacağı | false |<!--tr-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-chunks | Bir grup için maksimum parçaları ayarlar. Daha fazla bilgi için İzinler konusuna bakın. | |<!--tr-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims | Bir grup için maksimum alanları ayarlar. Daha fazla bilgi için İzinler konusuna bakın. | |<!--tr-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.give-after | Bir oyuncunun otomatik olarak bu gruba girinceye kadar oynaması için gereken saniye cinsinden süre. 0'dan küçük değerler "hiçbir zaman" anlamına gelir. | -1 |<!--tr-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price | Ekonomi etkinleştirilmişse, alana bir chunk eklemenin fiyatını belirler. | 20 |<!--tr-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.unclaim-reward | Ekonomi etkinleştirilmişse, bir chunkın iade edilmesi durumunda geri ödeme tutarını ayarlar. | 0 |<!--tr-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.free-chunks | Ekonomi etkinleştirilmişse, bir sonraki alan için ``limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price`` ödemesi gerekmeden önce bir oyuncunun ücretsiz olarak alabileceği chunk miktarını ayarlar.. | 4 |<!--tr-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims-in-world | Her dünyada aynı anda izin verilen maksimum alan sayısı. 1'den küçük değerler limitin olmadığını gösterir. | -1 |<!--tr-->
| dynmap-integration.enabled | True değerindeyse, XClaim başlangıçta Dynmap pluginini arayacak ve ona bağlanacaktır. Kapatıldığında hafif derecede hızlanma gözlemlenebilir. | true |<!--tr-->
| dynmap-integration.use-old-outline-style | Eğer doğruysa, Dynmap alandaki eski dışbükey gövde hatlarını kullanacaktır. Yeni taslak sistemi deneysel olduğundan bu esas olarak hata ayıklama amaçlıdır. | false |<!--tr-->
| disable-paper-warning | Sunucu Paper yerine Spigot'u çalıştırırken başlangıçta konsola gönderilen mesajı devre dışı bırakır | false |<!--tr-->
| worlds.use-whitelist | Eğer worlds.whitelist dikkate alınmalıysa | false |<!--tr-->
| worlds.use-blacklist | Eğer worlds.blacklist dikkate alınmalıysa | false |<!--tr-->
| worlds.case-sensitive | Beyaz/kara listedeki dünya adlarında büyük harf kullanımının önemli olup olmadığı | true |<!--tr-->
| worlds.whitelist | Bir dünyanın XClaim ile çalışması için bulunması gereken bir liste | a sample list |<!--tr-->
| worlds.blacklist | Bir dünyanın XClaim ile çalışması için içinde OLMAMASI gereken bir liste | a sample list |<!--tr-->
| worlds.grace-time | Bir alan izin verilmeyen bir dünyadaysa, alan otomatik olarak kaldırılmadan önce oyuncuların saniyeler içinde belirtilen zaman kadar zamanları olur | 604800 (1 week) |<!--tr-->
| language | 插件使用的语言, 对应的语言文件应当存在于 ``/plugins/XClaim/lang``否则它将会重置回默认值， 即 en-US | en-US |<!--zh-->
| veteran-time | 玩家自动进入驻留用户组所需时间 | 604800 (1 星期) |<!--zh-->
| stop-editing-on-shutdown | 服务器关闭时是否阻止玩家打开编辑器/关闭现有正在编辑的GUI | false |<!--zh-->
| stop-editing-on-leave | 当玩家自行离开时是否将其的领地编辑器强制关闭 | true |<!--zh-->
| exempt-claim-owner-from-permission-rules | 领地拥有者是否默认拥有全部权限. 该项不应被改动, 它主要用于调试 | true |<!--zh-->
| enforce-adjacent-claim-chunks | 领地内的所有区块是否必须相连 | true |<!--zh-->
| allow-diagonal-claim-chunks | 若 enforce-adjacent-claim-chunks 项为 true, 该设置将决定处在两个对角上的区块是否算作相邻区块 | true |<!--zh-->
| enter-chunk-editor-on-create | 若设置为 true, 每当玩家创建新领地时便会自动进入编辑模式 | true |<!--zh-->
| use-economy | 是否使用经济支持 | false |<!--zh-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-chunks | 设置用户组的最大可认领区块数. 详细信息见权限列表. | |<!--zh-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims | 设置用户组的最大可创建领地数. 详细信息见权限列表. | |<!--zh-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.give-after | 玩家延迟进入小组的时间, 设置为 -1 即表示禁用. | -1 |<!--zh-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price | 若经济支持启用, 该项可决定认领一个区块需要消耗多少游戏币. | 20 |<!--zh-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.unclaim-reward | 若经济支持启用, 该项可决定解除认领一个区块可退还多少游戏币. | 0 |<!--zh-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.free-chunks | 若经济支持启用, 该项可设置玩家在一个领地内可免费领取的区块数, 意味着若超出该数字则需要按照 ``limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.claim-price`` 选项来付费. | 4 |<!--zh-->
| limits.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦.max-claims-in-world | 单个玩家世界内最大可创建领地数量. 设置为小于 1 的值表示为禁用. | -1 |<!--zh-->
| dynmap-integration.enabled | 若开启, 本插件将会尝试对接到 Dynmap 并提供联动功能. 请在出现问题时禁用本项 | true |<!--zh-->
| dynmap-integration.use-old-outline-style | 若设置为 true 本插件将会使用旧版的描边轮廓. 该项主要用于调试. | false |<!--zh-->
| disable-paper-warning | 在非 Paper 服务器上启用本插件时是否关闭警告信息 | false |<!--zh-->
| worlds.use-whitelist | 是否启用分世界黑名单 | false |<!--zh-->
| worlds.use-blacklist | 是否启用分世界白名单| false |<!--zh-->
| worlds.case-sensitive | 是否在黑/白名单中检查世界的大小写 | true |<!--zh-->
| worlds.whitelist | 本插件将会启用的世界 | 列表 |<!--zh-->
| worlds.blacklist | 本插件不会启用的世界 | 列表 |<!--zh-->
| worlds.grace-time | 若领地在一个不被允许的世界创建, 玩家有多少时间来删除他们的领地并打包走人 | 604800 (1 星期) |<!--zh-->

## ${en:'Permissions', de:'Berechtigungen', tr:'Yetkiler', zh:'插件权限'}
Don't worry, there aren't that many.<!--en-->
Keine Sorge, es gibt nicht viele.<!--de-->
Merak etmeyin, o kadar da çok yok.<!--tr-->
不用担心, 这里并没有你想的那么可怕.<!--zh-->
| ${en:'Name', de:'Name', tr:'İsim', zh:'权限名称'} | ${en:'Description', de:'Beschreibung', tr:'Açıklama', zh:'权限描述'} |
| --: | :-- |
| xclaim.override | Allows you to overwrite claimed chunks |<!--en-->
| xclaim.admin | Allows you to modify/delete any claim |<!--en-->
| xclaim.import | Allows you to import claims from the ClaimChunk plugin |<!--en-->
| xclaim.update | Allows you to use the auto-updater |<!--en-->
| xclaim.restart | Allows you to restart xclaim |<!--en-->
| xclaim.clear | Allows clearing claims from players with /xclaim clear |<!--en-->
| xclaim.group.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦 | If a player has this permission, they are a part of this group. Players will inherit the "best" values from each group they are in. If the group is named "default", all players are in this group implicitly. |<!--en-->
| xclaim.override | Erlaubt es Chunks zu überschreiben. |<!--de-->
| xclaim.admin | Erlaubt es jeglichen Anspruch zu bearbeiten/löschen. |<!--de-->
| xclaim.import | Erlaubt es Ansprüche von ClaimChunk zu importieren. |<!--de-->
| xclaim.update | Erlaubt es automatisch Updates zu installieren. |<!--de-->
| xclaim.restart | Erlaubt es XClaim neu zu starten. |<!--de-->
| xclaim.clear | Erlaubt es alle Gebiete eines Spielers mit "/xclaim clear" freizugeben |<!--de-->
| xclaim.group.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦 | Wenn ein Spieler diese Berechtigung hat, ist dieser Teil der Gruppe. Spieler vererben die "besten" Werte aus all ihren Gruppen. Implizit sind alle Spieler in der "default" Gruppe. |<!--de-->
| xclaim.override | Sahip olunan chunkların üzerine yazmanıza olanak tanır |<!--tr-->
| xclaim.admin | Herhangi bir alanı değiştirmenizi/silmenizi sağlar |<!--tr-->
| xclaim.import | ClaimChunk eklentisinden alanları içe aktarmanıza olanak tanır |<!--tr-->
| xclaim.update | Otomatik güncelleyiciyi kullanmanızı sağlar |<!--tr-->
| xclaim.restart | XClaim'i yeniden başlatabilmenizi sağlar |<!--tr-->
| xclaim.clear | /xclaim clear komutuyla oyuncuların alanlarının temizlenmesine izin verir |<!--tr-->
| xclaim.group.𝘨𝘳𝘰𝘶𝘱-𝘯𝘢𝘮𝘦 | Bir oyuncu bu izne sahipse bu grubun bir parçasıdır. Oyuncular, bulundukları her gruptan "en iyi" değerleri devralır. Eğer grup "varsayılan" olarak adlandırılırsa, tüm oyuncular dolaylı olarak bu grupta yer alır. |<!--tr-->
| xclaim.override | 允许覆写已存在的领地 |<!--zh-->
| xclaim.admin | 允许你修改或删除任意领地 |<!--zh-->
| xclaim.import | 允许从 ClaimChunks 导入领地数据 |<!--zh-->
| xclaim.update | 允许你使用自动更新检查器 |<!--zh-->
| xclaim.restart | 允许你重启本插件 |<!--zh-->
| xclaim.clear | 允许通过 /xclaim clear 命令清除其他玩家的领地 |<!--zh-->
| xclaim.group.组名称 | 若玩家有该权限, 那么他们就是这个组的一部分. 玩家将优先位于权重值最高的组内. 若组名为 "default", 那么所有玩家都将默认包括在该组. |<!--zh-->

## ${en:'Commands', de:'Befehle', tr:'Komutlar', zh:'插件命令'}
| ${en:'Name', de:'Name', tr:'İsim', zh:'命令名称'} | ${en:'Description', de:'Beschreibung', tr:'Açıklama', zh:'命令描述'} |
| --: | :-- |
| xclaim | XClaim main command. Without any extra arguments, is the same as /xclaim gui |<!--en-->
| xclaim help | List the available subcommands |<!--en-->
| xclaim info | Provides basic info about XClaim |<!--en-->
| xclaim gui | Opens an easy-to-use GUI that covers most of XClaim's important features |<!--en-->
| xclaim update | Scans for new versions of XClaim and, if desired, runs the auto-updater |<!--en-->
| xclaim chunks \[claim_name] | Opens the chunk editor for the specified claim or, if absent, the current residing claim |<!--en-->
| xclaim current | Gets info about the current claim you are in |<!--en-->
| xclaim restart | Restart XClaim without restarting the server (experimental) |<!--en-->
| xclaim clear | Clear all claims from a player |<!--en-->
| xclaim list | Lists all claims a player owns |<!--en-->
| importclaims | Import claims from ClaimChunk |<!--en-->
| xclaim | Der XClaim Hauptbefehl. Ohne zusätzliche Argumente funktioniert er wie "/xclaim gui". |<!--de-->
| xclaim help | Listet die verfügbaren Unterbefehle auf. |<!--de-->
| xclaim info | Liefert grundlegende Informationen über XClaim. |<!--de-->
| xclaim gui | Öffnet eine leicht benutzbare Oberfläche, welches die meisten wichtigen Features von XClaim's abdeckt. |<!--de-->
| xclaim update | Sucht nach neuen Versionen von XClaim und installiert diese auf Wunsch automatisch. |<!--de-->
| xclaim chunks \[claim_name] | Öffnet den Chunk Editor für das spezifizierte Gebiet oder, falls dies nicht gegeben ist, das aktuell befindliche. |<!--de-->
| xclaim current | Liefert Informationen über das aktuell befindliche Gebiet. |<!--de-->
| xclaim restart | Startet XClaim neu, ohne dabei den Server neu starten zu müssen. *(experimentell)* |<!--de-->
| xclaim clear | Gibt alle Gebiete eines Spielers frei. |<!--de-->
| xclaim list | Listet alle Gebiete eines Spielers auf. |<!--de-->
| importclaims | Importiert beanspruchte Gebiete von ClaimChunk. |<!--de-->
| xclaim | XClaim ana komutu. Herhangi bir ekstra argüman olmadan, /xclaim gui ile aynıdır |<!--tr-->
| xclaim help | Kullanılabilir alt komutları listeleyin |<!--tr-->
| xclaim info | XClaim hakkında genel bilgileri sağlar |<!--tr-->
| xclaim gui | XClaim'in önemli özelliklerinin çoğunu kapsayan, kullanımı kolay bir GUI açar |<!--tr-->
| xclaim update | XClaim'in yeni sürümlerini tarar ve istenirse otomatik güncellemeyi çalıştırır |<!--tr-->
| xclaim chunks \[alan_ismi] | Belirtilen alan için chunk düzenleyicisini veya belirtilmediyse mevcut alan'ı açar |<!--tr-->
| xclaim current | Bulunduğunuz alan hakkında bilgi alır |<!--tr-->
| xclaim restart | Sunucuyu yeniden başlatmadan XClaim'i yeniden başlatın (deneysel) |<!--tr-->
| xclaim clear | Belirtilen oyuncunun tüm alanlarını siler |<!--tr-->
| xclaim list | Bir oyuncunun sahip olduğu tüm alanları listeler |<!--tr-->
| importclaims | ClaimChunk'tan alanları içe aktar |<!--tr-->
| xclaim | XClaim 的主命令. 不带任何参数则与 /xclaim gui 等价 |<!--zh-->
| xclaim help | 列出可用命令 |<!--zh-->
| xclaim info | 显示 XClaim 的基本信息 |<!--zh-->
| xclaim gui | 打开插件的 GUI, 可使用许多重要的功能 |<!--zh-->
| xclaim update | 检查更新, 若在配置文本中预先设置, 则会直接下载更新 |<!--zh-->
| xclaim chunks \[领地名称] | 编辑特定的领地, 留空则编辑已拥有的领地 |<!--zh-->
| xclaim current | 获取你所处领地的相关信息 |<!--zh-->
| xclaim restart | 在不重启服务器的情况下重启本插件 (测试功能) |<!--zh-->
| xclaim clear | 清除玩家的所有领地 |<!--zh-->
| xclaim list | 列出玩家所有的领地 |<!--zh-->
| importclaims | 从 ClaimChunk 插件导入领地数据 |<!--zh-->

## Placeholders<!--en--><!--tr-->
PlaceholderAPI integration was added in plugin version 1.13<!--en-->
PlaceholderAPI entegrasyonu pluginin 1.13 sürümünde eklendi<!--tr-->
| ${en:'Name', tr:'İsim'} | ${en:'Description', tr:'Açıklama'} |<!--en--><!--tr-->
| --: | :-- |<!--en--><!--tr-->
| xclaim_claim_count | Number of claims a player owns |<!--en-->
| xclaim_claim_count_in_*world* | Number of claims a player owns in *world* |<!--en-->
| xclaim_claim_max | Maximum number of claims a player could own |<!--en-->
| xclaim_chunk_count | Aggregate number of chunks a player owns |<!--en-->
| xclaim_chunk_count_in_*world* | Aggregate number of chunks a player owns in *world* |<!--en-->
| xclaim_chunk_max | Maximum number of chunks a player can have in **one claim** |<!--en-->
| xclaim_chunk_max_abs | Maximum number of chunks a player could own, if the player had as many claims as they possibly could and each claim had as many chunks as they possibly could |<!--en-->
| xclaim_claim_count | Bir oyuncunun sahip olduğu alan sayısı |<!--tr-->
| xclaim_claim_count_in_*world* | Bir oyuncunun *world* dünyasında sahip olduğu alan sayısı |<!--tr-->
| xclaim_claim_max | Bir oyuncunun sahip olabileceği maksimum alan sayısı |<!--tr-->
| xclaim_chunk_count | Bir oyuncunun sahip olduğu toplam chunk sayısı |<!--tr-->
| xclaim_chunk_count_in_*world* | Bir oyuncunun *world* dünyasında sahip olduğu chunkların toplam sayısı |<!--tr-->
| xclaim_chunk_max | Bir oyuncunun **bir alanda** alabileceği maksimum chunk sayısı |<!--tr-->
| xclaim_chunk_max_abs | Oyuncunun mümkün olduğu kadar çok alanı varsa ve her alanda mümkün olduğu kadar çok chunk varsa, bir oyuncunun sahip olabileceği maksimum chunk sayısı |<!--tr-->

## ${en:'Support', de:'Unterstützte Versionen', tr:'Desteklenen Sürümler', zh:'Support'}
|         | 1.8 - 1.11 | 1.12 - 1.13 | 1.14 - 1.16 | 1.17 - 1.19 | 1.20 | Folia | Paper & Spigot |
| --:     | :-:  | :-:  | :-:  | :-:  | :-:  | :-:  | :-:  |
| 1.5.0   | ❌   | ❌   | ❌   | ✔    | ❌    | ❌   | ✔     | 
| 1.8.0   | ❌   | ❌   | ✔    | ✔    | ❌    | ❌    | ✔     | 
| 1.9.0   | ❌   | ✔   | ✔    | ✔    |❌    | ❌    | ✔     |
| 1.9.1  | ✔   | ✔   | ✔    | ✔    | ❌    | ❌    | ✔     | 
| 1.10.0  | ✔   | ✔   | ✔    | ✔    | ✔    | ❌    | ✔     | 
| 1.10.2  | ✔   | ✔   | ✔    | ✔    | ✔    | ✔    | ✔     |
| 1.12.0  | ✔   | ✔   | ✔    | ✔    | ✔    | ✔    | ✔     | 

Versions before 1.5.0 are no longer supported<!--en-->
Versionen vor 1.5.0 werden nicht länger unterstützt!<!--de-->
Sürüm 1.5.0'dan öncesi artık desteklenmemekte<!--tr-->
在 1.5.0 之前的版本将不再受到支持<!--zh-->

## ${en:'Roadmap', de:'Entwicklungsplan', tr:'Planlar', zh:'未来计划'}
* Add more management commands<!--en-->
* Mehr Management-Befehle hinzufügen.<!--de-->
* Daha fazla yönetim komutu eklemek<!--tr-->
* 添加更多管理命令<!--zh-->

