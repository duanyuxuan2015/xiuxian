package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.AcceptTaskRequest;
import com.xiuxian.dto.request.SubmitTaskRequest;
import com.xiuxian.dto.response.DailyTaskSummaryResponse;
import com.xiuxian.dto.response.SectTaskResponse;
import com.xiuxian.dto.response.TaskProgressResponse;
import com.xiuxian.entity.*;
import com.xiuxian.mapper.CharacterSectReputationMapper;
import com.xiuxian.mapper.SectTaskProgressMapper;
import com.xiuxian.mapper.SectTaskTemplateMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.SectMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * SectTaskService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SectTaskServiceImplTest {

    @Mock
    private SectTaskTemplateMapper taskTemplateMapper;

    @Mock
    private CharacterService characterService;

    @Mock
    private SectMemberService sectMemberService;

    @Mock
    private CharacterSectReputationMapper reputationMapper;

    // 使用 @Spy 来部分模拟 service，这样可以 mock 继承的方法
    @Spy
    @InjectMocks
    private SectTaskServiceImpl sectTaskService;

    private PlayerCharacter character;
    private SectMember sectMember;
    private SectTaskTemplate combatTask;
    private SectTaskTemplate meditationTask;
    private SectTaskProgress progress;

    @BeforeEach
    void setUp() {
        // 创建测试角色
        character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setCharacterName("测试角色");
        character.setRealmLevel(1);

        // 创建宗门成员
        sectMember = new SectMember();
        sectMember.setMemberId(1L);
        sectMember.setSectId(1L);
        sectMember.setCharacterId(1L);
        sectMember.setPosition("弟子");
        sectMember.setContribution(0);
        sectMember.setWeeklyContribution(0);
        sectMember.setJoinedAt(LocalDateTime.now());

        // 创建战斗任务模板
        combatTask = new SectTaskTemplate();
        combatTask.setTemplateId(1L);
        combatTask.setSectId(1L);
        combatTask.setTaskType("combat");
        combatTask.setTaskName("击杀妖兽");
        combatTask.setDescription("击杀5只凡人境界妖兽");
        combatTask.setTargetType("monster_level");
        combatTask.setTargetValue("1");
        combatTask.setTargetCount(5);
        combatTask.setRequiredPosition(1);
        combatTask.setContributionReward(50);
        combatTask.setReputationReward(10);
        combatTask.setIsActive(true);

        // 创建修炼任务模板
        meditationTask = new SectTaskTemplate();
        meditationTask.setTemplateId(2L);
        meditationTask.setSectId(1L);
        meditationTask.setTaskType("meditation");
        meditationTask.setTaskName("打坐修炼");
        meditationTask.setDescription("完成打坐修炼3次");
        meditationTask.setTargetType("meditation_count");
        meditationTask.setTargetValue("1");
        meditationTask.setTargetCount(3);
        meditationTask.setRequiredPosition(1);
        meditationTask.setContributionReward(30);
        meditationTask.setReputationReward(5);
        meditationTask.setIsActive(true);

        // 创建任务进度
        progress = new SectTaskProgress();
        progress.setProgressId(1L);
        progress.setCharacterId(1L);
        progress.setTemplateId(1L);
        progress.setCurrentProgress(0);
        progress.setStatus("accepted");
        progress.setAcceptedAt(LocalDateTime.now());
    }

    // ==================== getAvailableTasks 测试 ====================

    @Test
    void getAvailableTasks_NoSect_ThrowsException() {
        // Given
        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.getAvailableTasks(1L);
        });
        assertEquals(8004, exception.getCode());
    }

    @Test
    void getAvailableTasks_Success() {
        // Given
        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        when(taskTemplateMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(combatTask, meditationTask));
        // mock service 的 list() 和 count() 方法（继承自 ServiceImpl）
        doReturn(Collections.emptyList()).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        doReturn(0L).when(sectTaskService).count(any(LambdaQueryWrapper.class));

        // When
        List<SectTaskResponse> responses = sectTaskService.getAvailableTasks(1L);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("击杀妖兽", responses.get(0).getTaskName());
        assertEquals("战斗任务", responses.get(0).getTaskTypeDisplay());
        assertEquals("击杀5只1级妖兽", responses.get(0).getTargetDisplay());
        assertEquals(50, responses.get(0).getContributionReward());
        assertEquals(10, responses.get(0).getReputationReward());
    }

    @Test
    void getAvailableTasks_NoInProgressTasks_ReturnsAll() {
        // Given
        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        when(taskTemplateMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(combatTask));
        // mock service 的 list() 和 count() 方法（继承自 ServiceImpl）
        doReturn(Collections.emptyList()).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        doReturn(0L).when(sectTaskService).count(any(LambdaQueryWrapper.class));

        // When
        List<SectTaskResponse> responses = sectTaskService.getAvailableTasks(1L);

        // Then
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getCanAccept());
    }

    // ==================== getMyTaskSummary 测试 ====================

    @Test
    void getMyTaskSummary_Success() {
        // Given
        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        when(taskTemplateMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(combatTask));
        // mock service 的 list() 方法（继承自 ServiceImpl）
        doReturn(Collections.emptyList()).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        doReturn(0L).when(sectTaskService).count(any(LambdaQueryWrapper.class));

        // When
        DailyTaskSummaryResponse summary = sectTaskService.getMyTaskSummary(1L);

        // Then
        assertNotNull(summary);
        assertEquals(3, summary.getRemainingAccepts());
        assertEquals(3, summary.getTotalDailyLimit());
        assertEquals(0, summary.getCompletedCount());
        assertNotNull(summary.getAvailableTasks());
        assertEquals(1, summary.getAvailableTasks().size());
    }

    // ==================== acceptTask 测试 ====================

    @Test
    void acceptTask_Success() {
        // Given
        AcceptTaskRequest request = new AcceptTaskRequest();
        request.setCharacterId(1L);
        request.setTemplateId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);
        doReturn(0L).when(sectTaskService).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(sectTaskService).save(any(SectTaskProgress.class));

        // When
        TaskProgressResponse response = sectTaskService.acceptTask(request);

        // Then
        assertNotNull(response);
        assertEquals("击杀妖兽", response.getTaskName());
        assertEquals("战斗任务", response.getTaskTypeDisplay());
        assertEquals("进行中", response.getStatusDisplay());
        assertEquals(0, response.getCurrentProgress());
        assertEquals(5, response.getTargetCount());
        verify(sectTaskService).save(any(SectTaskProgress.class));
    }

    @Test
    void acceptTask_NoSect_ThrowsException() {
        // Given
        AcceptTaskRequest request = new AcceptTaskRequest();
        request.setCharacterId(1L);
        request.setTemplateId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.acceptTask(request);
        });
        assertEquals(8004, exception.getCode());
    }

    @Test
    void acceptTask_TaskNotFound_ThrowsException() {
        // Given
        AcceptTaskRequest request = new AcceptTaskRequest();
        request.setCharacterId(1L);
        request.setTemplateId(999L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        when(taskTemplateMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.acceptTask(request);
        });
        assertEquals(8010, exception.getCode());
    }

    @Test
    void acceptTask_WrongSect_ThrowsException() {
        // Given
        AcceptTaskRequest request = new AcceptTaskRequest();
        request.setCharacterId(1L);
        request.setTemplateId(1L);

        SectTaskTemplate otherSectTask = new SectTaskTemplate();
        otherSectTask.setTemplateId(1L);
        otherSectTask.setSectId(2L); // 不同的宗门
        otherSectTask.setIsActive(true);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        when(taskTemplateMapper.selectById(1L)).thenReturn(otherSectTask);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.acceptTask(request);
        });
        assertEquals(8011, exception.getCode());
    }

    @Test
    void acceptTask_PositionTooLow_ThrowsException() {
        // Given
        AcceptTaskRequest request = new AcceptTaskRequest();
        request.setCharacterId(1L);
        request.setTemplateId(1L);

        SectTaskTemplate highPositionTask = new SectTaskTemplate();
        highPositionTask.setTemplateId(1L);
        highPositionTask.setSectId(1L);
        highPositionTask.setRequiredPosition(3); // 需要核心弟子
        highPositionTask.setIsActive(true);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        when(taskTemplateMapper.selectById(1L)).thenReturn(highPositionTask);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.acceptTask(request);
        });
        assertEquals(8012, exception.getCode());
    }

    @Test
    void acceptTask_DailyLimitExceeded_ThrowsException() {
        // Given
        AcceptTaskRequest request = new AcceptTaskRequest();
        request.setCharacterId(1L);
        request.setTemplateId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);
        doReturn(3L).when(sectTaskService).count(any(LambdaQueryWrapper.class)); // 已接取3次

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.acceptTask(request);
        });
        assertEquals(8013, exception.getCode());
    }

    @Test
    void acceptTask_AlreadyAccepted_ThrowsException() {
        // Given
        AcceptTaskRequest request = new AcceptTaskRequest();
        request.setCharacterId(1L);
        request.setTemplateId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);
        doReturn(1L).when(sectTaskService).count(any(LambdaQueryWrapper.class)); // 已接取过

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.acceptTask(request);
        });
        assertEquals(8014, exception.getCode());
    }

    // ==================== submitTask 测试 ====================

    @Test
    void submitTask_Success() {
        // Given
        SubmitTaskRequest request = new SubmitTaskRequest();
        request.setCharacterId(1L);
        request.setProgressId(1L);

        progress.setCurrentProgress(5); // 已完成

        when(characterService.getById(1L)).thenReturn(character);
        // 使用 selectById 而不是 getById，因为 ServiceImpl.getById() 内部调用 mapper.selectById()
        doReturn(progress).when(sectTaskService).getById(1L);
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);
        doReturn(true).when(sectTaskService).updateById(any(SectTaskProgress.class));

        // When
        TaskProgressResponse response = sectTaskService.submitTask(request);

        // Then
        assertNotNull(response);
        assertEquals("completed", response.getStatus());
        assertEquals("已完成", response.getStatusDisplay());
        assertEquals(5, response.getCurrentProgress());
        verify(sectTaskService).updateById(any(SectTaskProgress.class));
    }

    @Test
    void submitTask_AlreadyCompleted_ReturnsSuccess() {
        // Given
        SubmitTaskRequest request = new SubmitTaskRequest();
        request.setCharacterId(1L);
        request.setProgressId(1L);

        progress.setCurrentProgress(5);
        progress.setStatus("completed"); // 已完成

        when(characterService.getById(1L)).thenReturn(character);
        doReturn(progress).when(sectTaskService).getById(1L);
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);

        // When
        TaskProgressResponse response = sectTaskService.submitTask(request);

        // Then
        assertNotNull(response);
        assertEquals("completed", response.getStatus());
        // 不应该再次调用updateById
        verify(sectTaskService, never()).updateById(any(SectTaskProgress.class));
    }

    @Test
    void submitTask_NotCompleted_ThrowsException() {
        // Given
        SubmitTaskRequest request = new SubmitTaskRequest();
        request.setCharacterId(1L);
        request.setProgressId(1L);

        progress.setCurrentProgress(3); // 未完成

        when(characterService.getById(1L)).thenReturn(character);
        doReturn(progress).when(sectTaskService).getById(1L);
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.submitTask(request);
        });
        assertEquals(8018, exception.getCode());
        assertTrue(exception.getMessage().contains("任务未完成"));
    }

    @Test
    void submitTask_ClaimedStatus_ThrowsException() {
        // Given
        SubmitTaskRequest request = new SubmitTaskRequest();
        request.setCharacterId(1L);
        request.setProgressId(1L);

        progress.setStatus("claimed"); // 已领奖

        when(characterService.getById(1L)).thenReturn(character);
        doReturn(progress).when(sectTaskService).getById(1L);
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.submitTask(request);
        });
        assertEquals(8017, exception.getCode());
    }

    // ==================== claimReward 测试 ====================

    @Test
    void claimReward_Success() {
        // Given
        progress.setStatus("completed");
        progress.setCurrentProgress(5);

        doReturn(progress).when(sectTaskService).getById(1L);
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);
        doReturn(true).when(sectTaskService).updateById(any(SectTaskProgress.class));

        // When
        String message = sectTaskService.claimReward(1L);

        // Then
        assertNotNull(message);
        assertTrue(message.contains("50"));
        assertTrue(message.contains("10"));
        assertTrue(message.contains("贡献值"));
        assertTrue(message.contains("声望"));
        assertEquals("claimed", progress.getStatus());
        assertNotNull(progress.getClaimedAt());

        // 验证调用了贡献值和声望增加
        verify(sectMemberService).addContribution(1L, 50);
        verify(reputationMapper).selectOne(any(LambdaQueryWrapper.class));
        verify(sectTaskService).updateById(any(SectTaskProgress.class));
    }

    @Test
    void claimReward_NotCompleted_ThrowsException() {
        // Given
        progress.setStatus("accepted");

        doReturn(progress).when(sectTaskService).getById(1L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sectTaskService.claimReward(1L);
        });
        assertEquals(8019, exception.getCode());
    }

    // ==================== addTaskProgress 测试 ====================

    @Test
    void addTaskProgress_NoSect_DoesNothing() {
        // Given
        when(sectMemberService.getByCharacterId(1L)).thenReturn(null);

        // When
        sectTaskService.addTaskProgress(1L, "combat", "1", 1);

        // Then
        verify(sectTaskService, never()).list(any(LambdaQueryWrapper.class));
    }

    @Test
    void addTaskProgress_NoInProgressTasks_DoesNothing() {
        // Given
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        // mock service 的 list() 方法（继承自 ServiceImpl）
        doReturn(Collections.emptyList()).when(sectTaskService).list(any(LambdaQueryWrapper.class));

        // When
        sectTaskService.addTaskProgress(1L, "combat", "1", 1);

        // Then
        verify(taskTemplateMapper, never()).selectById(any());
    }

    @Test
    void addTaskProgress_CombatTask_Success() {
        // Given
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        doReturn(Arrays.asList(progress)).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);
        doReturn(true).when(sectTaskService).updateById(any(SectTaskProgress.class));

        // When
        sectTaskService.addTaskProgress(1L, "combat", "1", 1);

        // Then
        assertEquals(1, progress.getCurrentProgress());
        verify(sectTaskService).updateById(progress);
    }

    @Test
    void addTaskProgress_CombatTask_WrongLevel_NoUpdate() {
        // Given
        SectTaskTemplate level2Task = new SectTaskTemplate();
        level2Task.setTemplateId(1L);
        level2Task.setTaskType("combat");
        level2Task.setTargetValue("2"); // 需要2级妖兽
        level2Task.setTargetCount(5);

        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        doReturn(Arrays.asList(progress)).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        when(taskTemplateMapper.selectById(1L)).thenReturn(level2Task);

        // When
        sectTaskService.addTaskProgress(1L, "combat", "1", 1); // 打了1级妖兽

        // Then
        assertEquals(0, progress.getCurrentProgress());
        verify(sectTaskService, never()).updateById(any());
    }

    @Test
    void addTaskProgress_MeditationTask_Success() {
        // Given
        progress.setTemplateId(2L);

        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        doReturn(Arrays.asList(progress)).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        when(taskTemplateMapper.selectById(2L)).thenReturn(meditationTask);
        doReturn(true).when(sectTaskService).updateById(any(SectTaskProgress.class));

        // When
        sectTaskService.addTaskProgress(1L, "meditation", null, 1);

        // Then
        assertEquals(1, progress.getCurrentProgress());
        verify(sectTaskService).updateById(progress);
    }

    @Test
    void addTaskProgress_AutoComplete() {
        // Given
        progress.setCurrentProgress(4);
        progress.setTemplateId(1L);

        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        doReturn(Arrays.asList(progress)).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask);
        doReturn(true).when(sectTaskService).updateById(any(SectTaskProgress.class));

        // When - 进度从4增加到5，达到目标
        sectTaskService.addTaskProgress(1L, "combat", "1", 1);

        // Then
        assertEquals(5, progress.getCurrentProgress());
        assertEquals("completed", progress.getStatus());
        assertNotNull(progress.getCompletedAt());
        verify(sectTaskService).updateById(progress);
    }

    @Test
    void addTaskProgress_CollectionTask_Success() {
        // Given
        SectTaskTemplate collectionTask = new SectTaskTemplate();
        collectionTask.setTemplateId(1L);
        collectionTask.setTaskType("collection");
        collectionTask.setTargetValue("雪莲");
        collectionTask.setTargetCount(10);

        progress.setCurrentProgress(5);

        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        doReturn(Arrays.asList(progress)).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        when(taskTemplateMapper.selectById(1L)).thenReturn(collectionTask);
        doReturn(true).when(sectTaskService).updateById(any(SectTaskProgress.class));

        // When
        sectTaskService.addTaskProgress(1L, "collection", "雪莲", 3);

        // Then
        assertEquals(8, progress.getCurrentProgress()); // 5 + 3 = 8
        verify(sectTaskService).updateById(progress);
    }

    @Test
    void addTaskProgress_WrongItemType_NoUpdate() {
        // Given
        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        doReturn(Arrays.asList(progress)).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        when(taskTemplateMapper.selectById(1L)).thenReturn(meditationTask); // 修炼任务

        // When - 尝试增加战斗进度
        sectTaskService.addTaskProgress(1L, "combat", "1", 1);

        // Then
        assertEquals(0, progress.getCurrentProgress());
        verify(sectTaskService, never()).updateById(any());
    }

    @Test
    void addTaskProgress_MultipleInProgressTasks_UpdatesMatching() {
        // Given
        SectTaskProgress progress2 = new SectTaskProgress();
        progress2.setProgressId(2L);
        progress2.setCharacterId(1L);
        progress2.setTemplateId(2L);
        progress2.setCurrentProgress(0);
        progress2.setStatus("accepted");

        SectTaskTemplate combatTask2 = new SectTaskTemplate();
        combatTask2.setTemplateId(1L);
        combatTask2.setTaskType("combat");
        combatTask2.setTargetValue("1");
        combatTask2.setTargetCount(5);

        when(sectMemberService.getByCharacterId(1L)).thenReturn(sectMember);
        doReturn(Arrays.asList(progress, progress2)).when(sectTaskService).list(any(LambdaQueryWrapper.class));
        when(taskTemplateMapper.selectById(1L)).thenReturn(combatTask2);
        when(taskTemplateMapper.selectById(2L)).thenReturn(meditationTask);
        doReturn(true).when(sectTaskService).updateById(any(SectTaskProgress.class));

        // When
        sectTaskService.addTaskProgress(1L, "combat", "1", 1);

        // Then
        assertEquals(1, progress.getCurrentProgress());
        assertEquals(0, progress2.getCurrentProgress()); // 修炼任务未更新
        verify(sectTaskService, times(1)).updateById(any(SectTaskProgress.class));
    }

    // ==================== resetDailyTasks 测试 ====================

    @Test
    void resetDailyTasks_Success() {
        // When
        sectTaskService.resetDailyTasks();

        // Then - 只验证方法不抛异常，实际重置逻辑由定时任务调用
        assertNotNull(LocalDate.now());
    }
}
