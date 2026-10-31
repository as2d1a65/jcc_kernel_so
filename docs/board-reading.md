
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
