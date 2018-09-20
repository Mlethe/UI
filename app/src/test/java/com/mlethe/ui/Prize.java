package com.mlethe.ui;

import java.math.BigDecimal;

public class Prize {
    /**
     * 奖品唯一标示
     */
    private Long prizeId;

    /**
     * 中奖概率
     */
    private BigDecimal probability;

    /**
     * 奖品数量
     */
    private Integer quantity;

    public Prize() {
    }

    public Prize(Long prizeId, BigDecimal probability, Integer quantity) {
        this.prizeId = prizeId;
        this.probability = probability;
        this.quantity = quantity;
    }

    public Long getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(Long prizeId) {
        this.prizeId = prizeId;
    }

    public BigDecimal getProbability() {
        return probability;
    }

    public void setProbability(BigDecimal probability) {
        this.probability = probability;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
