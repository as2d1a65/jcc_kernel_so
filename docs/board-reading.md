
---

## 7. 棋盘读取链路（已打通）

```
CPlayer + 0x3c0  vecBattleHeroPos
   -> List<Z_PVE.TAC_BattleHeroPos>
        List<T> 布局:  +0x10 _items (T[])   +0x18 _size
   -> T[] 布局:       +0x18 max_length   元素从 +0x20 开始
        注意: 元素是对象指针，需要再解一层引用
   -> Z_PVE.TAC_BattleHeroPos
        +0x10 stHeroEntity (ptr)
        +0x18 x  (int32 列)
        +0x1c y  (int32 行)
   -> ZGameClient.TAC_HeroEntity
        +0x10 _iEntityID   (int32)
        +0x14 _iHeroConfID (int32)
```

工具：

```
jccboard <pid> <cplayer_addr>
```

实测输出（真实对局，8 个上场单位）：

```
CPlayer 0x6dda3c0800  vecBattleHeroPos=0x6dda3bc7c0
  _items=0x6dda36c6e0  _size=8

 idx  pos      entity        heroConfID   entityID
 0    (2,3)   0x6dd4d20820   21408        0x186ba
 1    (1,0)   0x6fdc357dc0   15397        0x186a1
 2    (0,3)   0x7046ad2b40   22412        0x186f6
 3    (2,0)   0x6dba58bea0   12406        0x186f2
 4    (0,0)   0x6dd4a3f140   24344        0x18707
 5    (1,3)   0x70472e37c0   23324        0x18719
 6    (3,3)   0x6da79512a0   24349        0x1871b
 7    (4,3)   0x7047398440   15296        0x18720
```

`heroConfID` 落在 12000~25000 区间，是配置表主键（不是 0 基下标）。

### 尚未拿到的字段

| 缺什么 | 在哪找 |
|---|---|
| 英雄星级 | `TAC_HeroEntity` 只有 entityID 和 heroConfID 两个字段。星级要么在父类（`class+0x58` 指向的不是父类，需重新确认偏移），要么在 `CPlayer.HeroPromotionMgr` @0x40，要么在 `m_stPBattleGround` @0x448 |
| 装备 | `CPlayer.EquipmentMgr` @0x488 |
| 商店 5 张卡 | `CPlayer.StoreController` @0x50 |
| 羁绊 | `CPlayer.fetterManager` @0x250、`m_vecFetter` @0x108 |
| 备战席 | `CPlayer.SurplusHeroList` @0x3e0（实测指向 `0x6ff5xxxxxx`，像是静态容器，需单独看布局） |

### 排查记录：两个坑

1. **`List<T>` 的元素是对象指针**，不是内联结构体。少解一层引用会得到垃圾坐标（实测出现 `(-1176296080,109)` 这种值）。
2. **`jccfields` 打印到名字指针失效就停**。`TAC_HeroEntity` 只有 2 个字段，`CPlayer` 有 204 个，都正常；但字段名出现乱码说明已越过表的边界，不要当成真实字段。

---

## 8. 方法表（打通「读寄存器」的前提）

### 8.1 MethodInfo 布局（实测）

```
+0x00  methodPointer   代码地址，落在 libil2cpp 的 r-xp
+0x10  const char* name
+0x18  Il2CppClass* klass     <- 用来校验/终止枚举
+0x40  uint32 token
```

### 8.2 Il2CppClass 偏移修正

```
+0x10  name
+0x18  namespaze
+0x78  fields     FieldInfo* 数组数据
+0x90  methods    MethodInfo* 数组数据
```

**重要：`+0x90` 直接指向 `MethodInfo*` 数组的数据区，没有托管数组头**（没有 `max_length`）。
终止条件是「指针为 0」或「`MethodInfo.klass` 不再等于本类」。

### 8.3 工具

```
jccmethods <pid> <class_addr> [max]
```

实测 `ZGameClient.TAC_HeroEntity`（13 个方法）：

```
token    name                       methodPointer
0x60039c9 get_iEntityID              0x70d72c0720
0x60039ca set_iEntityID              0x70d72c0780
0x60039cb get_iHeroConfID            0x70d72c07f4
0x60039cc set_iHeroConfID            0x70d72c0854
0x60039cd WriteTo                    0x70d72c08c8
0x60039ce ReadFrom                   0x70d72c098c
0x60039cf Display                    0x70d72c0a70
0x60039d0 Clear                      0x70d72c0b94
0x60039d1 DeepClone                  0x70d72c0c04
0x60039d2 .ctor                      0x70d72c0cf4
```

实测 `Z_PVE.CPlayer`（前 5 个）：

```
0x6022796 TAC_OnReqBuyGuideHero            0x70d0e9eda4
0x6022797 TAC_DoHeroPromotionByTurnState   0x70d0ea180c
0x6022798 TAC_DoHeroPromotionLOL           0x70d0ea1f7c
0x6022799 TAC_DoHeroPromotionInerForLOL    0x70d0ea319c
0x602279a ResetEquipment                   0x70d0ea4500
```

**`TAC_DoHeroPromotion*` 系列就是英雄升星**，是定位星级字段最直接的 hook 点。

### 8.4 用法

```
jccd regs <pid> <method_addr> <秒>     抓一次并 dump x0~x30（实际只有 x0~x3 有效）
```

`x0` 是 `this`，`x1~x3` 是前三个参数。注意 HANDOFF 4.5.1：`HOOK_READREG` 只可靠支持 x0~x3。

---

## 9. 换局验证（已做）

同一游戏进程内换局（进程未重启）：

| 项 | 结果 |
|---|---|
| 类指针 `0x70b36f8000` | 不变 |
| 字段表 `0x708b2953e8` | 不变 |
| 方法表 | 不变 |
| 8 名玩家实例地址 | 全部重建 |
| `jccplayer` 自动重找 | 正常，无需任何重新配置 |

**结论：换局只需重读实例地址，元数据（类/字段/方法）在进程生命周期内稳定。**

---

## 10. 星级定位进展（未完成，已排除两条路）

### 10.1 已排除：`TAC_HeroEntity` 里没有星级

该类只有两个数据字段：

```
+0x10  _iEntityID
+0x14  _iHeroConfID
```

对象大小 **0x20 字节**（实测两个实体地址相差 0x20，且池内紧邻排列）。`+0x18` / `+0x1c` 是仅有的两个额外 int，但实测无法对应星级：

| heroConfID | +0x18 |
|---|---|
| 12411 | 0 |
| 15281 | 1 |
| 11404 | 0x3f800000 (1.0f) |
| 13325（副本 A） | 0 |
| 13325（副本 B） | 4 |

