# AGENTS.md — LivingSystem

> ⚠️ **本文件目前是手工编写的临时知识图谱替代品**，不是 GitNexus 生成的真实图谱。
> 真实图谱需在可执行 shell 的环境中于项目根目录运行 `npx gitnexus analyze` 生成
> （详见下方「生成真实 GitNexus 图谱」）。GitNexus 运行后会**覆盖本文件与 `CLAUDE.md`**，
> 届时请把下方「核心原则」一节重新并入。该规则也已写入助手长期记忆，不依赖本文件存续。

---

# 核心原则：GitNexus 图谱优先工作流

在执行任何具体代码编写或文件修改前，必须无条件优先通过 GitNexus 的图谱能力进行上下文检索，
不要盲目使用大范围 grep 或泛滥读取不相关的源码文件。

GitNexus MCP 工具名：`gitnexus_query`（= 规则中的 `query()`）、`gitnexus_context`（= `context()`）、
`gitnexus_impact`（= `impact()`）。**若这些 MCP 工具在当前会话中尚不可用**（例如尚未运行
`npx gitnexus analyze`，或未重启 Claude Code 使 MCP server 生效），则以本文件下方的「依赖图」
作为回退，仍按同样的流程执行。

## 1. 任务承接期（先查图谱）
- 收到任何代码修改或咨询需求，首先调用 GitNexus 的 MCP 工具（`gitnexus_query` / `gitnexus_context`），
  或快速查阅本 `AGENTS.md`。
- 必须首先摸清涉及的代码类、函数、接口在图谱中的依赖节点，明确其核心职责。

## 2. 方案设计期（影响范围分析）
- 绝对禁止在不清楚下游调用者的情况下修改公共代码。
- 在提出任何具体修改方案前，必须调用 `gitnexus_impact({ target: "欲修改的类或方法", direction: "upstream" })`
  （无工具时改用本文件「破坏面热点」与「依赖图」推导）。
- **必须在第一句回复中首先向开发者汇报**：
  「经过 GitNexus 知识图谱分析，本次修改的潜在受影响（Break）范围包含以下几个调用链：……」。
- 若 `gitnexus_impact` 返回 HIGH / CRITICAL 风险，必须显式向开发者预警。

## 3. 精准实施期（锁定目标）
- 确认影响范围完全可控、且方案得到开发者确认后，再通过精准的文件定位进行极小范围的编辑，
  把上下文 Token 开销控制在最低限度。

---

# 生成真实 GitNexus 图谱

1. 前置：Node.js（提供 `npx`）、可执行 shell、网络访问。
2. 在 `D:\Git\NeoForge_1.21.1\LivingSystem` 运行：`npx gitnexus analyze`
3. 它会：用 Tree-sitter 解析源码 → 构建知识图谱（本地 LadybugDB）→ 生成 `AGENTS.md` / `CLAUDE.md`
   → 注册 MCP server、hooks、skills（Claude Code 深度集成）。
4. **MCP 工具（`gitnexus_*`）需重启 Claude Code 后才会在会话中出现。**
5. 若任一工具提示索引过期（stale），先重新运行 `npx gitnexus analyze`。

来源：GitNexus（开源，`abhigyanpatwari/GitNexus`）。

---

# 依赖图（手工，截至 2026-06-23；GitNexus 生成后以下方图谱为准）

NeoForge 1.21.1 / Java 21 模组，包根 `com.redpred.livingsystem`。入口 `LivingSystemMod`（`@Mod`）经
`bootstrap.*` 注册注册表/附件/数据组件/网络包/配置。服务端权威、规则数据驱动、事件适配与业务服务分离。
已完成阶段一（架构）与阶段二（资源桥接与机械创伤闭环：命中/创伤/结构损伤/出血/疼痛/骨折/症状/资源/
呼吸/HUD/基础死亡）。

