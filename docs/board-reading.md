
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
