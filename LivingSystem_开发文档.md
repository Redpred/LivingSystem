# LivingSystem（生命系统）完整开发规格

- 游戏版本：Minecraft 1.21.1
- 模组平台：NeoForge 1.21.1
- 文档用途：作为 Agent AI 实现 LivingSystem 的唯一主规格
- 实现顺序：先完成完整代码架构，再按阶段填充全部功能
- 语言规范：代码标识符使用英文；代码、配置、数据定义说明、日志和文档使用中文注释或中文文本

## 文档结构

```text
第一部分：完整功能规格
1～17：配置、身体结构、伤害、资源、生理循环、症状、防护、环境、治疗、UI、物品与完整闭环

第二部分：代码架构规格
18～34：分层、包结构、聚合根、规则、服务、事件、持久化、网络、客户端、性能、兼容与测试

第三部分：阶段任务
阶段一先建立完整代码架构，阶段二至阶段七按功能闭环逐步填充
```

## 文档执行规则

1. 本文中的“必须”“采用”“固定”“禁止”均为强制实现要求。
2. 功能规格描述完整版本目标，不因阶段任务而删减或降级。
3. 代码架构必须先于功能实现建立，后续功能只能通过既定服务边界接入。
4. 服务端是健康状态、伤势、治疗、暴露、死亡和游戏性限制的唯一权威来源。
5. 客户端只负责输入请求、动画、界面和只读快照显示。
6. 配置文件和数据包只定义规则与参数，不得改变系统不变量或绕过服务端校验。
7. 运行时可变状态使用可变类；配置对象、网络 DTO 和持久化 DTO 使用不可变 `record`。
8. 不得在事件监听器、物品类、伤势类或界面类中直接堆积跨系统业务逻辑。
9. 所有随机判定必须使用可复现的确定性随机种子。
10. 所有新增内容必须具有中文 JavaDoc、中文配置说明和中文错误信息。

# 第一部分：完整功能规格


## 1. 项目定位与核心原则

LivingSystem 参考《逃离塔科夫》《僵尸毁灭工程》等游戏中的健康系统，但不追求医学级人体仿真。系统只模拟对游戏玩法有明确意义的结构、状态和症状。

核心设计原则：

1. 不为身体部位和器官设置独立血条。
2. 不把所有伤害都解释为局部伤口。
3. 不把心率、血压、血氧等派生体征作为互相独立的资源值。
4. 保留原版及其他模组的伤害来源、护甲、附魔和减伤结算；LivingSystem替换玩家生命资源、伤势、生理循环、昏迷与死亡判定。
5. 服务端保存和计算真实健康状态；客户端只接收显示快照。
6. 功能实现必须数据驱动，允许整合包通过数据包适配武器、实体、方块、流体和伤害类型。
7. 症状只负责表达内部状态，不得反向直接修改内部健康状态。

系统完整链路：

```text
外部刺激：受击、环境暴露、病原体、毒物、辐射、玩家活动、治疗
→ 解析伤害或暴露上下文
→ 确定致伤机制
→ 确定局部身体部位或全身作用范围
→ 创建或更新健康影响实例
→ 更新组织结构状态
→ 运行全身生理循环
→ 计算继发后果
→ 生成症状快照
→ 汇总游戏性效果
→ 同步客户端并表现
```


## 2. 统一概念模型

LivingSystem 必须区分以下概念：

### 2.1 外部来源 `Cause`

表示伤害或暴露从哪里产生，例如：

- Minecraft `DamageType` 和 `DamageSource`
- 武器或投射物
- 攻击实体
- 危险方块和流体
- 毒素
- 病原体
- 放射源
- 环境缺氧、高温或低温

### 2.2 致伤机制 `HarmMechanism`

表示外部来源以何种机制影响身体，例如切割、钝击、穿透、缺氧、中毒和辐射。

### 2.3 健康影响实例 `HealthEffectInstance`

表示玩家实际获得的持续性健康影响，例如：

- 左臂切割伤
- 胸腔穿透伤
- 烧伤
- 溺水导致的呼吸损害
- 某种毒素暴露
- 某种病原体感染
- 辐射暴露

### 2.4 组织结构状态 `StructureState`

表示身体结构当前累计完整度和专用状态。它不是独立血量。

### 2.5 全身生理状态 `PhysiologyState`

表示血容量、体温、氧债、意识等全身动态状态。

### 2.6 派生体征 `DerivedVitals`

表示根据底层状态计算出的心率、血压、血氧、呼吸频率、灌注和休克程度。

### 2.7 症状 `Symptom`

表示玩家能够感受到或观察到的表现，例如疼痛、眩晕、呼吸困难、跛行、视野变暗和意识丧失。

### 2.8 游戏性输出 `GameplayOutput`

表示症状对 Minecraft 操作和画面的最终影响，例如移动速度下降、攻击速度下降、无法冲刺、镜头晃动、呼吸音和 HUD 提示。


## 3. 自定义数据、功能开关与难度配置

### 1. 设计目标

LivingSystem 不预设“简单、普通、困难”等固定难度等级。

模组实际难度由以下内容共同决定：

* 各健康系统是否启用；
* 各类伤势和症状是否启用；
* 伤势生成概率；
* 出血、疼痛、感染等状态的发展速度；
* 症状对玩家操作能力的影响程度；
* 治疗和自然恢复速度；
* 客户端画面与声音表现。

整合包作者能够通过服务端配置和数据包，自行组合出符合整合包玩法的难度规则，而不需要修改 LivingSystem 的 Java 源代码。

LivingSystem 应遵循以下原则：

```text
代码提供稳定的健康系统框架
数据文件定义具体伤势、症状和兼容规则
服务端配置控制系统开关和全局倍率
客户端配置只控制画面、声音和界面表现
```


### 2. 配置数据分类

LivingSystem 的配置数据分为以下四类：

#### 2.1 服务端规则配置

服务端配置负责控制会影响实际游戏结果的规则，包括：

```text
模组总开关
创伤系统开关
出血系统开关
骨折系统开关
疼痛系统开关
呼吸系统开关
体温系统开关
毒素系统开关
病原体系统开关
辐射系统开关
代谢系统开关
魔法异常系统开关
意识系统开关
症状游戏影响开关
治疗系统开关
自然恢复系统开关
```

同时允许配置全局倍率，例如：

```text
伤势生成概率倍率
结构损伤倍率
外出血速度倍率
内出血速度倍率
凝血速度倍率
疼痛强度倍率
感染概率倍率
病原体发展速度倍率
毒素代谢速度倍率
辐射剂量倍率
症状惩罚倍率
治疗效果倍率
自然恢复速度倍率
```

所有会影响实际游戏状态的配置必须以服务端为准。

客户端不得自行关闭骨折减速、攻击力下降、出血、感染或昏迷等实际游戏效果。


#### 2.2 数据包规则数据

数量较多、需要扩展或需要兼容其他模组的数据，应通过数据包定义，而不是写死在 Java 代码中。

数据包应支持配置：

```text
DamageType → DamageProfile
物品 → WeaponTraumaProfile
实体 → EntityAttackProfile
投射物 → ProjectileTraumaProfile
方块 → EnvironmentalHazardProfile
流体 → EnvironmentalHazardProfile

伤口定义
症状定义
毒素定义
病原体定义
辐射源定义
治疗物品定义
防护装备定义
结构损伤权重
症状触发条件
症状影响结果
```

其他模组增加武器、投射物、生物、方块、流体或伤害类型后，整合包作者应能够通过新增数据文件完成兼容，不需要修改 LivingSystem 源码。


#### 2.3 客户端表现配置

客户端配置只允许控制个人视觉、声音和界面表现，例如：

```text
是否显示健康HUD
是否显示身体部位状态
是否显示详细生命体征
是否显示伤口列表
是否启用镜头晃动
是否启用画面模糊
是否启用暗角
是否启用隧道视野
是否播放耳鸣
是否播放喘息声
是否播放心跳声
心跳声音量
画面效果强度
HUD位置
HUD缩放
```

客户端允许关闭眩晕画面、耳鸣或镜头晃动，但不能消除服务端计算的：

```text
移动速度下降
攻击速度下降
挖掘速度下降
无法冲刺
无法跳跃
手部操作能力下降
昏迷
```

客户端只负责展示服务端同步的健康结果。


#### 2.4 运行时规则快照

各健康系统不得在每次计算时直接读取 TOML 或 JSON 文件。

配置和数据包加载完成后，应构建统一的只读规则快照：

```java
public final class ResolvedRulesSnapshot {
    private final FeaturePolicyRegistry featurePolicies;
    private final GlobalMultipliers globalMultipliers;
    private final DamageProfileRegistry damageProfiles;
    private final InjuryDefinitionRegistry injuryDefinitions;
    private final SymptomDefinitionRegistry symptomDefinitions;
    private final ToxinDefinitionRegistry toxinDefinitions;
    private final PathogenDefinitionRegistry pathogenDefinitions;
    private final RadiationDefinitionRegistry radiationDefinitions;
    private final TreatmentDefinitionRegistry treatmentDefinitions;
    private final ProtectionDefinitionRegistry protectionDefinitions;
    private final long version;
}
```

运行时系统只读取当前有效的 `ResolvedRulesSnapshot`。

配置重新加载时按照以下流程处理：

```text
读取配置和数据文件
→ 校验数据格式
→ 校验引用关系
→ 处理覆盖关系
→ 构建新的规则快照
→ 原子替换旧快照
→ 重新计算玩家症状和属性输出
```

如果新配置加载失败，应保留上一份有效快照，避免健康系统进入部分加载或数据不一致状态。


### 3. 可配置的伤势参数

每种伤势定义应允许配置以下数据：

```text
是否启用
生成概率
最低有效伤害
最大生成数量
基础严重度
穿透深度
受影响面积
结构损伤权重
血管损伤概率
神经损伤概率
骨折概率
脑震荡概率
异物残留概率
污染程度
基础疼痛
基础恢复速度
```

不同伤势还应具有各自的专用参数。

例如切割伤允许配置：

```text
基础外出血速度
基础内出血速度
凝血难度
再出血概率
活动加重倍率
缝合效果倍率
包扎效果倍率
```

骨折允许配置：

```text
骨折等级概率
不稳定程度
移位概率
活动加重倍率
夹板效果
自然愈合速度
```

烧伤允许配置：

```text
受影响面积
组织损伤深度
持续热暴露
体液流失倍率
感染风险
疼痛强度
```

所有概率统一使用：

```text
0.0～1.0
```

所有速度和倍率必须明确其单位或计算周期，例如：

```text
bloodPerMinute
progressPerSecond
changePerHealthCycle
multiplier
```

不得使用含义不明确的浮点值。


### 4. 概率计算规则

伤势和结构损伤的最终概率按照统一顺序计算：

```text
基础概率
× 伤害来源倍率
× 伤害严重度倍率
× 身体部位倍率
× 结构易损倍率
× 防护修正
× 服务端全局倍率
= 最终概率
```

最终概率必须限制在：

```text
0.0～1.0
```

不得在不同模块中分别重复进行全局概率修正。

例如骨折概率只能由统一的骨折判定模块计算，不得由钝击处理器、身体部位处理器和症状处理器分别再次判定。


### 5. 功能开关层级

功能开关分为四个层级。

#### 5.1 模组总开关

```text
masterEnabled
```

关闭后：

```text
不生成新的健康影响
不运行健康循环
不产生新的症状
不施加属性和操作限制
```

但不得动态注销物品、注册表、附件类型或网络数据包。

已有健康数据默认保留，以便重新开启系统后继续使用。


#### 5.2 系统级开关

支持以下系统级开关：

```text
traumaSystem
bleedingSystem
painSystem
fractureSystem
respiratorySystem
temperatureSystem
toxinSystem
pathogenSystem
radiationSystem
metabolicSystem
arcaneSystem
consciousnessSystem
symptomSystem
healingSystem
treatmentSystem
environmentExposureSystem
```

系统关闭后，其上级伤势仍允许根据依赖关系保留。

例如：

```text
出血系统关闭
→ 切割伤仍然允许存在
→ 皮肤和软组织损伤仍然存在
→ 疼痛仍然允许存在
→ 不再消耗当前血液容量
→ 不再产生失血性休克
```

```text
症状系统关闭
→ 伤口和内部生理状态继续计算
→ 不产生移动、攻击、画面和声音影响
```

```text
骨折系统关闭
→ 钝击和挤压伤仍然允许生成
→ 不再创建新的骨折状态
```


#### 5.3 类型级开关

系统内部的具体类型也应允许单独关闭。

例如：

```text
injury.abrasion
injury.cut_wound
injury.puncture_wound
injury.penetrating_wound
injury.contusion
injury.crush_injury
injury.fracture
injury.concussion
injury.blast_trauma

symptom.limp
symptom.arm_impairment
symptom.dizziness
symptom.tremor
symptom.dyspnea
symptom.tunnel_vision
symptom.tinnitus
symptom.nausea
symptom.unconsciousness
```

关闭某一种伤势后，不再生成该类型的新实例。

关闭某一种症状后，只停止该症状的表现，不删除其来源伤势。


#### 5.4 输出级开关

一种症状根据配置产生多个不同影响，各个输出应允许单独启用或关闭。

例如手臂功能障碍影响：

```text
近战攻击力
攻击速度
挖掘速度
物品使用速度
弓箭或枪械稳定性
副手使用能力
镜头晃动
疼痛提示
```

用户允许只保留部分影响，例如：

```text
保留攻击速度降低
保留挖掘速度降低
关闭近战攻击力降低
关闭镜头晃动
```


### 6. 功能策略

主要功能不应只有一个简单的 `enabled` 字段。

采用以下功能策略：

```java
public record FeaturePolicy(
        boolean generationEnabled,
        boolean simulationEnabled,
        boolean gameplayEffectsEnabled,
        boolean presentationEnabled
) {
}
```

字段含义：

```text
generationEnabled
是否允许创建新的伤势、异常或症状来源

simulationEnabled
已有状态是否继续发展、恶化、恢复或参与健康循环

gameplayEffectsEnabled
是否影响移动、攻击、挖掘、冲刺和意识等实际玩法

presentationEnabled
是否生成客户端HUD、画面和声音表现
```

例如骨折允许设置为：

```text
generationEnabled = true
simulationEnabled = true
gameplayEffectsEnabled = false
presentationEnabled = true
```

其结果为：

```text
玩家仍按配置概率生成骨折
骨折仍然需要治疗和恢复
骨折仍然显示在身体状态界面
骨折不会降低移动、攻击或挖掘能力
```


### 7. 功能配置继承关系

功能配置应支持由上到下继承：

```text
模组全局默认
→ 系统级默认
→ 类型级默认
→ 单个定义覆盖
```

例如：

```text
全局允许游戏影响
→ 症状系统允许游戏影响
→ 手臂功能障碍允许游戏影响
→ 单独关闭手臂功能障碍的攻击力影响
```

下级配置允许覆盖上级配置，但上级系统完全关闭时，下级配置不得重新启用该系统。

例如：

```text
fractureSystem.generationEnabled = false
injury.fracture.generationEnabled = true
```

最终仍然不得生成骨折。


### 8. 症状数据驱动

伤势处理代码不得直接修改玩家属性。

禁止在骨折、伤口或毒素代码中直接执行：

```java
player.addAttributeModifier(...);
```

症状影响应按照以下流程处理：

```text
伤势和内部生理状态
→ 症状触发器
→ 症状强度计算
→ 症状输出规则
→ 所有输出统一汇总
→ 最终施加玩家属性和表现
```

症状定义至少包含：

```java
public record SymptomDefinition(
        ResourceLocation id,
        boolean enabled,
        SymptomTrigger trigger,
        IntensityCurve intensityCurve,
        List<GameplayOutputDefinition> outputs,
        StackingPolicy stackingPolicy
) {
}
```

症状触发条件读取：

```text
身体部位功能
结构完整度
骨折等级
神经功能
总疼痛
循环灌注
氧债
核心体温
毒素负荷
感染负荷
辐射影响
意识值
```

症状输出包括：

```text
属性倍率
移动限制
冲刺限制
跳跃限制
挖掘速度
攻击稳定性
物品使用能力
画面效果
声音效果
HUD提示
意识状态
```


### 9. 症状叠加规则

多个伤势和症状会同时影响同一个玩家属性，因此必须统一定义叠加规则。

支持：

```java
public enum StackingPolicy {
    MAX,
    MIN,
    SUM_CLAMPED,
    MULTIPLY,
    MOST_SEVERE,
    UNIQUE_BY_SOURCE
}
```

默认规则：

```text
相同症状的多个来源
→ MOST_SEVERE

不同症状的移动倍率
→ MULTIPLY

禁止冲刺、禁止跳跃等布尔限制
→ 任意来源禁止即禁止

画面模糊、暗角和镜头晃动
→ MAX

声音强度
→ MAX

疼痛贡献
→ SUM_CLAMPED
```

必须设置全局下限，防止多个症状叠加后玩家完全无法操作，例如：

```text
最低移动倍率
最低攻击速度倍率
最低挖掘速度倍率
最大镜头晃动强度
最大画面模糊强度
```

昏迷、死亡等明确的终止状态不受普通属性下限保护。


### 10. 功能关闭后的已有状态处理

配置关闭后，需要明确已有伤势或异常如何处理。

支持：

```java
public enum DisableExistingPolicy {
    KEEP_AND_FREEZE,
    KEEP_AND_SIMULATE_HIDDEN,
    RESOLVE_GRADUALLY,
    REMOVE_IMMEDIATELY
}
```

含义如下：

```text
KEEP_AND_FREEZE
保留已有数据，但停止发展、恢复和症状输出

KEEP_AND_SIMULATE_HIDDEN
继续内部计算，但不产生游戏影响或客户端表现

RESOLVE_GRADUALLY
停止生成新状态，已有状态按照恢复规则逐渐消失

REMOVE_IMMEDIATELY
立即删除已有状态
```

默认策略：

```text
关闭整个模组
→ KEEP_AND_FREEZE

关闭某种伤口生成
→ RESOLVE_GRADUALLY

关闭某种症状
→ REMOVE_IMMEDIATELY
仅删除症状缓存和属性输出，不删除来源伤势

关闭病原体系统
→ RESOLVE_GRADUALLY

关闭毒素或辐射系统
→ KEEP_AND_FREEZE

关闭客户端表现
→ 立即清除对应画面和声音效果
```

不得因为临时关闭系统就默认删除所有玩家持久化健康数据。


### 11. 系统依赖管理

所有功能开关必须由统一服务管理：

```java
public interface FeatureGateService {
    boolean isSystemEnabled(FeatureKey key);

    boolean allowsGeneration(FeatureKey key);

    boolean allowsSimulation(FeatureKey key);

    boolean allowsGameplayEffects(FeatureKey key);

    boolean allowsPresentation(FeatureKey key);

    DisableExistingPolicy disablePolicy(FeatureKey key);
}
```

其他服务只能通过 `FeatureGateService` 查询功能状态。

不得在大量业务类中直接散布：

```java
if (Config.enableBleeding) {
}
```

统一管理允许保证系统级开关、类型级开关和输出级开关按照固定优先级解析。


### 12. 数据重新加载规则

修改数据包后，不应直接重写已有伤势的所有原始属性。

数据分为两类。

#### 12.1 创建时固化的数据

伤势创建时写入实例，之后不受伤势定义修改直接影响：

```text
伤势类型
初始严重度
初始穿透深度
初始受影响面积
已造成的结构损伤
基础外出血能力
基础内出血能力
异物状态
初始污染程度
来源快照
```

例如修改切割伤的基础出血量，只影响重新加载后新生成的切割伤。


#### 12.2 动态规则数据

以下内容始终读取当前有效配置：

```text
全局出血倍率
凝血速度倍率
活动加重倍率
治疗效果倍率
自然恢复速度
症状触发阈值
症状输出强度
系统开关
客户端表现
```

例如：

```text
修改全局出血倍率
→ 立即影响所有仍在出血的伤口
```

```text
关闭手臂障碍的攻击速度输出
→ 立即移除已有玩家的对应攻击速度修正
```

重新加载后必须：

```text
清除旧症状缓存
重新计算派生生命体征
重新生成症状快照
重新汇总属性修饰器
向客户端同步新结果
```


### 13. 配置覆盖优先级

配置和数据文件按照以下顺序覆盖：

```text
LivingSystem内置默认数据
→ LivingSystem官方兼容数据
→ 整合包数据包
→ 世界数据包
→ 服务端开关和全局倍率
```

同一个定义被覆盖时，应保存来源信息，便于调试：

```text
定义ID
当前来源数据包
被覆盖的旧来源
最终解析结果
```

提供以下调试命令：

```text
/livingsystem config status
/livingsystem config validate
/livingsystem config explain <definition_id>
/livingsystem feature list
/livingsystem feature get <feature_id>
/livingsystem reload
```

`explain`命令应能够输出某个最终数值的来源，例如：

```text
手臂功能障碍攻击速度影响：

模组默认值：-0.30
整合包覆盖值：-0.20
服务端症状倍率：0.75
最终影响：-0.15
```


### 14. 数据校验

所有配置加载后必须经过校验。

校验内容至少包括：

