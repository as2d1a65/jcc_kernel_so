# libdemo.so 读取模型分析

从 `libdemo.so`（5,671,856 字节）静态提取，**未运行、未注入、无设备参与**。

方法：`.rodata` 里的字符串是明文，代码用 `ADRP + ADD` 相对 PC 寻址（不走重定位表），所以扫 `.text` 里所有 `ADRP+ADD`，把算出的目标反查成字符串，再按代码地址排序，就还原出**代码引用字符串的顺序**——也就是它执行逻辑的顺序。

工具：`tools/adrp_strings.py`

---

## 1. il2cpp 钩子安装序列（pc 0x295240，89 项）

按代码顺序排列，即实际执行顺序：

```
libil2cpp.so
libunity.so
libil2cpp.so                       ← dlopen
eglSwapBuffers                     ← 帧钩子
vkQueuePresentKHR                  ← 帧钩子（Vulkan 路径）
hook vkQueuePresentKHR
hook egl-only
/data/user/0/com.tencent.jkchess/cache/jcc-nb.txt
/proc/self/cmdline

HeroRoot_SetData
BuyHeroView_AnimateIn
SetRookieDeckData
GetRecommendData
HeroRoot_ctor
HeroRoot_Update
OnBuyHeroInfoChange
GetHeroKu
CSoGame_FrameTimeTick
GetMyPlayerModel
OpponentPrediction.SelectHighScore
AddPopPanel
RemovePopPanel
PopManager
PopLayout.show
PopLayout.show(bool)
PlayerListItem_SetData
PlayerListItem_SetMoney
PlayerListItem_SetPlayerLevel
PlayerListItem_SetBloodValue
PlayerListItem_SetPercentValue
TeamRecommend_InitData
TeamRecommend_ctor
TeamRecommendItemCallback0
TeamRecommendItemCallback1
TeamRecommendItemCallback2
TeamRecommendItemCallback3
RookieDeckView_Start
RookieDeckView_OnDestroy
RookieDeckView_SetHeroData
RecListPanelBattle_InitData
RecListPanelBattle_RenderDeckItems
GetRandomHACfgForSingle
FilterValidConfigs
HexStoreItem_SetData
BaseHexCard_SetData
HackHexCard_SetData
FRandom_RandomIntMax
FRandom_InternalSample
/proc/self/maps
%llx-%llx %4s %llx %*s %*s %1023s     ← maps 解析格式串
```

**结论：它不是从 `CSoGame` 走字段偏移链，而是 inline hook 上面这约 30 个方法，在钩子里直接从 `this` 指针 / 寄存器取数据。**

其中真正承载数据的核心只有四个：

| 方法 | 用途 |
|---|---|
| `CSoGame_FrameTimeTick` | 每帧调用，取 `CSoGame*` |
| `GetMyPlayerModel` | 取自己那份 PlayerModel |
| `HeroRoot_Update` | 棋盘英雄格子的刷新 |
| `PlayerListItem_SetMoney/SetBloodValue/SetPlayerLevel` | 8 名玩家的金币/血量/等级显示刷新 |

`eglSwapBuffers` / `vkQueuePresentKHR` 是 HUD 的渲染循环钩子，与游戏数据无关。

`FRandom_RandomIntMax` / `FRandom_InternalSample` / `GetRandomHACfgForSingle` / `FilterValidConfigs` / `HackHexCard_SetData` 是随机数劫持——用来实现"指定商店/指定装备"，即让游戏的随机结果按外挂的期望走。

---

## 2. IPC 命令表（pc 0x331ca0，149 项）

```
game.modifyGold
game.modifyHp
game.modifyLevel
game.modifyPopulation
chess.sell
chess.mirror
chess.customSwap
chess.allIn.start
chess.allIn.stop
drop.generateHeroes
drop.generateEquipment
```

配套字段：

```
matchGeneration    createdBootMs    ttlMs    maxTargets
value              targetMask       targetSelf    equipIds
```

**`game.modifyGold / modifyHp / modifyLevel / modifyPopulation` 是直接写内存**，用 `targetMask`（位掩码）选择作用于哪几个玩家，`targetSelf` 是否包含自己。这与本项目"内核写内存"的做法完全一致，不需要 il2cpp 调用。

`chess.sell` / `chess.mirror` / `chess.customSwap` / `chess.allIn` 是棋盘操作，`drop.generateHeroes` / `drop.generateEquipment` 是掉落生成（依靠上面的随机数劫持）。

`matchGeneration` / `createdBootMs` / `ttlMs` 是**动作有效期机制**：每个动作带一个对局代号和创建时间，超过 `ttlMs` 就丢弃，防止跨局误触发。这是必须实现的保护——否则跨局残留的地址会写坏内存。

---

## 3. 与本项目实测数据的对应

本项目通过对局中数值差分独立定位到的玩家数据块：

```
+0x00 int32  hp      ← 对应 PlayerListItem_SetBloodValue
+0x04 int32  level   ← 对应 PlayerListItem_SetPlayerLevel
+0x08 int32  0
+0x0C int32  回合计数（每回合 +2）
+0x10 int32  gold    ← 对应 PlayerListItem_SetMoney
+0x14 int32  1
```

字段顺序与三个 `Set*` 方法一一对应，说明**差分扫描命中的正是同一结构体**，只是未走注入路线。

实测样本：
- 上一局：`0x701d389b8c`（78 / 4 / 53，与游戏显示一致）
- 当前局：`0x6dda3c0b8c`（100 / 3 / 9，与游戏显示一致）
- 页内偏移恒为 `0xb8c`，跨局不变；地址每局重建

---

## 4. 对零注入路线的意义

需要盯的方法从"未知"收敛到四个，且都不在 il2cpp 公共 API 的热代码页上：

```
CSoGame_FrameTimeTick    → 拿 CSoGame*
GetMyPlayerModel         → 拿自己的 PlayerModel*
HeroRoot_Update          → 拿棋盘英雄 HeroRoot*
PlayerListItem_SetMoney  → 拿 8 名玩家的列表项
```

配合内核 PTE 钩子（`HOOK_ATTACH` mode 2 抓 x0）就能取到指针，之后全部用 `drv_read` 走字段。

**注意**：`HOOK_ATTACH` 是页粒度的（见 HANDOFF 4.5.2），所以必须挑**对战期才热、且代码页不与其他热路径共用**的方法。`HeroRoot_Update` 和 `CSoGame_FrameTimeTick` 符合条件——大厅期完全不被调用。

---

## 5. 尚未提取的部分

| 内容 | 状态 |
|---|---|
| il2cpp API 的 dlsym 名字 | 加密（`__vmenc_start` / `__vmenc_end` 段），但可从 `libil2cpp.so` 的 `.dynsym` 直接拿（208 个 `il2cpp_*`） |
| 字段偏移数值 | 代码里没有常量，运行时由 il2cpp metadata 计算；但外挂是 hook 方法取 `this` 后**按固定偏移读**的，说明偏移是硬编码立即数，被 VMP 保护 |
| VMP 字节码 | `arm64-llvm-vmp`，控制流虚拟化；数据表未受影响，所以名字能提出来 |
