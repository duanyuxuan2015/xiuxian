package com.xiuxian.dto.response;

import com.xiuxian.entity.Sect;
import com.xiuxian.entity.SectMember;

import java.time.LocalDateTime;

/**
 * 宗门成员响应DTO
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
    private LocalDateTime joinedAt;

    public SectMemberResponse() {
    }

    public static SectMemberResponse fromEntity(SectMember member, Sect sect, String characterName) {
        SectMemberResponse response = new SectMemberResponse();
        response.setMemberId(member.getMemberId());
        response.setSectId(member.getSectId());
        response.setSectName(sect != null ? sect.getSectName() : null);
        response.setCharacterId(member.getCharacterId());
        response.setCharacterName(characterName);
        response.setPosition(member.getPosition());
        response.setContribution(member.getContribution());
        response.setWeeklyContribution(member.getWeeklyContribution());
        response.setJoinedAt(member.getJoinedAt());
        return response;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        this.sectName = sectName;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getContribution() {
        return contribution;
    }

    public void setContribution(Integer contribution) {
        this.contribution = contribution;
    }

    public Integer getWeeklyContribution() {
        return weeklyContribution;
    }

    public void setWeeklyContribution(Integer weeklyContribution) {
        this.weeklyContribution = weeklyContribution;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