```text
数值是否超出合法范围
概率是否处于0.0～1.0
引用的伤势是否存在
引用的症状是否存在
引用的结构是否属于目标身体部位
引用的物品、实体、方块或属性是否存在
系统依赖是否有效
配置是否产生循环引用
曲线是否连续或具有有效节点
```

无效配置的处理原则：

```text
单个定义无效
→ 跳过该定义并记录错误

单个症状输出引用不存在的属性
→ 跳过该输出，保留症状其他有效输出

伤势定义引用不存在的解剖结构
→ 拒绝加载该伤势定义

整个规则快照无法构建
→ 保留上一份有效规则快照
```

不得因为一个兼容数据文件错误而导致整个模组无法运行。


### 15. 不允许配置的核心规则

“数据可配置”不代表模组所有行为都允许被数据文件替换。

以下内容必须由代码固定：

```text
服务端是健康状态的唯一权威来源
客户端不能直接修改伤势和生理状态
伤害解析的总体处理顺序
健康状态的生命周期
持久化数据基本结构
网络安全校验
功能开关优先级
症状不能反向修改伤势和生理状态
身体部位的核心语义
数值合法范围
健康影响实例的基础字段
配置加载和校验流程
```

数据文件负责定义规则和参数，不负责改变系统基本不变量。


### 16. 总体配置结构

LivingSystem 的可配置系统最终分为以下五部分：

```text
功能开关
决定某个系统、伤势、症状或输出是否参与运行

定义数据
决定能够生成哪些伤势、毒素、病原体、辐射和症状

数值参数
决定生成概率、严重程度、发展速度和恢复速度

症状输出配置
决定内部健康状态如何影响玩家操作和游戏表现

客户端偏好
决定服务端健康结果如何显示给玩家
```

通过以上结构，LivingSystem 不需要内置固定难度等级。

整合包作者能够自行制作不同的配置组合，例如：

```text
轻量创伤模式
仅启用部位损伤、疼痛和轻度操作影响

生存医疗模式
启用出血、骨折、感染、毒素和完整治疗系统

硬核生命模拟模式
启用全部系统并提高伤势发展速度和症状惩罚

表现模式
保留伤势和HUD显示，但关闭大部分操作惩罚
```

这些模式只是整合包作者自行制作的配置组合，LivingSystem 核心代码不需要识别或维护固定的难度名称。


## 4. 身体部位与解剖结构

### 3.1 身体部位

玩家固定分为七个身体部位：

```java
public enum BodyRegion {
    HEAD_NECK,
    CHEST,
    ABDOMEN,
    LEFT_ARM,
    RIGHT_ARM,
    LEFT_LEG,
    RIGHT_LEG
}
```

玩家界面中 `HEAD_NECK` 显示为“头部”。

身体部位不具有独立生命值。每个部位只保存：

- 该部位拥有的结构状态；
- 关联的活动健康影响 ID；
- 由结构和伤势推导出的部位功能；
- 必要的局部治疗状态。

### 3.2 通用组织结构

所有身体部位默认具有：

```java
public enum AnatomicalStructure {
    SKIN,
    SOFT_TISSUE,
    BONE,
    VASCULAR,
    NERVE,

    BRAIN,
    SENSORY_SYSTEM,
    UPPER_AIRWAY,
    CERVICAL_SPINAL_PATHWAY,

    HEART,
    LEFT_LUNG,
    RIGHT_LUNG,
    LOWER_AIRWAY,
    THORACIC_SPINAL_PATHWAY,

    SOLID_ORGANS,
    HOLLOW_ORGANS,
    LUMBOSACRAL_NERVE_PATHWAY
}
```

通用结构：

- `SKIN`：皮肤
- `SOFT_TISSUE`：肌肉、脂肪、肌腱、韧带等统一抽象
- `BONE`：该身体部位的整体骨骼
- `VASCULAR`：该身体部位的血管系统
- `NERVE`：该身体部位的周围神经系统

关键结构：

- 头部：`BRAIN`、`SENSORY_SYSTEM`、`UPPER_AIRWAY`、`CERVICAL_SPINAL_PATHWAY`
- 胸腔：`HEART`、`LEFT_LUNG`、`RIGHT_LUNG`、`LOWER_AIRWAY`、`THORACIC_SPINAL_PATHWAY`
- 腹部：`SOLID_ORGANS`、`HOLLOW_ORGANS`、`LUMBOSACRAL_NERVE_PATHWAY`

设计约束：

1. 不区分具体上臂骨、前臂骨、大腿骨和小腿骨。
2. 颅骨、肋骨、胸骨、脊椎和骨盆归入对应部位的 `BONE`。
3. 肝、脾、肾归入 `SOLID_ORGANS`。
4. 胃、肠道、膀胱归入 `HOLLOW_ORGANS`。
5. 上呼吸道和下呼吸道必须分开，禁止头部和胸腔同时保存同一个 `AIRWAY` 状态。
6. 腹部不建立“腰段脊髓”，使用游戏抽象 `LUMBOSACRAL_NERVE_PATHWAY`。

### 3.3 结构状态的唯一数据源

结构状态只保存能够持续变化且不能直接从其他字段得到的数据：

```java
public final class StructureState {
    private float integrity;              // 0.0～1.0，唯一权威完整度
    private StructureSpecificState extra; // 可选专用状态
}
```

以下字段不得重复持久化：

- `damageLevel`：等于 `1 - integrity`，应动态计算；
- `disabled`：根据完整度和专用状态动态计算；
- 通用 `function`：由完整度、伤势、疼痛和专用状态动态计算。

专用结构状态示例：

```text
BONE:
- fractureGrade
- instability
- displaced
- splintStability

VASCULAR:
- vesselClass

NERVE:
- motorIntegrity
- sensoryIntegrity

BRAIN:
- neurologicalIntegrity

SPINAL_PATHWAY:
- motorTransmission
- sensoryTransmission

HEART:
- rhythmStability

LUNG:
- airLeakSeverity
- fluidAccumulation

AIRWAY:
- obstruction
- swelling

SOLID_ORGANS:
- internalBleedingModifier

HOLLOW_ORGANS:
- leakSeverity
- contaminationRate
```


## 5. 伤害、暴露与致伤机制

### 4.1 内部致伤机制

```java
public enum HarmMechanism {
    ABRASIVE,
    BLUNT,
    CRUSH,
    CUTTING,
    PENETRATING,
    BALLISTIC,
    BLAST,
    FRAGMENTATION,

    THERMAL_HEAT,
    THERMAL_COLD,
    ELECTRICAL,
    CHEMICAL_CORROSIVE,

    ASPHYXIA,
    TOXIC,
    BIOLOGICAL,
    RADIATION,
    METABOLIC,

    ARCANE,
    NECROTIC,
    VOID,
    FORCED_KILL,
    UNKNOWN
}
```

一个来源根据规则产生多个机制。例如：

```text
烟花爆炸
→ BLAST
→ FRAGMENTATION
→ THERMAL_HEAT
```

具体物品名称不得作为致伤机制。剑、斧、箭、枪械和法术只提供配置，最终转换为统一机制。

### 4.2 健康影响分类

```java
public enum HealthEffectCategory {
    TRAUMA,
    THERMAL,
    ELECTRICAL,
    CHEMICAL,
    RESPIRATORY,
    TOXIC,
    PATHOGEN,
    RADIATION,
    METABOLIC,
    ARCANE
}
```

`TERMINAL` 不属于健康影响分类。死亡或生理崩溃由终止规则判断，不创建“终止伤口”。

### 4.3 局部创伤类型

```java
public enum TraumaKind {
    ABRASION,
    CUT_WOUND,
    PUNCTURE_WOUND,
    PENETRATING_WOUND,
    BALLISTIC_WOUND,
    CONTUSION,
    CRUSH_INJURY,
    FRACTURE,
    CONCUSSION,
    BLAST_TRAUMA
}
```

以下内容不是创伤类型：

- 出血；
- 疼痛；
- 血管损伤；
- 神经损伤；
- 感染；
- 休克；
- 气胸；
- 器官功能下降。

它们分别属于伤势组件、结构损伤、继发健康影响或派生生理状态。

### 4.4 健康影响运行时对象

持续变化的健康影响不得直接使用不可变 Java `record` 作为主要运行时状态。

：

```java
public sealed interface HealthEffectInstance
        permits TraumaInjuryState,
                ThermalInjuryState,
                ElectricalInjuryState,
                ChemicalInjuryState,
                RespiratoryInsultState,
                ToxicExposureState,
                PathogenState,
                RadiationExposureState,
                MetabolicConditionState,
                ArcaneConditionState {

    UUID id();
    UUID sourceEventId();
    HealthEffectCategory category();
    CauseSnapshot cause();
    float severity();
    long createdGameTime();
    long lastUpdatedGameTime();
    boolean active();
}
```

运行时使用可变状态类；用于网络和持久化的 DTO 使用不可变 `record`。

### 4.5 创伤数据

```java
public final class TraumaInjuryState implements HealthEffectInstance {
    private UUID id;
    private UUID sourceEventId;
    private CauseSnapshot cause;
    private BodyRegion bodyRegion;
    private TraumaKind traumaKind;

    private float severity;
    private float depth;
    private float affectedArea;

    private EnumMap<AnatomicalStructure, Float> structureDamage;

    private BleedingState bleeding;
    private FractureState fracture;
    private ForeignBodyState foreignBody;
    private ContaminationState contamination;
    private PainState pain;
    private TreatmentState treatment;
    private HealingState healing;

    private long createdGameTime;
    private long lastUpdatedGameTime;
    private boolean active;
}
```

字段语义：

- `severity`：伤势总体严重度，0.0～1.0；
- `depth`：伤害穿透深度，0.0～1.0；
- `affectedArea`：该部位受影响面积比例，0.0～1.0；
- `structureDamage`：本伤势对各结构造成的损伤贡献。

结构累计完整度只能通过统一的 `StructureDamageService` 修改，禁止伤势类直接任意修改结构状态。

### 4.6 出血数据

```java
public final class BleedingState {
    private float baseExternalRate;
    private float baseInternalRate;
    private VesselClass vesselClass;
    private float clotProgress;
    private float clotStability;
    private float rebleedRisk;
    private boolean arterialPattern;
    private boolean currentlyBleeding;
}
```

```java
public enum VesselClass {
    CAPILLARY,
    SMALL,
    MEDIUM,
    MAJOR
}
```

实际出血速率：

```text
基础出血速率
× 玩家凝血修正
× 当前灌注/血压修正
× 玩家活动修正
× 治疗修正
× 伤口再裂开修正
```

规则：

1. 出血速率属于具体伤势，不属于身体部位或血管结构。
2. `baseExternalRate` 和 `baseInternalRate` 不得在每次循环中被覆写。
3. 同一部位多个伤口的出血分别计算后再汇总。
4. 内出血不会自动显示为外部流血症状。

### 4.7 污染与感染

开放伤口允许具有 `ContaminationState`：

```text
contaminationLevel
contaminantId
infectionRisk
cleaned
```

污染不等于感染。

```text
伤口污染
→ 根据污染程度、防护、免疫和时间进行感染判定
→ 成功后创建单独的 PathogenState
```

通用创伤中不得再保存 `infectionProgress`，避免和 `PathogenState` 重复。

### 4.8 其他健康影响的数据重点

#### 热损伤

主要数据：

- 身体部位；
- 热损伤类型；
- 严重度；
- 受影响面积；
- 组织深度；
- 持续暴露；
- 体液流失修正；
- 感染风险。

`SCALD` 只用于热水、蒸汽或高温液体。岩浆使用高温烧伤，不使用烫伤。

#### 电击

主要数据：

- 接触部位；
- 暴露持续时间；
- 心律干扰；
- 神经干扰；
- 局部烧伤。

#### 化学腐蚀

必须建立独立 `ChemicalInjuryState`，不能错误复用热损伤：

- 腐蚀物 ID；
- 接触部位；
- 侵蚀深度；
- 受影响面积；
- 持续残留量；
- 冲洗或中和进度；
- 呼吸道作用。

#### 呼吸异常

```java
public enum RespiratoryInsultKind {
    DROWNING,
    AIRWAY_OBSTRUCTION,
    SMOKE_INHALATION,
    TOXIC_GAS,
    LOW_OXYGEN,
    AIR_LEAK,
    PRESSURE_INJURY
}
```

主要数据：

- 气道阻塞；
- 氧债增加速度；
- 肺部结构损伤；
- 肺部液体负担；
- 暴露是否仍在持续。

#### 中毒

```java
public enum ExposureRoute {
    INGESTION,
    INHALATION,
    INJECTION,
    DERMAL,
    WOUND,
    BITE_OR_STING
}
```

每种毒素由数据定义其吸收、代谢、靶结构、症状和解毒剂。毒素总负荷从所有活动 `ToxicExposureState` 汇总，不在全身生理状态中重复保存第二份总量。

#### 病原体

```java
public enum PathogenType {
    VIRUS,
    BACTERIA,
    FUNGUS,
    PARASITE
}
```

```java
public enum TransmissionRoute {
    AIRBORNE,
    DROPLET,
    DIRECT_CONTACT,
    FOOD_OR_WATER,
    WOUND,
    VECTOR
}
```

```java
public enum InfectionStage {
    EXPOSED,
    INCUBATING,
    SYMPTOMATIC,
    RECOVERING,
    CLEARED
}
```

病原体载量保存在每个 `PathogenState` 中。全身感染负担动态汇总。

#### 辐射

```java
public enum RadiationType {
    ALPHA,
    BETA,
    GAMMA,
    NEUTRON
}
```

辐射主要累积剂量，不应每 tick 调用原版 `hurt`。达到阶段阈值后再产生内部影响、症状和必要的持续伤害。

#### 代谢异常

```java
public enum MetabolicConditionKind {
    STARVATION,
    DEHYDRATION,
    EXHAUSTION,
    DESICCATION
}
```

这些是全身状态，不随机分配身体部位。


## 6. 受击部位判定

### 5.1 总体方案

采用兼容优先的混合方案：

```text
原版或其他模组负责判断“是否命中”
→ LivingSystem 只在命中成立后判断“命中哪个身体部位”
```

LivingSystem 不默认建立超出原版碰撞箱并参与真实碰撞的七个实体碰撞盒。

### 5.2 命中信息适配层

不同伤害来源能够提供的命中信息不同，因此必须建立统一适配层：

```java
public record HitEvidence(
        Optional<Vec3> exactHitPosition,
        Optional<Vec3> attackStart,
        Optional<Vec3> attackEnd,
        Optional<Vec3> sourcePosition,
        HitEvidenceQuality quality
) {}
```

```java
public enum HitEvidenceQuality {
    EXACT,
    RAY_APPROXIMATION,
    DIRECTION_ONLY,
    SOURCE_RULE,
    RANDOM_FALLBACK,
    NOT_LOCALIZABLE
}
```

不得把 `DamageSource` 的来源位置直接当作实际命中点。

### 5.3 部位解析器链

```java
public interface HitLocationResolver {
    Optional<HitLocationResult> resolve(DamageContext context);
}
```

解析优先级：

1. 其他模组兼容适配器提供的精确命中部位；
2. 精确命中坐标与七个局部虚拟盒求交；
3. 投射物上一位置到当前位置的射线近似；
4. 攻击者视线到玩家原版碰撞箱的交点近似；
5. 伤害来源专用规则；
6. 按身体姿态和来源方向进行确定性加权选择；
7. 无法局部化时返回全身作用，不强行随机生成局部伤口。

### 5.4 七个局部虚拟盒

虚拟盒只用于命中后的分类，不接管原版碰撞。

```text
headBox
chestBox
abdomenBox
leftArmBox
rightArmBox
leftLegBox
rightLegBox
```

虚拟盒保存于玩家局部坐标，根据站立、潜行、游泳、滑翔等姿态选择模板。

若没有精确射线，则使用原版碰撞箱高度分区作为后备方案：

| 高度比例 | 横向位置 | 身体部位 |
|---|---|---|
| 0.80～1.00 | 全部 | 头部 |
| 0.55～0.80 | 中间 | 胸腔 |
| 0.55～0.80 | 两侧 | 左/右臂 |
| 0.38～0.55 | 中间 | 腹部 |
| 0.38～0.55 | 两侧 | 左/右臂 |
| 0.00～0.38 | 左右半区 | 左/右腿 |

### 5.5 无法使用普通命中点的来源

以下来源使用专用规则：

- 坠落：腿部权重最高，严重坠落可附加胸腔、头部损伤；
- 撞墙：根据移动方向、玩家朝向和姿态选择头部或胸腔；
- 坠落铁砧/方块：头部和胸腔权重较高；
- 火焰、岩浆、冻结：按暴露面积分配，可同时影响多个部位；
- 窒息、溺水、饥饿、中毒、辐射：默认是全身健康影响，不计算随机命中部位；
- 爆炸：按爆心方向选择主受击面，并允许一次事件影响多个部位。

### 5.6 确定性随机

概率判定必须基于同一伤害事件的稳定种子：

```text
玩家 UUID
+ sourceEventId
+ 游戏时间
+ DamageType ID
```

禁止在同一事件的不同阶段使用互不关联的随机数，避免服务端重算或调试时结果完全不同。


## 7. 全身资源、生命体征与原版系统替换

### 7.1 权威资源

`PhysiologyState` 持久化以下全身资源：

```java
public final class PhysiologyState {
    /** 最大血液容量，默认值为5000毫升，由服务端配置控制。 */
    private float maxBloodVolume;
    /** 当前血液容量，单位为毫升。 */
    private float currentBloodVolume;
    /** 最大体力。 */
    private float maxStamina;
    /** 当前体力。 */
    private float currentStamina;
    /** 代谢能量储备。 */
    private float metabolicEnergy;
    /** 水分状态。 */
    private float hydration;
    /** 营养状态。 */
    private float nutrition;
    /** 核心体温，单位为摄氏度。 */
    private float coreTemperature;
    /** 呼吸储备。 */
    private float respiratoryReserve;
    /** 累计氧债。 */
    private float oxygenDebt;
    /** 意识值。 */
    private float consciousness;
    /** 基础凝血效率。 */
    private float baselineClottingEfficiency;
    /** 免疫储备。 */
    private float immuneReserve;
    /** 镇痛强度。 */
    private float analgesiaLevel;
    /** 镇静强度。 */
    private float sedationLevel;
}
```

心脏、肺、气道、脑和其他器官功能从结构状态推导，不在 `PhysiologyState` 中重复保存。总感染负担、总毒素负担、总疼痛和总辐射生物效应由活动健康影响动态汇总。

### 7.2 派生生命体征

```java
public record DerivedVitals(
        float externalBleedRate,
        float internalBleedRate,
        float perfusionIndex,
        float heartRate,
        float systolicPressure,
        float respiratoryRate,
        float oxygenSaturation,
        float totalPain,
        float shockLevel,
        float systemicInfectionBurden,
        float systemicToxinBurden,
        float systemicRadiationBurden,
        float recoveryCapacity
) {}
```

派生生命体征不是权威持久化资源。系统允许缓存派生体征，但底层状态变化后必须使缓存失效并重新计算。

### 7.3 原版生命值替换

LivingSystem 启用时，原版生命值不再作为玩家真实生命资源。所有普通伤害在原版与其他模组完成护甲、附魔和减伤结算后被 LivingSystem 捕获，转换为健康影响，并阻止同一次伤害再次扣除原版生命值。

`VanillaResourceBridge` 将原版生命值保持在安全哨兵值。仅当 `DeathConditionResolver` 判定死亡时，系统使用 LivingSystem 专用致死来源进入 Minecraft 正常死亡流程。原版自然回血和其他模组直接调用的普通回血不会修复血液、组织、骨折或伤口；兼容适配器将明确声明的治疗行为转换为 LivingSystem 治疗操作。

HUD 取消原版心形的生命值语义，使用相同风格重新绘制血液容量：

```text
血液显示值 = 当前血液容量 ÷ 最大血液容量 × 20
```

默认最大血液容量为5000毫升时，每个显示单位代表250毫升，每两个显示单位代表一个完整心形。显示取整结果不得反向修改真实血液容量。

### 7.4 原版饥饿值替换

原版饥饿值、饱和度、饥饿伤害和原版自然回血全部停用。原版饥饿图标使用相同风格重新绘制当前体力：

```text
体力显示值 = 当前体力 ÷ 最大体力 × 20
```

代谢能量与体力分开计算。食物主要补充代谢能量、营养和水分；体力由休息和生理条件恢复，并由冲刺、跳跃、游泳、攻击、挖掘、负重、缺氧、疼痛和体温异常消耗。

### 7.5 水分与呼吸显示

体力图标上方绘制20单位水分栏，使用完整和半个水滴图标。

原版气泡图标的视觉样式用于显示 `respiratoryReserve`。水下、低氧环境、气道阻塞和呼吸设备统一修改该资源。呼吸储备耗尽后不得触发原版溺水扣血，而是增加氧债、降低意识并进入 LivingSystem 呼吸异常和死亡判定。

### 7.6 资源桥接约束

