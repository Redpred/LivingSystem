# CLAUDE.md — LivingSystem

> 本项目采用 **GitNexus 图谱优先工作流**。完整规则与依赖图见根目录 [`AGENTS.md`](AGENTS.md)。
> 真实 GitNexus 图谱通过 `npx gitnexus analyze` 生成（需 shell + Node）；该命令会重新生成
> `AGENTS.md` / `CLAUDE.md`，届时请保留下述「核心原则」。

## 核心原则：GitNexus 图谱优先工作流

在执行任何具体代码编写或文件修改前，必须无条件优先通过 GitNexus 图谱能力进行上下文检索，
不要盲目使用大范围 grep 或泛滥读取不相关源码。GitNexus 工具：`gitnexus_query` / `gitnexus_context`
/ `gitnexus_impact`。**这些 MCP 工具不可用时，以 `AGENTS.md` 的依赖图为回退，流程不变。**

1. **任务承接期（先查图谱）**：先用 `gitnexus_query` / `gitnexus_context`（或查 `AGENTS.md`）摸清
   涉及的类/函数/接口的依赖节点与核心职责。
2. **方案设计期（影响范围分析）**：禁止在不清楚下游调用者时修改公共代码。提出方案前必须先做
   `gitnexus_impact({ target, direction: "upstream" })`（无工具时用 `AGENTS.md` 的「破坏面热点」推导），
   并**在第一句回复中先汇报**：「经过 GitNexus 知识图谱分析，本次修改的潜在受影响（Break）范围
   包含以下几个调用链：……」。`impact` 为 HIGH/CRITICAL 时显式预警。
3. **精准实施期（锁定目标）**：影响范围可控且方案获确认后，再做极小范围精准编辑，最小化 Token 开销。

## 构建/验证（既有约定，见记忆 livingsystem-build-verify）
- 编译：`$env:JAVA_TOOL_OPTIONS='-Duser.language=en -Duser.country=US'` 后
  `cmd /c '.\gradlew.bat compileJava --console=plain --rerun-tasks > build.log 2>&1'`。
- 专用服务器冒烟测试：后台 `runServer`，确认数据包规则加载且无 `invalid dist`，随后停止。

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
