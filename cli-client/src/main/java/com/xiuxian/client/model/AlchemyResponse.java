package com.xiuxian.client.model;

/**
 * 炼丹响应
 */
public class AlchemyResponse {
    private boolean success;
    private String pillName;
    private String resultQuality;
    private Integer quantity;
    private Integer expGained;
    private String message;

    public boolean isSuccess() { return success; }
    public String getPillName() { return pillName; }
    public String getResultQuality() { return resultQuality; }
    public Integer getQuantity() { return quantity; }
    public Integer getExpGained() { return expGained; }
    public String getMessage() { return message; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setPillName(String pillName) { this.pillName = pillName; }
    public void setResultQuality(String resultQuality) { this.resultQuality = resultQuality; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setExpGained(Integer expGained) { this.expGained = expGained; }
    public void setMessage(String message) { this.message = message; }
}