1. 自定义 HUD 快照是显示数据源；原版生命值、FoodData 和空气值不是健康权威数据。
2. 原版资源数值仅用于兼容哨兵或镜像，不参与内部生理公式。
3. 所有取消、转换和死亡重入保护集中在 `VanillaResourceBridge`，禁止分散在多个监听器中。
4. 当模组总开关关闭时，桥接器恢复原版资源行为，并冻结 LivingSystem 持久化状态。

## 8. 内部健康循环与继发后果

### 6.1 持久化基础状态

```java
public final class PhysiologyState {
    private float maxBloodVolume;
    private float currentBloodVolume;
    private float coreTemperature;
    private float oxygenDebt;
    private float consciousness;
    private float baselineClottingEfficiency;
    private float immuneReserve;
    private float analgesiaLevel;
    private float sedationLevel;
}
```

说明：

- 心脏、肺、气道和脑功能从结构状态推导，不在 `PhysiologyState` 中重复保存。
- 总感染负担从全部病原体实例汇总。
- 总毒素负担从全部毒素实例汇总。
- 总疼痛从活动伤势和疾病汇总。

### 6.2 派生生命体征

```java
public record DerivedVitals(
        float externalBleedRate,
        float internalBleedRate,
        float perfusionIndex,
        float heartRate,
        float systolicPressure,
        float respiratoryRate,
        float oxygenSaturation,
        float totalPain,
        float shockLevel,
        float systemicInfectionBurden,
        float systemicToxinBurden
) {}
```

派生体征不作为权威持久化数据。允许缓存，但底层状态变化后必须标记为失效并重新计算。

### 6.3 内部健康循环顺序

每次急性更新按照固定顺序执行：

```text
1. 更新持续暴露和活动健康影响
2. 计算各伤口实际外出血和内出血
3. 扣减当前血容量
4. 更新凝血和再次出血风险
5. 更新气道、肺部通气和氧债
6. 更新心脏输出与循环灌注
7. 更新核心体温
8. 更新毒素吸收与代谢
9. 更新病原体阶段与载量
10. 更新辐射剂量和生物效应
11. 更新组织修复与伤口愈合
12. 计算意识变化
13. 计算派生生命体征
14. 判断生理崩溃、昏迷和死亡条件
15. 标记症状与客户端快照为脏数据
```

### 6.4 玩家活动反馈

玩家活动不是症状，而是下一轮生理计算的输入：

```java
public record ActivitySnapshot(
        boolean resting,
        boolean walking,
        boolean sprinting,
        boolean jumping,
        boolean swimming,
        boolean usingMainHand,
        boolean usingOffHand,
        float recentMovementIntensity
) {}
```

示例：

- 冲刺增加出血和耗氧；
- 受伤腿部承重增加疼痛和再损伤概率；
- 使用骨折手臂增加疼痛和治疗失效概率；
- 休息提高凝血和恢复效率。

症状不得直接写入伤势。只有真实玩家活动通过 `ActivitySnapshot` 反馈到下一轮健康循环。


### 7. 继发后果规则

继发后果不是统一的新类型，而是规则引擎根据当前状态创建或更新其他健康影响。

示例：

```text
污染伤口
→ 创建或推进 PathogenState
```

```text
胸腔穿透伤损伤肺部
→ 创建 RespiratoryInsultState(AIR_LEAK)
```

```text
严重失血
→ shockLevel 上升
→ 不创建单独“休克伤口”
```

```text
严重缺氧
→ consciousness 下降
→ 不创建单独“缺氧伤口”
```

所有继发创建必须携带：

```text
parentEffectId
sourceEventId
```

并通过去重键避免每轮循环重复创建同一种继发影响。


## 9. 症状与游戏表现

### 8.1 症状层定位

症状层位于内部状态和客户端表现之间：

```text
伤势、结构、全身生理状态、派生体征
→ SymptomEngine
→ SymptomSnapshot
→ GameplayEffectAggregator
→ 属性、操作限制、画面、声音、HUD
```

症状不是健康状态的权威来源。症状系统必须是单向只读计算：

```text
健康状态根据规则产生症状
症状不得反向修改健康状态
```

### 8.2 症状严重度

```java
public enum SymptomTier {
    NONE,
    MILD,
    MODERATE,
    SEVERE,
    CRITICAL
}
```

```java
public record SymptomState(
        ResourceLocation symptomId,
        SymptomTier tier,
        float intensity,
        Optional<BodyRegion> bodyRegion,
        Set<UUID> causeEffectIds
) {}
```

`intensity` 范围为 0.0～1.0。

症状快照默认不持久化。玩家重新登录后，由持久化健康状态重新计算。为了防止阈值附近闪烁，服务端保留短期运行时迟滞缓存。

### 8.3 症状迟滞

每个症状必须具有进入阈值和退出阈值：

```text
进入中度眩晕：强度 >= 0.45
退出中度眩晕：强度 < 0.35
```

症状还允许配置：

- 最短持续时间；
- 淡入速度；
- 淡出速度；
- 客户端表现冷却；
- 是否允许在第三人称或无障碍模式中关闭。

### 8.4 核心症状列表

#### 全身症状

```text
living_system:pain
living_system:weakness
living_system:fatigue
living_system:dizziness
living_system:tunnel_vision
living_system:confusion
living_system:tremor
living_system:nausea
living_system:fever
living_system:chills
living_system:air_hunger
living_system:unconsciousness
```

#### 局部或部位症状

```text
living_system:visible_bleeding
living_system:limb_pain
living_system:arm_impairment
living_system:leg_impairment
living_system:limp
living_system:numbness
living_system:paralysis
living_system:head_disorientation
living_system:tinnitus
living_system:blurred_vision
living_system:cough
living_system:shallow_breathing
living_system:abdominal_pain
living_system:burning_sensation
living_system:cold_numbness
```

症状 ID 使用 `ResourceLocation`，允许附属模组扩展。

### 8.5 症状来源规则

#### 疼痛

输入：

- 各伤势基础疼痛；
- 伤势严重度；
- 受影响结构；
- 当前活动；
- 炎症、感染和烧伤；
- 镇痛强度。

输出：

- 局部疼痛；
- 全身疼痛；
- 高疼痛时的颤抖、操作不稳和注意力下降。

#### 失血和低灌注

输入：

- 当前血容量比例；
- `perfusionIndex`；
- 出血速度；
- 心脏功能。

症状递进：

```text
轻度：心跳声增强、轻微虚弱
中度：眩晕、耐力下降、视野边缘变暗
重度：严重虚弱、无法冲刺、明显隧道视野、意识下降
危重：倒地或昏迷、濒死状态
```

#### 缺氧

输入：

- 氧债；
- 血氧；
- 气道通畅；
- 肺功能。

症状递进：

```text
呼吸急促
→ 空气饥饿
→ 视野变暗和混乱
→ 动作迟缓
→ 意识丧失
```

#### 脑和头部损伤

根据配置产生：

- 定向障碍；
- 耳鸣；
- 镜头轻微晃动；
- 视野模糊；
- 恶心；
- 意识下降。

不应默认采用强烈闪屏或不可关闭的长时间镜头旋转。

#### 手臂损伤

左、右臂分别计算：

- 攻击速度修正；
- 挖掘速度修正；
- 拉弓、装填或持续使用物品的稳定性；
- 盾牌和副手使用能力；
- 严重神经或脊髓损伤时的失用。

默认不强制让玩家随机丢弃手中物品，除非配置明确启用。

#### 腿部损伤

左、右腿分别计算：

- 移动速度；
- 跳跃能力；
- 冲刺许可；
- 跌倒或踉跄概率；
- 承重疼痛。

单腿严重损伤产生跛行；双腿严重损伤产生更强移动限制。

#### 呼吸道和肺部损伤

根据配置产生：

- 咳嗽；
- 呼吸急促；
- 浅呼吸；
- 空气饥饿；
- 运动时症状显著加重。

#### 感染

按病原体配置产生：

- 发热；
- 寒战；
- 虚弱；
- 恶心；
- 呼吸道症状；
- 神经症状。

#### 中毒

症状由毒素配置决定，不得硬编码为统一“中毒效果”。可包括：

- 恶心；
- 颤抖；
- 视力异常；
- 呼吸抑制；
- 心律异常；
- 意识下降；
- 疼痛。

#### 辐射

症状根据累计剂量和延迟规则产生：

- 恶心；
- 虚弱；
- 发热；
- 恢复能力下降；
- 高剂量时出血倾向和意识异常。

### 8.6 游戏性效果汇总

所有症状和局部功能最终由一个汇总器生成唯一输出：

```java
public record GameplayEffectSnapshot(
        float movementSpeedMultiplier,
        float jumpStrengthMultiplier,
        float attackSpeedMultiplier,
        float miningSpeedMultiplier,
        float mainHandStability,
        float offHandStability,
        boolean sprintAllowed,
        boolean jumpAllowed,
        float cameraSway,
        float vignetteIntensity,
        float blurIntensity,
        float breathingAudioIntensity,
        float heartbeatAudioIntensity,
        boolean unconscious
) {}
```

规则：

1. 所有倍率在汇总器中统一夹取上下限。
2. 不同症状不得各自反复添加同类属性修饰器。
3. 属性修饰器使用固定 ID，更新数值而不是每 tick 新增修饰器。
4. 画面效果必须可配置强度，并提供无障碍开关。
5. Shader 模糊不是强制依赖；没有兼容实现时使用遮罩、暗角、FOV 和声音替代。
6. 自定义症状默认不批量映射成原版药水效果，避免 HUD 图标污染和与其他模组冲突。

### 8.7 玩家可见信息分级

玩家不应默认看到所有内部精确数值。

#### 无工具时

显示：

- 可见流血；
- 疼痛和功能异常；
- 呼吸、意识和视觉症状；
- 大致身体部位。

#### 自我检查或医疗界面

显示：

- 伤口类型；
- 部位；
- 大致严重度；
- 已进行的治疗；
- 可观察的感染迹象。

#### 医疗设备

可显示：

- 心率；
- 血压；
- 血氧；
- 体温；
- 精确出血量或辐射剂量等设备专属数据。

这样症状是诊断线索，而不是简单重复内部数据库。


## 10. 原版 DamageType 默认解析

### 11.1 火焰和温度

```text
IN_FIRE、CAMPFIRE、ON_FIRE、LAVA、HOT_FLOOR
→ THERMAL_HEAT
→ HEAT_BURN
```

```text
FREEZE
→ THERMAL_COLD
→ COLD_INJURY
```

### 11.2 缺氧和代谢

```text
IN_WALL
→ CRUSH + ASPHYXIA
```

```text
DROWN
→ ASPHYXIA
→ DROWNING
```

```text
STARVE
→ METABOLIC
→ STARVATION
```

```text
DRY_OUT
→ METABOLIC
→ DESICCATION
```

### 11.3 机械创伤

```text
CRAMMING
→ CRUSH
```

```text
CACTUS、SWEET_BERRY_BUSH、THORNS
→ ABRASIVE 或 PENETRATING
```

```text
FALL、FLY_INTO_WALL、FALLING_BLOCK
→ BLUNT
→ CONTUSION
→ 严重时 FRACTURE
→ 头部按配置概率附加 CONCUSSION
```

```text
FALLING_ANVIL
→ CRUSH
→ CRUSH_INJURY
→ 高骨折权重
```

```text
STALAGMITE、FALLING_STALACTITE
→ PENETRATING
→ PENETRATING_WOUND
```

### 11.4 近战和投射物

```text
PLAYER_ATTACK、MOB_ATTACK、MOB_ATTACK_NO_AGGRO
→ 查询武器或实体攻击配置
```

```text
ARROW、TRIDENT
→ PENETRATING
→ PENETRATING_WOUND
→ 可创建 ForeignBodyState
```

```text
MOB_PROJECTILE、THROWN
→ 查询投射物配置
→ 无配置时 UNKNOWN
```

```text
STING
→ PUNCTURE_WOUND
→ 可附加 ToxicExposureState
```

```text
SPIT
→ 来源配置决定 BLUNT、TOXIC 或 BIOLOGICAL
```

### 11.5 爆炸和冲击

```text
WIND_CHARGE、SONIC_BOOM
→ BLAST
→ BLAST_TRAUMA
```

```text
FIREWORKS
→ BLAST + FRAGMENTATION + THERMAL_HEAT
```

```text
FIREBALL、UNATTRIBUTED_FIREBALL
→ THERMAL_HEAT + BLAST
```

```text
EXPLOSION、PLAYER_EXPLOSION、BAD_RESPAWN_POINT
→ BLAST
→ 配置决定是否附加 FRAGMENTATION 和 THERMAL_HEAT
```

### 11.6 魔法、虚空和强制击杀

```text
MAGIC
→ ARCANE
```

```text
INDIRECT_MAGIC
→ 查询药水或投射物配置
→ 无配置时 ARCANE
```

```text
WITHER
→ NECROTIC
```

```text
WITHER_SKULL
→ NECROTIC
→ 可附加 BLAST
```

```text
DRAGON_BREATH
→ ARCANE
→ 配置可附加 CHEMICAL_CORROSIVE 或 TOXIC
```

```text
FELL_OUT_OF_WORLD、OUTSIDE_BORDER
→ VOID
→ 默认不创建持久伤势
```

```text
GENERIC_KILL
→ FORCED_KILL
→ 不创建持久伤势
```

```text
GENERIC
→ UNKNOWN
→ 不随机猜测具体创伤
```


## 11. 防护系统

### 1.1 设计目标

LivingSystem 的防护系统用于补充原版护甲无法完整表达的防护能力，包括：

```text
钝击防护
切割防护
穿刺防护
弹道防护
爆炸与破片防护
高温与低温防护
呼吸防护
生物污染防护
化学防护
辐射屏蔽
```

防护系统必须兼容原版护甲和其他模组装备，不得对同一次攻击重复计算原版护甲减伤。

原版护甲负责完成的伤害减免，必须视为已经处理完成。LivingSystem 读取经过原版及其他模组修正后的最终有效伤害，再根据专用防护属性调整：

```text
致伤机制
穿透概率
组织损伤分布
伤口生成概率
污染或暴露量
```

LivingSystem 不应再次使用普通护甲值重复降低最终伤害。


### 1.2 防护计算层级

防护计算分为两类。

#### 局部攻击防护

适用于：

```text
切割
穿刺
枪弹
钝击
爆炸破片
热源接触
```

计算流程：

```text
确定受击部位
→ 获取覆盖该部位的防护装备
→ 读取原版和其他模组已经完成的伤害修正结果
→ 计算LivingSystem专用防护能力
→ 调整穿透、结构损伤和伤势概率
→ 消耗装备完整度
```

#### 环境屏障防护

适用于：

```text
毒气
烟雾
空气病原体
飞沫
化学液体
污染水体
放射性环境
极端高温或低温
```

计算流程：

```text
计算环境基础暴露量
→ 检查装备覆盖范围
→ 检查密封性
→ 检查过滤器或防护材料
→ 检查装备完整度
→ 计算穿透后的实际暴露量
```

环境屏障防护不依赖原版护甲伤害减免。


### 1.3 防护装备数据

其他模组添加的装备必须能够通过数据包获得 LivingSystem 防护能力。

定义：

```java
/**
 * 定义一个装备对LivingSystem伤害和环境暴露的防护能力。
 */
public record ProtectionProfile(

        /** 配置ID。 */
        ResourceLocation id,

        /** 中文说明。 */
        String descriptionZhCn,

        /** 匹配的物品或物品标签。 */
        ItemMatcher itemMatcher,

        /** 该装备覆盖的身体部位。 */
        Set<BodyRegion> coveredRegions,

        /** 对不同致伤机制的防护能力。 */
        Map<HarmMechanism, Float> traumaResistance,

        /** 抵抗穿透的能力。 */
        float penetrationResistance,

        /** 降低伤势影响面积的能力。 */
        float areaReduction,

        /** 装备的基础密封性。 */
        float sealQuality,

        /** 呼吸防护能力。 */
        float respiratoryProtection,

        /** 生物污染防护能力。 */
        float biologicalProtection,

        /** 化学防护能力。 */
        float chemicalProtection,

        /** 不同辐射类型的屏蔽能力。 */
        Map<RadiationType, Float> radiationShielding,

        /** 高温防护能力。 */
        float heatProtection,

        /** 低温防护能力。 */
        float coldProtection,

        /** 是否需要过滤器或其他耗材。 */
        boolean requiresConsumable,

        /** 防护装备损坏时的性能曲线。 */
        ResourceLocation integrityCurveId
) {
}
```

数值范围默认使用：

```text
0.0
→ 没有防护

1.0
→ 理论上完全阻挡
```

实际计算仍然需要限制最大减免，避免单件普通装备无条件阻止全部暴露。


### 1.4 身体部位覆盖

每件防护装备必须明确覆盖哪些身体部位。

例如：

```text
头盔
→ 头部

防弹背心
→ 胸腔和腹部

防刺手套
→ 左臂和右臂

防护靴
→ 左腿和右腿

全身化学防护服
→ 全部身体部位
```

覆盖身体部位不代表完全密封。

例如穿戴防护服但没有面罩时：

```text
皮肤接触防护较高
→ 呼吸道仍然按暴露规则吸入毒气或病原体
```


### 1.5 密封性

呼吸、生物和化学防护需要考虑密封性。

密封性允许受到以下因素影响：

```text
装备类型
装备耐久
是否缺少配套部件
头部和身体装备是否匹配
过滤器是否安装
过滤器是否耗尽
玩家是否处于水下
装备是否被穿刺或破坏
```

全身防护要求形成一个防护组合：

```text
头部面罩
+
身体防护服
+
手套
+
防护靴
→ 完整密封
```

缺少任意关键部件时，根据配置降低整体密封效果。


### 1.6 过滤器与耗材

呼吸防护装备使用过滤器。

过滤器至少保存：

```text
过滤器类型
剩余容量
支持过滤的暴露类型
过滤效率
当前饱和度
是否损坏
```

不同过滤器允许分别处理：

```text
普通烟雾
粉尘
生物气溶胶
有毒气体
腐蚀性气体
放射性粉尘
```

过滤器耗尽后，不应立即删除装备，而是逐渐降低防护效果并向玩家提供提示。


### 1.7 装备完整度与污染

防护装备具有 LivingSystem 专用完整度。

装备受到攻击或长时间暴露后按以下规则降低性能：

```text
降低防刺能力
降低密封性
降低化学防护能力
降低辐射屏蔽能力
出现泄漏
```

被污染的防护装备还会成为持续暴露源。

例如：

```text
玩家离开放射性区域
→ 防护服表面仍有污染
→ 继续产生较低剂量
→ 必须脱下或进行去污
```

是否启用装备污染系统应允许配置。


### 1.8 防止重复减伤

必须建立统一的：

```java
/**
 * 统一计算LivingSystem专用防护结果。
 */
public interface ProtectionResolver {

    ProtectionResult resolveTraumaProtection(
            DamageContext damageContext,
            BodyRegion bodyRegion,
            List<ItemStack> equippedItems
    );

    ProtectionResult resolveExposureProtection(
            ExposureContext exposureContext,
            List<ItemStack> equippedItems
    );
}
```

`ProtectionResult`只保存一次防护计算结果，后续各伤势模块读取该结果，不得分别重新计算装备防护。

```java
/**
 * 一次攻击或暴露经过防护后的统一结果。
 */
public record ProtectionResult(

        /** 穿透防护后的最终穿透倍率。 */
        float penetrationMultiplier,

        /** 结构损伤倍率。 */
        float structureDamageMultiplier,

        /** 伤势影响面积倍率。 */
        float affectedAreaMultiplier,

        /** 实际进入呼吸道的暴露比例。 */
        float respiratoryPassThrough,

        /** 实际接触皮肤的暴露比例。 */
        float dermalPassThrough,

        /** 实际辐射剂量比例。 */
        float radiationPassThrough,

        /** 应消耗防护装备的强度。 */
        float equipmentWear
) {
}
```


## 12. 环境暴露系统

### 2.1 基本原则

以下健康影响不能完全依赖 `DamageEvent`：

```text
高温
低温
缺氧
毒气
烟雾
空气病原体
污染水体
化学污染
辐射
放射性粉尘
魔法环境
```

环境暴露需要独立的采样系统。

环境采样系统按照固定时间间隔检查玩家周围的环境，并计算玩家当前受到的暴露。


### 2.2 可配置的环境来源

数据包应允许将以下内容配置为环境暴露来源：

```text
维度
群系
方块
方块标签
流体
流体标签
实体
实体标签
物品实体
天气
时间
高度范围
特定区域
特定结构
环境温度
空气条件
魔法区域
```

例如：

```text
某个沼泽群系
→ 存在蚊虫传播的寄生虫暴露

某种腐烂方块
→ 向周围释放细菌或真菌孢子

某种有毒流体
→ 接触时产生皮肤毒素暴露
→ 浸没时产生更高暴露
→ 头部浸没时产生吸入暴露

某种反应堆方块
→ 周围持续产生伽马辐射

某种生物
→ 周围一定范围传播飞沫病原体

某个维度
→ 整体存在低氧和高辐射环境
```


### 2.3 环境暴露触发方式

支持：

