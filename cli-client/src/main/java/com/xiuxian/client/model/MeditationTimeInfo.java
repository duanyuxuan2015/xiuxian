package com.xiuxian.client.model;

/**
 * 打坐时间信息
 */
public class MeditationTimeInfo {
    private Long characterId;
    private Integer baseTime;
    private Integer mindset;
    private Integer comprehension;
    private Integer reductionTime;
    private Integer finalTime;
    private String message;

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

    public Integer getMindset() {
        return mindset;
    }

    public void setMindset(Integer mindset) {
        this.mindset = mindset;
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
