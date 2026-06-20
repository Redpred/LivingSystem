package com.redpred.livingsystem.domain.effect;

/**
 * 毒素或物质进入体内的途径（见开发文档 §5.8 中毒）。
 */
public enum ExposureRoute {
    INGESTION,
    INHALATION,
    INJECTION,
    DERMAL,
    WOUND,
    BITE_OR_STING
}