```java
public enum ExposureTriggerMode {

    /** 玩家接近暴露源。 */
    PROXIMITY,

    /** 玩家身体接触方块、流体或实体。 */
    CONTACT,

    /** 玩家身体部分或全部浸没。 */
    IMMERSION,

    /** 玩家眼部或呼吸位置处于危险介质中。 */
    INHALATION,

    /** 玩家食用或饮用污染物。 */
    INGESTION,

    /** 开放伤口接触污染环境。 */
    WOUND_CONTACT,

    /** 暴露源需要与玩家之间无遮挡。 */
    LINE_OF_SIGHT,

    /** 玩家位于指定群系、维度或区域内。 */
    AREA_OCCUPANCY
}
```

同一个来源允许同时使用多个触发方式。


### 2.4 环境暴露定义

```java
/**
 * 定义一种环境条件如何向玩家施加暴露。
 */
public record EnvironmentalHazardDefinition(

        /** 暴露定义ID。 */
        ResourceLocation id,

        /** 中文说明。 */
        String descriptionZhCn,

        /** 是否启用。 */
        boolean enabled,

        /** 暴露来源匹配条件。 */
        EnvironmentalSourceMatcher sourceMatcher,

        /** 暴露触发方式。 */
        Set<ExposureTriggerMode> triggerModes,

        /** 暴露类型。 */
        ExposureCategory exposureCategory,

        /** 具体毒素、病原体、辐射或魔法异常ID。 */
        ResourceLocation effectDefinitionId,

        /** 基础暴露强度。 */
        float baseIntensity,

        /** 最大生效距离。 */
        float effectiveRange,

        /** 距离衰减曲线。 */
        ResourceLocation distanceCurveId,

        /** 是否需要计算方块遮挡。 */
        boolean requiresLineOfSight,

        /** 接触面积倍率。 */
        float contactAreaMultiplier,

        /** 浸没倍率。 */
        float immersionMultiplier,

        /** 开放伤口接触倍率。 */
        float woundContactMultiplier,

        /** 暴露开始阈值。 */
        float activationThreshold,

        /** 暴露停止阈值。 */
        float deactivationThreshold
) {
}
```


### 2.5 环境采样内容

采样器收集：

```text
玩家所在维度
玩家所在群系
玩家脚部方块
玩家眼部方块
玩家接触的流体
玩家浸没比例
玩家周围指定范围内的危险方块
玩家周围的危险实体
天气
环境温度
玩家高度
是否存在方块遮挡
玩家是否存在开放伤口
玩家当前防护装备
```

不应在每个游戏刻对大范围方块进行完整扫描。


### 2.6 暴露累积

环境采样不应每次都创建新的健康影响实例。

正确流程为：

```text
发现暴露源
→ 计算本次实际暴露量
→ 应用防护修正
→ 累加到对应ExposureAccumulator
→ 达到阈值后创建或更新健康影响实例
```

必须建立：

```java
/**
 * 累积玩家对一种环境危害的接触量。
 */
public final class ExposureAccumulator {

    /** 暴露定义ID。 */
    private ResourceLocation hazardId;

    /** 当前暴露强度。 */
    private float currentIntensity;

    /** 累计暴露剂量。 */
    private float accumulatedDose;

    /** 上次接触该来源的时间。 */
    private long lastExposureGameTime;

    /** 当前是否仍处于暴露状态。 */
    private boolean active;
}
```

例如病原体区域：

```text
短暂经过
→ 只累积少量暴露剂量
→ 不一定感染

长时间停留
→ 暴露剂量达到阈值
→ 创建病原体暴露状态
```


### 2.7 水体污染

流体暴露至少区分：

```text
皮肤接触
部分浸没
完全浸没
头部浸没
饮用
开放伤口接触
```

同一种污染水体允许配置不同结果：

```text
皮肤接触
→ 低强度毒素或病原体暴露

开放伤口接触
→ 高伤口污染和感染风险

头部浸没
→ 按暴露规则吸入病原体或毒素

饮用
→ 消化道中毒或病原体暴露
```


### 2.8 动态暴露源

除数据包静态定义外，必须提供动态暴露源接口。

例如其他模组的机器允许根据自身运行状态动态产生：

```text
辐射
热量
毒气
污染
病原体
魔法能量
```

提供：

```java
/**
 * 允许方块实体、实体或其他系统动态提供环境危害。
 */
public interface EnvironmentalHazardEmitter {

    Collection<EnvironmentalEmission> getActiveEmissions();
}
```


## 13. 治疗系统

### 1. 中文注释规范

LivingSystem 项目中的代码、配置示例、数据文件说明和开发文档必须使用中文注释。

代码中的类、接口、字段和关键方法应使用中文 JavaDoc，例如：

```java
/**
 * 表示一次正在进行的治疗过程。
 *
 * 治疗过程由服务端创建和更新，客户端只能接收用于显示的治疗进度。
 */
public final class TreatmentSession {
}
```

以下内容必须添加中文注释：

```text
公共类和公共接口
核心数据结构
配置字段
计算公式
复杂条件分支
网络数据包
持久化字段
兼容接口
容易产生误解的状态转换
```

简单赋值、显而易见的 Getter 和 Setter 不要求重复添加无意义注释。

变量名、类名、枚举名和资源 ID 继续使用英文，以保证代码风格和兼容性；它们对应的含义、用途和限制必须使用中文说明。

#### 1.1 配置文件注释

TOML 配置文件直接使用中文注释：

```toml
# 是否允许生成新的骨折状态
fractureGenerationEnabled = true

# 全局外出血速度倍率
externalBleedingMultiplier = 1.0
```

Minecraft 数据包使用严格 JSON 格式，不能使用以下写法：

```json
{
  // 这是非法JSON注释
  "enabled": true
}
```

对于 LivingSystem 自定义 JSON 数据，允许在 Codec 中允许以下说明字段：

```json
{
  "description_zh_cn": "用于处理开放性伤口的无菌绷带",
  "notes_zh_cn": "只能作用于一个具体伤口实例",
  "enabled": true
}
```

这些说明字段只用于开发、配置和调试，不参与实际计算。

对于不能增加自定义说明字段的原版 JSON，应在同目录提供中文 Markdown 说明文件或配置模板说明。


### 2. 治疗系统总体原则

LivingSystem 的治疗系统不采用“使用物品后立即恢复生命值或清除全部异常”的设计。

治疗系统遵循以下原则：

```text
局部治疗必须指定一个具体伤势
系统性药物作用于玩家或具体疾病状态
治疗具有过程和治疗速率
治疗只能处理其支持的状态
治疗不等于立即痊愈
治疗结果由服务端计算
医疗物品和治疗行为必须支持数据配置
```

治疗系统的完整流程为：

```text
选择医疗物品
→ 选择患者
→ 选择具体伤势或全身状态
→ 服务端验证治疗条件
→ 创建治疗过程
→ 按治疗速率推进
→ 分阶段产生治疗效果
→ 完成或中断治疗
→ 更新伤势、药物和恢复状态
→ 重新计算生命体征与症状
```


### 3. 治疗目标

治疗目标分为以下四类：

```java
public enum TreatmentTargetType {

    /**
     * 作用于一个明确的局部伤势实例。
     * 例如切割伤、穿刺伤、烧伤或骨折。
     */
    INJURY_INSTANCE,

    /**
     * 作用于一个非局部健康影响实例。
     * 例如中毒、感染、辐射污染或呼吸异常。
     */
    HEALTH_EFFECT_INSTANCE,

    /**
     * 作用于某个身体部位。
     * 仅在无法绑定单一伤势的辅助治疗中使用。
     */
    BODY_REGION,

    /**
     * 作用于玩家整体生理状态。
     * 例如输液、输血、吸氧或口服药物。
     */
    SYSTEMIC
}
```

### 3.1 局部外伤必须指定具体伤势

以下治疗必须选择一个明确的 `HealthEffectInstance` 或 `TraumaInjury`：

```text
包扎
加压止血
止血材料填塞
缝合
异物取出
伤口清洗
伤口消毒
清创
烧伤处理
骨折固定
胸部开放伤封闭
复杂手术
```

例如玩家左臂存在两个伤口：

```text
左臂伤口A：切割伤
左臂伤口B：穿刺伤
```

使用一份绷带时，必须选择伤口 A 或伤口 B，不能同时处理两个伤口。

一个治疗物品的一次使用最多绑定一个局部伤势实例。

### 3.2 系统性治疗

以下治疗允许作用于玩家整体或一个系统性健康影响：

```text
口服药物
注射药物
输液
输血
吸氧
解毒
抗感染治疗
辐射污染处理
营养和水分补充
```

系统性药物影响多个符合条件的状态，但不能无条件删除所有疾病。

例如抗菌药物允许：

```text
降低指定细菌病原体的复制速度
提高免疫控制效率
缩短症状期
```

但不能直接清除所有病毒、真菌和寄生虫状态。


### 4. 治疗效果分类

所有治疗行为按照目的分为以下五类：

```java
public enum TreatmentPurpose {

    /**
     * 直接处理伤势或疾病原因。
     * 例如取出异物、清创、解毒和抗感染治疗。
     */
    CAUSAL,

    /**
     * 阻止患者状态继续恶化。
     * 例如止血、固定骨折和封闭胸部开放伤。
     */
    STABILIZATION,

    /**
     * 只减轻症状，不处理伤势原因。
     * 例如镇痛、退热和止吐。
     */
    SYMPTOMATIC,

    /**
     * 支持全身生理状态。
     * 例如输液、输血和吸氧。
     */
    SUPPORTIVE,

    /**
     * 促进后续恢复。
     * 例如伤口护理、固定和康复治疗。
     */
    RECOVERY
}
```

必须区分病因治疗和症状治疗。

例如：

```text
止痛药
→ 降低疼痛症状
→ 不减少伤口出血
→ 不修复骨折
→ 不恢复结构完整度
```

```text
抗生素
→ 抑制符合定义的细菌病原体
→ 不清除伤口
→ 不直接恢复血液容量
```

```text
绷带
→ 降低目标伤口的外出血
→ 不处理内出血
→ 不取出异物
→ 不直接恢复皮肤完整度
```


### 5. 治疗过程

治疗不是立即执行的单次结果，而是一个持续过程。

每次开始治疗时，由服务端创建：

```java
/**
 * 表示玩家正在执行的一次治疗操作。
 */
public final class TreatmentSession {

    /** 本次治疗过程的唯一标识。 */
    private UUID id;

    /** 执行治疗的实体。 */
    private UUID practitionerId;

    /** 接受治疗的玩家。 */
    private UUID patientId;

    /** 被治疗的具体健康影响实例，可为空。 */
    private UUID targetEffectId;

    /** 被治疗的身体部位，可为空。 */
    private BodyRegion targetRegion;

    /** 使用的治疗行为定义。 */
    private ResourceLocation treatmentActionId;

    /** 使用的物品类型。 */
    private ResourceLocation sourceItemId;

    /** 已经完成的治疗进度，范围为0.0到1.0。 */
    private float progress;

    /** 开始治疗的游戏时间。 */
    private long startedGameTime;

    /** 上次更新治疗进度的游戏时间。 */
    private long lastUpdatedGameTime;

    /** 治疗是否已经被中断。 */
    private boolean interrupted;

    /** 治疗是否已经完成。 */
    private boolean completed;
}
```

### 5.1 治疗进度

治疗进度按照以下规则计算：

```text
基础治疗速率
× 医疗物品质量
× 治疗者能力修正
× 患者状态修正
× 环境修正
× 服务端治疗倍率
= 实际治疗速率
```

未接入医疗技能系统时：

```text
治疗者能力修正 = 1.0
```

但应保留接口，供接入技能模组或职业系统。

### 5.2 治疗效果提交方式

治疗行为支持以下提交模式：

```java
public enum TreatmentCommitPolicy {

    /**
     * 只有治疗完整完成后才产生效果。
     * 适用于取出异物、缝合和骨折复位。
     */
    ON_COMPLETE,

    /**
     * 根据已完成进度产生同比例效果。
     * 适用于清洗、消毒、吸氧和输液。
     */
    PROPORTIONAL,

    /**
     * 达到不同阶段时逐步产生效果。
     * 适用于包扎、手术和复杂处理。
     */
    STAGED
}
```

例如包扎允许设置为：

```text
进度达到25%
→ 轻微降低出血

进度达到60%
→ 明显降低出血

进度达到100%
→ 完整应用绷带状态
```

取出异物则必须达到 100% 才能真正移除异物。


### 6. 外伤治疗类型

### 6.1 基础止血

基础止血主要处理外部出血。

支持以下治疗方式：

```text
直接压迫
普通包扎
加压包扎
止血纱布填塞
止血带
伤口缝合
手术止血
```

不同止血方式具有不同适用范围。

#### 普通绷带

适用于：

```text
擦伤
轻度切割伤
轻度穿刺伤
小血管出血
```

效果：

```text
降低外出血速度
保护伤口
降低后续污染速度
```

限制：

```text
不能有效处理主要血管出血
不能处理内出血
不能处理未取出的巨大异物
```

#### 加压绷带

适用于：

```text
中度切割伤
中度穿刺伤
中等血管损伤
```

效果：

```text
比普通绷带具有更高止血倍率
降低再出血概率
```

#### 止血纱布

适用于：

```text
深部开放伤
无法直接闭合的创口
中重度外出血
```

效果：

```text
逐渐降低外出血速度
提高凝血进度
```

#### 止血带

只允许用于：

```text
左臂
右臂
左腿
右腿
```

效果：

```text
大幅降低目标肢体伤口的外出血
在高强度配置下完全阻断远端血流
```

止血带必须绑定到一个身体部位，但其主要治疗目标仍然是该部位的一个严重出血伤口。

止血带持续时间过长时，允许根据配置产生：

```text
肢体灌注下降
神经功能下降
软组织缺血
疼痛增加
```

该副作用必须允许通过配置关闭。


### 6.2 伤口清洁与消毒

支持以下处理：

```text
清水冲洗
无菌盐水冲洗
消毒剂处理
污染物移除
清创
```

清洗主要降低：

```text
表面污染
残留污物
感染风险
```

消毒主要降低：

```text
可存活病原体负荷
伤口感染概率
```

清洗和消毒不能完全替代清创。

严重污染伤口需要：

```text
冲洗
→ 清创
→ 再次消毒
→ 闭合或包扎
```


### 6.3 异物取出

箭头、弹片、枪弹碎片、木刺等形成 `ForeignBodyData`。

异物必须使用支持该类型的工具处理：

```text
镊子
异物钳
手术器械
综合性手术包
```

异物取出过程必须绑定一个具体伤势。

异物取出完成后：

```text
将foreignBody.present设为false
降低持续结构刺激
允许后续清创和缝合
```

异物取出根据配置产生：

```text
短时间出血增加
疼痛增加
污染增加
组织二次损伤
```

如果异物压迫了血管，直接取出按配置概率加重出血。该风险应由伤口定义和治疗配置决定。


### 6.4 伤口闭合

支持以下闭合方式：

```text
伤口闭合贴
缝合
皮肤钉合
复杂手术闭合
```

闭合前要求：

```text
异物已经取出
污染低于指定阈值
严重坏死组织已经清除
出血低于指定阈值
```

闭合效果：

```text
降低外出血
降低外部污染
提高伤口稳定性
提高后续愈合效率
```

不应将缝合设计为立即恢复结构完整度。

缝合只负责：

```text
稳定伤口
减少伤口继续裂开
改善后续愈合条件
```


### 6.5 骨折处理

骨折治疗支持：

```text
临时固定
夹板固定
骨折复位
手术固定
```

#### 夹板

夹板必须选择一个具体骨折实例。

效果：

```text
降低骨折不稳定程度
降低活动加重概率
降低疼痛增长
提高自然恢复效率
```

夹板不能立即清除骨折。

#### 骨折复位

适用于移位骨折。

要求：

```text
治疗过程完整完成
患者保持稳定
需要镇痛或麻醉
```

复位失败或中断允许导致：

```text
疼痛增加
软组织损伤增加
骨折不稳定性增加
```

#### 手术固定

属于高级治疗，允许：

```text
降低严重骨折的不稳定性
修正移位
提高可恢复上限
减少长期功能损失
```

综合性手术包一次只能处理一个骨折实例。


### 6.6 烧伤与冻伤处理

烧伤治疗方式：

```text
降温处理
烧伤敷料
伤口覆盖
体液支持
清创
高级手术处理
```

烧伤处理主要影响：

```text
持续热损伤
疼痛
体液流失
污染风险
愈合速度
```

冻伤治疗方式：

```text
缓慢复温
保温处理
局部保护
体液支持
```

冻伤治疗不得使用“瞬间完全恢复温度和组织”的机制。

错误或过快处理通过配置增加：

```text
疼痛
组织损伤
再灌注风险
```


### 6.7 胸部与呼吸系统处理

胸部和呼吸异常使用以下治疗：

```text
开放胸部伤封闭
气道清理
供氧
胸腔减压
胸腔引流
呼吸支持
```

#### 胸部密封敷料

用于：

```text
胸腔穿透伤
开放性胸部伤
空气泄漏
```

效果：

```text
降低airLeakSeverity
降低呼吸功能继续下降速度
```

不能恢复已经损伤的肺组织。

#### 气道处理

用于：

```text
上呼吸道阻塞
下呼吸道阻塞
烟雾或分泌物影响
```

降低：

```text
airwayObstruction
swelling
呼吸困难程度
```

#### 供氧设备

供氧属于支持性治疗。

效果：

```text
降低氧债增长
提高氧债恢复速度
缓解低血氧症状
```

供氧不能修复：

```text
肺部穿透伤
严重气胸
心脏停止工作
```

#### 胸腔减压和引流

属于高级工具处理。

适用于：

```text
严重空气泄漏
胸腔压力异常
胸腔液体积聚
```

操作具有持续时间，并要求患者静止、工具完整和环境条件。


### 7. 系统性支持治疗

### 7.1 输液

输液补充：

```text
循环液体
水分
部分电解质或营养状态
```

输液不等于补充血液。

输液主要改善：

```text
脱水
低循环容量
部分灌注不足
```

过量输液允许作为可选副作用。

### 7.2 输血

输血直接增加：

```text
currentBloodVolume
```

输血不能超过：

```text
maxBloodVolume
```

输血需要：

```text
血液袋
输液工具
持续治疗时间
```

血型兼容通过以下字段实现：

```text
compatibilityId
bloodType
transfusionReactionRisk
```

以供未来扩展。

输血不能直接修复仍在持续出血的伤口。

如果出血未控制，补充的血液仍然会继续流失。

### 7.3 吸氧

吸氧逐步作用于：

```text
oxygenDebt
oxygenSaturation
呼吸症状
```

吸氧效果受以下因素限制：

```text
airwayPatency
leftLungFunction
rightLungFunction
cardiacFunction
```


### 8. 药物治疗系统

药物不应在使用时直接修改最终状态，而应创建持续存在的药物作用实例。

```java
/**
 * 表示一次已经进入玩家体内的药物剂量。
 */
public final class MedicationEffectInstance {

    /** 药物定义ID。 */
    private ResourceLocation medicationId;

    /** 给药途径。 */
    private MedicationRoute route;

    /** 本次使用剂量。 */
    private float dose;

    /** 尚未吸收的药物量。 */
    private float absorptionReservoir;

    /** 当前已经吸收的药物量。 */
    private float absorbedAmount;

    /** 当前有效药物强度。 */
    private float activeStrength;

    /** 药物使用时间。 */
    private long administeredGameTime;

    /** 药物是否仍然有效。 */
    private boolean active;
}
```

给药途径包括：

```java
public enum MedicationRoute {

    /** 通过食物或药片口服。 */
    ORAL,

    /** 通过注射器注射。 */
    INJECTION,

    /** 通过静脉输液进入体内。 */
    INFUSION,

    /** 通过呼吸设备吸入。 */
    INHALATION,

    /** 涂抹在皮肤或伤口表面。 */
    TOPICAL
}
```

### 8.1 药物作用阶段

药物作用按照以下阶段变化：

```text
使用药物
→ 吸收延迟
→ 药效逐渐上升
→ 达到峰值
→ 维持有效时间
→ 药效逐渐下降
→ 完全代谢
```

药物定义至少支持：

```text
给药途径
基础剂量
吸收速度
起效延迟
药效上升时间
有效持续时间
药效衰减速度
最大安全剂量
过量阈值
治疗目标
副作用
```


### 9. 药物类型

### 9.1 镇痛药

效果：

```text
提高analgesiaLevel
降低疼痛症状输出
降低疼痛导致的镜头、操作和意识影响
```

镇痛药不能：

```text
停止出血
恢复骨折
清除感染
恢复组织完整度
```

### 9.2 抗菌药物

作用于：

```text
PathogenType.BACTERIA
```

效果：

```text
降低细菌复制速度
提高免疫控制
降低感染恶化速度
```

### 9.3 抗病毒药物

作用于配置中指定的病毒。

效果：

```text
降低病毒复制效率
缩短部分症状阶段
```

不能默认对所有病毒有效。

### 9.4 抗真菌药物

作用于：

```text
PathogenType.FUNGUS
```

### 9.5 抗寄生虫药物

作用于：

```text
PathogenType.PARASITE
```

