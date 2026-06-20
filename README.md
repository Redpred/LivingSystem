# LivingSystem

A Minecraft mod for **NeoForge 1.21.1** (Java 21).

中文名「生命系统」：为玩家提供一套类塔科夫（Tarkov-like）的分部位生命健康系统。

## 生命系统功能（已实现 v1）

- **6 个身体部位**：头部、躯体、左臂、右臂、左腿、右腿，各自有独立生命值
  （`com.livingsystem.body.BodyPart`）。
- **分部位受击**：受到伤害时按伤害来源的几何位置定位命中部位
  （弓箭等抛射物用弹体落点，爆炸用爆心；近战暂用按轮廓面积加权随机）。见
  `BodyPartLocator`。
- **分部位反应**：某部位生命值归零时触发对应负面效果（头→致盲+反胃，臂→挖掘疲劳，
  腿→缓慢，躯干→虚弱）。见 `event/CombatEventHandler`。
- **健康栏界面**：原版背包上方新增标签栏，第一栏为原版背包、第二栏为健康栏；
  按 **H** 键也可直接打开。左上角四分之一区域用 6 个方块绘制小人，颜色按各部位
  健康度（绿→黄→红）显示，悬停显示具体数值；右半部列出各部位数值条。
- **按键绑定**：H 键已注册到 设置 → 按键绑定 的「生命系统」分类下，可自由改键。
- **数据存储与同步**：部位生命值用 NeoForge 数据附件（`ModAttachments.PLAYER_HEALTH`）
  存档，并通过 `SyncBodyHealthPayload` 同步到客户端供界面渲染。

> 设计说明：v1 中原版血量保持不变，部位生命值作为**平行系统**追踪，便于先跑通整套
> 链路。后续迭代计划把原版血量整体替换为部位驱动（含死亡/出血/回血等）。

## 默认操作

| 操作 | 默认键 |
| --- | --- |
| 打开健康栏 | H（可在按键设置中修改） |
| 关闭健康栏 | Esc / E（背包键） |
| 在背包/健康栏之间切换 | 点击界面上方标签 |

## 项目结构

```
LivingSystem/
├── build.gradle                 # ModDevGradle (MDG) 构建脚本
├── settings.gradle              # Gradle 设置与插件仓库
├── gradle.properties            # 版本号 / 模组元数据（在这里改 MC、NeoForge 版本）
├── gradlew / gradlew.bat        # Gradle Wrapper 启动脚本
├── gradle/wrapper/
│   ├── gradle-wrapper.properties
│   └── gradle-wrapper.jar       # ⚠️ 需补充（见下方说明）
└── src/main/
    ├── java/com/livingsystem/
    │   ├── LivingSystem.java     # 主类（@Mod 入口）
    │   ├── Config.java           # 配置（ModConfigSpec）
    │   └── registry/
    │       ├── ModItems.java         # 物品注册
    │       ├── ModBlocks.java        # 方块注册（含 BlockItem）
    │       └── ModCreativeTabs.java  # 创造模式物品栏
    ├── templates/META-INF/
    │   └── neoforge.mods.toml    # 模组描述（构建时替换占位符）
    └── resources/
        ├── pack.mcmeta
        ├── assets/livingsystem/
        │   ├── lang/{en_us,zh_cn}.json
        │   ├── models/{item,block}/example_*.json
        │   └── blockstates/example_block.json
        └── data/livingsystem/loot_table/blocks/example_block.json
```

## ⚠️ 缺一个二进制文件：gradle-wrapper.jar

由于当前环境无法生成二进制文件，`gradle/wrapper/gradle-wrapper.jar` 还没有。
用以下任一方式补齐后即可构建：

1. **用 IntelliJ IDEA 打开项目**（推荐）：选择 "Open"，IDEA 会自动按 `gradle-wrapper.properties`
   下载 Gradle 8.10.2 并补全 wrapper。
2. **已装 Gradle 时**，在项目根目录执行：
   ```
   gradle wrapper --gradle-version 8.10.2
   ```
3. 从任意已有的 Gradle 8.10.2 项目复制 `gradle/wrapper/gradle-wrapper.jar` 过来。

## 常用命令

```
./gradlew build           # 打包模组 jar（输出在 build/libs/）
./gradlew runClient       # 启动开发客户端
./gradlew runServer       # 启动开发服务端
./gradlew runData         # 运行数据生成（输出到 src/generated/resources）
```

## 缺少的贴图

`example_item` / `example_block` 引用了贴图但还没有 PNG，需自行添加：
- `src/main/resources/assets/livingsystem/textures/item/example_item.png`
- `src/main/resources/assets/livingsystem/textures/block/example_block.png`

在补齐贴图前，物品/方块会显示为紫黑方块（缺失贴图），但不影响加载和运行。

## 改版本

所有版本集中在 `gradle.properties`：`minecraft_version`、`neo_version`、`parchment_*`。
最新可用的 NeoForge 1.21.1 版本可在 https://projects.neoforged.net/neoforged/neoforge 查询。

构建插件用的是 **ModDevGradle (MDG)**，在 `build.gradle` 的 `plugins {}` 块里：
`id 'net.neoforged.moddev' version '2.0.78'`。若该版本无法解析（或想用更新版），
到 https://projects.neoforged.net/neoforged/ModDevGradle 查最新版本号，改这一行即可。
