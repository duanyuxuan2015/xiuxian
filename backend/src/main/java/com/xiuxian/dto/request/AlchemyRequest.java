package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 炼丹请求DTO
 */
public class AlchemyRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "丹方ID不能为空")
    private Long recipeId;

    public AlchemyRequest() {
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