### 9.6 解毒剂

解毒剂必须配置其能够处理的毒素 ID 或毒素标签。

效果包括：

```text
降低毒素吸收
提高毒素代谢
中和部分已吸收毒素
降低特定器官影响
```

不得设计一个能够解除所有中毒的通用解毒剂，除非整合包作者明确配置。

### 9.7 退热药

效果：

```text
降低发热症状
降低病原体导致的体温升高
```

不能直接清除病原体。

### 9.8 止吐药

效果：

```text
降低恶心和呕吐症状
降低进食失败概率
```

不能清除中毒或感染来源。

### 9.9 促凝药物

效果：

```text
临时提高clottingEfficiency
提高伤口凝血速度
```

根据配置产生副作用。

### 9.10 麻醉药物

主要用于高级医疗操作。

效果：

```text
降低手术疼痛
降低治疗中断概率
允许执行部分复杂手术
```

麻醉根据规则影响：

```text
意识
呼吸
心脏稳定性
```

麻醉系统采用可配置药效模型，并提供完整麻醉模型扩展接口。


### 10. 病原体治疗

病原体治疗不直接删除 `PathogenInstance`。

治疗主要修改：

```text
replicationRate
immuneControl
pathogenLoad
symptomIntensity
recoveryProgress
```

病原体最终清除条件为：

```text
病原体负荷降低到清除阈值
+
免疫控制达到要求
+
治疗或自然恢复完成
```

疫苗或预防性药物允许作为：

```text
ImmunityState
```

实现，不应和当前感染实例混为一体。


### 11. 辐射治疗

辐射相关治疗分为：

```text
外部污染清除
内部污染处理
症状支持
生理恢复支持
```

### 外部污染清除

使用：

```text
清洗设备
去污包
污染衣物移除
```

效果：

```text
降低external contamination
停止或降低持续剂量率
```

### 内部污染处理

使用：

```text
体内污染清除剂
特定放射性污染处理药物
```

效果：

```text
提高contaminationLoad清除速度
降低后续剂量累积
```

已经产生的累计辐射剂量不能被普通药物直接清零。

辐射治疗主要负责：

```text
停止继续暴露
降低体内污染
缓解症状
支持恢复
```


### 12. 综合性手术系统

综合性手术包不是“使用一次修复全身”的物品。

它是一个可重复使用或消耗耐久的医疗工具集合，能够提供多种手术行为。

每次手术必须：

```text
选择一个患者
选择一个具体伤势
选择一种手术行为
完成对应治疗过程
```

综合性手术包支持：

```text
深部异物取出
复杂清创
深部止血
血管修复
伤口闭合
骨折复位
骨折固定
胸腔处理
部分器官伤势稳定
```

一次治疗过程只能执行其中一种主要行为。

复杂创伤要求连续执行：

```text
止血
→ 异物取出
→ 清创
→ 修复
→ 闭合
```

每一步都创建独立的治疗过程。

### 12.1 手术条件

手术行为允许配置以下前置条件：

```text
患者保持静止
治疗者保持静止
使用指定工具
拥有足够工具耐久
拥有麻醉或镇痛条件
伤口已经暴露
环境清洁度达到要求
出血低于指定阈值
患者意识或姿态符合要求
```

### 12.2 手术风险

手术失败或中断根据配置造成：

```text
出血增加
污染增加
疼痛增加
结构二次损伤
治疗工具消耗
手术结果不完整
```

风险应允许通过配置关闭或调整。

通用手术包不能完全修复：

```text
严重大脑损伤
完全脊髓断裂
心脏完全失效
已经坏死的组织
已经达到终末状态的器官
```


### 13. 医疗物品设计

### 13.1 基础止血物品

```text
普通绷带
加压绷带
止血纱布
止血带
伤口闭合贴
```

### 13.2 清洁与消毒物品

```text
清水容器
无菌盐水
伤口清洗剂
消毒剂
清创工具
```

### 13.3 伤口处理工具

```text
镊子
异物钳
缝合包
皮肤钉合器
拆线工具
综合性手术包
```

### 13.4 骨折处理物品

```text
临时夹板
标准夹板
高级固定支具
骨折复位工具
骨科手术组件
```

### 13.5 热损伤处理物品

```text
冷却包
烧伤敷料
烧伤凝胶
保温毯
复温包
```

### 13.6 胸部与呼吸物品

```text
胸部密封敷料
气道清理工具
氧气瓶
氧气面罩
便携式供氧设备
胸腔减压针
胸腔引流工具
```

### 13.7 输液和输血物品

```text
输液器
基础补液袋
营养液
血液袋
血浆或血液替代品
输血工具
```

### 13.8 药物物品

```text
口服镇痛药
注射镇痛药
抗菌药
抗病毒药
抗真菌药
抗寄生虫药
退热药
止吐药
促凝药
麻醉药
通用或特定解毒剂
辐射污染处理药物
```

### 13.9 辐射去污物品

```text
表面去污包
全身去污设备
污染衣物收纳袋
内部污染清除剂
```


### 14. 医疗物品和工具消耗

医疗物品分为：

```java
public enum MedicalItemUsageType {

    /** 使用一次后消耗一个物品。 */
    CONSUMABLE,

    /** 使用时消耗耐久。 */
    DURABILITY_TOOL,

    /** 需要同时消耗工具和材料。 */
    TOOL_AND_MATERIAL,

    /** 持续连接患者并消耗内部资源。 */
    CONTINUOUS_DEVICE
}
```

示例：

```text
绷带
→ CONSUMABLE

镊子
→ DURABILITY_TOOL

综合性手术包
→ TOOL_AND_MATERIAL

氧气设备
→ CONTINUOUS_DEVICE
```

治疗开始时不一定立即消耗全部物品。

物品消耗允许设置为：

```text
开始治疗时消耗
达到指定进度时消耗
治疗完成时消耗
按照治疗进度分阶段消耗
```

避免玩家在即将完成治疗时取消操作并无损重复使用。


### 15. 治疗状态保存

伤口不应继续使用简单布尔字段：

```text
bandaged
sutured
splinted
tourniquetApplied
```

应改为应用治疗状态列表：

```java
/**
 * 保存一个伤势当前已经接受的治疗。
 */
public final class AppliedTreatmentState {

    /** 治疗定义ID。 */
    private ResourceLocation treatmentId;

    /** 治疗占用的功能槽位。 */
    private TreatmentSlot slot;

    /** 当前有效强度。 */
    private float effectiveness;

    /** 治疗物品或处理的完整程度。 */
    private float integrity;

    /** 治疗应用时间。 */
    private long appliedGameTime;

    /** 剩余有效时间，永久状态使用特殊值。 */
    private long remainingDuration;

    /** 治疗是否仍然有效。 */
    private boolean active;
}
```

治疗槽位包括：

```java
public enum TreatmentSlot {

    /** 覆盖伤口的绷带或敷料。 */
    WOUND_COVER,

    /** 加压、填塞或止血带等止血措施。 */
    HEMORRHAGE_CONTROL,

    /** 缝合、钉合或闭合贴。 */
    WOUND_CLOSURE,

    /** 夹板或固定支具。 */
    FRACTURE_STABILIZATION,

    /** 胸部密封或胸腔处理。 */
    CHEST_SUPPORT,

    /** 气道或供氧支持。 */
    RESPIRATORY_SUPPORT,

    /** 复杂手术修复状态。 */
    SURGICAL_REPAIR
}
```

同一伤势的同一槽位默认只能存在一个主要治疗状态。

新的治疗允许：

```text
替换旧治疗
升级旧治疗
拒绝应用
与旧治疗共存
```

具体规则由治疗定义决定。


### 16. 治疗行为定义

治疗行为必须支持数据驱动。

```java
/**
 * 定义一种医疗物品执行的治疗行为。
 */
public record TreatmentActionDefinition(

        /** 治疗行为ID。 */
        ResourceLocation id,

        /** 中文说明。 */
        String descriptionZhCn,

        /** 治疗目标类型。 */
        TreatmentTargetType targetType,

        /** 治疗目的。 */
        TreatmentPurpose purpose,

        /** 完成治疗所需时间。 */
        int durationTicks,

        /** 治疗结果提交方式。 */
        TreatmentCommitPolicy commitPolicy,

        /** 基础治疗速率。 */
        float baseRate,

        /** 一次治疗能够达到的最大效果。 */
        float maxEffect,

        /** 支持治疗的伤势类型或标签。 */
        List<ResourceLocation> compatibleTargets,

        /** 使用治疗前必须满足的条件。 */
        List<TreatmentRequirementDefinition> requirements,

        /** 治疗完成或推进时产生的结果。 */
        List<TreatmentOperationDefinition> operations,

        /** 根据配置产生的副作用。 */
        List<TreatmentSideEffectDefinition> sideEffects,

        /** 是否允许玩家自我治疗。 */
        boolean allowSelfTreatment,

        /** 治疗期间是否要求保持静止。 */
        boolean requiresStationary
) {
}
```

不得允许配置文件通过任意字段路径或 Java 反射直接修改玩家健康数据。

治疗结果必须使用代码预先定义的安全操作类型。

例如：

```java
public enum TreatmentOperationType {
    REDUCE_EXTERNAL_BLEEDING,
    REDUCE_INTERNAL_BLEEDING,
    INCREASE_CLOT_PROGRESS,
    REDUCE_CONTAMINATION,
    REMOVE_FOREIGN_BODY,
    APPLY_WOUND_CLOSURE,
    STABILIZE_FRACTURE,
    REDUCE_FRACTURE_DISPLACEMENT,
    REDUCE_AIR_LEAK,
    REDUCE_AIRWAY_OBSTRUCTION,
    REDUCE_LUNG_FLUID_LOAD,
    RESTORE_BLOOD_VOLUME,
    RESTORE_BODY_FLUID,
    REDUCE_TOXIN_LOAD,
    MODIFY_PATHOGEN_REPLICATION,
    REDUCE_RADIATION_CONTAMINATION,
    APPLY_ANALGESIA,
    APPLY_MEDICATION_EFFECT,
    MODIFY_HEALING_RATE
}
```


### 17. 治疗前置条件

治疗行为要求：

```text
目标伤势类型匹配
目标伤势仍然活动
伤势位于允许的身体部位
伤口污染低于阈值
异物已经取出
出血低于阈值
目标尚未存在相同治疗
患者没有移动
治疗者没有移动
治疗者距离患者足够近
医疗工具完整
拥有必要的消耗材料
环境满足要求
指定系统已经启用
```

如果条件不满足，服务端必须拒绝治疗并返回明确的中文原因。

例如：

```text
无法缝合：伤口内仍然存在异物
无法使用止血带：目标不是四肢
无法固定：目标伤势不是骨折
无法继续手术：患者已经离开治疗范围
```


### 18. 治疗中断

以下情况会中断治疗：

```text
治疗者主动取消
患者主动取消
治疗者移动超过阈值
患者移动超过阈值
治疗者受到伤害
患者受到新的严重伤害
治疗距离过远
治疗物品被移除
治疗工具损坏
患者死亡
世界或维度发生变化
```

中断后的结果由 `TreatmentCommitPolicy` 决定：

```text
ON_COMPLETE
→ 未完成时不提交主要治疗结果

PROPORTIONAL
→ 保留已经完成的比例效果

STAGED
→ 保留已经达到的治疗阶段
```

侵入性治疗中断时，允许根据配置产生额外风险。


### 19. 治疗并发限制

默认规则：

```text
一个治疗者同时只能执行一个治疗过程
一个具体伤势同时只能接受一个主要主动治疗过程
一个患者允许由不同治疗者处理不同伤势
```

例如：

```text
玩家A正在为患者处理左臂切割伤
玩家B允许同时处理患者右腿骨折
玩家C不能同时处理同一个左臂切割伤
```

该限制通过服务端配置调整。


### 20. 治疗结果与自然恢复

治疗完成后不应直接删除伤势。

治疗结果主要表现为：

```text
停止或减缓恶化
降低出血
降低污染
稳定骨折
移除异物
减少呼吸异常
抑制病原体
降低毒素负荷
提高自然恢复速度
提高结构可恢复上限
```

伤势最终清除仍然由恢复系统判断：

```text
伤势严重度降低到清除阈值
+
所有持续性并发症已经结束
+
对应结构达到最低恢复要求
+
不再产生症状或游戏影响
```

只有在满足清除条件后，才将伤势实例标记为已解决并移除。


### 21. 治疗配置与功能开关

治疗系统需要支持以下开关：

```text
是否启用治疗系统
是否启用自我治疗
是否允许治疗其他玩家
是否启用治疗时间
是否允许治疗被移动中断
是否允许受伤中断治疗
是否启用治疗失败
是否启用手术风险
是否启用止血带副作用
是否启用药物副作用
是否启用药物过量
是否启用工具耐久
是否启用环境清洁度
```

每一种治疗行为和医疗物品也应允许单独关闭。

例如：

```text
treatment.bandage.enabled
treatment.tourniquet.enabled
treatment.suture.enabled
treatment.foreign_body_removal.enabled
treatment.fracture_splint.enabled
treatment.surgery.enabled
treatment.transfusion.enabled
treatment.oxygen_support.enabled
```

关闭某种治疗行为后：

```text
不允许创建新的对应治疗过程
已有AppliedTreatmentState默认继续有效
已经开始但尚未完成的治疗过程立即中断
```


### 22. 客户端治疗界面

使用医疗物品时，打开治疗选择界面。

界面至少显示：

```text
患者
身体部位
可识别的伤势
伤势类型
出血状态
污染状态
异物状态
骨折状态
当前已经应用的治疗
当前物品支持的治疗行为
预计治疗时间
失败原因
```

普通玩家未使用检查工具时，不一定能够查看所有精确内部数值。

界面显示模糊描述：

```text
轻微出血
严重出血
疑似骨折
伤口内存在异物
存在明显污染
```

医疗检查工具显示更详细的数据。

客户端选择治疗目标后，只向服务端发送：

```text
患者ID
目标健康影响实例ID
治疗行为ID
使用的物品槽位
```

服务端必须重新验证全部条件，不能信任客户端提交的伤势状态和治疗结果。


## 14. 摄入物、药物、恢复、昏迷与死亡

### 1. 食物与可摄入物治疗系统

#### 1.1 基本原则

LivingSystem 不应只识别原版食物。

整合包中存在其他模组添加的食物、饮料、药膳、营养液、能量饮料和特殊消耗品，因此必须允许整合包作者通过数据文件配置任意物品的摄入效果。

摄入物的效果不限于补充能量和水分，也使用治疗系统允许的安全操作，例如：

```text
补充代谢能量
补充水分
补充营养
补充电解质
恢复少量体力
提高体力恢复速度
提高伤口恢复速度
提高凝血能力
降低毒素负荷
抑制特定病原体
降低恶心
降低体温异常
提供短时间镇痛
产生药物作用
产生中毒、过敏或其他副作用
```

摄入物不应通过任意字段路径或反射直接修改玩家健康数据。

所有效果必须使用治疗系统预先定义的安全操作类型。


#### 1.2 能量与体力必须分开

模组中的代谢能量和体力是两个不同概念。

```text
代谢能量
→ 表示身体能够长期使用的能量储备
→ 主要通过食物补充
→ 影响基础代谢、体温维持、自然恢复和体力恢复

体力
→ 表示玩家短时间进行活动的能力
→ 受到冲刺、跳跃、攻击、游泳、挖掘等活动消耗
→ 休息时逐渐恢复
```

普通食物主要补充代谢能量，而不是直接将体力恢复到满值。

部分特殊物品允许配置为直接恢复体力，例如：

```text
能量饮料
高糖食品
兴奋剂
特殊药物
```

但直接恢复体力的效果必须允许单独配置，并允许附带后续疲劳、脱水或其他副作用。


#### 1.3 摄入物数据定义

必须建立：

```java
/**
 * 定义某个物品被成功摄入后产生的健康效果。
 */
public record ConsumableTreatmentDefinition(

        /** 定义的资源ID。 */
        ResourceLocation id,

        /** 中文说明。 */
        String descriptionZhCn,

        /** 是否启用该定义。 */
        boolean enabled,

        /** 匹配的具体物品或物品标签。 */
        ItemMatcher itemMatcher,

        /** 摄入途径。 */
        MedicationRoute route,

        /** 使用该物品需要的时间。 */
        int useDurationTicks,

        /** 该物品成功摄入后执行的治疗操作。 */
        List<TreatmentOperationDefinition> operations,

        /** 根据配置产生的副作用。 */
        List<TreatmentSideEffectDefinition> sideEffects,

        /** 与其他匹配定义的合并方式。 */
        ConsumableMergePolicy mergePolicy
) {
}
```

支持以下匹配方式：

```text
具体物品ID
物品标签
物品数据组件条件
物品耐久或自定义数据条件
```

例如：

```text
minecraft:apple
→ 补充代谢能量
→ 补充少量水分

某饮料模组的矿泉水
→ 补充大量水分
→ 补充少量电解质

某食物模组的汤
→ 补充代谢能量
→ 补充水分
→ 提高短时间体温

某科技模组的营养液
→ 补充代谢能量
→ 补充营养
→ 提高恢复能力
```


#### 1.4 配置覆盖和合并

同一个物品会同时匹配具体物品定义和物品标签定义，因此需要配置合并策略：

```java
public enum ConsumableMergePolicy {

    /** 在已有摄入效果基础上追加新效果。 */
    APPEND,

    /** 完全替换优先级较低的定义。 */
    REPLACE,

    /** 只覆盖相同类型的治疗操作。 */
    OVERRIDE_SAME_OPERATION
}
```

匹配优先级：

```text
具体物品和数据组件条件
→ 具体物品ID
→ 物品标签
→ 原版食物属性转换
→ 无额外效果
```


#### 1.5 对其他模组食物的兼容

对于其他模组已经允许正常食用或饮用的物品：

```text
不接管其原有右键逻辑
不重复消耗物品
不修改其容器返还规则
不替换其原有音效和动画
```

LivingSystem 只在确认该物品已经成功完成食用或饮用后，追加配置中的健康和治疗效果。

如果其他模组物品本身已经产生原版饥饿值、饱和度或药水效果，LivingSystem 通过配置决定：

```text
保留原有效果
屏蔽原版饥饿值变化
将原版营养值转换为代谢能量
将原版饱和度转换为体力恢复修正
追加LivingSystem效果
完全使用自定义配置替换
```

对于本身不能使用的普通物品，不应仅因为存在摄入配置就自动将其变为可食用物品。

如需将普通物品改造成可摄入物，应使用单独的：

```text
standaloneConsumable
```

配置，并进行更严格的兼容性校验。


#### 1.6 摄入物的治疗目标限制

普通食物和口服物品默认属于系统性治疗，不能直接执行需要选择具体伤口的操作。

普通摄入物执行：

```text
补充能量、水分和营养
施加药物实例
降低符合条件的毒素负荷
影响符合条件的病原体
提供镇痛、退热和止吐效果
提高恢复能力
```

普通摄入物不能直接执行：

```text
取出异物
缝合伤口
应用夹板
关闭开放胸部伤
修复具体血管
移除具体骨折
```

如果一个特殊消耗品需要作用于某个健康影响，应使用目标选择规则：

```java
public enum SystemicTargetSelection {

    /** 作用于所有符合条件的健康影响。 */
    ALL_MATCHING,

    /** 作用于最严重的一个符合条件的健康影响。 */
    MOST_SEVERE_MATCHING,

    /** 作用于随机一个符合条件的健康影响。 */
    RANDOM_MATCHING,

    /** 只修改玩家的全身生理状态。 */
    PHYSIOLOGY_ONLY
}
```


### 2. 治疗物品使用动画

#### 2.1 动画系统目标

医疗物品不应全部使用同一种原版蓄力动作。

LivingSystem 必须提供统一的治疗动画框架，使每种治疗行为能够配置自己的：

```text
使用姿势
手部动作
物品位置和旋转
使用音效
粒子效果
动画持续时间
动画阶段
第一人称表现
第三人称表现
```

动画只负责显示，不负责决定治疗是否成功。

服务端的 `TreatmentSession` 是治疗进度和治疗结果的唯一权威来源。


#### 2.2 动画类型

定义以下治疗动画类型：

```java
public enum TreatmentAnimationType {

    /** 使用原版进食动画。 */
    EAT,

    /** 使用原版饮用动画。 */
    DRINK,

    /** 向身体部位缠绕绷带。 */
    BANDAGE,

    /** 使用止血带。 */
    APPLY_TOURNIQUET,

    /** 使用注射器注射药物。 */
    INJECT,

    /** 清洗或喷洒伤口。 */
    CLEAN_WOUND,

    /** 使用镊子或异物钳取出异物。 */
    REMOVE_FOREIGN_BODY,

    /** 缝合或钉合伤口。 */
    SUTURE,

    /** 应用夹板或固定支具。 */
    APPLY_SPLINT,

    /** 使用敷料处理烧伤。 */
    APPLY_DRESSING,

    /** 执行较复杂的医疗操作。 */
    SURGERY,

    /** 连接输液、输血或供氧设备。 */
    CONNECT_DEVICE,

    /** 持续使用医疗设备。 */
    OPERATE_DEVICE,

    /** 使用原版动作作为兼容回退。 */
    VANILLA_FALLBACK,

    /** 不播放特殊动画。 */
    NONE
}
```


