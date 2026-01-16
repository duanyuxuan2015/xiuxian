package com.xiuxian.client.model;

import java.time.LocalDateTime;

/**
 * 宗门成员响应
 */
public class SectMemberResponse {
    private Long memberId;
    private Long sectId;
    private String sectName;
    private Long characterId;
    private String characterName;
    private String position;
    private Integer contribution;
    private Integer weeklyContribution;
    private Integer reputation;  // 声望值
    private String joinedAt;

    public Long getMemberId() { return memberId; }
    public Long getSectId() { return sectId; }
    public String getSectName() { return sectName; }
    public Long getCharacterId() { return characterId; }
    public String getCharacterName() { return characterName; }
    public String getPosition() { return position; }
    public Integer getContribution() { return contribution; }
    public Integer getWeeklyContribution() { return weeklyContribution; }
    public Integer getReputation() { return reputation; }
    public String getJoinedAt() { return joinedAt; }

    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public void setSectId(Long sectId) { this.sectId = sectId; }
    public void setSectName(String sectName) { this.sectName = sectName; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    public void setCharacterName(String characterName) { this.characterName = characterName; }
    public void setPosition(String position) { this.position = position; }
    public void setContribution(Integer contribution) { this.contribution = contribution; }
    public void setWeeklyContribution(Integer weeklyContribution) { this.weeklyContribution = weeklyContribution; }
    public void setReputation(Integer reputation) { this.reputation = reputation; }
    public void setJoinedAt(String joinedAt) { this.joinedAt = joinedAt; }
}
