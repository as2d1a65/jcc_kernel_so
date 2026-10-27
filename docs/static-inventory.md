# libdemo.so 静态提取清单

从 `libdemo.so`（5,671,856 字节，`.rodata` `0x15000`~`0x26000`）里**明文**提取，未运行、未注入、无设备参与。

字符串保护是不完整的：段表里有 `__vmenc_start`/`__vmenc_end` 标记，说明它做了字符串加密，但这一大块名字表是明文。

## 1. 游戏侧锚点方法（16 个，可直接作为 `HOOK_ATTACH` 目标）

| 名字 | 文件偏移 |
|---|---|
| `CSoGame_FrameTimeTick` | 0x0195ca |
| `PlayerListItem_SetMoney` | 0x01998a |
| `BaseHexCard_SetData` | 0x0199a2 |
| `TKFrame.StageRunner` | 0x0199b6 |
| `HeroRoot.ctor` | 0x01a17b |
| `PlayerListItem_SetPercentValue` | 0x01b36d |
| `TKFrame.UIPanel` | 0x01cebc |
| `HeroRoot_ctor` | 0x01d671 |
| `HeroRoot.Update` | 0x01daa1 |
| `PlayerListItem_SetData` | 0x01dbcb |
| `PlayerListItem_SetBloodValue` | 0x01e012 |
| `HeroRoot_Update` | 0x01f157 |
| `TKFrame.Services` | 0x01f9ff |
| `HeroRoot_SetData` | 0x021c56 |
| `PlayerListItem_SetPlayerLevel` | 0x02252e |
| `TKFrame.Entry_Ref_Ref` | 0x022b59 |

## 2. 游戏侧字段（6 个明文 `m_*`）

| 名字 | 偏移 |
|---|---|
| `m_sIconUrl` | 0x01a246 |
| `m_currentStage` | 0x01be1f |
| `m_strHeadUrl` | 0x01c193 |
| `m_sHeadUrl` | 0x01c9b6 |
| `m_sFaceUrl` | 0x01e46a |
| `m_strIconUrl` | 0x01e95b |

## 3. 对象属性名（19 个）

`fromCol` `completed` `available` `heroes` `starLevel` `slots` `count` `values` `pool` `result` `index` `level` `row` `gold` `array` `heroId` `col` `name` `type`


## 4. 外挂功能开关（33 个）

| 名字 | 偏移 |
|---|---|
| `specifiedMovePlanning` | 0x01963c |
| `specifiedShopUseLineup` | 0x019a11 |
| `specifiedShopReserveGold` | 0x019a28 |
| `excludedHeroIds` | 0x019a41 |
| `livePoolTotalWeight` | 0x019cff |
| `autoPick5CostChosen` | 0x019dad |
| `auxiliaryMode` | 0x019dc1 |
| `lineupStateKnown` | 0x019dcf |
| `autoPickAllChosen` | 0x01a27c |
| `boardEntityPreview` | 0x01a28e |
| `specifiedShop` | 0x01b3a5 |
| `specifiedShopCostMask` | 0x01b3c3 |
| `autoPlay` | 0x01b850 |
| `lineupAppliedStarTargets` | 0x01b870 |
| `autoRefreshReserveGold` | 0x01be51 |
| `specifiedMoveCombat` | 0x01c1fd |
| `autoPick` | 0x01ca88 |
| `lineupAppliedHeroIds` | 0x01ca91 |
| `livePool` | 0x01d23a |
| `auxiliaryStarTargets` | 0x01d2c4 |
| `boardHeroHeadOverlay` | 0x01d6c6 |
| `autoRefreshShop` | 0x01e4ba |
| `autoPickCostMask` | 0x01e9ec |
| `boardHeroes` | 0x01f116 |
| `auxiliaryAutoPickHeroIds` | 0x01f1b6 |
| `specifiedShopRefreshOnly` | 0x02020a |
| `specifiedShopStrict` | 0x020d9c |
| `autoRefreshFallbackMs` | 0x021cb6 |
| `livePoolWeights` | 0x022100 |
| `boardCount` | 0x022117 |
| `boardMask` | 0x022122 |
| `specifiedShopAffordOnly` | 0x022193 |
| `lineups` | 0x0221f5 |

## 5. 路径与敏感调用

- `/proc/self/task/`
- `/proc/self/statu`
- `/system/build.pr`
- `/proc/self/maps`
- `/proc/self/task`
- `/data/user/0/com.tencent.jkchess/cache/jcc-nb.txt`
- `/proc/self/maps`
- `/proc/cpuinfo`
- `/data/user/%u/%s/libdemo.so`
- `/proc/self/fd/%s`
- `/system/lib64/liblzma.so`
- `/proc/self/fd`
- `/data/data/com.tencent.jkchess/.xfjcc_ipc_v2_bootstrap`
- `/proc/self/map_files/%llx-%llx`
- `/proc/self/fd/%d`
- `/proc/self/fd/%d%c`
- `/system/build.prop`
- `/proc/self/cmdline`
- `/proc/self/mem`
- `/system/lib64`
- `/system/bin/linker64`
- `/system/bin/app_process64`

另有明文 `/proc/self/maps` —— 外挂自己用它定位 `libil2cpp.so` 基址。