#### 2.3 动画实现层级

动画分为三个实现层级。

##### 基础动画

直接使用原版已经存在的使用动画：

```text
吃
喝
举起物品
刷动物作
长按蓄力
```

适用于：

```text
食物
饮料
口服药物
简单吸入型药物
不需要复杂动作的兼容物品
```

##### 自定义手部动画

医疗物品使用自定义动画类型时：

```text
物品返回CUSTOM使用动画
→ 客户端读取当前TreatmentSession
→ 根据动画进度修改第一人称手和物品的位置
→ 根据动画类型设置第三人称手臂姿势
```

适用于：

```text
注射
包扎
止血带
镊子取物
缝合
夹板
胸部敷料
```

##### 复杂动画

复杂医疗操作允许进一步支持：

```text
双手动作
分阶段动作
工具和材料分别出现
物品模型切换
目标身体部位定位
治疗其他玩家时的朝向修正
```

复杂动画使用独立动画资源实现；动画资源缺失时回退到进度驱动手部姿势。

动画框架支持进度驱动手部姿势和复杂骨骼动画；复杂动画资源缺失时回退到进度驱动姿势。


#### 2.4 动画必须绑定治疗行为而不是物品类

动画应优先绑定：

```text
TreatmentActionDefinition
```

而不是直接写死在医疗物品 Java 类中。

例如：

```text
普通绷带
急救包
其他模组添加的绷带
```

只要它们执行相同的：

```text
living_system:bandage_wound
```

治疗行为，就使用同一个包扎动画。

这样通过数据文件配置的其他模组医疗物品，也能够使用 LivingSystem 的治疗动作。


#### 2.5 其他模组物品的动画兼容

对于 LivingSystem 自己注册的物品，允许完整控制其使用动画。

对于其他模组的物品：

```text
优先保留其他模组自身动画
如果没有冲突，则叠加LivingSystem自定义手部动画
发生冲突时使用兼容回退动画
不能安全处理时只显示治疗进度条
```

为治疗定义增加：

```java
public enum TreatmentAnimationCompatibilityMode {

    /** 强制使用LivingSystem动画。 */
    FORCE_CUSTOM,

    /** 优先使用其他模组物品自己的动画。 */
    PREFER_ITEM_ANIMATION,

    /** 只使用原版兼容动画。 */
    VANILLA_FALLBACK,

    /** 不修改物品动画，只显示进度。 */
    PROGRESS_ONLY
}
```

默认使用：

```text
PREFER_ITEM_ANIMATION
```

以减少与其他模组的渲染冲突。


#### 2.6 动画配置数据

```java
/**
 * 定义一个治疗行为的客户端动画表现。
 */
public record TreatmentAnimationProfile(

        /** 动画配置ID。 */
        ResourceLocation id,

        /** 中文说明。 */
        String descriptionZhCn,

        /** 动画类型。 */
        TreatmentAnimationType animationType,

        /** 动画兼容模式。 */
        TreatmentAnimationCompatibilityMode compatibilityMode,

        /** 动画是否使用双手。 */
        boolean twoHanded,

        /** 动画总持续时间。 */
        int durationTicks,

        /** 动画过程中触发的音效节点。 */
        List<AnimationSoundKeyframe> soundKeyframes,

        /** 动画过程中触发的粒子节点。 */
        List<AnimationParticleKeyframe> particleKeyframes,

        /** 第一人称手部和物品的关键帧。 */
        List<FirstPersonTransformKeyframe> firstPersonKeyframes,

        /** 第三人称手臂姿势关键帧。 */
        List<ThirdPersonPoseKeyframe> thirdPersonKeyframes
) {
}
```

所有关键帧使用归一化进度：

```text
0.0
→ 动画开始

0.5
→ 动画进行到一半

1.0
→ 动画结束
```


### 3. 治疗使用进度条

#### 3.1 显示位置

玩家执行具有使用时间的治疗行为时，在物品栏上方的空闲区域显示治疗进度条。

进度条不得覆盖：

```text
快捷栏
经验条
生命图标
体力图标
水分图标
氧气图标
其他LivingSystem主要HUD
```

客户端配置允许调整：

```text
水平位置
垂直位置
宽度
高度
缩放
透明度
是否显示百分比
是否显示治疗名称
是否显示目标身体部位
```


#### 3.2 进度条内容

进度条至少显示：

```text
治疗行为名称
正在使用的物品图标
目标患者
目标身体部位
目标伤势的简要名称
当前进度
剩余时间
```

例如：

```text
正在包扎：左臂切割伤
████████░░ 80%
```

对于系统性治疗：

```text
正在注射：镇痛药
██████░░░░ 60%
```


#### 3.3 服务端权威

进度条只显示服务端同步的治疗进度。

客户端不得通过修改进度条或本地计时直接完成治疗。

同步数据至少包括：

```java
public record TreatmentProgressSnapshot(

        UUID sessionId,
        ResourceLocation treatmentActionId,
        ResourceLocation sourceItemId,
        @Nullable BodyRegion targetRegion,
        @Nullable UUID targetEffectId,
        float progress,
        int remainingTicks,
        boolean interrupted
) {
}
```

客户端允许在两次同步之间进行平滑插值，但最终结果以服务端为准。


### 4. 治疗与恢复统一系统

#### 4.1 基本关系

自然恢复和医疗治疗属于同一个广义治疗系统中的不同组成部分。

```text
自然恢复
→ 玩家身体在满足条件时自行修复

医疗治疗
→ 改善恢复条件、处理恢复障碍或直接稳定伤势

药物治疗
→ 在一定时间内修改生理状态和恢复能力

食物和饮料
→ 为身体恢复提供能量、水分和营养
```

治疗物品通常不直接将伤口变为完全恢复。

治疗物品主要负责：

```text
停止继续恶化
移除恢复障碍
降低并发症
提高恢复速度
提高结构能够恢复的上限
```

最终组织恢复仍由恢复循环逐步完成。


#### 4.2 恢复能力

玩家具有全身基础恢复能力：

```java
/**
 * 表示玩家当前能够用于身体修复的综合恢复能力。
 */
public final class RecoveryCapacity {

    /** 综合恢复能力，范围为0.0到1.0。 */
    private float totalCapacity;

    /** 代谢能量提供的修正。 */
    private float energyFactor;

    /** 水分状态提供的修正。 */
    private float hydrationFactor;

    /** 营养状态提供的修正。 */
    private float nutritionFactor;

    /** 氧合状态提供的修正。 */
    private float oxygenationFactor;

    /** 循环灌注提供的修正。 */
    private float perfusionFactor;

    /** 核心体温提供的修正。 */
    private float temperatureFactor;

    /** 休息和活动状态提供的修正。 */
    private float activityFactor;

    /** 感染、毒素和辐射造成的综合惩罚。 */
    private float complicationPenalty;
}
```

全身恢复能力是动态派生值，不作为独立资源消耗。


#### 4.3 单个伤势恢复速度

单个伤势的恢复速度按照以下顺序计算：

```text
伤势基础恢复速度
× 组织类型修正
× 伤势严重度修正
× 全身恢复能力
× 治疗状态修正
× 固定和稳定修正
× 玩家活动修正
× 感染修正
× 毒素和辐射修正
× 服务端恢复倍率
= 实际恢复速度
```

不同组织具有不同恢复速度，例如：

```text
轻度皮肤损伤
→ 恢复较快

软组织损伤
→ 恢复中等

骨折
→ 恢复较慢

神经损伤
→ 恢复非常慢或存在恢复上限

严重大脑和脊髓损伤
→ 无法自然完全恢复
```

所有数值和恢复上限必须允许配置。


#### 4.4 恢复阶段

伤势使用以下简化恢复阶段：

```java
public enum RecoveryStage {

    /** 伤势仍在持续恶化或存在严重未处理问题。 */
    UNSTABLE,

    /** 伤势已经得到控制，但尚未开始明显修复。 */
    STABILIZED,

    /** 组织正在主要修复阶段。 */
    REPAIRING,

    /** 组织完成基础修复，功能逐渐恢复。 */
    REMODELING,

    /** 伤势不再产生有效影响，允许归档或移除。 */
    RESOLVED
}
```

伤势从一个阶段进入下一阶段需要满足条件。

例如切割伤：

```text
出血得到控制
+
异物已经取出
+
污染低于阈值
→ 从UNSTABLE进入STABILIZED
```

```text
能量、水分和灌注满足要求
+
没有严重感染
→ 从STABILIZED进入REPAIRING
```


#### 4.5 治疗状态对恢复的作用

治疗状态通过提供 `RecoveryModifier` 影响恢复。

```java
/**
 * 表示一个治疗状态对伤势恢复产生的修正。
 */
public record RecoveryModifier(

        /** 修正来源。 */
        ResourceLocation sourceId,

        /** 恢复速度倍率。 */
        float recoveryRateMultiplier,

        /** 可恢复完整度上限修正。 */
        float recoverableIntegrityBonus,

        /** 是否满足伤势稳定条件。 */
        boolean providesStability,

        /** 是否阻止伤口继续污染。 */
        boolean preventsContamination,

        /** 是否降低活动造成的恶化。 */
        boolean reducesActivityAggravation
) {
}
```

例如：

```text
绷带
→ 降低出血
→ 降低污染增长
→ 轻度提高恢复速度

缝合
→ 提供伤口稳定
→ 提高伤口闭合速度
→ 降低重新裂开概率

夹板
→ 提供骨折稳定
→ 提高骨折恢复速度
→ 降低移动造成的恶化

抗菌药物
→ 降低细菌复制
→ 间接移除感染造成的恢复惩罚
```


#### 4.6 自然恢复条件

轻度伤势即使不使用医疗物品，也会缓慢恢复。

允许自然恢复的伤势包括：

```text
轻度擦伤
轻度切割伤
轻度钝挫伤
轻度软组织损伤
轻度烧伤
轻度冻伤
轻度脑震荡
```

以下情况允许阻止或显著减慢自然恢复：

```text
持续出血
存在未取出的异物
伤口污染严重
伤口已经感染
骨折未固定
身体部位持续高强度活动
代谢能量不足
严重脱水
循环灌注不足
持续缺氧
核心体温异常
严重中毒
高辐射影响
```


#### 4.7 玩家活动影响

恢复系统需要记录玩家近期活动强度。

```java
public enum ActivityLevel {

    /** 睡眠、躺卧或长时间静止。 */
    RESTING,

    /** 普通站立和缓慢移动。 */
    LIGHT,

    /** 行走、普通挖掘和一般活动。 */
    MODERATE,

    /** 冲刺、跳跃、游泳和持续战斗。 */
    HEAVY,

    /** 长时间高强度活动。 */
    EXTREME
}
```

活动影响：

```text
体力消耗
能量消耗
出血速度
再出血风险
伤口重新裂开概率
骨折不稳定性
疼痛
恢复速度
```

活动影响必须使用阈值、冷却时间和累计强度处理。

不得因为玩家每移动一个游戏刻，就直接对伤口反复造成新的结构伤害。

固定逻辑：

```text
累计活动负荷
→ 超过伤势允许阈值
→ 触发一次恶化判定
→ 进入冷却时间
```


#### 4.8 睡眠与时间跳过

Minecraft 睡眠会快速跳过夜晚，因此恢复系统必须明确时间跳过规则。

支持：

```java
public enum TimeSkipRecoveryPolicy {

    /** 跳过的时间不产生额外恢复。 */
    NONE,

    /** 根据配置上限计算有限恢复。 */
    CLAMPED,

    /** 完整模拟全部跳过时间。 */
    FULL
}
```

默认采用：

```text
CLAMPED
```

避免玩家通过反复睡觉立即治愈严重伤势。


#### 4.9 离线恢复

玩家退出服务器后是否继续恢复由服务端配置决定。

```java
public enum OfflineRecoveryPolicy {

    /** 离线期间完全停止恢复。 */
    PAUSED,

    /** 根据离线时间计算有限恢复。 */
    CLAMPED,

    /** 完整计算离线期间恢复。 */
    FULL
}
```

无论选择哪一种方式，都必须设置最大模拟时间，避免玩家长时间离线后一次执行大量循环。


### 6. 昏迷系统

#### 6.1 昏迷定义

昏迷是 LivingSystem 内部的意识状态，不是原版药水效果。

昏迷由以下因素导致：

```text
脑功能下降
循环灌注不足
严重失血
严重缺氧
严重毒素影响
严重感染
电击
极端体温
强烈疼痛或休克
魔法异常
```


#### 6.2 昏迷状态

采用：

```java
public enum ConsciousnessState {

    /** 正常清醒。 */
    ALERT,

    /** 意识轻度模糊。 */
    IMPAIRED,

    /** 接近失去意识。 */
    CRITICAL,

    /** 已经失去意识。 */
    UNCONSCIOUS,

    /** 已经满足死亡条件。 */
    TERMINAL
}
```


#### 6.3 进入与恢复阈值

进入昏迷和恢复清醒必须使用不同阈值，避免反复切换。

例如：

```text
意识值低于0.10
→ 进入昏迷

意识值恢复到0.25以上
→ 允许逐渐苏醒
```

具体阈值必须允许配置。


#### 6.4 昏迷期间限制

昏迷期间默认：

```text
不能移动
不能攻击
不能挖掘
不能使用物品
不能主动打开普通交互界面
不能自行进行治疗
```

但仍允许：

```text
其他玩家对其治疗
继续受到伤害
继续出血
继续积累氧债
继续执行毒素和疾病循环
接收输液、输血和供氧
```

昏迷玩家退出并重新进入世界后，意识状态必须继续保存。


#### 6.5 昏迷表现

客户端表现包括：

```text
画面逐渐变暗
声音变得低沉
心跳和呼吸声
画面模糊
完全黑屏
显示模糊的身体状态提示
```

昏迷不应默认改变玩家碰撞箱。

系统提供趴卧模型、特殊姿势和搬运昏迷玩家的扩展接口。


### 7. 死亡判定

#### 7.1 死亡不是单纯血液归零

LivingSystem 的死亡条件包括：

```text
血液容量低于不可维持阈值并持续一定时间
心脏功能完全失效
大脑功能完全失效
严重缺氧持续超过终末时间
循环灌注完全崩溃
致死毒素负荷
终末感染或多系统衰竭
虚空或强制击杀
配置定义的不可逆伤势
```

死亡条件由统一的：

```java
DeathConditionResolver
```

计算。

各个伤口、毒素或器官模块不得直接独立调用玩家死亡。


#### 7.2 原版死亡流程

当 LivingSystem 判定玩家死亡时：

```text
生成死亡原因快照
→ 生成死亡报告
→ 设置内部死亡处理标记
→ 触发LivingSystem专用致死来源
→ 进入Minecraft正常死亡流程
```

必须使用重入保护，避免专用致死来源再次被伤害解析系统转换成新的伤口。

死亡后继续遵循原版或服务器已有规则：

```text
死亡画面
重生
物品掉落
经验掉落
保留物品规则
死亡计分
其他模组死亡事件
```


### 8. 死亡报告

#### 8.1 报告生成时机

玩家满足死亡条件时，服务端在进入原版死亡流程前生成：

```java
/**
 * 保存玩家本次死亡的医学和伤势原因摘要。
 */
public record DeathReportSnapshot(

        /** 报告唯一ID。 */
        UUID reportId,

        /** 玩家ID。 */
        UUID playerId,

        /** 死亡发生的游戏时间。 */
        long deathGameTime,

        /** 主要死亡原因。 */
        ResourceLocation primaryCauseId,

        /** 直接导致死亡的生理崩溃。 */
        ResourceLocation terminalFailureId,

        /** 主要伤势或健康影响。 */
        List<DeathContribution> majorContributions,

        /** 次要促进因素。 */
        List<DeathContribution> secondaryContributions,

        /** 死亡前最终生命体征。 */
        FinalVitalSnapshot finalVitals,

        /** 死亡前接受的主要治疗。 */
        List<ResourceLocation> appliedTreatments,

        /** 用于显示的关键事件时间线。 */
        List<DeathTimelineEntry> timeline
) {
}
```


#### 8.2 报告内容

死亡报告至少显示：

```text
死亡时间
死亡地点
主要死亡原因
直接生理失败原因
主要伤势
次要促进因素
死亡前血液容量
死亡前意识状态
死亡前氧合和循环状态
是否存在未处理出血
是否存在严重感染、中毒或辐射影响
死亡前接受过的主要治疗
造成最初伤势的实体、物品或环境来源
```

示例：

```text
主要死亡原因：
失血性休克

直接原因：
胸腔穿透伤导致持续性内出血

伤势来源：
骷髅发射的箭矢

促进因素：
伤口内存在异物
未进行有效止血
身体处于脱水状态

死亡前状态：
血液容量：18%
意识：昏迷
循环灌注：极低
血氧：低
```


#### 8.3 死亡原因回溯

死亡报告不应只记录最后一次受到的伤害。

需要按照因果关系回溯：

```text
终末生理失败
→ 导致该失败的生理异常
→ 导致异常的伤势或健康影响
→ 产生伤势的DamageSource或暴露来源
```

例如：

```text
死亡
→ 循环灌注崩溃
→ 大量失血
→ 左腿主要血管切割伤
→ 某玩家使用长剑攻击
```

报告将最主要的因果链作为主要死亡原因，其余重要状态作为促进因素。


#### 8.4 死亡报告界面

进入死亡画面后，允许：

```text
自动弹出死亡报告摘要
或
在死亡画面增加“查看死亡报告”按钮
```

默认行为：

```text
首次显示简要摘要
→ 玩家点击按钮查看完整报告
```

完整报告界面提供：

```text
报告正文
伤势列表
关键时间线
复制文本按钮
返回死亡界面按钮
```

报告界面不能阻止正常重生。


#### 8.5 报告保存

保存玩家最近一次死亡报告。

通过服务端配置设置历史报告数量：

```text
0
→ 不保存历史，只显示当前报告

1
→ 只保存最近一次

大于1
→ 保存有限数量历史报告
```

必须设置上限，避免死亡报告无限增加存档大小。

提供：

```text
/livingsystem deathreport last
/livingsystem deathreport view <player>
/livingsystem deathreport clear <player>
```


## 15. 医疗检查、健康界面、HUD、设置与按键

### 15.1 医疗检查与信息分级

#### 15.1.1 信息显示原则

LivingSystem 不应默认向玩家显示所有内部精确数据。

玩家获得的信息分为以下等级：

```java
public enum MedicalInformationLevel {

    /** 玩家根据自身感受获得的信息。 */
    SUBJECTIVE,

    /** 玩家能够直接观察到的信息。 */
    OBSERVED,

    /** 使用基础设备测量得到的信息。 */
    MEASURED,

    /** 使用高级设备或检查手段获得的信息。 */
    DIAGNOSED,

    /** 仅管理员和调试工具可见的完整内部数据。 */
    DEBUG
}
```


#### 15.1.2 玩家默认可见信息

无需医疗设备时，玩家允许看到：

```text
疼痛
眩晕
呼吸困难
恶心
疲劳
寒冷或炎热
肢体活动异常
可见外出血
可见伤口
明显骨折
意识状态
身体部位综合严重程度
```

玩家默认不应看到：

```text
精确心率
精确血压
精确血氧
精确病原体负荷
精确毒素剂量
精确辐射累计剂量
精确内部出血速度
精确器官完整度
```


#### 15.1.3 医疗检查结果

医疗设备产生的结果应保存为带时间和精度的检查快照。

```java
/**
 * 表示一次医疗设备检查得到的结果。
 */
public record MedicalObservationSnapshot(

        /** 检查结果ID。 */
        UUID id,

        /** 被检查玩家。 */
        UUID patientId,

        /** 使用的设备或检查定义。 */
        ResourceLocation examinationId,

        /** 信息等级。 */
        MedicalInformationLevel informationLevel,

        /** 检查结果数据。 */
        Map<ResourceLocation, MedicalObservationValue> values,

        /** 检查完成时间。 */
        long measuredGameTime,

        /** 结果有效时间。 */
        long validityDuration,

        /** 结果精确度。 */
        float accuracy
) {
}
```

检查结果不是实时读取内部数据库，而是某一时刻的测量快照。

例如五分钟前测量的血压，在玩家大量出血后超过有效时长后过期。


#### 15.1.4 医疗检查工具

包含以下检查工具：

```text
体温计
→ 核心体温

脉搏血氧仪
→ 心率、血氧

血压计
→ 血压和循环状态

听诊器
→ 呼吸音、心脏状态的模糊判断

便携式生命体征监护仪
→ 心率、血压、血氧、呼吸频率

辐射剂量计
→ 当前环境剂量率

个人剂量计
→ 玩家累计辐射剂量的估算值

盖革计数器
→ 周围辐射源和污染物

毒素检测试剂
→ 特定毒素或毒素分类

病原体检测试剂
→ 特定病原体或病原体分类

伤口检查工具
→ 出血、污染、异物和伤口深度

便携式成像设备
→ 骨折、异物和部分内部损伤

高级医疗扫描仪
→ 多项结构和生命体征
```

