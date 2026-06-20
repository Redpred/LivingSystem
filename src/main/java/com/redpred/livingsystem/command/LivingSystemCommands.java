package com.redpred.livingsystem.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.redpred.livingsystem.LivingSystemMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * {@code /livingsystem} 命令根与子命令（见开发文档 §34）。
 *
 * <p>阶段一注册全部子命令根节点，但各子命令仅回送中文“尚未实现”提示；具体逻辑在后续阶段填充。
 * 所有命令需要管理员权限（等级 2）。</p>
 */
@EventBusSubscriber(modid = LivingSystemMod.MOD_ID)
public final class LivingSystemCommands {

    private static final String[] SUBCOMMANDS = {
            "health", "injury", "exposure", "treatment", "protection", "config", "data", "sync", "debug"
    };

    private LivingSystemCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root =
                Commands.literal("livingsystem").requires(source -> source.hasPermission(2));
        for (String sub : SUBCOMMANDS) {
            root.then(Commands.literal(sub).executes(ctx -> {
                ctx.getSource().sendSuccess(
                        () -> Component.translatable("commands.livingsystem.not_implemented", sub), false);
                return 1;
            }));
        }
        event.getDispatcher().register(root);
    }
}
