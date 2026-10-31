# 游戏内存结构图（实测，一加 PJX110 / 内核 6.1.57）

全部数据通过内核驱动 `access_process_vm` 读取得到。**不使用任何 hook、不修改游戏代码、不注入内存。**

---

## 1. 如何从一个类名找到字段表

`libil2cpp.so` 里保存着完整的元数据，类名和字段名都是**明文**。

### 1.1 Il2CppClass 布局（实测）

```
+0x00  const Il2CppImage*  image
+0x08  void*               gc_desc
+0x10  const char*         name          <- 类名，明文
+0x18  const char*         namespaze     <- 命名空间，明文
+0x20  Il2CppType          byval_arg     (16 字节)
+0x30  Il2CppType          this_arg      (16 字节)
+0x40  Il2CppClass*        element_class
+0x48  Il2CppClass*        castClass
+0x58  Il2CppClass*        parent
+0x78  FieldInfo*          fields        <- 字段表（本版本实测位置）
```

### 1.2 FieldInfo 布局（实测，每条 32 字节）

```
+0x00  const char*       name      <- 字段名，明文
+0x08  const Il2CppType* type
+0x10  Il2CppClass*      parent    <- 指回所属类，可用来校验表位置
+0x18  int32_t           offset    <- 字段在对象内的偏移
+0x1c  uint32_t          token
```

### 1.3 工具

```
jccfields <pid> <class_addr> [max]     打印某个类的完整字段表（名字 + 偏移）
jccscan  u64 <pid> <lo> <hi>           扫内存里落在区间的 8 字节值（找类指针/实例）
```

找一个类的实例：扫所有可写区，找首个 qword 等于该类指针的位置。

---

## 2. `Z_PVE.CPlayerGameStatistics`（204 字段中的关键项）

| 字段 | 偏移 | 说明 |
|---|---|---|
| `CSoGame` | 0x060 | 游戏对象 |
| `m_stOwner` | 0x068 | 指向 `Z_PVE.CPlayer`，实测恒等于统计实例 + 0x800 |
| `<Level>` | 0x0e8 | |
| `<LifeHp>` | 0x140 | |
| `<RemainMoney>` | 0x0fc | |
| `<Rank>` | 0x13c | 名次 |
| `<IsObserver>` | 0x148 | 观战标记，可用于过滤噪声 |
| `<AIID>` | 0x15c | |
| `<HumanCount>` | 0x0ec | |
| `<CombatPower>` | 0x164 | 战力 |
| `m_vecBattleHero` | 0x100 | 棋盘英雄数组 |
| `m_vecFetter` | 0x108 | 羁绊 |
| `<ListWaitHero>` | 0x110 | 备战席 |
| `<ListDropEquipment>` | 0x118 | 掉落装备 |
| `<RefreshCount>` | 0x0c4 | 刷新次数 |
| `<LockCount>` | 0x0c8 | 锁商店次数 |
| `m_mapHeroStar` | 0x1a0 | 英雄星级映射 |

---

## 3. `Z_PVE.CPlayer`（204 字段中的关键项）

| 字段 | 偏移 | 类型 | 说明 |
|---|---|---|---|
| `RealPlayerID` | 0x1a4 | int32 | 真实玩家 ID |
| `i8ChairId` | 0x248 | int8 | 座位号 0~7 |
| `i16PlayerId` | 0x268 | int16 | |
| `bTAC_IsAI` | 0x280 | bool | 是否 AI |
| `iBattleIndex` | 0x2d4 | int32 | |
| `iEnemyId` | 0x2d8 | int32 | 当前对手 |
| `iRank` | 0x2dc | int32 | 名次 1~8 |
| `iTAC_Level` | 0x2e0 | int32 | 真实等级 |
| `iExtraPopulation` | 0x2e4 | int32 | 人口上限加成 |
| `iTAC_Exp` | 0x2ec | int32 | 真实经验 |
| `m_iPlayerInitLife` | 0x370 | int32 | 初始血量 |
| `m_iPlayerLife` | 0x374 | int32 | 真实血量 |
| `m_deductedLife1` | 0x378 | int32 | |
| `m_deductedLife` | 0x37c | int32 | 累计扣血 |
| `m_outUserCount` | 0x380 | int32 | |
| `mirrorDeductLife` | 0x388 | int32 | |
| **`m_iFakeLife`** | **0x38c** | int32 | **显示用血量** |
| **`m_iFakeLevel`** | **0x390** | int32 | **显示用等级** |
| `m_iFakeExtraPopulation` | 0x394 | int32 | 显示用人口加成 |
| **`m_iFakeExp`** | **0x398** | int32 | **显示用经验** |
| **`m_iFakeMoney`** | **0x39c** | int32 | **显示用金币** |
| `bHasSelected` | 0x3a0 | bool | |
| `vecBattleHeroPos` | 0x3c0 | pointer | **棋盘英雄位置数组** |
| `_vecBattleHeroPos` | 0x400 | pointer | |
| `SurplusHeroList` | 0x3e0 | pointer | 备战席 |
| `m_stPBattleGround` | 0x448 | struct | 战斗场地 |
| `m_stTACBuyHero` | 0x450 | struct | |
| `m_stTACWaitHero` | 0x458 | struct | |
| `m_stStatistics` | 0x460 | pointer | 指回 `CPlayerGameStatistics` |
| `m_fCombatPower` | 0x474 | float | 战力 |
| **`m_iTAC_Money`** | **0x480** | int32 | 真实金币 |
| `EquipmentMgr` | 0x488 | pointer | 装备管理器 |
| `LootMgr` | 0x490 | pointer | |
| `LatestBattleHeroDic` | 0x4f0 | pointer | 最近战斗英雄表 |
| `fetterManager` | 0x250 | pointer | 羁绊管理器 |
| `_heroPool` | 0x38 | pointer | 英雄池 |
| `StoreController` | 0x50 | pointer | 商店 |