不同设备显示的信息范围和精度必须由数据定义。


#### 15.1.5 检查其他玩家

检查其他玩家时必须满足服务端条件，例如：

```text
检查者与患者距离足够近
患者处于允许检查的状态
检查者持有正确设备
检查过程完整完成
设备仍然可用
```

服务器允许配置是否需要患者同意。

昏迷玩家默认允许被其他玩家检查和治疗。

客户端发送检查请求后，服务端重新验证全部条件。


### 15.2 健康系统界面

#### 15.2.1 打开方式

在游戏按键设置中增加：

```text
分类：LivingSystem（生命系统）
```

默认按键：

```text
H
→ 打开健康系统界面
```

其他可选按键默认不绑定，避免与整合包其他模组冲突：

```text
打开快速治疗界面
打开医疗检查界面
切换身体部位HUD
切换状态图标列表
打开HUD布局编辑器
```

所有按键均允许玩家自行修改。


#### 15.2.2 健康系统主界面

按 H 打开健康系统主界面。

界面右上角显示齿轮按钮，点击后进入 LivingSystem 客户端设置界面。

主界面分为以下页签：

```text
概览
伤势
生命体征
暴露与疾病
治疗与药物
防护装备
恢复状态
```

玩家只能看到当前信息等级允许显示的内容。


#### 15.2.3 概览页布局

##### 左上区域：身体部位状态

以 Minecraft 方块人形样式显示：

```text
头部
胸腔
腹部
左臂
右臂
左腿
右腿
```

部位颜色：

```text
黑色
→ 危重，存在立即危及生命或需要高级处理的异常

红色
→ 严重，已经明显影响身体功能，需要尽快处理

黄色
→ 警告，存在异常，会继续恶化

绿色
→ 正常、相对正常或只有无实际影响的轻微异常
```

青蓝色不应覆盖异常严重程度。

如果身体部位既存在伤势又受到增强效果，应采用：

```text
危险颜色
+
青蓝色边框、光效或增强图标
```

例如胸腔存在严重伤势并受到增益时，仍显示红色，而不是显示青蓝色。

颜色不能作为唯一识别方式，还应使用：

```text
图标
文字
边框样式
闪烁
严重程度标记
```

以支持色觉障碍玩家。


##### 右上区域：部位症状列表

按照以下顺序显示：

```text
头部
胸腔
腹部
左臂
右臂
左腿
右腿
```

没有问题的部位不显示。

每个部位下显示：

```text
可感知症状
可观察伤口
已知治疗状态
严重程度
```

内容超出时显示滚动条。

列表默认只显示玩家已知的信息，不显示隐藏的器官数值。


##### 下方区域：基础状态卡片

显示：

```text
血液容量
体力
水分
代谢能量
意识状态
核心体温
呼吸状态
恢复能力
```

对于需要设备才能确定的数值：

```text
未测量
估算
正常范围
偏低
严重偏低
```

而不是始终显示精确数值。

例如未使用血压计时：

```text
循环状态：偏低
```

使用血压计后：

```text
血压：具体测量值
测量时间：30秒前
```


##### 下方辅助区域：当前处理与恢复

显示：

```text
正在进行的治疗
已经应用的绷带、夹板或设备
当前药物
恢复阶段
阻碍恢复的主要因素
```

例如：

```text
左臂切割伤
恢复阶段：稳定
恢复阻碍：轻度脱水
当前处理：加压绷带
```


#### 15.2.4 伤势页

按照身体部位列出已知伤势。

每个伤势可显示：

```text
伤势名称
身体部位
严重程度
是否出血
是否存在异物
是否污染
是否固定或包扎
恢复阶段
可执行的治疗
```

只有经过检查后才能显示更详细内容。

选择伤势后，进入治疗目标选择流程。


#### 15.2.5 生命体征页

显示：

```text
血液容量
心率
血压
血氧
呼吸频率
核心体温
意识状态
循环灌注
```

未经测量的数据应显示为：

```text
未知
无法判断
估算范围
```

检查设备测量后，应显示：

```text
测量值
测量时间
设备名称
测量精度
结果是否已经过期
```


#### 15.2.6 暴露与疾病页

显示玩家已经感知、观察或诊断出的：

```text
中毒
病原体感染
辐射暴露
环境缺氧
魔法异常
```

病原体未被诊断前，允许只显示：

```text
疑似感染
发热原因不明
呼吸道异常
```

辐射未被测量前，允许只显示：

```text
恶心
虚弱
原因不明的恢复下降
```


#### 15.2.7 治疗与药物页

显示：

```text
当前治疗过程
已经应用的治疗状态
当前药物
药物剩余作用时间
医疗设备连接状态
治疗失败或中断原因
```


#### 15.2.8 防护装备页

显示：

```text
各身体部位覆盖状态
穿刺和切割防护
呼吸防护
化学防护
生物防护
辐射屏蔽
装备密封性
过滤器剩余容量
装备污染程度
```

普通玩家能够查看自己装备的已知参数，但隐藏其他模组未公开的内部数据。


### 15.3 游戏内HUD

#### 15.3.1 身体部位状态HUD

默认位于屏幕左上角。

使用简化方块人形显示七个身体部位状态。

HUD只显示：

```text
身体部位颜色
危重图标
出血图标
骨折图标
特殊增强图标
```

不显示完整伤势文本。


#### 15.3.2 血液容量HUD

使用原版心形图标样式显示血液容量。

```text
最大血液容量：5000 mL
显示单位：20
每1显示单位：250 mL
每2显示单位：1个完整心形
```

计算公式：

```text
显示值
= 当前血液容量 ÷ 最大血液容量 × 20
```

显示值不能反向修改真实血液容量。


#### 15.3.3 体力HUD

原版饥饿值图标改为显示当前体力。

每个玩家的体力上限允许不同，因此采用比例显示：

```text
当前体力 ÷ 最大体力 × 20
```

体力图标只负责显示 LivingSystem 体力，不再表示原版饥饿值。


#### 15.3.4 水分HUD

在体力图标上方显示水分值。

水分同样映射为20个显示单位，使用完整和半个水滴图标。


#### 15.3.5 呼吸HUD

原版气泡图标继续用于显示呼吸储备。

以下情况显示：

```text
水下
低氧环境
气道阻塞
呼吸设备供氧
```

气泡耗尽后不直接扣原版生命值，而是增加氧债和意识风险。


#### 15.3.6 状态图标列表

在屏幕右侧显示纵向状态图标列表。

按照以下优先级排序：

```text
立即致命或危重
严重出血和呼吸异常
严重器官或意识异常
中毒、感染和辐射
骨折和神经障碍
疼痛和体温异常
药物和增强效果
恢复与治疗状态
```

状态图标显示：

```text
图标
名称
严重程度
剩余时间
身体部位
堆叠数量
```

客户端设置可控制：

```text
是否显示名称
是否显示时间
是否显示轻微状态
最大显示数量
排序方式
图标大小
透明度
```


#### 15.3.7 其他必要HUD

还必须提供：

```text
治疗进度条
检查进度条
昏迷和意识提示
严重暴露警告
医疗设备连接状态
过滤器即将耗尽提示
装备密封失效提示
```


#### 15.3.8 HUD布局编辑器

所有 LivingSystem HUD 元素都应支持：

```text
拖动位置
修改缩放
修改透明度
隐藏
恢复默认位置
显示测试数据预览
```

HUD位置和大小属于客户端个人配置，不影响服务端游戏规则。


### 15.4 设置界面与按键设置

#### 15.4.1 设置界面分类

LivingSystem 专属设置界面包含：

```text
常规
HUD布局
HUD内容
画面效果
声音效果
治疗动画
信息显示
无障碍
兼容性
```


#### 15.4.2 客户端个人设置

允许玩家调整：

```text
HUD是否显示
HUD位置和大小
身体部位面板大小
状态图标数量
是否显示精确百分比
是否显示症状名称
是否显示治疗剩余时间
镜头晃动强度
画面模糊强度
暗角强度
耳鸣音量
心跳音量
呼吸音量
治疗动画强度
是否使用双手动画
是否减少闪烁效果
色觉辅助模式
文字大小
```


#### 15.4.3 服务端配置显示

影响游戏实际规则的服务端配置不能由普通客户端修改。

设置界面提供只读页面，显示：

```text
当前启用的健康系统
伤势和症状开关
全局难度倍率
治疗规则
死亡规则
环境暴露规则
```

拥有权限的管理员通过命令或专用管理界面修改。


## 16. 模组物品、方块与医疗生产

### 16.1 模组物品

#### 16.1.1 基础外伤处理物品

```text
普通绷带
加压绷带
止血纱布
止血带
伤口闭合贴
胸部密封敷料
烧伤敷料
烧伤凝胶
冷却包
保温毯
复温包
```


#### 16.1.2 清洁和消毒物品

```text
无菌盐水
伤口清洗剂
消毒剂
去污液
无菌纱布
清创材料
```


#### 16.1.3 医疗工具

```text
镊子
异物钳
缝合包
皮肤钉合器
拆线工具
临时夹板
标准夹板
高级固定支具
胸腔减压针
胸腔引流工具
综合性手术包
```


#### 16.1.4 生命支持物品

```text
输液器
补液袋
营养液
血液袋
血浆或血液替代品
输血器
氧气瓶
氧气面罩
便携式供氧设备
```


#### 16.1.5 药物

```text
口服镇痛药
注射镇痛药
抗菌药
抗病毒药
抗真菌药
抗寄生虫药
退热药
止吐药
促凝药
麻醉药
特定解毒剂
辐射污染清除药物
兴奋剂
镇静剂
```

所有具体药物效果应通过数据文件定义。


#### 16.1.6 医疗检查物品

```text
体温计
血压计
脉搏血氧仪
听诊器
便携式生命体征监护仪
伤口检查工具
病原体检测试剂
毒素检测试剂
辐射剂量计
个人剂量计
盖革计数器
便携式成像设备
高级医疗扫描仪
```


#### 16.1.7 防护装备与耗材

```text
防刺背心
防弹防护装备
防化手套
防护靴
防护眼镜
呼吸面罩
防毒面具
生物防护面罩
化学防护服
辐射防护服
铅防护围裙
普通过滤器
生物过滤器
化学过滤器
放射性粉尘过滤器
```


#### 16.1.8 医疗生产材料

```text
空注射器
空药瓶
无菌容器
蒸馏水
无菌水
化学试剂
培养基
疫苗基质
药物原料
过滤材料
血液保存袋
检测样本容器
```


### 16.2 模组新增方块与外部生产兼容

#### 16.2.1 医疗检查设备

```text
生命体征监护仪
血液分析仪
毒素分析仪
病原体检测仪
辐射检测仪
医学成像设备
综合医疗扫描仪
```


#### 16.2.2 治疗设备

```text
医疗床
手术台
输液设备
输血设备
氧气供应设备
呼吸支持设备
除颤或心脏支持设备
去污淋浴设备
辐射污染处理设备
```


#### 16.2.3 医疗物资生产设备

```text
药品工作台
无菌处理台
蒸馏设备
离心设备
培养设备
疫苗制备设备
针剂灌装设备
血液保存设备
消毒设备
医疗材料加工设备
```


#### 16.2.4 储存设施

```text
药品柜
医疗工具柜
冷藏药品柜
血液冷藏设备
危险样本储存柜
放射性物质储存箱
```


#### 16.2.5 方块禁用原则

模组方块注册后，不应根据运行时配置动态取消注册，否则会破坏已有存档和客户端同步。

禁用某个方块时应采用：

```text
隐藏或移除默认配方
阻止新的正常获取方式
禁用机器处理功能
显示中文禁用提示
保留已有方块和存档数据
```


#### 16.2.6 外部科技模组生产兼容

LivingSystem 不应强制要求使用自己的生产设备。

生产配方应抽象为：

```text
输入材料
处理条件
输出物品
处理时间
所需机器角色
```

定义机器角色：

```java
public enum MedicalMachineRole {
    STERILIZER,
    DISTILLER,
    CENTRIFUGE,
    CHEMICAL_PROCESSOR,
    CULTURE_CHAMBER,
    PHARMACEUTICAL_ASSEMBLER,
    INJECTION_FILLER,
    REFRIGERATED_STORAGE
}
```

整合包作者能够选择：

```text
使用LivingSystem自带机器
使用原版工作台或熔炉
使用其他模组机器
完全关闭该物品生产
通过战利品或任务获得
```

例如整合包使用 Create 时，允许：

```text
关闭LivingSystem针剂灌装设备的默认配方
→ 使用数据包添加Create加工配方
→ 最终仍然产出LivingSystem针剂物品
```

模组核心逻辑不依赖特定生产机器。


## 17. 完整功能闭环与全局不变量

完整运行链路固定为：

```text
外部伤害、环境暴露、摄入、玩家活动或治疗请求
→ 构建统一上下文
→ 功能开关和规则快照校验
→ 防护、来源、机制和目标解析
→ 创建或更新健康影响
→ 事务式提交结构和全身状态变化
→ 分频运行生理、暴露、药物、恢复和继发影响
→ 计算派生体征、意识、昏迷和死亡
→ 生成症状和游戏性输出
→ 生成HUD、健康界面、动画、音效和医疗检查快照
→ 按脏标记同步客户端
```

全局不变量：

1. `PlayerHealthData` 是玩家健康数据的唯一聚合根。
2. 结构完整度只能由 `StructureDamageService` 修改。
3. 死亡只能由 `DeathConditionResolver` 提交。
4. 症状只读取健康状态，不反向写入伤势或生理资源。
5. 局部医疗物品一次只处理一个明确伤势实例。
6. 系统性药物只执行预注册安全操作，不通过反射修改任意字段。
7. 原版护甲减伤只计算一次；LivingSystem防护只处理穿透、组织分布和环境屏障。
8. 环境暴露按剂量累积，不通过每tick原版伤害模拟。
9. 所有客户端请求都由服务端重新验证。
10. 所有配置重载均先完整构建新规则快照，再原子替换旧快照。

# 第二部分：代码架构规格

## 18. 架构目标与分层

代码采用服务端权威、领域模型集中、规则数据驱动、事件适配与业务服务分离的架构。

固定分层：

```text
接入层：NeoForge事件、网络请求、按键、菜单、物品交互、兼容适配器
→ 上下文层：DamageContext、ExposureContext、TreatmentContext、ActivitySnapshot
→ 领域服务层：解析、伤势、生理、症状、治疗、恢复、防护、死亡
→ 聚合状态层：PlayerHealthData
→ 规则层：ResolvedRulesSnapshot
→ 展示层：客户端只读快照、Screen、HUD、动画和声音
```

事件监听器只构建上下文并调用服务。领域状态类不直接访问客户端、注册事件或发送网络包。

## 19. 包结构

```text
com.<author>.livingsystem
├─ LivingSystemMod
├─ bootstrap
│  ├─ ModRegistries
│  ├─ ModAttachments
│  ├─ ModDataComponents
│  ├─ ModPayloads
│  ├─ ModConfigs
│  └─ ModContent
├─ api
│  ├─ LivingSystemApi
│  ├─ damage
│  ├─ exposure
│  ├─ protection
│  ├─ treatment
│  ├─ examination
│  └─ event
├─ domain
│  ├─ body
│  ├─ effect
│  ├─ physiology
│  ├─ symptom
│  ├─ treatment
│  ├─ recovery
│  ├─ exposure
│  ├─ protection
│  ├─ medication
│  └─ death
├─ service
│  ├─ damage
│  ├─ hit
│  ├─ structure
│  ├─ physiology
│  ├─ symptom
│  ├─ treatment
│  ├─ recovery
│  ├─ exposure
│  ├─ protection
│  ├─ examination
│  ├─ resource
│  ├─ death
│  ├─ migration
│  └─ scheduling
├─ rule
│  ├─ definition
│  ├─ registry
│  ├─ codec
│  ├─ validation
│  ├─ reload
│  └─ snapshot
├─ persistence
│  ├─ dto
│  ├─ codec
│  ├─ migrator
│  └─ repository
├─ network
│  ├─ payload
│  ├─ handler
│  └─ snapshot
├─ compatibility
│  ├─ vanilla
│  ├─ neoforge
│  └─ adapter
├─ client
│  ├─ state
│  ├─ screen
│  ├─ hud
│  ├─ animation
│  ├─ sound
│  ├─ key
│  └─ config
├─ content
│  ├─ item
│  ├─ block
│  ├─ blockentity
│  ├─ menu
│  └─ recipe
├─ command
└─ test
```

客户端包只能在物理客户端加载。公共和服务端代码不得引用 `net.minecraft.client`。

## 20. 玩家健康聚合根

玩家只保存一个健康聚合根：

```java
public final class PlayerHealthData {
    /** 持久化结构版本。 */
    private int schemaVersion;
    /** 当前规则快照版本，仅用于诊断和重算。 */
    private long rulesVersion;
    /** 全身权威生理状态。 */
    private PhysiologyState physiology;
    /** 七个身体部位的结构状态。 */
    private EnumMap<BodyRegion, BodyRegionState> bodyRegions;
    /** 全部活动健康影响。 */
    private Map<UUID, HealthEffectInstance> activeEffects;
    /** 已应用治疗。 */
    private Map<UUID, AppliedTreatmentState> appliedTreatments;
    /** 当前药物剂量实例。 */
    private List<MedicationEffectInstance> medications;
    /** 环境暴露累积器。 */
    private Map<ResourceLocation, ExposureAccumulator> exposureAccumulators;
    /** 活动治疗会话。 */
    private Map<UUID, TreatmentSession> treatmentSessions;
    /** 有限数量的医疗检查结果。 */
    private List<MedicalObservationSnapshot> observations;
    /** 有限数量的死亡报告。 */
    private List<DeathReportSnapshot> deathReports;
    /** 运行时脏标记，不持久化。 */
    private transient HealthDirtyFlags dirtyFlags;
}
```

禁止为血液、器官、伤势、症状、药物和治疗分别注册多个玩家附件。`PlayerHealthData` 是唯一聚合根。

### 20.1 数据分类

持久化权威状态：

```text
PhysiologyState
BodyRegionState与StructureState
HealthEffectInstance
AppliedTreatmentState
MedicationEffectInstance
ExposureAccumulator
未完成TreatmentSession
MedicalObservationSnapshot
DeathReportSnapshot
schemaVersion
```

可重建缓存：

```text
DerivedVitals
身体部位功能
SymptomSnapshot
GameplayEffectSnapshot
HUD摘要
防护临时结果
恢复能力
```

瞬时输入：

```text
DamageContext
ExposureContext
TreatmentContext
ActivitySnapshot
ProtectionResult
```

## 21. 运行时对象、DTO与引用规则

1. 持续变化的运行时状态使用可变类。
2. 网络 DTO、配置定义和持久化 DTO 使用不可变 `record`。
3. 持久化对象不得保存 `Entity`、`Level`、`DamageSource`、完整 `ItemStack` 或客户端对象。
4. 跨对象引用使用 UUID 或 `ResourceLocation`。
5. 同一来源产生的多个健康影响共享 `sourceEventId`。
6. 继发影响保存 `parentEffectId`，并使用去重键阻止每轮重复创建。

## 22. 配置与规则架构

配置分为：

```text
SERVER配置：功能开关、全局倍率、安全限制和更新频率
CLIENT配置：HUD、画面、声音、动画和无障碍偏好
数据包定义：伤势、症状、毒素、病原体、辐射、药物、治疗、装备、环境、摄入物和兼容映射
```

加载流程：

```text
读取内置数据和数据包
→ Codec解析
→ 数值范围校验
→ 注册表引用校验
→ 依赖和循环引用校验
→ 按覆盖优先级合并
→ 构建ResolvedRulesSnapshot
→ 原子替换旧快照
→ 清理派生缓存并重新计算
```

运行时服务只读取 `ResolvedRulesSnapshot`，不得直接读取 TOML 或 JSON。

固定覆盖顺序：

```text
LivingSystem内置默认数据
→ LivingSystem官方兼容数据
→ 整合包数据包
→ 世界数据包
→ 服务端功能策略和全局倍率
```

NeoForge Data Map 用于注册对象到配置对象的映射；独立定义使用 Codec 驱动的数据注册表或资源重载管理器；兼容数据使用加载条件限定目标模组存在时加载。

## 23. 核心服务边界