## 6. 英雄内部名候选（343 个，已剔除噪声，仍需人工确认）

`aatrox` `accepted` `active` `add` `address` `ahri` `akali` `alistar` `allocator` `alune` `ambessa` `amumu` `anchors` `anivia` `annie` `aphelios` `applied` `area` `ascent` `ashe` `aurelionsol` `aurora` `authorized` `aux` `azir` `bard` `baronnashor` `bclen` `belveth` `binary` `binding` `blitzcrank` `blob` `bool` `boolean` `bootstrap` `bottom` `brambleback` `brand` `braum` `briar` `brock` `busy` `caitlyn` `camille` `card` `cards` `cassiopeia` `char` `checktrue` `chogath` `cinderling` `copies` `corki` `cost` `darius` `decltype` `device` `diana` `disabled` `discarded` `done` `double` `draven` `drmundo` `ekko` `elderdragon` `elise` `entries` `enum` `equips` `evelynn` `execution` `expected` `expired` `expires` `expiry` `extra` `ezreal` `ezrealfinal` `fiddlesticks` `fiora` `fizz` `float` `fpgme` `frank` `galio` `gangplank` `garen` `gasp` `generation` `generic` `ghost` `gnar` `gnarbig` `gragas` `graves` `gromp` `gwen` `half` `head` `heartbeat` `hecarim` `height` `hijklmno` `hwei` `illaoi` `inext` `inextp` `int` `invalid` `iostream` `irelia` `ivern` `janna` `jarvan` `jax` `jayce` `jhin` `jinx` `jit` `kaisa` `kalista` `karma` `karthus` `katarina` `kayle` `kayn` `kennen` `khazix` `kid` `kindred` `kobuko` `kobukoyuumi` `kogmaw` `krug` `ksante` `lease` `leblanc` `leesin` `left` `leona` `levels` `lib` `lillia` `lineups` `lissandra` `loading` `locked` `loris` `lucian` `lulu` `lux` `malphite` `malzahar` `manifest` `maokai` `mask` `masteryi` `match` `meepsie` `mel` `method` `milio` `missfortune` `module` `monkeyking` `mordekaiser` `morgana` `murkwolf` `nami` `nasus` `nautilus` `neeko` `nframe` `nidalee` `nilah` `noexcept` `nonce` `nregs` `null` `nullptr` `number` `nunu` `object` `offset` `offsets` `olaf` `operator` `opponent` `opponents` `orianna` `ornn` `pairs` `pantheon` `params` `payload` `pending` `ping` `planning` `platform` `pointers` `policy` `poppy` `positions` `post` `pqrstuvw` `pyke` `qiyana` `quality` `query` `rakan` `rammus` `rank` `raptor` `ready` `reason` `recast` `region` `reksai` `rell` `remove` `renekton` `rengar` `request` `response` `revision` `rhaast` `riftherald` `right` `riven` `rng` `rumble` `ryze` `samira` `samples` `save` `schema` `scope` `scuttlecrab` `sea` `seed` `sejuani` `senna` `sentry` `seq` `seraphine` `services` `session` `sett` `shaco` `shen` `shop` `short` `shyvana` `sig` `sign` `singed` `sion` `sivir` `skarner` `sona` `soraka` `stage` `stages` `star` `status` `std` `string` `struct` `summary` `swain` `sylas` `syndra` `tahmkench` `taliyah` `talon` `target` `targets` `taric` `teemo` `terminating` `text` `this` `thresh` `threshold` `throw` `title` `top` `total` `translate` `transport` `triples` `tristana` `true` `tryndamere` `twistedfate` `twitch` `udyr` `ull` `uncaught` `unexpected` `union` `unknown` `update` `urgot` `variants` `varus` `vayne` `vector` `veigar` `velkoz` `verified` `version` `vex` `vhea` `viego` `viktor` `vladimir` `void` `volibear` `waiting` `warwick` `width` `words` `writes` `wukong` `xayah` `xerath` `xinzhao` `yasuo` `yone` `yorick` `yptnk` `yunara` `yuumi` `zaahen` `zac` `zed` `ziggs` `zilean` `zoe` `zyra`


## 7. 静态拿不到的部分及替代

| 缺什么 | 为什么拿不到 | 替代方案 |
|---|---|---|
| **字段数值偏移** | il2cpp 运行时按 metadata 算，二进制里没有常量 | `drv_read` 读 `Il2CppClass.fields[]`，`FieldInfo.offset` 直接给出 |
| **il2cpp API 名字** | `dlopen("libil2cpp.so")` + 加密的 dlsym 名字（`__vmenc` 段） | 从 `libil2cpp.so` 的 `.dynsym` 直接读，208 个 `il2cpp_*` 全在，`jccd sym` 已验证可解析 |
| **类/方法 → 地址** | 类名在加密的 `global-metadata.dat`（magic 被改成 `94 43 72 12`） | 运行时 hook `il2cpp_class_get_method_from_name`，`x0` 即 `Il2CppClass*`，`jccd capture` 已验证精确抓取 |
| **VMP 字节码逻辑** | `arm64-llvm-vmp` 保护 | 不需要抄：数据常量在 `.rodata`，控制流不必复制 |

**分工结论：静态分析回答"做什么"，运行时回答"在哪里"。两者都不需要往游戏进程里放代码。**
