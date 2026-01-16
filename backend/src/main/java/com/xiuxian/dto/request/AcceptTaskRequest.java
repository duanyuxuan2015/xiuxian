package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 接取宗门任务请求DTO
 */
public class AcceptTaskRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "任务模板ID不能为空")
    private Long templateId;

    public AcceptTaskRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