同一个英雄的两个副本值不同，且存在浮点值，判为**非星级**。

**注意：dump 实体时必须只取 0x20 字节**。取 0xC0 会把邻居实体的数据一起拍进来，diff 全是噪声（踩过这个坑）。

### 10.2 已定位：星级在英雄管理器的嵌套 map 里

```
CPlayer + 0x450  m_stTACBuyHero   -> Z_PVE.CTAC_HeroMgr
   +0x20  m_mapHeroMgr            -> LinkedHashMap`2
   +0x28  m_iMaxHeroNum = 5
   +0x2c  m_iPosType    = 3

m_mapHeroMgr 的 LinkedHashMap 字段：
   +0x10 entries
   +0x18 buckets
   +0x20 comparer
   +0x28 keys
   +0x30 values
   +0x40 internalCount   <- 实测 5，与场上单位数一致
   +0x44 version
   +0x48 freeList
   +0x4c freeCount
   +0x50 headIndex
   +0x54 tailIndex
```

`values` 里是 `{key, innerMap}` 配对，步长 0x10，其中 `innerMap` 也是 `LinkedHashMap`2`：

```
0x7078a552a0: 10 82 ff 45 70 00 00 00   30 02 16 f0 6f 00 00 00
              ^key (0x7045ff8210)        ^inner map (0x6ff0160230)
0x7078a552c0: 10 82 ff 45 70 00 00 00   a0 09 16 f0 6f 00 00 00
              ^同一个 key                ^另一个 inner map (0x6ff01609a0)
```

两个同 heroConfID 的单位共享同一个 key，但各有独立的 inner map —— **星级就在各自的 inner map 里**。

### 10.3 当前障碍

`entries` 指针（实测 `0x70689a6660`）**驱动读不到**（落在 `r--p` 段或未驻留页）。这是已知限制（见 HANDOFF 4.5.4）。

绕法：
1. 从 `buckets`（`int[]`）或 `headIndex/tailIndex` 链式遍历，避开 `entries`
2. 或换一个 `entries` 落在可读区的实例
3. 或用 `keys`/`values` 数组——但实测 `keys` 为 null，`values` 是上述配对区

### 10.4 备选（更快）

英雄合成时会调用 `CPlayer.TAC_DoHeroPromotionByTurnState`（`0x70d0ea180c`）等三个升星方法。若能拿到它们的参数，星级直接可见。但 `HOOK_ATTACH` 在 `libil2cpp` 代码段不可用——实测 `hits=5001 x0=0x0`，因为 4KB 页里混有高频函数，PTE 钩子被灌满（见 HANDOFF 4.5.2）。

## 11. 工具补充

```
jccsnap <pid> <cplayer> <out.bin>    把每个棋盘实体的 0x20 字节原始数据写成二进制快照
tools/diffsnap.py <A.bin> <B.bin>    两份快照逐字段比对（int/float 双解释）
```

用途：合成/买卖前后各拍一次，diff 出变化的字段。

---

## 12. 重大修正：扫描器漏了元数据区（已修）

### 12.1 问题

`jccscan` 扫描量长期卡在 ~1400 MB，类名/字段名永远 0 命中。三个叠加的 bug：

| # | 问题 | 后果 |
|---|---|---|
| 1 | 过滤规则只接受 `rw-`，把 `rw-s` 跳过 | **元数据就在 `rw-s` 里，整块被漏掉** |
| 2 | 区域上限 `cap = 1024` | 游戏映射数超过 1024，后面的区被截断 |
| 3 | 1MB 块读失败就整块跳过，无逐页回退 | 有非驻留页的区被整块丢弃 |

### 12.2 元数据在哪里

```
70c5a00000-70c9ceb000 rw-s 00000000 00:01 867535    /dev/zero (deleted)
```

68.6 MB 的 `/dev/zero` 共享可写映射 —— **这就是解密后的 `global-metadata.dat`**。游戏把加密的 metadata 解密后放在这里，所有类名、字段名、方法名都是明文。

类名示例：`0x70c628eff3` → `"CPlayerGameStatistics"`；`0x70c5e23d1d` → `"starLevel"`。

### 12.3 修复后的效果

| 指标 | 修复前 | 修复后 |
|---|---|---|
| 扫描覆盖 | ~1400 MB | **4472 MB** |
| `CPlayerGameSt` | 0 命中 | 6 命中 |
| **`starLevel`** | **0 命中** | **5 命中** |
| `UnityEngine` | 260 命中 | 260 命中 |

### 12.4 由此确认的事实

**`starLevel` 确实是这个游戏的字段名**（元数据里有 5 处），只是不属于我们已定位的 4 个类：

| 类 | 有 star 字段吗 |
|---|---|
| `Z_PVE.CPlayer`（204 字段） | 无 |
| `Z_PVE.CPlayerGameStatistics`（204 字段） | 无 |
| `ZGameClient.TAC_HeroEntity`（2 字段） | 无 |
| `Z_PVE.TAC_BattleHeroPos`（3 字段） | 无 |

### 12.5 下一个明确入口

`CPlayerGameStatistics` 有个字段一直没查：

```
m_mapHeroStar    @0x1a0    LinkedHashMap`2
```

实测（stats 实例 = CPlayer - 0x800）：

```
map   = 0x6ddab83a80
  +0x10 entries        = 0x6ddad7f0e0
  +0x18 buckets        = 0x6fdcc34360
  +0x20 comparer       = 0x712317b620
  +0x40 internalCount  = 1
  +0x44 version        = 4
  +0x50 headIndex      = 0
  +0x54 tailIndex      = 0

entries 数组:
  +0x00 klass = 0x7089d78e30
  +0x18 max_length = 3
  元素区从 +0x20 起，实测内容:
    0x6ddad7f100: 02 00 00 00  ff ff ff ff  ff ff ff ff  ff ff ff ff
    0x6ddad7f110: 02 00 00 00  04 00 00 00  00 00 00 00  00 00 00 00
```

按 0x20 字节/条解读：`{hashCode=2, next=-1, prev=-1, key=2, value=4}`。
`internalCount=1` 说明只有一个条目。**key 和 value 的语义需要再确认**（可能是 星级→数量，也可能是 英雄ID→星级，但 key=2 不像 heroConfID）。

### 12.6 工具修正清单

```
jccscan 修复三处：
  - 接受 rw-s 共享映射
  - cap 1024 -> 8192
  - 增加逐页(4KB)读回退，失败页填 0xCD
```

`jccfind` / `jccread` / `jccplayer` 仍用旧过滤规则，若需要扫元数据要同步修改。

---

## 13. `m_mapHeroStar` 语义已确认

`CPlayerGameStatistics.m_mapHeroStar` @0x1a0，`LinkedHashMap`2`，条目 0x18 字节：

