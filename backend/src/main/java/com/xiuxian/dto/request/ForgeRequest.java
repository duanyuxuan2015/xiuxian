package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 锻造请求DTO
 */
public class ForgeRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "配方ID不能为空")
    private Long recipeId;

    public ForgeRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
}
