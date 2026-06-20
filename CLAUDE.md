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