```
+0x00 int32 hashCode
+0x04 int32 next
+0x08 int32 prev
+0x0C int32 reserved
+0x10 int32 key        <- 星级
+0x14 int32 value      <- 该星级的单位数量
```

四个玩家实测对照：

| 座位 | internalCount | 条目 |
|---|---|---|
| 7（等级7） | 2 | key=2 → 6 个，key=3 → 1 个 |
| 1（等级9） | 1 | key=2 → 4 个 |

**结论：这是「星级 → 单位数」的聚合统计，不含"哪个单位几星"。** 只统计 ≥2 星的单位（1 星不入表）。

## 14. `starLevel` 在元数据里是属性而非字段

用 `jccfindfield <pid> starLevel` 找到 7 处引用，结构一致：

```
+0x00  name 指针（指向 "starLevel"）
+0x08  0
+0x0C  0x0802e640 之类（token / id）
+0x10  0x70cd2c8060（Il2CppType，疑似 int32）
+0x18  负数（不是合理的字段偏移）
+0x20  0x70d02dc620（**落在 libil2cpp 代码区**）
```

`+0x20` 是代码指针，说明这不是 FieldInfo，而是带 getter 的**属性**。游戏自己的成员缓存表记录了 `starLevel` 的读取函数地址 `0x70d02dc620`。

这也解释了为什么 `jccfields` 在四个类里都找不到 `starLevel`——它不是字段。

**不能走 hook 拿它**：`libil2cpp` 代码段的 PTE 钩子实测不可用（HANDOFF 4.5.2，`hits=5001 x0=0x0`）。

## 15. 仍未打通的逐单位星级

已排查并排除：

| 位置 | 结论 |
|---|---|
| `TAC_HeroEntity` +0x18/+0x1c | 非星级（同英雄两副本值不同、含浮点） |
| `m_mapHeroStar` | 星级→数量，聚合值 |
| `m_vecBattleHero` @0x100 | `_size=3` 但 `_items` 数组头 `max_length=0`，布局需重推 |
| `m_mapHeroPromotionTree` | `entries` 落在 r--p 段，驱动读不到 |

未排查完的入口：

| 入口 | 状态 |
|---|---|
| `CPlayer.LatestBattleHeroDic` @0x4f0 | `internalCount=22`，`entries` 可读，条目含 entityID（`0x18709` 这类），布局待推 |
| `CPlayer.SurplusHeroList` @0x3e0 | 未展开 |
| `CPlayer._vecBattleHeroPos` @0x400 | 未展开 |
| 游戏成员缓存表 | 已知含 `starLevel` 的 getter 地址，但要靠 hook 才能用 |

## 16. 工具补充

```
jccfindfield <pid> <fieldname>
```

按名字反查成员：扫全内存找名字串 → 找指向它的槽 → 按 FieldInfo 解读并打印 `parent` 类名与 `offset`。失败时打印原始上下文，便于识别真实布局。

实测 `starLevel` 输出 7 个候选槽，并把结构打印出来（即第 14 节的分析来源）。

**注意**：该工具与 `jccscan` 一致，接受 `rw-s` 映射、`cap=8192`、逐页读回退。`jccfind` / `jccread` / `jccplayer` 仍是旧规则。

---

## 17. `CSoGame` 已定位（112 字段）

用扫描器修好后的元数据，按类名找到：

```
字符串 "CSoGame" @ 0x70c5fd27ba
指向它的槽    @ 0x70c4d3f568
类指针 = 槽 - 0x10 = 0x70c4d3f558
```

### 17.1 关键字段

| 字段 | 偏移 | 说明 |
|---|---|---|
| `CurLogicFrameTime` | 0x38 | 当前逻辑帧 |
| `UserInfoDic` | 0x40 | 用户信息字典 |
| `PlayerPlayBookDic` | 0x60 | |
| `_vecBuyHero` | 0x78 | 购买区英雄 |
| `_vecWaitHero` | 0x80 | 备战席英雄 |
| `_vecBattleGroundHero` | 0x88 | 场上英雄 |
| `_vecHeroValueChange` | 0x90 | |
| `_roomId` | 0xf8 | 房间号 |
| `_battleId` | 0x100 | |
| `_mySeatId` | 0x10c | 自己的座位 |
| `CGameStatistics` | 0x148 | |
| `CSoConfig` | 0x150 | |
| `NPCManager` | 0x158 | |
| `PlayerMgr` | 0x160 | 玩家管理器 |
| `roundSelectCore` | 0x168 | 选秀核心 |
| `chessBattleCore` | 0x170 | 战斗核心 |
| `OpponentMgr` | 0x180 | 对手管理器 |
| `ListBattleVSPlayers` | 0x198 | |
| **`ListBattleHeroDatas`** | **0x1a0** | 战斗英雄数据 |
| `_dicBattleField` | 0x1a8 | |
| `gameFettersCtrl` | 0x1b8 | 羁绊控制 |
| `HeroPool` | 0x250 | 英雄池 |
| `UpStarCount` | 0x300 | 升星计数 |
| `UpStarCountByQuality` | 0x308 | 按品质的升星计数 |
| `Data` | 0x318 | |

### 17.2 实例获取

从任意 `CPlayer` 反查（`CPlayer.CSoGame` @0x30）：

```
CPlayer 0x6ddab81800  -> CSoGame 0x701df78540
```

注意：直接按 `klass == 0x70c4d3f558` 扫会得到 401 个命中，绝大多数是元数据表里存放的类指针，不能用来找实例。

## 18. 逐单位星级：已把实体链走到底，仍未出现

完整链路实测：

```
CSoGame +0x1a0  ListBattleHeroDatas  = LinkedHashMap`2
   entries@0x10  buckets@0x18  comparer@0x20  internalCount@0x40
   条目 0x20 字节: {hash, next, prev, 序号, 对象指针, 编号}
   实测 4 个条目，key 值 3/4/5/6
   -> 每个值是一个 List<T>（System.Collections.Generic.List`1）
        _items@0x10  _size@0x18
        实测第一条列表 _size = 11
   -> 元素是 ZGameClient.TAC_BattleGroundHero
        stHeroEntity       @0x10
        stBattleGroundPos  @0x18
        bIsRealHero        @0x20
        bIsPartnerHero     @0x21
        iDragonEggCfg      @0x22
   -> stHeroEntity 是 ZGameClient.TAC_HeroEntity（只有 _iEntityID / _iHeroConfID）
```

**整条链上没有任何星级字段。**

