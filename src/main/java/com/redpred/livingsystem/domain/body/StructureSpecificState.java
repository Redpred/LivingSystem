package com.redpred.livingsystem.domain.body;

/**
 * 结构专用状态的密封接口。除通用完整度（{@link StructureState#integrity}）外，部分结构还具有
 * 各自专用的可持续变化状态。运行时使用可变实现类；其许可的实现见 {@code permits} 列表。
 *
 * <p>对应关系：脊髓通路类结构使用 {@link SpinalPathwayState}；左右肺使用 {@link LungState}；
 * 上、下呼吸道使用 {@link AirwayState}；其余按类型一一对应。{@code SKIN}、{@code SOFT_TISSUE}、
 * {@code SENSORY_SYSTEM} 等仅使用通用完整度，专用状态可为空。</p>
 */
public sealed interface StructureSpecificState
        permits BoneState,
                VascularState,
                NerveState,
                BrainState,
                SpinalPathwayState,
                HeartState,
                LungState,
                AirwayState,
                SolidOrgansState,
                HollowOrgansState {
}
