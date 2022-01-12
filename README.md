# 自动方舟（auto-ark）

自动方舟是一款仅为学习交流使用的的明日方舟辅助软件，致力于**全自动**化。

与其它的辅助软件不同，自动方舟是被设计在服务器（Windows Server）上工作的。自动方舟没有 GUI，所有交互均由配置文件管理，这使得它在与 Task Scheduler 一同使用时尤为方便。

如果你对于本软件有什么问题或建议，请提一个 issue 或 pull request！

# 使用方式

> 自动方舟仅为学习交流使用，因此不提供编译完成的 release 版本下载。

对照文档，修改配置文件和缓存文件即可。

建议的配置文件修改方式：不直接修改 `base-config.toml`，而是复制 `base-config.toml` 为 `config.toml`，然后修改 `config.toml`
。这样做的好处是，你的所有配置都是基于 `base-config.toml` 做的修改，当想要重置默认设置时，只需要删除 `config.toml` 即可。

缓存文件为 `cache.yaml`，记录了会随着自动方舟运行而不断变化的数据。它只有在第一次完整运行自动方舟之后才会生成。

## 编译方式

环境要求：JDK >= 17。

首先，克隆本仓库：

```shell
git clone --recurse-submodules --depth 1 https://github.com/FlandiaYingman/auto-ark.git
cd auto-ark
```

然后使用 `gradlew` 编译：

```shell
./gradlew build
```

若要启动，则：

```shell
./gradlew run
```

# 目前支持的功能

首先，自动方舟会启动模拟器（见下“模拟器配置”）。

```toml
# 要登录的服务器
# "OFFICIAL" 官服
# "BILIBILI" B服
server = "OFFICIAL"
# 缓存文件路径
cacheLocation = "cache.yaml"
# 是否强制登录。
# 如果为 false，自动方舟在启动时会检查模拟器上正在运行的应用是否为明日方舟，若是，则复用。
# 如果为 true，自动方舟无论如何都会（重新）启动明日方舟。
forceLogin = true
```

## 更新（Update）

自动更新明日方舟。在大版本更新时，自动方舟会自动下载最新的明日方舟安装包并自动安装；在小版本更新时，自动方舟会等待其更新完毕。

## 登录（Login）

自动登录明日方舟。本模块的登录不包括输入账号及密码等隐私功能（考虑到安全性问题），而是使用此前已登陆过的账户进行操作。如果需要多账号操作，请使用模拟器自带的多开功能。

## 行动（Operate）

行动，翻译成人能听得懂的话就是刷关卡。目前支持的关卡有：作战记录、龙门币、技巧概要、四种芯片及芯片组、主线 1-7、剿灭作战（龙门外环）和大部分活动关卡的后两到三关（蓝色材料）。

本模块有四个可配置选项。

```toml
[operateConfig]
# "WAIT" 不使用任何恢复理智的道具。
# "POTION" 使用应急理智顶液和应急理智合剂等**非源石**道具恢复理智。
# "IFF_EXPIRE_SOON" 仅当即将过期时，使用应急理智顶液和应急理智合剂等**非源石**道具恢复理智。
# "ORIGINITE" 使用源石恢复理智。隐含[POTION]，即：优先使用**非原石**道具，只有当不存在**非原石**道具时，才会使用源石。
strategy = "IFF_EXPIRE_SOON"

# 自动刷剿灭作战（每次启动一次）。若本周剿灭次数已到达上限，则不会继续。
doFarmAnnihilation = true
# 自动刷任务计划（见下文）。
doFarmPlan = true
# 自动刷日常副本。
# MONDAY -> LS_5
# TUESDAY -> CE_5
# WEDNESDAY -> CA_5
# THURSDAY -> CE_5
# FRIDAY -> CA_5
# SATURDAY -> CE_5
# SUNDAY -> CE_5
doFarmDaily = false
```

## 基建（RIIC）

自动收取基建生产；自动换班（排前面的干员优先）。此外，还会把所有线索全都扔给第一个好友。

## 商店（Store）

自动领取信用点，并且以自左往右、从上到下的顺序购买所有物品，直到信用不足。

## 任务（Mission）

自动领取所有能领取的任务奖励。

## 招募（Recruit）

> 警告：本模块不保证识别率达到 100%，同时也不为任何可能遗漏的“高级资深干员”标签负责。

自动“公开招募”。本模块以从左到右、自上往下的顺序遍历所有的“公开招募”槽位。其遵循以下原则：

- 若某个槽位最低可招募三星干员，且能够刷新标签，则刷新。
- 保证最低可能招募的干员稀有度最高。
- 若存在“高级资深干员”标签，不对该槽位做任何处理。

# 模拟器配置

为了稳定性，目前只支持 BlueStacks 5 国际版。目前支持 Hyper-V 版本。

```toml
[emulator]
# BlueStacks 的安装路径，默认情况下不用配置
blueStacksHome = "C:/Program Files/BlueStacks_nxt"
# BlueStacks 的资料路径，默认情况下不用配置
blueStacksData = "C:/ProgramData/BlueStacks_nxt"
# 要启动的 BlueStacks Instance。Hyper-V 版本可能需要修改为 Nougat 64
instance = "Nougat32"
# ADB 连接的地址。
adbHost = "127.0.0.1"
# ADB 连接的端口。0 为自动检测。
adbPort = 0
```