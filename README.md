# LivingSystem（生命系统）

面向 **NeoForge 1.21.1 / Java 21** 的 Minecraft 模组。参考《逃离塔科夫》《僵尸毁灭工程》等游戏，
为玩家提供一套**服务端权威、数据驱动**的分部位健康系统：分部位受击、机械创伤、出血、疼痛、骨折、
全身生理循环、症状与游戏性输出，并替换原版生命/饥饿/回血/溺水语义。

> 完整功能与架构规格见 [`LivingSystem_开发文档.md`](LivingSystem_开发文档.md)（唯一主规格）。
> 本文件只做总览与上手说明。

## 架构总览

包根 `com.redpred.livingsystem`，按开发文档第二部分（§18–34）分层：

```
com.redpred.livingsystem
├─ bootstrap     注册：注册表 / 附件 / 数据组件 / 网络包 / 配置 / 内容
├─ api           对外兼容 API（damage/exposure/protection/treatment/examination/event）
├─ domain        领域模型：body / effect / physiology / symptom / treatment / recovery /
│                exposure / protection / medication / death（运行时可变状态 + 不可变 DTO）
├─ service       业务服务（接口 + Default 实现）：damage / hit / structure / physiology /
│                symptom / treatment / recovery / exposure / protection / examination /
│                resource / death / feature / scheduling
├─ rule          规则数据：definition / registry / codec / validation / reload / snapshot
├─ persistence   持久化：repository / migrator（玩家健康数据附件）
├─ network       网络：payload / handler / snapshot（StreamCodec + 协议版本 + 服务端校验）
├─ compatibility 兼容：vanilla（伤害拦截/资源桥接）/ adapter
├─ client        仅客户端：state / screen / hud / animation / sound / key / config
├─ content       物品 / 方块 / 方块实体 / 菜单
└─ command       /livingsystem 调试命令族
```

服务端是健康状态、伤势、治疗、死亡的唯一权威来源；客户端只接收只读快照用于 HUD、界面与表现。
公共/服务端代码不引用 `net.minecraft.client`，开屏等客户端入口经 `client.ClientHooks` 惰性隔离。

## 开发进度

- **阶段一（代码架构）**：完成。完整分层骨架、领域模型、服务接口与默认实现、规则快照、持久化、
  网络、客户端、命令均已建立，可启动客户端/专用服务端/数据生成。
- **阶段二（资源桥接与机械创伤闭环）**：完成。
  - 机械创伤：`LivingIncomingDamageEvent` 最低优先级拦截并清零原版伤害与减伤；
    命中部位解析（`HitLocationService`）→ 创伤生成（`HealthEffectFactory`）→ 结构损伤
    （`StructureDamageService`）→ 急性失血写入聚合根。
  - 出血动态：`PhysiologyEngine` 逐周期处理外/内出血、凝血（动脉更难凝）、凝块稳定与再出血。
  - 疼痛：创伤固化基础疼痛 → 循环平滑更新当前疼痛 → 汇总总疼痛（含全局倍率与镇痛扣减）→
    `pain`/`tremor` 症状 → 操作稳定度、镜头摇晃、移动/攻速惩罚。
  - 骨折：按伤害画像骨折概率（×全局倍率）做确定性判定，固化骨折等级/不稳定/移位并追加骨结构损伤 →
    `limp`/加重 `arm_impairment` → 移动/疾跑/跳跃/手部稳定惩罚。
  - 全身资源与呼吸：体力（活动消耗/休息恢复）、呼吸储备（缺氧消耗/出水恢复）、氧债与意识；
    溺水屏蔽（原版溺水不再扣血，呼吸由 `respiratoryReserve` 自管理），缺氧终末死亡。
  - 资源桥接与 HUD：原版生命/饥饿/空气钉哨兵、关闭自然回血；HUD 文字显示血液/体力/水分/呼吸；
    症状汇总为统一 `GameplayEffectSnapshot` 并经属性修饰器与客户端快照应用。基础死亡流程（失血/
    要害结构归零/缺氧终末 → 专用致死来源）闭环。
