package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 宗门成员实体
 */
@TableName("sect_member")
public class SectMember {

    @TableId(type = IdType.AUTO)
    private Long memberId;

    private Long sectId;

    private Long characterId;

    private String position;

    private Integer contribution;

    private Integer weeklyContribution;

    private LocalDateTime joinedAt;

    public SectMember() {
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

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
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
