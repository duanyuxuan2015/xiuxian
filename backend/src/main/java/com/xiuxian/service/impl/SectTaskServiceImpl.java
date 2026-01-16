package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.AcceptTaskRequest;
import com.xiuxian.dto.request.SubmitTaskRequest;
import com.xiuxian.dto.response.DailyTaskSummaryResponse;
import com.xiuxian.dto.response.SectTaskResponse;
import com.xiuxian.dto.response.TaskProgressResponse;
import com.xiuxian.entity.CharacterSectReputation;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.SectMember;
import com.xiuxian.entity.SectTaskProgress;
import com.xiuxian.entity.SectTaskTemplate;
import com.xiuxian.mapper.CharacterSectReputationMapper;
import com.xiuxian.mapper.SectTaskProgressMapper;
import com.xiuxian.mapper.SectTaskTemplateMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.SectMemberService;
import com.xiuxian.service.SectTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 宗门任务Service实现类
 */
@Service
public class SectTaskServiceImpl extends ServiceImpl<SectTaskProgressMapper, SectTaskProgress> implements SectTaskService {

    private static final Logger logger = LoggerFactory.getLogger(SectTaskServiceImpl.class);

    private static final int DAILY_ACCEPT_LIMIT = 3;

    private static final Map<String, Integer> POSITION_LEVELS = Map.of(
            "弟子", 1,
            "内门弟子", 2,
            "核心弟子", 3,
            "长老", 4,
            "掌门", 5);

    private static final Map<String, String> TASK_TYPE_DISPLAY = Map.of(
            "combat", "战斗任务",
            "meditation", "修炼任务",
            "collection", "收集任务",
            "crafting", "制作任务");

    private static final Map<String, String> STATUS_DISPLAY = Map.of(
            "accepted", "进行中",
            "completed", "已完成",
            "claimed", "已领奖");

    private final SectTaskTemplateMapper taskTemplateMapper;
    private final CharacterService characterService;
    private final SectMemberService sectMemberService;
    private final CharacterSectReputationMapper reputationMapper;

    public SectTaskServiceImpl(SectTaskTemplateMapper taskTemplateMapper,
                                CharacterService characterService,
                                @Lazy SectMemberService sectMemberService,
                                CharacterSectReputationMapper reputationMapper) {
        this.taskTemplateMapper = taskTemplateMapper;
        this.characterService = characterService;
        this.sectMemberService = sectMemberService;
        this.reputationMapper = reputationMapper;
    }

    @Override
    public List<SectTaskResponse> getAvailableTasks(Long characterId) {
        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证已加入宗门
        SectMember member = sectMemberService.getByCharacterId(characterId);
        if (member == null) {
            throw new BusinessException(8004, "未加入任何宗门");
        }

        // 3. 获取职位等级
        int memberPosition = POSITION_LEVELS.getOrDefault(member.getPosition(), 1);

        // 4. 获取今日已接取次数
        int todayAcceptCount = getTodayAcceptCount(characterId);

        // 5. 查询宗门的活跃任务模板
        LambdaQueryWrapper<SectTaskTemplate> templateWrapper = new LambdaQueryWrapper<>();
        templateWrapper.eq(SectTaskTemplate::getSectId, member.getSectId())
                .eq(SectTaskTemplate::getIsActive, true)
                .orderByAsc(SectTaskTemplate::getRequiredPosition)
                .orderByAsc(SectTaskTemplate::getTaskType);
        List<SectTaskTemplate> templates = taskTemplateMapper.selectList(templateWrapper);

        // 6. 查询已接取的任务ID
        LambdaQueryWrapper<SectTaskProgress> progressWrapper = new LambdaQueryWrapper<>();
        progressWrapper.eq(SectTaskProgress::getCharacterId, characterId)
                .in(SectTaskProgress::getStatus, List.of("accepted", "completed"));
        List<SectTaskProgress> existingProgress = this.list(progressWrapper);
        List<Long> existingTemplateIds = existingProgress.stream()
                .map(SectTaskProgress::getTemplateId)
                .collect(Collectors.toList());

        // 7. 构建响应
        List<SectTaskResponse> responses = new ArrayList<>();
        for (SectTaskTemplate template : templates) {
            // 跳过已接取的任务
            if (existingTemplateIds.contains(template.getTemplateId())) {
                continue;
            }

            SectTaskResponse response = convertToResponse(template, memberPosition);
            // 检查是否可以接取（职位等级 + 每日限制）
            boolean canAccept = memberPosition >= template.getRequiredPosition()
                    && todayAcceptCount < DAILY_ACCEPT_LIMIT;
            response.setCanAccept(canAccept);
            responses.add(response);
        }

        return responses;
    }