- **阶段三（治疗、恢复与医疗信息闭环）**：完成。
  - 治疗数据驱动：`TreatmentDefinition` 完整模型 + `treatment_definition/*.json`，共享 `RulesSnapshotBuilder` 协调多目录装配；6 个基础医疗物品（绷带/止血纱布/止血带/夹板/缝合包/消毒剂）。
  - 局部治疗闭环：右键医疗物品自疗 → 服务端校验/自动选目标 → 逐刻推进 → 按提交策略将预注册操作施加到出血/凝血/污染/骨折 → 静止中断 → 完成记录已应用治疗；HUD 进度条；网络 start/cancel 校验入口。
  - 药物与摄入：止痛药药代 → 镇痛接回疼痛汇总；食物补充代谢能量/营养/水分；基础代谢消耗（不致死，门控体力恢复）。
  - 自然恢复：综合恢复能力驱动造血、伤口愈合、结构修复、骨折降级；缝合加速、剧烈活动减缓、愈合完成移除伤势。
  - 医疗检查与界面：`MedicalExaminationService` 生成精确体征观察（自检入口）；健康主界面（H 键 → 服务端快照 → 七部位健康度/体征摘要/伤势列表）。
  - 昏迷与死亡报告：低意识 → 昏迷症状与游戏锁定；死亡按实际死因回溯生成报告并向玩家反馈。
- **阶段四（环境与防护闭环）**：进行中。
  - 4.1 环境温度→体温：按生物群系/岩浆/火/粉雪/浸水推算环境温度，护甲提供保暖，核心体温平滑趋近环境，派生发热/寒战症状与游戏影响。
  - 4.2 数据驱动环境危害：`EnvironmentalHazardProfile` + `environmental_hazard/*.json` + reload/registry，采样器按方块/流体/群系匹配；热/冷叠加温度、呼吸类（毒气/烟雾）消耗呼吸储备并累积氧债，毒素/辐射类累积剂量待阶段五。
  - 4.3 防护系统：`ProtectionProfile` + `protection_profile/*.json` + reload，`ProtectionResolver` 按穿戴装备以透过率连乘合并为唯一防护结果；创伤减免穿透严重度/结构损伤/伤口与骨折生成，环境减免隔热/呼吸过滤/辐射屏蔽。示例：防毒面具（呼吸过滤）、皮革护甲（隔热）。
  - 剩余：动态发射源（运行时临时危害源）。毒素/辐射暴露的健康影响并入阶段五。
- **阶段五及以后**：毒素/病原体/辐射/魔法、医疗设备/生产/兼容、平衡与发布。对应服务多为接口骨架，待逐步填充。

> 持久化现状：生理状态、各部位结构完整度、**活动创伤（含出血/骨折/疼痛/污染/异物/愈合组件与已应用治疗）** 均随玩家存档持久化（`PlayerHealthData.CODEC` 经附件序列化）。治疗会话、药物剂量、医疗观察与死亡报告目前为运行时态（重登录不保留，多为短时效数据）；其它 `HealthEffectInstance` 类型在对应系统接入时扩展为分发 Codec。医疗物品尚无模型/贴图（属阶段七资源补齐）。

## 常用命令

```
./gradlew compileJava     # 编译（验证用）
./gradlew build           # 打包模组 jar（输出 build/libs/）
./gradlew runClient       # 启动开发客户端
./gradlew runServer       # 启动开发专用服务端
./gradlew runData         # 运行数据生成
```

> Windows 下编译建议设 `JAVA_TOOL_OPTIONS=-Duser.language=en -Duser.country=US` 以规避编码问题。

## 调试

服务端配置 `debugChat`（默认开）会把命中/失血/骨折/死亡等关键事件输出到聊天框；
`debugCommands`（默认开）放开 `/livingsystem` 命令权限便于测试。发布前应关闭二者。

## 版本

版本集中在 `gradle.properties`（`minecraft_version`、`neo_version`、`parchment_*`）。
构建插件为 ModDevGradle（`build.gradle` 的 `plugins {}`）。
