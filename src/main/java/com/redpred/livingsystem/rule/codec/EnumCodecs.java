package com.redpred.livingsystem.rule.codec;

import com.mojang.serialization.Codec;

/**
 * 枚举 Codec 工具。数据包中枚举以名称（大小写不敏感）表示。
 */
public final class EnumCodecs {

    private EnumCodecs() {
    }

    public static <E extends Enum<E>> Codec<E> of(Class<E> type) {
        return Codec.STRING.xmap(s -> Enum.valueOf(type, s.trim().toUpperCase()), Enum::name);
    }
}