```text
FeatureGateService：规则层级解析和关闭策略
RulesSnapshotService：加载、校验、构建和原子切换规则快照
DamageContextFactory：从伤害事件构建统一伤害上下文
DamageProfileResolver：解析DamageType、武器、实体和投射物配置
HitLocationService：根据证据链确定身体部位
ProtectionResolver：统一计算创伤与环境防护，防止重复减伤
HealthEffectFactory：创建对应健康影响实例
StructureDamageService：唯一允许修改结构完整度的服务
PhysiologyEngine：执行血液、呼吸、循环、体温、毒素、病原体、辐射和意识循环
SecondaryEffectEngine：创建和更新继发影响
SymptomEngine：从权威状态生成症状快照
GameplayEffectAggregator：统一汇总属性、操作、画面和声音输出
TreatmentService：验证并推进治疗会话
RecoveryEngine：统一计算自然恢复和治疗增强恢复
ConsumableEffectService：处理食物、饮料和其他摄入物
MedicationService：处理药物吸收、作用和代谢
EnvironmentalExposureSampler：采集玩家周围环境
EnvironmentalHazardRegistry：管理环境危害定义
ExposureAccumulatorService：合并环境暴露剂量
MedicalExaminationService：执行检查并生成带时效的观察快照
MedicalObservationRegistry：保存和同步检查结果
VanillaResourceBridge：替换原版生命、饥饿、回血和溺水行为
ConsciousnessService：处理意识状态、昏迷和苏醒
DeathConditionResolver：唯一死亡判定入口
DeathReportService：构建、保存和同步死亡报告
LivingHudLayoutManager：管理HUD布局
StatusIconAggregator：汇总状态图标
MedicalContentRegistry：管理医疗物品和设备定义
MedicalProductionService：管理医疗物资生产抽象
HealthTickScheduler：分频调度各子系统
PlayerHealthRepository：统一读写玩家健康聚合根
PlayerHealthDataMigrationService：执行逐版本迁移
HealthDebugCommandService：提供调试命令
HealthPerformanceProfiler：记录模块性能
```

## 24. 事件处理管线

### 24.1 伤害管线

```text
原版和其他模组完成攻击、护甲、附魔和减伤结算
→ 构建DamageContext
→ 检测LivingSystem专用致死重入标记
→ 解析DamageProfile和HarmMechanism
→ 解析ProtectionResult
→ 判断局部或全身作用
→ 解析命中部位
→ 创建HealthEffectInstance
→ 通过StructureDamageService提交结构损伤
→ 写入PlayerHealthData事务
→ 标记生理、症状和同步脏状态
→ VanillaResourceBridge阻止重复扣除原版生命值
```

### 24.2 环境管线

```text
HealthTickScheduler触发采样
→ EnvironmentalExposureSampler读取维度、群系、方块、流体、实体、天气和动态发射源
→ 合并同类暴露源
→ ProtectionResolver计算屏障防护
→ ExposureAccumulatorService累积剂量
→ 达到阈值后创建或更新健康影响
→ 标记生理和症状脏状态
```

### 24.3 治疗管线

```text
客户端选择患者、目标和治疗行为
→ 服务端重新验证距离、物品、目标、状态和并发限制
→ 创建TreatmentSession
→ 调度器推进进度
→ 按提交策略执行安全TreatmentOperation
→ 消耗物品、耐久或设备资源
→ 更新AppliedTreatmentState和恢复修正
→ 重新计算生理、症状与客户端快照
```

### 24.4 摄入管线

```text
物品成功完成食用或饮用
→ 解析ConsumableTreatmentDefinition
→ 根据合并策略生成治疗操作
→ 更新能量、水分、营养、体力或MedicationEffectInstance
→ 不重复消耗物品，不接管其他模组容器返还逻辑
```

## 25. 持久化与数据迁移

玩家健康数据使用可序列化的实体 Data Attachment 保存。附件不承担完整客户端状态同步，网络层发送裁剪后的只读快照。

`schemaVersion` 必须存在。读取流程：

```text
读取原始标签
→ 检查schemaVersion
→ 按版本顺序执行迁移器
→ 校验结果
→ 构建PlayerHealthData
→ 保存最新版本
```

```java
public interface PlayerHealthDataMigrator {
    int sourceVersion();
    int targetVersion();
    CompoundTag migrate(CompoundTag oldData);
}
```

迁移失败时保存旧数据备份、记录中文错误、使用安全默认值恢复并标记管理员检查。不得静默丢弃全部健康状态。

玩家死亡后的数据保留策略由服务端配置决定。默认重生时清除活动伤势、暴露、药物和治疗会话，保留有限死亡报告；永久后遗症系统启用时按定义迁移指定状态。

### 25.1 数据版本

`PlayerHealthData` 必须包含：

```java
/**
 * 玩家健康数据的持久化格式版本。
 */
private int schemaVersion;
```

`schemaVersion` 表示存档数据结构版本，不表示模组版本。

还应分别保存：

```text
schemaVersion
→ 玩家健康数据结构版本

rulesVersion
→ 当前规则快照版本

networkProtocolVersion
→ 网络同步协议版本
```

三者不得混用。


### 25.2 迁移流程

读取玩家数据时：

```text
读取schemaVersion
→ 判断是否为当前版本
→ 按顺序执行每一级迁移
→ 校验迁移结果
→ 构建当前PlayerHealthData
→ 保存为最新版本
```

例如：

```text
版本1
→ 版本2
→ 版本3
```

不得直接从版本1跳过中间迁移逻辑，除非明确提供专用迁移器。


### 25.3 迁移器

```java
/**
 * 将某一旧版本健康数据迁移到下一版本。
 */
public interface PlayerHealthDataMigrator {

    int sourceVersion();

    int targetVersion();

    CompoundTag migrate(CompoundTag oldData);
}
```

所有迁移器必须使用中文注释说明：

```text
旧字段含义
新字段含义
默认值来源
是否存在数据丢失
```


### 25.4 迁移失败保护

迁移失败时不得直接导致服务器崩溃或静默删除玩家数据。

流程：

```text
记录完整错误日志
→ 保存原始旧数据备份
→ 尝试使用安全默认值恢复
→ 标记该玩家数据为需要检查
→ 向管理员提供中文警告
```

提供：

```text
/livingsystem data validate <player>
/livingsystem data migrate <player>
/livingsystem data backup <player>
/livingsystem data reset <player>
```


### 25.5 迁移测试

每个旧版本都应保留测试样本。

测试至少验证：

```text
旧数据允许成功读取
迁移后字段正确
伤口和治疗引用没有丢失
迁移允许重复执行而不继续破坏数据
无效数据能够安全回退
```


## 26. 网络协议

所有 Payload 使用明确协议版本和 `StreamCodec`。客户端请求只包含标识和意图，不包含可信治疗结果或健康数值。

客户端接收：

```text
HudSummaryPayload
HealthScreenSnapshotPayload
TreatmentProgressPayload
MedicalObservationPayload
DeathReportPayload
RulesSummaryPayload
```

客户端发送：

```text
OpenHealthScreenRequest
StartTreatmentRequest
CancelTreatmentRequest
MedicalExaminationRequest
```

服务端必须校验玩家身份、权限、距离、目标存在、物品槽位、会话状态和速率限制。网络线程不得直接修改世界和玩家状态，实际修改切换到逻辑主线程执行。

## 27. 客户端架构

客户端维护只读 `ClientHealthState`：

```text
HUD摘要
健康界面快照
治疗进度
医疗检查结果
死亡报告
服务端规则摘要
```

客户端不得持有或推演完整权威健康数据库。

Screen 使用普通 `Screen`；具有物品槽位的机器界面使用 `Menu` 与 `AbstractContainerScreen`。HUD元素由独立图层渲染器管理。按键通过 LivingSystem 分类注册，H 默认打开健康界面，其他按键默认不绑定。

治疗动画由 `TreatmentAnimationService` 根据 `TreatmentActionDefinition` 驱动，第一人称、第三人称、声音和粒子共享归一化进度。动画失败或冲突时回退到物品原动画或仅显示进度条，不影响服务端治疗进度。

## 28. 属性与操作限制

伤势、症状和药物不得直接向玩家反复添加属性修饰器。

固定流程：

```text
SymptomSnapshot
→ GameplayEffectAggregator
→ GameplayEffectSnapshot
→ 使用固定UUID或固定ResourceLocation更新有限数量修饰器
```

布尔限制由统一控制器处理：冲刺、跳跃、攻击、挖掘、主手、副手、物品使用和昏迷输入锁定。状态解除时必须清除对应修饰器和限制。

## 29. 调度、性能与事务

固定默认频率：

```text
每1 tick：活动治疗会话、立即操作限制
每5 tick：出血、呼吸储备、氧债、危重意识和死亡判定
每10 tick：体力、活动负荷、快速环境采样和过滤器消耗
每20 tick：体温、毒素、药物、生命体征、症状和常规环境采样
每40～100 tick：病原体、辐射生物效应、装备污染和慢速代谢
每100～200 tick：组织修复、骨折恢复、伤口愈合和恢复阶段
```

危重快速通道只提升相关模块频率。使用脏标记避免无变化重算。环境危险源建立区块索引和静态缓存，限制扫描半径并分帧处理。相同来源、同一部位、同一类型的小伤势合并为聚合实例。

一次健康更新使用事务式变更集：

```text
读取旧状态
→ 计算HealthMutationSet
→ 校验数值和引用
→ 原子提交
→ 设置脏标记
→ 触发必要同步
```

任何子模块异常不得留下半写入状态。

### 29.1 基本原则

LivingSystem 不允许所有系统每个游戏刻对所有玩家进行完整计算。

使用统一的健康调度器，将不同系统分配到不同更新频率。


### 29.2 更新频率

```text
每1 tick
→ 当前治疗进度
→ 需要立即响应的操作限制
→ 客户端动画插值由客户端自行处理

每5 tick
→ 外出血和内出血
→ 呼吸储备
→ 氧债
→ 危重意识判断
→ 终末死亡判定

每10 tick
→ 体力消耗与恢复
→ 玩家活动负荷
→ 快速环境暴露采样
→ 防护装备耗材状态

每20 tick
→ 核心体温
→ 毒素
→ 药物
→ 生命体征
→ 症状汇总
→ 普通环境暴露采样

每40至100 tick
→ 病原体发展
→ 辐射生理影响
→ 装备污染
→ 慢速代谢状态

每100至200 tick
→ 组织恢复
→ 骨折恢复
→ 伤口愈合
→ 长期恢复阶段变化
```

所有频率必须通过服务端配置调整，但需要设置安全范围。


### 29.3 危重快速通道

危重玩家不能等待慢速更新。

以下状态进入快速通道：

```text
主要血管出血
严重缺氧
即将昏迷
心脏功能严重下降
终末毒素影响
立即致命的环境暴露
```

快速通道只提高必要模块的更新频率，不应让全部系统恢复到每 tick 全量计算。


### 29.4 脏标记

状态没有变化时，不重复计算全部派生数据。

采用：

```text
伤口数据已变化
结构状态已变化
生理状态已变化
治疗状态已变化
装备防护已变化
规则快照已变化
```

只有对应脏标记存在时，才重新计算：

```text
部位功能
生命体征
症状
HUD摘要
属性修饰器
```


### 29.5 环境采样优化

环境采样器应采用：

```text
限制最大扫描半径
分帧扫描
缓存群系和维度规则
缓存静态方块暴露源
按区块建立危险源索引
仅在需要时计算视线遮挡
实体暴露源使用附近实体查询
玩家未移动时复用部分结果
```

不得每20 tick遍历玩家周围所有方块并逐个匹配全部数据定义。


### 29.6 健康影响数量限制

相同来源产生的大量小伤势应聚合。

例如：

```text
同一次爆炸
+
同一个身体部位
+
同一种伤势
→ 合并为一个聚合伤势实例
```

还应设置：

```text
单个玩家最大活动伤势数量
单类暴露累积器数量
死亡报告历史数量
检查结果历史数量
```

超过上限时应合并、归档或清理，不得无限增长。


### 29.7 网络同步

客户端不需要接收完整内部健康数据。

同步分为：

```text
HUD摘要
健康界面摘要
治疗进度
检查结果
死亡报告
```

只同步发生变化的字段。

普通健康循环不应每 tick 发送网络数据包。

客户端允许在两次同步之间平滑显示体力、血液和治疗进度。


## 30. 兼容API

公开API提供：

```text
注册DamageProfile、WeaponTraumaProfile、EntityAttackProfile和ProjectileTraumaProfile
注册EnvironmentalHazardEmitter
注册ProtectionProfile提供器
注册TreatmentAction、Medication和MedicalExamination
查询只读健康摘要
订阅伤势创建、治疗完成、昏迷和死亡报告事件
提供精确HitEvidence
声明其他模组治疗或回血行为的转换规则
```

API不得暴露可任意修改 `PlayerHealthData` 的公共可变引用。所有变更通过受校验的命令对象和服务执行。

## 31. 物品、方块与生产架构

所有物品和方块始终注册，功能关闭时通过移除配方、禁用机器逻辑和显示中文提示实现，不动态注销注册内容。

医疗生产使用抽象机器角色。LivingSystem内置机器、原版配方和其他科技模组配方均可产出相同医疗物品。核心治疗逻辑不依赖任何特定机器。

可消耗医疗物品使用数据组件保存剂量、容量、过滤器剩余量、污染、血型兼容标识和设备资源；普通配置映射使用 Data Map 或规则定义，不修改其他模组物品类。

## 32. 错误处理与安全降级

1. 单个无效数据定义被隔离并记录中文错误，不阻止其他有效定义加载。
2. 整体规则快照构建失败时保留上一份有效快照。
3. 不存在的属性、物品、实体、方块或标签引用被跳过并输出来源路径。
4. 严重持久化错误触发备份和安全默认状态，不删除原始标签。
5. 客户端缺少非关键动画或贴图时回退到兼容表现。
6. 服务端不信任客户端提交的进度、数值、目标状态和检查结果。
7. 所有数值在提交前执行有限值、范围和 NaN 校验。

## 33. 中文注释、日志与文档生成

1. 公共类、接口、核心方法、持久化字段、网络包、配置字段和复杂公式必须具有中文 JavaDoc。
2. TOML 使用中文 `#` 注释。
3. 严格 JSON 不写非法注释，使用 `description_zh_cn`、`notes_zh_cn` 和自动生成的中文 Markdown 数据字典。
4. 日志、命令帮助、配置校验和管理员提示使用中文，并保留资源ID与文件路径便于定位。
5. 数据生成器同时生成默认数据、示例覆盖文件和中文字段说明。

## 34. 调试与自动化测试

必须实现完整调试命令族和管理员调试界面。所有随机测试支持固定种子。测试覆盖伤害、资源桥接、治疗、暴露、防护、状态机、迁移、网络权限和多人性能。

所有命令帮助、错误信息和输出内容必须使用中文。

命令结构：

```text
/livingsystem health
/livingsystem injury
/livingsystem exposure
/livingsystem treatment
/livingsystem protection
/livingsystem config
/livingsystem data
/livingsystem sync
/livingsystem debug
```


### 34.1 健康状态命令

```text
/livingsystem health view <player>
/livingsystem health reset <player>
/livingsystem health setblood <player> <value>
/livingsystem health setstamina <player> <value>
/livingsystem health sethydration <player> <value>
/livingsystem health setenergy <player> <value>
/livingsystem health setconsciousness <player> <value>
```


### 34.2 伤势命令

```text
/livingsystem injury add <player> <region> <injury_id> <severity>
/livingsystem injury list <player>
/livingsystem injury remove <player> <injury_uuid>
/livingsystem injury bleed <player> <injury_uuid> <rate>
/livingsystem injury fracture <player> <region> <grade>
/livingsystem injury heal <player> <injury_uuid>
```


### 34.3 暴露命令

```text
/livingsystem exposure toxin <player> <toxin_id> <dose>
/livingsystem exposure pathogen <player> <pathogen_id> <dose>
/livingsystem exposure radiation <player> <type> <dose>
/livingsystem exposure magic <player> <affliction_id> <intensity>
/livingsystem exposure clear <player> <effect_id>
```


### 34.4 治疗命令

```text
/livingsystem treatment apply <player> <effect_uuid> <treatment_id>
/livingsystem treatment remove <player> <effect_uuid> <treatment_id>
/livingsystem treatment list <player>
/livingsystem treatment complete <session_uuid>
/livingsystem treatment cancel <session_uuid>
```


### 34.5 防护命令

```text
/livingsystem protection inspect <player>
/livingsystem protection explain <player> <mechanism>
/livingsystem protection refillfilter <player>
/livingsystem protection clearcontamination <player>
```


### 34.6 配置与同步命令

```text
/livingsystem config validate
/livingsystem config reload
/livingsystem config explain <definition_id>
/livingsystem feature list
/livingsystem sync view <player>
/livingsystem sync force <player>
```


### 34.7 调试显示

提供管理员调试界面，显示：

```text
活动伤势
健康影响实例
派生生命体征
症状来源
功能开关结果
防护计算结果
环境暴露来源
规则定义来源
网络同步状态
各模块计算耗时
```

调试界面不得对普通玩家开放。


### 34.1 单元测试

至少测试：

```text
概率计算是否正确限制在0.0到1.0
防护是否避免重复计算原版减伤
症状叠加是否遵守策略
治疗是否只作用于指定伤势
摄入物是否不能执行局部手术操作
暴露剂量是否正确累积
过滤器耗尽后防护是否下降
恢复阶段是否正确转换
死亡因果链是否正确
配置覆盖是否符合优先级
```


### 34.2 序列化与迁移测试

测试：

```text
PlayerHealthData保存和读取
旧版本数据迁移
伤势和治疗引用恢复
死亡报告保存
检查结果过期
无效数据安全处理
```


### 34.3 游戏测试

必须建立可重复的测试场景：

```text
受到切割伤并包扎
箭矢造成穿透伤
佩戴防刺装备受到穿刺
佩戴和未佩戴防毒面具进入毒气区域
进入病原体群系
浸泡污染水体
靠近辐射方块
接受医疗检查
进入昏迷并被其他玩家治疗
死亡后生成死亡报告
```

所有随机判定必须允许指定随机种子，以便复现测试结果。


### 34.4 性能测试

至少记录：

```text
单玩家健康循环耗时
多人同时在线时总耗时
环境扫描耗时
活动伤势数量
同步数据包大小
每秒发送数据包数量
规则重载耗时
数据迁移耗时
```

需要提供可配置的性能日志开关，默认关闭详细性能日志。


# 第三部分：阶段任务

## 阶段一：建立完整代码架构

本阶段只建立能够承载完整功能的稳定架构，不填充具体医学数值和平衡数据。

必须完成：

1. 创建最终包结构和模块边界。
2. 注册 `PlayerHealthData` 单一 Data Attachment，并完成空数据保存、读取、克隆策略和 `schemaVersion`。
3. 建立全部领域枚举、接口、运行时状态基类、DTO、上下文和错误类型。
4. 建立 `ResolvedRulesSnapshot`、Codec、规则注册表、校验器、覆盖来源追踪和原子重载框架。
5. 建立 `FeatureGateService` 与系统、类型、输出三级功能策略。
6. 建立伤害、命中、防护、结构、生理、症状、治疗、恢复、暴露、检查、资源桥接、意识、死亡和迁移服务接口及默认空实现。
7. 建立事务式 `HealthMutationSet` 和统一脏标记。
8. 建立 `HealthTickScheduler` 的全部频率通道和危重快速通道。
9. 注册网络协议、Payload DTO、服务端校验入口和客户端只读状态。
10. 建立 H 键、健康主界面空壳、HUD图层空壳、HUD布局管理器和治疗动画接口。
11. 注册完整物品、方块、方块实体、菜单、数据组件和机器角色骨架；默认配方与具体功能留空。
12. 建立原版资源桥接接口和重入保护，但不启用最终伤害替换逻辑。
13. 建立兼容 API 和示例适配器。
14. 建立全部调试命令根节点和空子命令处理器。
15. 建立单元测试、GameTest、序列化测试、迁移测试和网络权限测试框架。
16. 确保客户端、专用服务端、单人世界和数据生成任务均可启动。

阶段验收：

```text
项目编译通过
专用服务端启动通过
客户端进入世界通过
PlayerHealthData可保存和读取
空规则快照可加载和重载
空HUD与健康界面可打开
客户端能收到服务端空摘要
所有服务通过依赖注入或服务注册表获得
不存在事件监听器直接修改领域字段
不存在客户端类泄漏到服务端
```

## 阶段二：完成资源桥接与机械创伤闭环

实现原版生命、饥饿、回血和溺水屏蔽，完成血液、体力、水分、能量、呼吸储备、身体部位命中、机械致伤、结构损伤、出血、疼痛、骨折、症状、HUD和基础死亡流程。

## 阶段三：完成治疗、恢复与医疗信息闭环

实现局部单伤口治疗、药物、摄入物、治疗动画、进度条、自然恢复、活动恶化、医疗检查、健康主界面、昏迷、死亡报告和基础医疗物品。

## 阶段四：完成环境与防护闭环

实现专用防护、密封、过滤器、装备污染、环境采样、群系、方块、流体、实体危害、毒气、高低温、低氧、污染水体和动态发射源。

## 阶段五：完成毒素、病原体、辐射与魔法闭环

实现毒素药代状态、四类病原体状态机、环境传播、免疫与药物、四类辐射、外部与体内污染、魔法异常、凋零机制和对应治疗。

## 阶段六：完成医疗设备、生产与整合包兼容

实现医疗设备、治疗设备、生产方块、机器角色、外部科技模组配方接入、装备和食物映射、公开兼容 API、官方兼容数据包及完整管理界面。

## 阶段七：完整平衡、性能与发布验收

补齐所有默认数据、中文资源、动画资源、音效、配置模板、数据字典、迁移样本、自动化测试、多人压力测试、兼容矩阵和发布文档。所有完整功能规格在此阶段达到可用状态。