    @Override
    public DailyTaskSummaryResponse getMyTaskSummary(Long characterId) {
        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证已加入宗门
        SectMember member = sectMemberService.getByCharacterId(characterId);
        if (member == null) {
            throw new BusinessException(8004, "未加入任何宗门");
        }

        // 3. 获取职位等级
        int memberPosition = POSITION_LEVELS.getOrDefault(member.getPosition(), 1);

        // 4. 获取今日已接取次数
        int todayAcceptCount = getTodayAcceptCount(characterId);

        // 5. 获取可接取任务
        List<SectTaskResponse> availableTasks = getAvailableTasks(characterId);

        // 6. 获取进行中任务
        LambdaQueryWrapper<SectTaskProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectTaskProgress::getCharacterId, characterId)
                .in(SectTaskProgress::getStatus, List.of("accepted", "completed"))
                .orderByDesc(SectTaskProgress::getAcceptedAt);
        List<SectTaskProgress> inProgress = this.list(wrapper);

        List<TaskProgressResponse> inProgressTasks = new ArrayList<>();
        for (SectTaskProgress progress : inProgress) {
            SectTaskTemplate template = taskTemplateMapper.selectById(progress.getTemplateId());
            if (template != null) {
                TaskProgressResponse response = convertToProgressResponse(progress, template);
                inProgressTasks.add(response);
            }
        }

        // 7. 构建汇总响应
        DailyTaskSummaryResponse summary = new DailyTaskSummaryResponse();
        summary.setRemainingAccepts(Math.max(0, DAILY_ACCEPT_LIMIT - todayAcceptCount));
        summary.setCompletedCount(getTodayCompletedCount(characterId));
        summary.setTotalDailyLimit(DAILY_ACCEPT_LIMIT);
        summary.setAvailableTasks(availableTasks);
        summary.setInProgressTasks(inProgressTasks);

