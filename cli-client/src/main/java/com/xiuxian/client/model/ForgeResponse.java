package com.xiuxian.client.model;

/**
 * 锻造响应
 */
public class ForgeResponse {
    private boolean success;
    private String equipmentName;
    private String resultQuality;
    private Integer expGained;
    private String message;

    public boolean isSuccess() { return success; }
    public String getEquipmentName() { return equipmentName; }
    public String getResultQuality() { return resultQuality; }
    public Integer getExpGained() { return expGained; }
    public String getMessage() { return message; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public void setResultQuality(String resultQuality) { this.resultQuality = resultQuality; }
    public void setExpGained(Integer expGained) { this.expGained = expGained; }
    public void setMessage(String message) { this.message = message; }
}
