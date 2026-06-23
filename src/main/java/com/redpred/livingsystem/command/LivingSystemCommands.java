package com.redpred.livingsystem.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.service.LivingServices;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * {@code /livingsystem} 命令（见开发文档 §34）。
 *
 * <p>调试期（{@code debugCommands} 开启）下所有命令对所有玩家可用，<b>无需 OP 或开作弊</b>，便于测试；
 * 关闭后退回为需要管理员权限（等级 2）。{@code debug}（查看血量/伤势）、{@code heal}（重置）、
 * {@code creative}/{@code survival}（切换游戏模式）已可用，其余子命令为占位。</p>
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID)
public final class LivingSystemCommands {

    private static final String[] PLACEHOLDER_SUBS = {
            "injury", "exposure", "treatment", "protection", "config", "data", "sync"
    };

    private LivingSystemCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("livingsystem")
                .requires(source -> ModConfigs.DEBUG_COMMANDS.get() || source.hasPermission(2));

        root.then(Commands.literal("debug").executes(LivingSystemCommands::runDebug));
        root.then(Commands.literal("heal").executes(LivingSystemCommands::runHeal));
        root.then(Commands.literal("creative").executes(ctx -> setGameMode(ctx, GameType.CREATIVE)));
        root.then(Commands.literal("survival").executes(ctx -> setGameMode(ctx, GameType.SURVIVAL)));
        for (String sub : PLACEHOLDER_SUBS) {
            root.then(Commands.literal(sub).executes(ctx -> {
                ctx.getSource().sendSuccess(
                        () -> Component.translatable("commands.livingsystem.not_implemented", sub), false);
                return 1;
            }));
        }
        event.getDispatcher().register(root);
    }

    private static int runDebug(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        PlayerHealthData data = LivingServices.REPOSITORY.get(player);
        PhysiologyState physiology = data.physiology();
        CommandSourceStack source = ctx.getSource();

        float percent = physiology.getMaxBloodVolume() > 0
                ? physiology.getCurrentBloodVolume() / physiology.getMaxBloodVolume() * 100.0F : 100.0F;
        source.sendSuccess(() -> Component.literal(String.format(
                "§c血容量 §f%.0f/%.0f mL §7(%.0f%%)",
                physiology.getCurrentBloodVolume(), physiology.getMaxBloodVolume(), percent)), false);

        int count = 0;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof TraumaInjuryState trauma) {
                count++;
                source.sendSuccess(() -> Component.literal(String.format(
                        "§e- %s / %s  严重度 %.2f  外出血 %.0f 内出血 %.0f mL/min (凝血 %.0f%%%s)",
                        trauma.getBodyRegion(), trauma.getTraumaKind(), trauma.severity(),
                        trauma.getBleeding().getBaseExternalRate(), trauma.getBleeding().getBaseInternalRate(),
                        trauma.getBleeding().getClotProgress() * 100.0F,
                        trauma.getBleeding().isArterialPattern() ? " §c动脉§e" : "")), false);
            }
        }
        final int total = count;
        source.sendSuccess(() -> Component.literal("§7活动伤势：" + total), false);
        return 1;
    }

    private static int runHeal(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        PlayerHealthData data = LivingServices.REPOSITORY.get(player);
        data.activeEffects().clear();
        data.physiology().copyFrom(new PhysiologyState());
        data.bodyRegions().values().forEach(region -> {
            region.getStructures().clear();
            region.getActiveEffectIds().clear();
        });
        ctx.getSource().sendSuccess(() -> Component.literal("§a已重置 LivingSystem 健康状态。"), false);
        return 1;
    }

    private static int setGameMode(CommandContext<CommandSourceStack> ctx, GameType mode) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        player.setGameMode(mode);
        ctx.getSource().sendSuccess(() -> Component.literal("§a已切换游戏模式：" + mode.getName()), false);
        return 1;
    }
}
