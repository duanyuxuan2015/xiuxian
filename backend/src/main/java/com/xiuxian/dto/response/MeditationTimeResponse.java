package com.xiuxian.dto.response;

/**
 * 打坐时间响应DTO
 */
public class MeditationTimeResponse {

    private Long characterId;
    private Integer baseTime;          // 基础时间（秒）
    private Integer spirit;            // 精神属性
    private Integer comprehension;     // 悟性属性
    private Integer reductionTime;     // 减免时间（秒）
    private Integer finalTime;         // 最终时间（秒）
    private String message;            // 提示信息

    public MeditationTimeResponse() {}

    // 构造函数
    public MeditationTimeResponse(Long characterId, Integer baseTime,
                                   Integer spirit, Integer comprehension,
                                   Integer reductionTime, Integer finalTime) {
        this.characterId = characterId;
        this.baseTime = baseTime;
        this.spirit = spirit;
        this.comprehension = comprehension;
        this.reductionTime = reductionTime;
        this.finalTime = finalTime;
        this.message = String.format("预计打坐时间：%d秒（精神%d + 悟性%d，减免%d秒）",
            finalTime, spirit, comprehension, reductionTime);
    }

    // Getter和Setter方法
    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Integer getBaseTime() {
        return baseTime;
    }

    public void setBaseTime(Integer baseTime) {
        this.baseTime = baseTime;
    }

    public Integer getSpirit() {
        return spirit;
    }

    public void setSpirit(Integer spirit) {
        this.spirit = spirit;
    }

    public Integer getComprehension() {
        return comprehension;
    }

    public void setComprehension(Integer comprehension) {
        this.comprehension = comprehension;
    }

    public Integer getReductionTime() {
        return reductionTime;
    }

    public void setReductionTime(Integer reductionTime) {
        this.reductionTime = reductionTime;
    }

    public Integer getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(Integer finalTime) {
        this.finalTime = finalTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
