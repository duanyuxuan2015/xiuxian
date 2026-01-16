package com.xiuxian.dto.response;

import java.util.List;

/**
 * 一键装备响应DTO
 */
public class AutoEquipResponse {

    private List<EquipmentChangeInfo> changes;
    private Integer totalChanges;
    private String priorityAttribute;
    private String message;

    public AutoEquipResponse() {}

    public AutoEquipResponse(List<EquipmentChangeInfo> changes, String priorityAttribute) {
        this.changes = changes;
        this.totalChanges = changes.size();
        this.priorityAttribute = priorityAttribute;
        this.message = String.format("一键装备完成，共更换 %d 件装备", totalChanges);
    }

    public List<EquipmentChangeInfo> getChanges() {
        return changes;
    }

    public void setChanges(List<EquipmentChangeInfo> changes) {
        this.changes = changes;
    }

    public Integer getTotalChanges() {
        return totalChanges;
    }

    public void setTotalChanges(Integer totalChanges) {
        this.totalChanges = totalChanges;
    }

    public String getPriorityAttribute() {
        return priorityAttribute;
    }

    public void setPriorityAttribute(String priorityAttribute) {
        this.priorityAttribute = priorityAttribute;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