        return summary;
    }

    @Override
    @Transactional
    public TaskProgressResponse acceptTask(AcceptTaskRequest request) {
        Long characterId = request.getCharacterId();
        Long templateId = request.getTemplateId();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证已加入宗门
        SectMember member = sectMemberService.getByCharacterId(characterId);
        if (member == null) {
            throw new BusinessException(8004, "未加入任何宗门");
        }

        // 3. 验证任务模板
        SectTaskTemplate template = taskTemplateMapper.selectById(templateId);
        if (template == null || !template.getIsActive()) {
            throw new BusinessException(8010, "任务不存在或已关闭");
        }

        // 4. 验证任务属于玩家的宗门
        if (!template.getSectId().equals(member.getSectId())) {
            throw new BusinessException(8011, "该任务不属于您的宗门");
        }

        // 5. 验证职位要求
        int memberPosition = POSITION_LEVELS.getOrDefault(member.getPosition(), 1);
        if (memberPosition < template.getRequiredPosition()) {
            throw new BusinessException(8012, "职位不足，需要职位等级: " + template.getRequiredPosition());
        }

        // 6. 检查每日接取次数
        int todayAcceptCount = getTodayAcceptCount(characterId);
        if (todayAcceptCount >= DAILY_ACCEPT_LIMIT) {
            throw new BusinessException(8013, "今日接取次数已达上限");
        }

        // 7. 检查是否已接取该任务
        LambdaQueryWrapper<SectTaskProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectTaskProgress::getCharacterId, characterId)
                .eq(SectTaskProgress::getTemplateId, templateId)
                .in(SectTaskProgress::getStatus, List.of("accepted", "completed"));
        if (this.count(wrapper) > 0) {
            throw new BusinessException(8014, "您已接取过该任务");
        }

        // 8. 创建任务进度记录
        SectTaskProgress progress = new SectTaskProgress();
        progress.setCharacterId(characterId);
        progress.setTemplateId(templateId);
        progress.setCurrentProgress(0);
        progress.setStatus("accepted");
        progress.setAcceptedAt(LocalDateTime.now());
        this.save(progress);

        logger.info("接取宗门任务: characterId={}, taskId={}, taskName={}",
                characterId, templateId, template.getTaskName());

        // 9. 返回进度信息
        return convertToProgressResponse(progress, template);
    }

    @Override
    @Transactional
    public TaskProgressResponse submitTask(SubmitTaskRequest request) {
        Long characterId = request.getCharacterId();
        Long progressId = request.getProgressId();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 查询任务进度
        SectTaskProgress progress = this.getById(progressId);
        if (progress == null) {
            throw new BusinessException(8015, "任务进度不存在");
        }

        // 3. 验证任务属于该角色
        if (!progress.getCharacterId().equals(characterId)) {
            throw new BusinessException(8016, "无权操作该任务");
        }

        // 4. 查询任务模板
        SectTaskTemplate template = taskTemplateMapper.selectById(progress.getTemplateId());
        if (template == null) {
            throw new BusinessException(8010, "任务模板不存在");
        }

        // 5. 如果任务已经是completed状态，直接返回成功
        if ("completed".equals(progress.getStatus())) {
            logger.info("任务已完成: characterId={}, taskId={}, progress={}/{}",
                    characterId, progress.getTemplateId(),
                    progress.getCurrentProgress(), template.getTargetCount());
            return convertToProgressResponse(progress, template);
        }

        // 6. 验证任务状态（只允许accepted状态提交）
        if (!"accepted".equals(progress.getStatus())) {
            throw new BusinessException(8017, "任务状态不正确，当前状态: " + progress.getStatus());
        }

        // 7. 验证任务是否完成
        if (progress.getCurrentProgress() < template.getTargetCount()) {
            throw new BusinessException(8018,
                    String.format("任务未完成，当前进度: %d/%d",
                            progress.getCurrentProgress(), template.getTargetCount()));
        }

        // 8. 更新任务状态为已完成
        progress.setStatus("completed");
        progress.setCompletedAt(LocalDateTime.now());
        this.updateById(progress);

        logger.info("提交宗门任务: characterId={}, taskId={}, progress={}/{}",
                characterId, progress.getTemplateId(),
                progress.getCurrentProgress(), template.getTargetCount());

        return convertToProgressResponse(progress, template);
    }

    @Override
    @Transactional
    public String claimReward(Long progressId) {
        // 1. 查询任务进度
        SectTaskProgress progress = this.getById(progressId);
        if (progress == null) {
            throw new BusinessException(8015, "任务进度不存在");
        }

        // 2. 验证任务状态
        if (!"completed".equals(progress.getStatus())) {
            throw new BusinessException(8019, "任务尚未完成");
        }

        // 3. 查询任务模板
        SectTaskTemplate template = taskTemplateMapper.selectById(progress.getTemplateId());
        if (template == null) {
            throw new BusinessException(8010, "任务模板不存在");
        }

        // 4. 增加贡献值
        sectMemberService.addContribution(progress.getCharacterId(), template.getContributionReward());

        // 5. 增加声望
        addReputation(progress.getCharacterId(), template.getSectId(), template.getReputationReward());

        // 6. 更新任务状态为已领奖
        progress.setStatus("claimed");
        progress.setClaimedAt(LocalDateTime.now());
        this.updateById(progress);

        logger.info("领取任务奖励: characterId={}, taskId={}, contribution={}, reputation={}",
                progress.getCharacterId(), progress.getTemplateId(),
                template.getContributionReward(), template.getReputationReward());

        return String.format("成功领取奖励！获得 %d 贡献值，%d 声望",
                template.getContributionReward(), template.getReputationReward());
    }

    @Override
    @Transactional
    public void addTaskProgress(Long characterId, String taskType, String targetValue, Integer count) {
        // 1. 验证角色已加入宗门
        SectMember member = sectMemberService.getByCharacterId(characterId);
        if (member == null) {
            logger.debug("角色 {} 未加入宗门，跳过任务进度更新", characterId);
            return; // 未加入宗门，不处理任务进度
        }

        // 2. 查询该角色进行中的相关任务
        LambdaQueryWrapper<SectTaskProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectTaskProgress::getCharacterId, characterId)
                .eq(SectTaskProgress::getStatus, "accepted");
        List<SectTaskProgress> progressList = this.list(wrapper);

        if (progressList.isEmpty()) {
            logger.debug("角色 {} 没有进行中的任务", characterId);
            return; // 没有进行中的任务
        }

        logger.info("更新宗门任务进度: characterId={}, taskType={}, targetValue={}, count={}, 进行中任务数={}",
                characterId, taskType, targetValue, count, progressList.size());

        // 3. 匹配任务类型和目标值
        for (SectTaskProgress progress : progressList) {
            SectTaskTemplate template = taskTemplateMapper.selectById(progress.getTemplateId());
            if (template == null) {
                logger.warn("任务模板不存在: templateId={}", progress.getTemplateId());
                continue;
            }

            logger.debug("检查任务: taskId={}, taskType={}, targetValue={}, currentProgress={}/{}",
                    template.getTemplateId(), template.getTaskType(), template.getTargetValue(),
                    progress.getCurrentProgress(), template.getTargetCount());

            if (!taskType.equals(template.getTaskType())) {
                logger.debug("任务类型不匹配: 需要={}, 实际={}", template.getTaskType(), taskType);
                continue;
            }

            // 检查目标值是否匹配
            boolean targetMatches = false;
            if ("meditation".equals(taskType)) {
                // 修炼任务不检查目标值
                targetMatches = true;
                logger.debug("修炼任务，跳过目标值检查");
            } else if (targetValue != null && targetValue.equals(template.getTargetValue())) {
                targetMatches = true;
                logger.debug("目标值匹配: {}", targetValue);
            } else {
                logger.debug("目标值不匹配: 需要={}, 实际={}", template.getTargetValue(), targetValue);
            }

            if (targetMatches) {
                // 增加进度
                int oldProgress = progress.getCurrentProgress();
                int newProgress = Math.min(progress.getCurrentProgress() + count,
                        template.getTargetCount());
                progress.setCurrentProgress(newProgress);

                logger.info("任务进度更新: characterId={}, taskId={}, {} -> {}",
                        characterId, template.getTemplateId(), oldProgress, newProgress);

                // 如果完成任务，更新状态
                if (newProgress >= template.getTargetCount()) {
                    progress.setStatus("completed");
                    progress.setCompletedAt(LocalDateTime.now());
                    logger.info("宗门任务自动完成: characterId={}, taskId={}, taskName={}",
                            characterId, template.getTemplateId(), template.getTaskName());
                }

                this.updateById(progress);
            }
        }
    }

    @Override
    public void resetDailyTasks() {
        // 定时任务调用，重置每日任务次数
        // 当前实现中，次数限制是基于当日接取任务数量统计的
        // 这个方法可以用于清理过期数据或重置某些状态
        logger.info("每日任务重置执行: {}", LocalDate.now());
    }

    // ==================== 私有辅助方法 ====================

    private SectTaskResponse convertToResponse(SectTaskTemplate template, int memberPosition) {
        SectTaskResponse response = new SectTaskResponse();
        response.setTemplateId(template.getTemplateId());
        response.setTaskName(template.getTaskName());
        response.setTaskType(template.getTaskType());
        response.setTaskTypeDisplay(TASK_TYPE_DISPLAY.getOrDefault(template.getTaskType(), template.getTaskType()));
        response.setDescription(template.getDescription());
        response.setTargetCount(template.getTargetCount());
        response.setContributionReward(template.getContributionReward());
        response.setReputationReward(template.getReputationReward());
        response.setRequiredPosition(template.getRequiredPosition());

        // 构建目标显示文本
        String targetDisplay = buildTargetDisplay(template);
        response.setTargetDisplay(targetDisplay);

        return response;
    }

    private TaskProgressResponse convertToProgressResponse(SectTaskProgress progress, SectTaskTemplate template) {
        TaskProgressResponse response = new TaskProgressResponse();
        response.setProgressId(progress.getProgressId());
        response.setTaskName(template.getTaskName());
        response.setTaskType(template.getTaskType());
        response.setTaskTypeDisplay(TASK_TYPE_DISPLAY.getOrDefault(template.getTaskType(), template.getTaskType()));
        response.setStatus(progress.getStatus());
        response.setStatusDisplay(STATUS_DISPLAY.getOrDefault(progress.getStatus(), progress.getStatus()));
        response.setCurrentProgress(progress.getCurrentProgress());
        response.setTargetCount(template.getTargetCount());
        response.setProgressDisplay(String.format("%d/%d",
                progress.getCurrentProgress(), template.getTargetCount()));
        response.setContributionReward(template.getContributionReward());
        response.setReputationReward(template.getReputationReward());

        return response;
    }

    private String buildTargetDisplay(SectTaskTemplate template) {
        switch (template.getTaskType()) {
            case "combat":
                return String.format("击杀%d只%s级妖兽", template.getTargetCount(), template.getTargetValue());
            case "meditation":
                return String.format("完成%d次打坐修炼", template.getTargetCount());
            case "collection":
                return String.format("收集%d个%s", template.getTargetCount(), template.getTargetValue());
            case "crafting":
                return String.format("制作%d个%s", template.getTargetCount(), template.getTargetValue());
            default:
                return "完成目标";
        }
    }

    private int getTodayAcceptCount(Long characterId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<SectTaskProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectTaskProgress::getCharacterId, characterId)
                .ge(SectTaskProgress::getAcceptedAt, todayStart);
        return (int) this.count(wrapper);
    }

    private int getTodayCompletedCount(Long characterId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<SectTaskProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectTaskProgress::getCharacterId, characterId)
                .eq(SectTaskProgress::getStatus, "claimed")
                .ge(SectTaskProgress::getClaimedAt, todayStart);
        return (int) this.count(wrapper);
    }

    private void addReputation(Long characterId, Long sectId, int reputation) {
        if (reputation <= 0) {
            return;
        }

        // 查询现有声望记录
        LambdaQueryWrapper<CharacterSectReputation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterSectReputation::getCharacterId, characterId)
                .eq(CharacterSectReputation::getSectId, sectId);
        CharacterSectReputation record = reputationMapper.selectOne(wrapper);

        if (record != null) {
            // 更新现有记录
            record.setReputation(record.getReputation() + reputation);
            reputationMapper.updateById(record);
        } else {
            // 创建新记录
            record = new CharacterSectReputation();
            record.setCharacterId(characterId);
            record.setSectId(sectId);
            record.setReputation(reputation);
            record.setCreatedAt(LocalDateTime.now());
            reputationMapper.insert(record);
        }

        logger.info("增加宗门声望: characterId={}, sectId={}, added={}, total={}",
                characterId, sectId, reputation, record.getReputation());
    }
}
