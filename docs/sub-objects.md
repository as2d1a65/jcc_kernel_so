# 棋盘与子对象：进一步探查记录

本文档记录 `CPlayer` 各子对象指针指向的类与字段，以及**尚未打通**的部分。

全部通过 `jccfields` / `jccd` 实测，纯读取。

---

## 1. 已确认的子对象

| CPlayer 偏移 | 字段名 | 指向地址（样本） | 类 | 关键字段 |
|---|---|---|---|---|
| +0x40 | `HeroPromotionMgr` | 0x6ff5f60150 | `Z_PVE.CTAC_HeroPromotionMgr` | `_cSoGame` @0x10、`m_mapHeroPromotionTree` @0x18 |
| +0x448 | `m_stPBattleGround` | 0x7023dab420 | `Z_PVE.TAC_PBattleGround` | `m_iRowDimen`、`m_iColDimen` |
| +0x250 | `fetterManager` | 0x6dda3f3700 | （未展开） | 羁绊 |
| +0x50 | `StoreController` | 0x6ff5ca62a0 | （未展开） | 商店 |
| +0x488 | `EquipmentMgr` | 0x6dda3d3960 | `Z_PVE.EquipmentMgr` | 见下 |
| +0x3c0 | `vecBattleHeroPos` | 0x6dda3bc7c0 | `List<TAC_BattleHeroPos>` | 见 board-reading.md |

### TAC_PBattleGround 的棋盘尺寸

字段表：`m_iRowDimen` @0x48、`m_iColDimen` @0x4c。

但实测在对象 +0x50 / +0x54 读到 `7` 和 `4`，正好是 TFT 的 7 列 × 4 行。说明该类的字段表起点不是对象起点（有基类字段占据 0x10~0x40）。**用字段表偏移时要先确认是否要叠加基类偏移。**

### EquipmentMgr 字段（部分）

```
DROP_ITEM_COLIDER_R   @0x00
CSoGame               @0x90
_player               @0x98
_hasCheckedMonsterIds @0xa0
_soulEquipment        @0xa8
_completeEquipment    @0xb0
_singleEquipment      @0xb8
_npcEquipmentDic      @0xc0
_promoteEquipments    @0xc8
_playerEquipments     @0xd0
DropPosDataMap        @0xf0
```

---

## 2. 棋盘实测（10 单位，对局中期）

```
 idx  pos      entity        heroConfID   entityID
 0    (1,0)   0x6dd4d20820   15397        0x186a1
 1    (0,3)   0x6fdc357dc0   22412        0x186f6
 2    (2,0)   0x7046ad2b40   12406        0x186f2
 3    (0,0)   0x6dba58bea0   24344        0x18707
 4    (1,3)   0x6dd4a3f140   23324        0x18719
 5    (3,3)   0x70472e37c0   24349        0x1871b
 6    (4,3)   0x6da79512a0   15296        0x18720
 7    (2,3)   0x7047398440   15402        0x1872b
 8    (2,2)   0x6d9efdddc0   18453        0x1872c
 9    (5,3)   0x7024917f20   14352        0x18743
```

坐标范围 x∈[0,6] y∈[0,3]，与 `TAC_PBattleGround` 的 7×4 一致。

---

## 3. 尚未打通的：英雄星级

### 试过的三条路

**路 1：英雄实体自身**
`ZGameClient.TAC_HeroEntity` 的字段表只有两个字段：

```
+0x10  _iEntityID
+0x14  _iHeroConfID
```

对象 +0x60 处的指针是**共享**的（10 个实体只有 4 种不同值），说明那是配置/类引用而不是实例数据。一度怀疑 +0x70=1 / +0x74=3 是星级，但换一批实体后不成立，判为巧合。

**路 2：父类**
`class+0x58` 读出来是 `0x2940` 这种小值，不是类指针。**该版本 Il2CppClass 的 parent 偏移不是 0x58**，需要重新确认。确认方法：遍历类结构前 0x100 字节，把每个 qword 当作潜在类指针，检查其 `+0x18`（namespaze）能否读出 ASCII 字符串——能读出的就是真类指针。

**路 3：`CTAC_HeroPromotionMgr.m_mapHeroPromotionTree`**

```
mgr  @ CPlayer+0x40   = 0x6ff5f60150
+0x10  _cSoGame              = 0x704771d540
+0x18  m_mapHeroPromotionTree = 0x6dda3d6540   (Dictionary)
```

字典内部：

```
Dictionary 0x6dda3d6540
  +0x10  _buckets = 0x6d9e453660
  +0x18  _entries = 0x6d9e5ecf80
  +0x20  0x712317b620      <- 不是 int 的 _count，说明此 Dictionary 变体布局与标准不同
```

`_entries` 数组 `max_length = 7`，但元素区里出现了 UTF-16 字符串 `"ChessBattleUnit"`（长度前缀 0x0f），说明**条目布局不是标准的 `{int hashCode; int next; K key; V value;}` 24 字节**。这块需要按实际数据反推元素大小，不能套标准布局。

### 建议的下一步

1. **先定 Il2CppClass 的 parent 偏移**（方法见上），拿到父类链就能看 `TAC_HeroEntity` 的基类是否有星级字段。
2. **反推 `_entries` 元素大小**：数组起点 `0x6d9e5ecfa0`，`max_length=7`，下一个数组头在 `0x6d9e5ed000`，所以 7 个元素占 `0x6d9e5ed000 - 0x6d9e5ecfa0 = 0x60 = 96` 字节 → **元素大小 96/7 ≈ 13.7，不是整数**。说明 `max_length` 不是 7，或元素起点不是 +0x20。需要用 `jccd dump` 在这一小段里定位真正的元素边界。
3. **或者绕开**：星级在服务端也会下发到客户端的英雄列表，可以从 `CPlayer.LatestBattleHeroDic` @0x4f0 或 `HeroMaxEquipmentCountDic` @0x1d8 附近找同类字典。
4. **或者用行为验证**：合成两个同英雄时观察哪个字段 +1，直接对比前后快照即可定位星级字段——这是最省事的方法。

---

## 4. 工具使用要点

```
jccfields <pid> <class_addr>        打印完整字段表（名字 + 偏移 + token）
jccboard  <pid> <cplayer_addr>      打印棋盘（坐标 + 英雄 ID）
jccplayer <pid> <stats_class_addr>  打印全部 8 名玩家的战况
jccd dump <pid> <addr> <len>        hexdump
jccd ptr  <pid> <addr> <depth>      跟指针链
jccd str  <pid> <addr>              读 C 字符串
```

**注意**：`jccfields` 打印到字段名出现乱码即表示已越过字段表边界，那些行不是真实字段。
