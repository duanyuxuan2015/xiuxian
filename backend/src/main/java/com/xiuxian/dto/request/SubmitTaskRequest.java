package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 提交宗门任务请求DTO
 */
public class SubmitTaskRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "进度记录ID不能为空")
    private Long progressId;

    public SubmitTaskRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
    }
}