同时 `CSoGame.UpStarCount` @0x300 / `UpStarCountByQuality` @0x308 只是全局计数。

### 结论

逐单位的星级只可能来自两处：

1. `CPlayer.HeroPromotionMgr` -> `m_mapHeroPromotionTree`（英雄->升星树的映射），但该 map 的 `entries` 落在 r--p 段，驱动读不到
2. `CPlayer.LatestBattleHeroDic` @0x4f0：`internalCount=22`，`entries` 可读，条目含 entityID

## 19. 附带确认：`starLevel` 是属性

`jccfindfield <pid> starLevel` 找到 7 处引用，结构：

```
+0x00  name -> "starLevel"
+0x0C  token (0x0802e640 之类)
+0x10  Il2CppType (0x70cd2c8060，int32)
+0x20  0x70d02dc620  <- libil2cpp 代码区，getter
```

所以四个已知类里都查不到 `starLevel` 字段，它是属性。

## 20. 扫描器修复带来的连锁收益

| 类名 | 修复前 | 修复后 |
|---|---|---|
| `CSoGame` | 0 命中 | **32 命中** |
| `HeroRoot` | 0 命中 | **50 命中** |
| `PlayerListItem` | 0 命中 | 45 命中 |
| `CHero` | 0 命中 | 7 命中 |
| `starLevel` | 0 命中 | 5 命中 |

之前"这些类不存在"的判断全部推翻——它们一直都在，只是扫描器没覆盖元数据。

**找类的通用方法**：

```
jccscan str <pid> <ClassName>          找到类名字符串地址 S
jccscan ptr <pid> <S去掉0x>            找到指向 S 的槽
类指针 = 槽 - 0x10
jccfields <pid> <类指针>               打印该类全部字段
```

---

## 21. 逐单位星级：全部候选入口均已排查（均失败）

| 入口 | 实测结果 |
|---|---|
| `CSoGame+0x1a0 ListBattleHeroDatas` | `LinkedHashMap`2`，4 条目 → `List<TAC_BattleGroundHero>` → `stHeroEntity`(只有 id+confID) |
| `CPlayer+0x4f0 LatestBattleHeroDic` | `entries` 全 `-1`，`freeCount==internalCount` = **空表** |
| `CPlayer+0x40 HeroPromotionMgr+0x18 m_mapHeroPromotionTree` | `internalCount=9, freeCount=9, headIndex=-1` = **空表** |
| `CPlayer+0x450 CTAC_HeroMgr+0x20 m_mapHeroMgr` | 内层 `LinkedHashMap` `internalCount=1, freeCount=1` = **空表** |
| `TAC_HeroEntity` +0x18/+0x1c | 同英雄两副本值不同、含浮点，非星级 |
| `CSoGame+0x80 _vecWaitHero` | `_size=958` 的全局池，元素为 null，不是备战席 |
| `{星级,英雄ID}` 整数对搜索 | 排列 B 有命中（`0x380100000001`），但上下文是英雄池计数表，非单位星级 |

**规律**：`LatestBattleHeroDic` / `m_mapHeroPromotionTree` / `m_mapHeroMgr` 这三张表都是**战斗期缓存**，在备战期全部为空。要验证它们必须在对战进行中抓。

### 已确认的替代数据

`CPlayerGameStatistics.m_mapHeroStar` @0x1a0 提供**聚合**星级分布：

```
chair 7: 2星 6 个，3星 1 个
chair 1: 2星 4 个
```

条目布局 0x18 字节：`{hash, next, prev, reserved, key=星级, value=数量}`。

## 22. 下一步建议（最快路径）

**请用户在合成瞬间给出该单位的格子坐标**，然后直接对比该格实体与一个 1 星实体的内存差异。

理由：`vecBattleHeroPos` 已能读出每个单位的 `{坐标, entityID, heroConfID, 实体地址}`，同一格子的实体地址在对局内稳定，两次 dump 同一地址即可隔离出星级字段，不需要遍历任何容器。

这是目前唯一能绕开"容器为空/读不到"的方法。

---

## 23. `m_mapHeroStar` 已用真机对照验证

新对局实测（用户声明"棋盘左上角那个格子是两星的"）：

```
座位判定：只有 chair 5 的单位在 (0,3)（左上角）
  chair5 board = (1,3)h15287  (0,3)h21408  (0,0)h13320

chair5 stats = CPlayer - 0x800 = 0x7048e0b000
m_mapHeroStar @ stats+0x1a0 = 0x7048d43230
  internalCount = 1
  entries 数组 max_length = 3
  entry0 = { hash=2, next=-1, prev=-1, reserved=-1, key=2, value=1 }
         = { 星级=2 -> 数量=1 }
```

**与用户声明完全一致。** 至此 `m_mapHeroStar` 语义确证：

```
m_mapHeroStar : 星级(>=2) -> 该星级的单位数量
```

## 24. 逐单位星级：结论

同一个 2 星单位（hero 21408 @ (0,3)）的实体与两个 1 星单位的实体**逐字节对比**：

| 单位 | 实体地址 | +0x18 | +0x1C |
|---|---|---|---|
| (1,3) hero 15287 | 0x6da40d7cc0 | 0 | 19 |
| **(0,3) hero 21408（2星）** | **0x6d8c018d40** | **0** | **0** |
| (0,0) hero 13320 | 0x7078ce44a0 | 0 | 0 |

`TAC_HeroEntity` 的 0x20 字节全部字段已穷举：`{klass, 0, entityID, heroConfID, +0x18, +0x1C}`，**没有星级**。

补充搜索全部落空：

- `{2, 21408}` 相邻 int32（两种字节序）→ 0 命中
- `{21408, 0}` 的 8 字节（带 4 字节填充）→ 0 命中
- `{星级2, 任意英雄ID}` 值域扫描 → 命中全是 arm64 代码字节
- `m_iPlayerLife` 等 CPlayer 字段与星级无关

**结论：逐单位的星级不在任何可读的实体/容器里。** 它由 `starLevel` 属性在运行时计算，属性 getter 地址为 `0x70d02dc620`（`jccfindfield` 实测），而 `libil2cpp` 代码段的 PTE 钩子不可用（HANDOFF 4.5.2），因此拿不到返回值。

## 25. 当前可用能力清单

| 能力 | 状态 | 入口 |
|---|---|---|
| 8 名玩家血量/等级/经验/金币/名次/座位/AI | 可用 | `CPlayerGameStatistics` + `m_stOwner` |
| 每名玩家的棋盘（坐标 + 英雄ID + entityID） | 可用 | `CPlayer+0x3c0 vecBattleHeroPos` |
| **每名玩家的星级分布（2星几个、3星几个）** | **可用，已验证** | `CPlayerGameStatistics+0x1a0 m_mapHeroStar` |
| 游戏根对象 CSoGame（112 字段） | 可用 | `CPlayer+0x30` |
| 英雄配置表/池 | 部分 | `CSoGame+0x78/+0x80` |
| **逐单位星级** | **不可用** | 需 hook `starLevel` getter |

### 实用结论

"每名玩家的星级分布 + 棋盘英雄ID列表" 已足够支撑多数决策：

- 知道对手有几个 2 星/3 星 → 判断战力
- 知道自己有几个 2 星 → 判断还能不能合成
- 星级变化时 `m_mapHeroStar` 的 value 会变 → **可检测升星事件**

不能做的：判断"具体哪一个棋子是几星"（例如自动决定卖哪个）。

---

## 26. 战斗逻辑层类（新发现）

`TAC_` 前缀的类全是客户端显示/观战数据。真正的战斗模拟数据在 **`ChessBattle*`** 系列：

```
CPlayer.m_logicPlayer @0x200 -> .ChessBattleLogicPlayer
   <ChairId>            @0x110
   m_battleField        @0x118
   m_battleLogicField   @0x120 -> .ChessBattleLogicField
   m_battleLogicUnitId  @0x128 -> LinkedHashMap`2
   m_waitLogicUnitId    @0x130 -> LinkedHashMap`2
   m_heroPromotionList  @0x138 -> List<T>
   m_maxFrame @0x140  m_nowFrame @0x144  m_currentId @0x148
   m_logicPlayerData    @0x1d0 -> .ChessBattleLogicPlayerData
        m_cplayer @0x28  m_observerModel @0x30  m_attackData @0x48
   m_equipMgr           @0x1f8