## 节点与职责（核心）
| 节点 | 职责 | 主要被谁依赖 |
|---|---|---|
| `domain.body.BodyRegion` (enum, 7 部位) | 身体部位定义（无独立血条） | 结构状态、命中、症状、网络、存档、HUD |
| `domain.body.BodyRegionState` / `StructureState` | 部位结构集合 + 完整度（唯一权威，0~1）；专用状态见 `StructureSpecificState` | 死亡判定、结构损伤、症状 |
| `domain.PlayerHealthData` | **唯一健康聚合根**：physiology + bodyRegions + activeEffects + 治疗/药物/暴露/观察/死亡报告 + gameplay 缓存 | 仓库、桥接、各引擎、网络 |
| `domain.physiology.PhysiologyState` (Codec) | 全身权威资源：血容量/体力/水分/能量/呼吸储备/氧债/意识/凝血/镇痛… | 生理循环、桥接、HUD、死亡判定 |
| `domain.effect.*` | `HealthEffectInstance`（`TraumaInjuryState` 等）+ 组件 `BleedingState`/`FractureState`/`PainState`/… | 创伤工厂、生理循环、症状引擎 |
| `rule.definition.DamageProfile` (record+Codec) | DamageType→画像：机制/创伤类型/严重度/结构权重/失血/骨折概率/基础疼痛 | reload 监听器、创伤工厂 |
| `data.DamageProfileReloadListener` | reload；`get(msgId)` ← `data/livingsystem/damage_profile/*.json` | 创伤工厂、桥接 |
| `service.damage.*` | `DamageContextFactory` / `HealthEffectFactory`（创伤+出血+疼痛+骨折固化） | 桥接 `handleIncoming` |
| `service.hit.HitLocationService` | 命中点高度分区/来源规则/确定性随机 → 单一部位或全身 | 桥接 |
| `service.structure.StructureDamageService` | **唯一**结构完整度修改入口 | 桥接、恢复 |
| `service.physiology.PhysiologyEngine` | `runCycle`（出血/凝血/疼痛/呼吸/氧债/体力/意识）+ `computeVitals`（含总疼痛） | 桥接 tick |
| `service.symptom.{SymptomEngine,GameplayEffectAggregator}` | 健康状态→症状（单向只读）→唯一 `GameplayEffectSnapshot` | 桥接 tick |
| `service.death.DeathConditionResolver` | **唯一**死亡判定：失血/要害结构归零/缺氧终末 | 桥接 tick |
| `service.resource.VanillaResourceBridge` | 原版伤害转换、资源钉哨兵、溺水屏蔽、死亡重入（集中不变量） | 桥接 |
| `compatibility.vanilla.VanillaDamageInterceptor` | 事件接入：`LivingIncomingDamageEvent`（清零原版伤害）+ `PlayerTickEvent`（循环编排）+ `BreakSpeed` + 关闭自然回血 | 事件订阅，编排上述服务 |
| `service.LivingServices` | 服务定位器（各 Default 实现单例） | 事件接入层 |
| `network.payload.*`（`HudSummary`/`SyncGameplay`/…） | StreamCodec 网络包 | `ServerPayloadHandler`/`ClientPayloadReceiver` |
| `client.state.ClientHealthState` | 客户端只读快照（HUD/界面/表现） | HUD、客户端事件 |
| `client.ClientHooks` | 仅客户端、惰性开屏入口（隔离 `Minecraft`/`HealthScreen`） | 客户端接收器（lambda 内） |
| `bootstrap.ModConfigs` / `ModAttachments` | 服务端/客户端配置；`PLAYER_HEALTH` 附件 | 各处 |

## 破坏面热点（修改前务必做影响分析）
- **`BodyRegion` 常量顺序 / `AnatomicalStructure`** → `PlayerHealthData`/部位结构的存档与网络、HUD 布局、所有服务。改顺序＝存档/协议破坏。
- **`PhysiologyState` 的 `Codec` / 字段** → 附件序列化、生理循环、桥接、HUD、死亡判定。
- **`DamageProfile` record/Codec** → 数据包加载、创伤工厂、桥接；新增字段必须用 `optionalFieldOf` 带默认值以兼容旧档与现有 JSON。
- **`VanillaDamageInterceptor` / `VanillaResourceBridge`** → 伤害拦截清零、资源钉哨兵、死亡重入集中于此，禁止分散到其它监听器。
- **网络 payload 类型 / StreamCodec** → 客户端+服务端；改线格式须协议版本自增。
- **Dist 安全**：`ClientPayloadReceiver`/`ClientHealthState` 不得引用 `net.minecraft.client.*`；开屏只能经 `ClientHooks`（否则专用服务器加载崩溃）。
- **唯一入口约束**：结构完整度只经 `StructureDamageService`；死亡只经 `DeathConditionResolver`；症状单向只读，禁止反向修改伤势与生理状态。
- **数据包规则**：`damage_profile/*.json` 以伤害源 `msgId`（小写）为键。

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **LivingSystem** (2129 symbols, 3135 relationships, 26 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> Index stale? Run `node .gitnexus/run.cjs analyze` from the project root — it auto-selects an available runner. No `.gitnexus/run.cjs` yet? `npx gitnexus analyze` (npm 11 crash → `npm i -g gitnexus`; #1939).

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows. For regression review, compare against the default branch: `detect_changes({scope: "compare", base_ref: "main"})`.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `rename` which understands the call graph.
- NEVER commit changes without running `detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/LivingSystem/context` | Codebase overview, check index freshness |
| `gitnexus://repo/LivingSystem/clusters` | All functional areas |
| `gitnexus://repo/LivingSystem/processes` | All execution flows |
| `gitnexus://repo/LivingSystem/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->
