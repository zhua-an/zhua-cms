package com.zhua.pro.cms.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhua
 * @Description: {工作流状态}
 * @date 2020-12-04 14:57
 */
@Getter
@AllArgsConstructor
public enum WorkFlowStatusEnum {
    /**
     * 工作流状态
     */
    APPLY(1L, "待审批"),
    REJECT(2L, "审批驳回"),
    PASS(3L, "审批通过");

    private final Long id;
    private final String name;

}