```

实测备战期：`m_battleLogicUnitId.entries = 0`、`m_heroPromotionList._size = 0` —— **全是战斗期缓存**。

## 27. `starLevel` 后备字段解析（方法已跑通）

用「字段名 → FieldInfo → parent+offset」的路径成功解析：

```
m_starLevel  -> parent = 0x70b19e0f30 = .WarehouseDetailStarListItem   offset = 0x294
```

FieldInfo 布局再次确认：`name@+0x00, type@+0x08, parent@+0x10, offset@+0x18`。

同类解析结果（均为 UI 层，非棋盘单位）：

| 字段名 | 所属类 | 偏移 |
|---|---|---|
| `m_starLevel` | `WarehouseDetailStarListItem` | 0x294 |
| `m_HeroStarThreeEntity` | `AITreeNode_ChooseSellChessV2` | 0x68 |
| `m_star` | `RookieDeckDockViewItem` | 0xe4 |
| `m_star` | `TinyUnlockStarItem` | 0x78 |
| `m_HeroStarTextureNameCache` | `ChessHeroMaterialResPool` | 0x10 |

**`AITreeNode_ChooseSellChessV2` 值得注意**：这是游戏自己的 AI「选择卖哪个棋子」节点，字段全是 `m_SellHero` / `m_NoTypeHero_Wait` / `m_LineUpHero_Battle` / `m_HeroStarThreeEntity` 之类——证明游戏 AI 确实在做星级判断，但数据来源不在这张表里。

## 28. 为什么 APK 能拿到、我们拿不到（机制层面）

| | libdemo.so | 本项目 |
|---|---|---|
| 手段 | inline hook `HeroRoot_SetData` | 无 hook |
| 取法 | 在函数被调用时读**参数寄存器** | 只能读内存 |
| 需要星级被存下来吗 | **不需要**，值在调用瞬间就在寄存器里 | **需要**，必须存在于某处 |

`starLevel` 是**属性**（getter `0x70d02dc620`），说明它是**运行时计算**出来的，不是持久化字段。所以"内存里找不到"是合理的——它可能压根没存。

### 由此推出的新假设

TFT 类游戏的星级是**派生值**：拥有 3 个同英雄 → 2 星，9 个 → 3 星。若游戏采用此模型，则它只需要存「每个英雄的拥有数量」，星级按数量算：

```
1~2 个 -> 1 星
3~8 个 -> 2 星
9+  个 -> 3 星
```

旁证：

1. `starLevel` 是属性，是算出来的
2. `m_mapHeroStar` 只缓存**聚合结果**（星级→数量），正符合"原始数据是数量、星级是派生"的设计
3. 早前在对局中实测到形如 `{数量, 英雄ID}` 的相邻整数对：`{0,0x3802} {10,0x3802} {1,0x3801} {6,0x3801}` —— 其中的 10 / 6 / 1 很像拥有数量

**下一步**：找"每英雄拥有数量"的可读容器，自己按数量算星级，效果等价于读星级，且不需要 hook。

---

## 29. 星级问题已解决：星级编码在 heroConfID 里

**不需要任何 hook、不需要任何额外结构。星级就是英雄配置 ID 的万位偏移。**

### 29.1 英雄配置表（实测 114 项）

从 `CPlayer._heroPool` -> `CTAC_HeroPool.m_stHeroKuGroup` -> `CTAC_HeroKuGroup.m_mapHeroQuality` @0x20 读出：

```
114 个英雄，按费用分组：
  1 费 14 个   起始 ID 11400
  2 费 20 个   起始 ID 12400
  3 费 23 个   起始 ID 13320
  4 费 28 个   起始 ID 14330
  5 费 29 个   起始 ID 15270
  最大 ID 15403
```

### 29.2 星级解码规则

棋盘上读到的 `heroConfID` 会出现大于 15403 的值：

```
11402  15278  21408  11402  21409
```

`21408 = 11408 + 10000`、`21409 = 11409 + 10000`、`25401 = 15401 + 10000`、`23325 = 13325 + 10000`。

**规则：**

```
1 星:  11400 ~ 15403
2 星:  21400 ~ 25403    (基础 ID + 10000)
3 星:  31400 ~ 35403    (基础 ID + 20000)

基础英雄 ID = heroConfID - (star - 1) * 10000
```

### 29.3 双重验证

同一时刻两个独立来源一致：

**来源一：棋盘 heroConfID 解码**

```
 idx  pos      heroConfID star  base      entityID
 0    (3,3)   11402      1star base=11402  0x927ce
 1    (3,2)   15278      1star base=15278  0x927c1
 2    (1,3)   21408      2star base=11408  0x927e5
 3    (0,1)   11402      1star base=11402  0x927df
 4    (2,3)   21409      2star base=11409  0x927ec
