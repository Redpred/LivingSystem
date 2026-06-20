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

# 依赖图（手工，截至 2026-06-17；GitNexus 生成后以图谱为准）

NeoForge 1.21.1 / Java 21 模组，包根 `com.livingsystem`。入口 `LivingSystem`（`@Mod`）注册
`registry.*` 的 DeferredRegister 与 `Config.SPEC`。

## 节点与职责（核心）
| 节点 | 职责 | 主要被谁依赖 |
|---|---|---|
| `body.BodyPart` (enum, 8 部位) | 部位定义、`maxHealth`、`isVital()`、`neighbours()` 拓扑 | 几乎所有部位逻辑 + UI + 网络 + 存档 |
| `body.PlayerHealthData` | 权威生命数据：`health[]` + 治疗计时器；`Codec`；`condition/derivedHealth`；`hurtWithOverflow`；`applyHealOverTime/tickHeal` | 附件、各事件处理器、`HealthScreen`、网络 |
| `body.DamageDistribution` (enum) | NONE/ONE_OF/ALL 分配 | `BodyDamageRule`、`CombatEventHandler` |
| `body.BodyDamageRule` / `HealingRule` (record+Codec) | 数据包规则模型 | 对应 reload listener + 事件处理器 |
| `body.BodyDamageService` (static) | 护甲减伤、邻接溢出、`kill()` | `CombatEventHandler`、`LivingTickHandler` |
| `body.BodyPartLocator` (static) | 几何命中定位 → 单一部位 | `CombatEventHandler` |
| `data.BodyDamageRules` | reload listener；`get(msgId)` ← `data/livingsystem/body_damage/*.json` | `CombatEventHandler` |
| `data.HealingRules` | reload listener；`get(itemId)` ← `body_healing/*.json` | `HealingHandler` |
| `event.CombatEventHandler` | `LivingIncomingDamageEvent`：路由伤害到部位，回灌派生血量 | （事件订阅，无下游调用者） |
| `event.LivingTickHandler` | 每 tick：治疗/再生/debuff/出血/血量对齐/致死 | （事件订阅） |
| `event.HealingHandler` | 物品使用治疗、睡眠恢复、`HealLimbPayload` 服务端应用 | 被 `ModNetworking` 引用（C2S handler） |
| `event.PlayerDataEventHandler` | 登录/重生/克隆 同步 | （事件订阅） |
| `network.ModNetworking` | 注册 payload；`sendTo`/`sendOpenHealScreen`；`PROTOCOL_VERSION` | `CombatEventHandler`、`LivingTickHandler`、`HealingHandler` |
| `network.{SyncBodyHealth,OpenHealScreen,HealLimb}Payload` | 三个网络包 | `ModNetworking`、`ClientPayloadHandler`、`HealthScreen` |
| `client.ClientPayloadHandler` | S2C 处理器（**禁止 import `net.minecraft.client.*`**） | `ModNetworking`（双端链接） |
| `client.ClientHooks` | 仅客户端、惰性加载的开屏入口（隔离 `Minecraft`/`HealthScreen`） | `ClientPayloadHandler`（lambda 内） |
| `client.screen.HealthScreen` | 8 格人形 UI + 治疗模式 | `ClientHooks`、`ClientEventHandler` |
| `registry.ModAttachments` | `PLAYER_HEALTH` 附件（持久化、死亡重置） | 各处 `getData/setData` |
| `Config` | `ModConfigSpec` 全部可调项 | 各处 |

## 破坏面热点（修改前务必做影响分析）
- **`BodyPart` 常量顺序 / `maxHealth`** → `PlayerHealthData` 数组与 `Codec`、网络、存档、`HealthScreen` 布局、所有处理器。改顺序＝存档/协议破坏。
- **`PlayerHealthData` 的 `Codec` / 公共方法** → 附件序列化、网络、全部调用方。
- **`ModNetworking` 的 payload 类型/StreamCodec** → 客户端+服务端；改线格式须 `PROTOCOL_VERSION` 自增。
- **Dist 安全**：`ClientPayloadHandler` 必须保持无客户端类引用；开屏只能经 `ClientHooks`（否则专用服务器加载崩溃，已有前车之鉴）。
- **数据包规则**：`body_damage/*.json` 以伤害源 `msgId`（小写）为键；`body_healing/*.json` 以规则内 `item` 字段为键。

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