### 注意：`m_iFake*` 与真实值是两组

`m_iFakeLife / m_iFakeLevel / m_iFakeExp / m_iFakeMoney` 是**给 UI 显示用的表现值**，也是本项目早期用数值差分扫到的那一组。真实战斗值在 `m_iPlayerLife` / `iTAC_Level` / `iTAC_Exp` / `m_iTAC_Money`。

两者大部分时间相同，但需要判断真实状态时应读真实字段。

---

## 4. 一次读取全部玩家

```
jccplayer <pid> <statistics_class_addr>
```

实测输出（真实对局）：

```
class Z_PVE.CPlayerGameStatistics  (0x70b36f8000)

 chair AI  rank  hp   lv  exp   gold  CPlayer        board
 7     0   2     90   8   136   35    0x6dd44be800   board=0x6dd4490340
 5     0   4     69   7   80    1     0x6dd4868800   board=0x6dd486bfc0
 6     0   1     90   8   168   16    0x6dd4a3d800   board=0x6dd4a45f40
 4     0   3     78   8   144   66    0x6dd4c5b800   board=0x6dd4c11bf0
 2     0   7     44   7   85    52    0x6dd9dfe800   board=0x6dd9df3740
 3     0   6     52   7   104   12    0x6dd9ef9800   board=0x6dd9edc7c0
 0     0   8     30   8   146   53    0x6dda556000   board=0x6dda17ff00
 1     0   5     53   8   136   42    0x6dda3c0800   board=0x6dda3bc7c0

players: 8
```

### 过滤误命中的三条校验

扫"首个 qword == 类指针"会命中两类东西：真实实例，以及 il2cpp 元数据表里存放的类指针。用以下条件区分：

```
i8ChairId       ∈ [0,7]
iRank           ∈ [1,8]
m_iFakeLife     ∈ [0,100]
m_iFakeLevel    ∈ [1,10]
*(CPlayer)      是一个指向 0x7000000000~0x7100000000 的指针（自身类指针）
```

不加校验会多出约 90 行垃圾。

---

## 5. 跨局稳定性

| 项 | 结论 |
|---|---|
| 类指针 `0x70b36f8000` | 进程内稳定，换局不变 |
| 实例地址 | 每局重建 |
| `m_stOwner == 实例 + 0x800` | 两次实测成立 |
| 字段偏移 | 同版本内恒定 |

重定位方法：扫 `klass` 指针值找到实例（约 1 秒扫完可写区）。

---

## 6. 下一步待挖

| 内容 | 入口字段 |
|---|---|
| 棋盘英雄（id / 星级 / 站位） | `vecBattleHeroPos` @ 0x3c0 |
| 备战席英雄 | `SurplusHeroList` @ 0x3e0 |
| 商店 5 张卡 | `StoreController` @ 0x50 |
| 装备 | `EquipmentMgr` @ 0x488、`LootMgr` @ 0x490 |
| 羁绊 | `fetterManager` @ 0x250、`m_vecFetter` @ 0x108 |

每个都是 `List<T>` / `Dictionary` 指针，需要读 il2cpp 容器布局：
`List<T>` 通常是 `{ klass, monitor, T[] _items, int _size, int _version }`，`_items` 在 +0x10，`_size` 在 +0x18。
`Dictionary<K,V>` 布局更复杂，需要 `_buckets` / `_entries`。

英雄数据结构体尚未定位，定位方法：从 `vecBattleHeroPos` 读出 `_items` 指针和 `_size`，然后 dump 元素，对照 `heroId` 的取值范围即可确认。