```

**来源二：`CPlayerGameStatistics.m_mapHeroStar` @0x1a0**

```
internalCount = 1
entry = { key=2(星级), value=2(数量) }   ->  2 星有 2 个
```

**两者完全一致。** 用户也确认"棋盘上有两个两星"。

### 29.4 为什么之前找不到

`TAC_HeroEntity` 的 FieldInfo 数组原始数据实测只有 **2 个字段**：

```
entry0: name=0x70c5e247aa  type=0x70cd2c8080  parent=0x70b2210e38  offset=0x10  token=0x04002629
entry1: name=0x70c5e39df5  type=0x70cd2c8080  parent=0x70b2210e38  offset=0x14  token=0x0400262a
entry2: name 指向类自身 = 数组结束标记
```

星级不是字段，而是**编码在主键里**——所以任何"找星级字段"的思路都会失败。

### 29.5 工具更新

`jccboard` 现在直接打印星级：

```
jccboard <pid> <cplayer_addr>

 idx  pos      entity        heroConfID star  base      entityID
 2    (1,3)   0x6ff58e4a40   21408      2star base=11408  0x927e5
```

**至此逐单位星级完全可用，且全程纯读取、零 hook。**

## 30. 第三次验证（三个 2 星）+ 升星时实体重建

用户声明"现在有三个两星"，实测：

```
 idx  pos      heroConfID star  base      entityID
 0    (3,2)   15278      1star base=15278  0x927c1
 1    (1,3)   21408      2star base=11408  0x927e5
 2    (2,3)   21409      2star base=11409  0x927ec
 3    (3,3)   21402      2star base=11402  0x92803
 4    (1,1)   13324      1star base=13324  0x927e4
```

`m_mapHeroStar` 条目 `{key=2, value=3}` —— **一致**。

### 升星会重建实体（重要）

同一格子 (3,3) 前后两次读取：

| 时刻 | heroConfID | star | entityID |
|---|---|---|---|
| 升星前 | 11402 | 1 | 0x927df |
| 升星后 | 21402 | 2 | 0x92803 |

**游戏在合成时销毁旧实体、创建新实体，`entityID` 随之改变。**

影响：

1. 缓存的实体地址在升星后失效
2. 不能跨回合复用棋盘元素地址，必须每轮重新读 `vecBattleHeroPos`
3. 用 `entityID` 追踪同一单位也不可靠，升星后会变

自动合成/自动卖牌的实现必须每轮重新枚举棋盘。

---

## 31. 商店读取（已打通）

### 31.1 位置

商店**不在** `CPlayer+0x50 StoreController` 里（那是个 30 字段的子商店聚合器）。英雄商店在：

```
CPlayer + 0x450  m_stTACBuyHero -> Z_PVE.CTAC_HeroMgr
   +0x20  m_mapHeroMgr    LinkedHashMap`2
   +0x28  m_iMaxHeroNum   = 5     <- 商店格数
   +0x2c  m_iPosType      = 3
   +0x30  m_lUsedPos      = 0x1f  <- 位掩码，5 格全占
```

### 31.2 容器链

```
map + 0x10  entries 数组          + 0x40 internalCount (=5)
entries 元素从 +0x20 开始，步长 0x20：
   +0x00 hash(=slot)   +0x04 next   +0x08 prev   +0x0C seq
   +0x10 CTAC_Hero*    +0x18 slot

Z_PVE.CTAC_Hero
   +0x10 m_stHeroEntity (TAC_HeroEntity*)
   +0x1c m_iPos
   +0x20 m_iBuddChildCnt (无伙伴时为 -1)
   +0x24 m_iSrcPos
   +0x28 m_bIsRealHero
   +0x29 m_bIsPartnerHero
   +0x2a m_iDragonEggsCfg

TAC_HeroEntity
   +0x10 _iEntityID
   +0x14 _iHeroConfID   <- 星级编码在这里
```

### 31.3 费用解码

配置表实测边界（`CTAC_HeroKuGroup.m_mapHeroQuality`）：

| 费用 | 基础 ID 区间 |
|---|---|
| 1 费 | 11400 ~ 12399 |
| 2 费 | 12400 ~ 13319 |
| 3 费 | 13320 ~ 14329 |
| 4 费 | 14330 ~ 15269 |
| 5 费 | 15270 ~ 15403 |

### 31.4 工具

```
jccshop <pid> <cplayer_addr>
```

实测输出：

```
CTAC_HeroMgr 0x6d8ec71d20  m_iMaxHeroNum=5  m_iPosType=3  m_lUsedPos=0x1f
map 0x6d8ec70770  entries=0x7079a70dd0  internalCount=5

 slot  pos  heroConfID  star  base   cost  entityID
 0     0     11412       1     11412  1     0x9280a
 1     1     11407       1     11407  1     0x9280b
 2     2     11405       1     11405  1     0x9280c
 3     3     12416       1     12416  2     0x9280d
 4     4     12414       1     12414  2     0x9280e
```

### 31.5 注意事项

- 商店刷新后 `CTAC_Hero` 对象地址**全部重建**（实测两次读取之间对象地址完全变了），必须每次重读，不能缓存
- 商店卡永远是 1 星（`heroConfID` 落在 11400~15403），星级解码对商店卡恒为 1
- `m_lUsedPos` 的位掩码可以判断有几格有卡（空商店/锁定状态）

---

## 32. 商店读取已用真机对照验证

商店读出 5 张卡，费用分布 **3 张 1 费 + 2 张 2 费**，用户看屏确认"对的"。

同时截图可读出英雄名，建立了首批 ID→名字对照：

| heroConfID | 英雄 | 费用 |
|---|---|---|
| 11412 | 布里茨 | 1 |
| 11407 | 烬 | 1 |
| 11405 | 璐璐 | 1 |
| 12416 | 艾希 | 2 |
| 12414 | 科加斯 | 2 |

## 33. CPlayer 状态字段校正（重要修正）

之前文档里的 `hp@0x38c / exp@0x398` 标签中，**`0x398` 是错的**。用截图 + 名次相关性重新校验：

### 已验证

| 偏移 | 含义 | 证据 |
|---|---|---|
| `0x370` | 初始/最大血量 = 100 | 8 名玩家恒为 100 |
| **`0x38c`** | **当前血量** | 与 8 人名次完全单调对应 |
| **`0x390`** | **等级** | 截图显示等级 6，读出 6 |
| **`0x39c`** | **金币** | 截图显示金币 107，读出 107 |

血量与名次的实测对应（同一次读取）：

```
名次 1 chair3 hp=100
名次 2 chair7 hp= 85
名次 3 chair1 hp= 76
名次 4 chair5 hp= 66
名次 5 chair2 hp= 64
名次 6 chair0 hp= 54
名次 7 chair4 hp= 48
名次 8 chair6 hp= 30
```

