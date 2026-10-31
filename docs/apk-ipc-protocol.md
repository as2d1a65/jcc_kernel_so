# 工具 APK 的 IPC 协议分析（JCC_zpcnb666.apk）

目标是**复用它的界面、把数据来源换成内核读取**，全程不注入、不改游戏内存。

- 包名：`com.android.support`（伪装成 Android 支持库）
- minSdk 28 / targetSdk 37
- 入口：`com.android.support.LoginActivity` → `MainActivity`
- 安装后设备 UID：`10527`

## 1. 组成

```
classes.dex                  9.8 MB   UI（Kotlin）
lib/arm64-v8a/libdemo.so     5.7 MB   注入引擎（VMP 保护）
lib/arm64-v8a/libdemo_compat.so
assets/hex_icons/*           数百张海克斯图标
assets/hex_catalog.json      641 KB   数据驱动的界面资源
assets/equipment_table.txt / hex_augment_table.txt
```

关键类（`fw-decomp/com/android/support/` 下已反编译）：

| 文件 | 作用 |
|---|---|
| `FloatingWindowService.java` | 1.3 MB，整个悬浮窗 UI |
| `SharedMemManager.java` | 数据总线，所有 UI 都从它取数 |
| `LocalSocketIPC.java` | 与 native 的套接字协议实现 |
| `IpcV2*.java` | 协议各层（帧、token、握手、请求表） |
| `IpcPlayersParser` / `IpcHeroPoolParser` / `IpcShopSlotsParser` | 数据解析 |
| `SpecifiedShopControlBinder` | 「指定商店」功能的 UI 绑定 |

## 2. 通信拓扑

**APK 是服务端，native（注入进游戏进程的那段）是客户端。**

```java
serverGeneration2.localServer = new LocalServerSocket(SOCKET_NAME);
LocalSocket s = serverGeneration.localServer.accept();
```

| 项 | 值 |
|---|---|
| 正式 socket | `xf_game_control_v2` |
| 候选 socket | `xf_game_control_v2_candidate` |
| 测试 socket | `xf_game_control_v2_test` |
| TCP 端口起始 | 39816 / 39821 / 39826 |
| 协议版本 | 2 |

## 3. 帧格式

`IpcV2FrameCodec`：4 字节长度头 + UTF-8 JSON 负载，单帧上限 1 MB（`MAX_FRAME_BYTES`）。

消息体（`writeHandshake` / 常规帧）：

```json
{"v":2,"type":"request","id":"<16字节base64url>","seq":0,"method":"<name>","params":{...}}
```

`type` 取值：`request` / `response` / `state` / `event` / `ping` / `pong` / `error`

## 4. 认证

token 记录文件：

```
/data/user/0/com.android.support/app_ipc_v2/ipc_v2_current_token.json
/data/user/0/com.android.support/app_ipc_v2/ipc_v2_pending_token.json
```

目录由 `SharedMemManager` 用 `context.getDir("ipc_v2", 0)` 得到，文件权限 0600、属主为应用 UID。

记录格式（`IpcV2TokenStore.RECORD_PATTERN`）：

```json
{"v":2,"tokenEpoch":<正整数>,"token":"<43 字符 base64url，即 32 字节>"}
```

握手用 HMAC-SHA256（`IpcV2Protocol`）：

```
transcript  = BE{ int32 2, int64 serverGeneration, int64 connectionId, int64 tokenEpoch,
                  byte[16] challengeId, byte[32] serverNonce, byte[32] clientNonce,
                  byte transport(0=abstract,1=tcp) }
clientProof = HMAC-SHA256(token, "ipc-v2/cl" + transcript)
serverProof = HMAC-SHA256(token, "ipc-v2/sv" + transcript + sessionId)
```

> `domain()` 会给字符串末尾补一个 `\0`，实现时必须一致。

## 5. 准入检查（三道门）

```java
acceptAbstractPeer(pid, uid, selfPid, expectedPeerUid)
  -> uid % 100000 == expectedPeerUid % 100000  &&  pid != selfPid

acceptExpectedBootstrapPeer(pid, expectedBootstrapPeerPid)
  -> 客户端 PID 必须等于注入目标 PID（未注入时该值为 -1）

startServer()
  -> 先判 GameRegionState.current().isRuntimeReady()
```

`expectedPeerUid` 来自 `configure(dir, getApplicationInfo(游戏包).uid)`，
含义是**客户端必须拥有与被注入进程相同的 UID**（默认即游戏 UID 10321）。
以 root 运行客户端后 `setuid(10321)` 即可满足。

## 6. 数据方法名（native → UI 推送）

```
connected
match.started / match.ended
updatePlayers                   玩家血量/等级/金币/人口
updateHeroPool                  共享牌池
updateShopSlots                 商店 5 格
updateMyOwnedHeroes             自己阵容
updateBoardPositions            棋盘位置
updateBoardHeroAnchors
updateOpponentCardingStatus
updateOpponentDiagnostics
updateRankAnchors
updateMatchup
updateHexPrediction
updateSpecifiedShopStatus
updateSpecifiedShopPreview      指定商店预览
recast.specified.status / recast.pool.catalog
runtime.launchStatus / runtime.hookStatus
toast
```

完整清单见 `IpcV2PushMethods.java`，反向的 UI→native 操作方法见 `IpcV2ActionMethods.java`。

## 7. 实测进展

1. APK 已安装（`com.android.support`）。
2. 已确认私有目录可读写：`/data/user/0/com.android.support/app_ipc_v2`（root + `su` 域可访问）。
3. **已自行生成并写入 token**（`tools/mktoken.py`），属主与权限按应用要求设置：
   ```
   -rw------- 1 u0_a527 u0_a527 76 ipc_v2_current_token.json
   ```
   这样客户端身份完全由我们掌握，**不需要启动它自带的注入流程**。
4. 启动 `MainActivity` 后，`/proc/net/unix` 中**尚未出现** `xf_game_control_v2`。
   说明 `startServer()` 未通过 `GameRegionState.isRuntimeReady()`，服务端还没起来。
   下一步需要在游戏运行且被识别为就绪时再观察，或从 smali 层确认这条判定的具体内容。

## 8. 下一步

1. 确认 `GameRegionState.isRuntimeReady()` 的判定条件（`GameRegionState.java`）。
2. 服务端起来后，用 C++ 实现客户端：
   - `setuid(10321)` → 连接抽象命名空间 `xf_game_control_v2`
   - 读 token 文件 → 完成 HMAC 握手
   - 只推送 `updatePlayers` / `updateHeroPool` / `updateShopSlots`
   - **不响应任何修改类方法**（`game.modifyGold` 等一律拒绝），保持纯只读
3. 数据来自本项目已有的内核驱动工具（`jccdash` 同款读法）。

## 9. 注意

- 该 APK 的原始工作方式是**注入**（`libdemo.so`），风险高，本方案完全不使用它。
- 我们只借它的界面与协议，数据全部来自内核只读访问。