### 仍未确定

`0x398`：8 名玩家取值 58~151，无单调规律，不是经验也不是血量。已验证过它不是等级、不是金币、不是血量。

### 教训

早期 `CPlayer` 字段表输出在终端被截断，`0x38c~0x39c` 的字段名（`m_iFakeLife` 等）**从未真正读到过**，当时的标签是补出来的。现已改为只标已验证项，未确定项直接标偏移名 `F398`。

**屏幕对照是最可靠的验证手段**：金币、等级这类数值可以直接肉眼核对。

---

## 34. 商店刷新概率模型（已打通）

### 34.1 完整数据链

```
CPlayer + 0x390            等级
CPlayer + 0x38             _heroPool -> Z_PVE.CTAC_HeroPool
   +0x18  m_stHeroKuGroup -> CTAC_HeroKuGroup
        +0x18  m_mapQualityHeroKu -> map<费用, map<heroConfID, CTAC_HeroKuNode*>>
   +0x20  m_mapLevelWeigth    -> map<等级, map<费用, 权重int>>

Z_PVE.CTAC_HeroKuNode
   +0x10  m_iHeroConfID
   +0x14  m_iHeroQuality     (费用档 1~5)
   +0x18  m_iWeight          <- 抽取权重
   +0x1c  m_iCount           <- 池内剩余份数
   +0x20  m_iInitCount       <- 初始份数
```

### 34.2 等级 → 费用概率（实测 6 级）

```
cost 1 -> 30
cost 2 -> 40
cost 3 -> 25
cost 4 -> 5
cost 5 -> 0
```

与游戏界面显示的 `30% 40% 25% 5% 0%` **完全一致**，验证通过。

### 34.3 抽取公式

游戏商店 = 5 个独立格子，每格两步：

1. 按等级权重掷出费用档：`P(cost = c) = lw[c] / 100`
2. 在该费用档内按英雄权重抽取：`P(hero = h | cost = c) = weight[h] / Σ weight[c]`

因此单个格子命中任意目标英雄：

```
p_slot = Σ_c  ( lw[c]/100 ) × ( Σ weight[targets in c] / Σ weight[all in c] )
```

整店 5 格至少出一个：

```
p_shop = 1 - (1 - p_slot)^5
```

期望刷新次数 `= 1 / p_shop`，期望金币 `= 2 / p_shop`（刷新一次 2 金）。

### 34.4 实测算例

等级 6，目标 11412（布里茨，1 费）：

```
cost 1 权重总和 1400，目标权重 100  ->  档内占比 7.14%
p_slot = 30% × 7.14% = 2.14%
p_shop = 1 - (1 - 0.0214)^5 = 10.2%
期望刷新 9.8 次 ≈ 20 金
```

### 34.5 工具

```
jccodds <pid> <cplayer> <基础英雄ID> [更多ID...]
```

- 基础英雄 ID 为 1 星形态（11400~15403），2/3 星形态会自动归一
- 输出：各费用档权重占比、目标剩余份数、单格/整店命中率、期望刷新次数与金币

### 34.6 由此可做的决策

1. **现在该刷还是该升级**：分别计算当前等级与 `等级+1` 的 `p_shop`，对比单位金币收益
2. **该不该刷**：金币 ≥ `2 / p_shop` 时成功率过半
3. **池子被抢**：目标英雄 `m_iCount` 下降会直接降低 `weight`（权重要么等于剩余份数，要么与之同比例），命中率随之下降
4. **多目标**：同时传入多个基础 ID，按权重合并计算

### 34.7 注意

- `m_iWeight` 是游戏实际使用的抽取权重，直接用它算，不需要自己按份数建模
- 每次读取都取当前快照；池内份数会随其他玩家购买而变
- 7 级及以上 4/5 费权重上升，低费下降，切换等级后需重算

---

## 35. 概率计算器（最终版）与真机验证

### 35.1 双重验证通过

同一时刻、同一等级：

| 来源 | 结果 |
|---|---|
| 内存读 `m_mapLevelWeigth` | `c1=75% c2=25% c3=0% c4=0% c5=0%`（等级 3） |
| 游戏界面显示 | `75% 25% 0% 0% 0%` |

**完全一致。** 另外 5 张商店卡的 `heroConfID` 与截图英雄一一对应：

| 槽 | heroConfID | 英雄 | 费用 |
|---|---|---|---|
| 0 | 11410 | 凯特琳 | 1 |
| 1 | 11412 | 布里茨 | 1 |
| 2 | 11407 | 烬 | 1 |
| 3 | 12409 | 雷克塞 | 2 |
| 4 | 12417 | 厄斐琉斯 | 2 |

已知 ID→名字对照累计：11405 璐璐、11407 烬、11410 凯特琳、11412 布里茨、
12409 雷克塞、12414 科加斯、12416 艾希、12417 厄斐琉斯。

### 35.2 工具输出

```
jccodds <pid> <cplayer> <基础ID> [更多ID...]
```

实测（等级 3，目标 11410+11412，都是 1 费）：

```
cost | now odds  targetWeight/allWeight  share  | +1 odds
  1  |   75%      200    / 1400          14.29%  |   55%
  2  |   25%      0      / 1300           0.00%  |   30%
  3  |    0%      0      / 1400           0.00%  |   15%

targets (remaining/init):  11410(c1 27/30)  11412(c1 28/30)

== now (level 3) ==
  per-slot : 10.714%
  per-shop : 43.257%

  refreshes  gold  P(at least one)
     1         2       43.26%
     2         4       67.80%
     3         6       81.73%
     4         8       89.63%
     5        10       94.12%
     ...
  gold for 50% confidence : 3
  gold for 90% confidence : 9

== after level up (level 4) ==
  per-shop : 33.579%
```

### 35.3 关键结论：升级会降低低费卡命中率

同一目标下，**3 级 43.26% / 4 级 33.58%**，升级让命中率下降 22%。

原因：1 费概率 75% → 55%，而多出来的 15% 三费概率对目标毫无帮助。

**决策规则**：

- 目标是 1~2 费牌 → **不要升级**，压等级反复刷新最划算
- 目标是 3 费牌 → 3 级概率 0%，必须先升到 4 级（15%）或 5 级
- 目标是 4~5 费牌 → 必须高等级，低等级概率为 0

### 35.4 使用方法

1. 先查商店有什么：`jccshop <pid> <cplayer>`
2. 确定要追的英雄基础 ID（可用商店/棋盘读到的 ID，或已知对照表）
3. 算概率：`jccodds <pid> <cplayer> <ID...>`
4. 按「gold for 90% confidence」决定攒多少钱再开始刷

### 35.5 假设与限制

- 假设 5 个格子相互独立（游戏允许同店出现重复英雄，故成立）
- 池内份数每次实时读取，其他玩家买牌后概率会下降
- 刷新成本固定 2 金（截图确认）
- 未考虑「锁定商店」影响

---

## 36. 商店自动化：触摸注入与自动买卖

### 36.1 坐标（2780x1264 横屏，实测）

```
商店 5 格：y = 300，x = 573 / 1059 / 1545 / 2031 / 2517
刷新按钮：(2595, 830)
金币/商店开关：(2595, 1130)
买经验： (150, 1100)
```

**测量方法很重要**：早期我按聊天里缩放后的预览图估算，得出的 (496,828,1160,...) 和刷新 (1845,640) **全是错的**。正确做法是把原始 PNG 用 `crop.py` 裁出小区域放大并叠加 100px 网格，读绝对像素坐标。

### 36.2 触摸注入：内核路径不可用

```
drv_touch_init(2780,1264) rc=0        virtual input device created (10 -> 11)
drv_touch(DOWN/MOVE/UP) rc=0          所有 ioctl 返回成功
```

但**事件到不了应用层**。已排除：

| 尝试 | 结果 |
|---|---|
| range 2780x1264 + 横屏坐标 | 无反应 |
| range 1264x2780 + 四种旋转映射 | 全部无反应 |
| 加入 DOWN→60ms→MOVE→30ms→UP 时序 | 无反应 |
| 对照：Android `input tap` 同坐标 | **立即生效** |

结论：该驱动的触摸注入在此设备上不工作（虚拟设备创建成功但不投递到应用）。
**当前 jccauto 默认走 `input tap`**，`--drv` 可切回内核路径。

代价：`input tap` 会短暂拉起一个进程，属于可见痕迹。内核路径待修。

### 36.3 自动循环

```
jccauto <pid> <cplayer> <基础英雄ID> [...] [--dry] [--reserve N] [--maxrolls N] [--maxbuy N]
```

逻辑：

1. 读商店 5 格 + 金币
2. 有目标且金币够 → 点该格
3. 否则金币 ≥ reserve+2 且未超 maxrolls → 点刷新
4. 每次操作后**轮询等待状态真正变化**（最多 8×200ms），连续 3 次无变化则停止

实测跑通（目标 11407）：

```
gold=30 shop: 11407(c1) 11413(c1) 12406(c2) 12402(c2) 12402(c2)
  -> BUY slot 0 (base 11407 cost 1) at (573,300)
...
gold=13 shop: 11407(c1) 11407(c1) 11407(c1) 11400(c1) 12401(c2)
  -> BUY slot 0 -> BUY slot 1 -> BUY slot 2      (一次买到 3 张)
```

### 36.4 第一版的教训

初版没有状态等待和限额，导致：

1. 前 5 轮读到的是同一份旧商店，重复下了 5 次购买指令（实际只成交 1 次）
2. 一路刷新把金币从 30 烧到 4

修正：每次 tap 后轮询签名变化；加 `--reserve`（默认 2）、`--maxrolls`（默认 8）、`--maxbuy`（默认 9）。

### 36.5 注意

- 选秀（carousel）回合商店不可操作，自动化需避开
- 回合结束会自动刷新一次商店，调试时不要把它误判成自己点的
- 免费刷新次数为 0 时才扣金币

---

## 37. 内核触摸注入：已修复并跑通

### 37.1 真因：坐标变换

虚拟触摸屏的 raw 范围是 **20224 x 44480**（= 1264*16, 2780*16），显示器视口是 `ROT_270`。

```
RawToDisplay Transform: 0.0625 (=1/16)
Viewport transform: (ROT_270)  x'=y,  y'=-x+1264
```

正确换算：

```
raw_x = 16 * (1264 - screen_y)
raw_y = 16 * screen_x
```

**验证**：点卡 (573,300) → raw (15424, 9168)，卡被买走、金币 -1。

> 早期一直点在屏幕左上角，因为直接发了屏幕坐标（2595,830），系统按 1/16 换算后落在 (162, 52)。

### 37.2 第二个必要条件：商店必须是打开的

商店可以隐藏（点右下角金币区域切换）。**商店隐藏时，任何 shop 区域的点击都不会生效。**

关键教训：连续多次"点击无效"的排查中，有一半是因为商店被前面的测试点关掉了，另一半是因为处在不可操作阶段。

### 37.3 第三个必要条件：当前阶段可操作

`CSoGame.m_CurrTurnState` @0xb0：

| 值 | 阶段 |
|---|---|
| 3 | **正常（可购物）** |
| 5 / 6 / 8 / 11 | 强化符文选择等过渡阶段，商店不可操作 |
| — | 选秀（carousel）同样不可操作 |

自动化前应确认 `turnState == 3`，且商店处于展开状态。

### 37.4 三个 bug 的修复

| 问题 | 现象 | 修复 |
|---|---|---|
| 循环路径未调用 `drv_touch_init` | 只有 `--tap` 能点，循环里点了无效 | 循环开始时 init 并等 1.2s |
| 卡片数组下标 ≠ 槽位 | 买到 slot0 后再点仍是 slot0 坐标（已空） | 用 `CTAC_Hero.m_iPos` 定位坐标 |
| 盲目切换商店 | ensure-open 把商店关掉且回不来 | 默认不碰开关，需要时 `--toggle` |

### 37.5 最终实测输出

```
jccauto 9447 0x6d9da5a000 11400 --reserve 50 --maxrolls 3 --maxbuy 3

touch_init(20224,44480) rc=0
gold=154 shop: 11400(c1) 12406(c2) 13321(c3) 13475(c3)
  -> BUY pos 1 (base 11400 cost 1) at (1059,300)
gold=153 ...
  -> REFRESH at (2595,830)
gold=153 shop: 11408(c1) 12402(c2) 12416(c2) 12416(c2) 13325(c3)
  -> REFRESH at (2595,830)
gold=153 shop: 11402(c1) 11400(c1) 11411(c1) 11402(c1) 13328(c3)
  -> BUY pos 1 (base 11400 cost 1) at (1059,300)
gold=152 ...
  -> stop: maxrolls 3 reached

done: buys=2 rolls=3
```

**买到 2 张目标卡，刷新 3 次，限额正确生效，全程内核 input_event 注入、零进程。**

### 37.6 使用前置条件

1. 游戏在前台，`m_CurrTurnState == 3`
2. **商店处于展开状态**（点右下角金币区域切换）
3. 有足够金币/免费刷新次数

`jccauto` 默认不再自动切换商店；需要时加 `--toggle`。
