package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.JoinSectRequest;
import com.xiuxian.dto.request.SectShopBuyRequest;
import com.xiuxian.dto.response.SectMemberResponse;
import com.xiuxian.dto.response.SectResponse;
import com.xiuxian.dto.response.SectShopItemResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Sect;
import com.xiuxian.entity.SectMember;
import com.xiuxian.entity.SectShopItem;
import com.xiuxian.mapper.SectMemberMapper;
import com.xiuxian.mapper.SectShopItemMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.InventoryService;
import com.xiuxian.service.SectMemberService;
import com.xiuxian.service.SectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 宗门成员Service实现类
 */
@Service
public class SectMemberServiceImpl extends ServiceImpl<SectMemberMapper, SectMember> implements SectMemberService {

    private static final Logger logger = LoggerFactory.getLogger(SectMemberServiceImpl.class);

    private static final Map<String, Integer> POSITION_LEVELS = Map.of(
            "弟子", 1,
            "内门弟子", 2,
            "核心弟子", 3,
            "长老", 4,
            "掌门", 5);

    private final CharacterService characterService;
    private final SectService sectService;
    private final InventoryService inventoryService;
    private final SectShopItemMapper shopItemMapper;

    public SectMemberServiceImpl(@Lazy CharacterService characterService,
            SectService sectService,
            InventoryService inventoryService,
            SectShopItemMapper shopItemMapper) {
        this.characterService = characterService;
        this.sectService = sectService;
        this.inventoryService = inventoryService;
        this.shopItemMapper = shopItemMapper;
    }

    @Override
    public List<SectResponse> getAllSects(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        List<Sect> sects = sectService.list();
        SectMember currentMember = getByCharacterId(characterId);

        List<SectResponse> responses = new ArrayList<>();
        for (Sect sect : sects) {
            int memberCount = countSectMembers(sect.getSectId());
            boolean isJoined = currentMember != null && currentMember.getSectId().equals(sect.getSectId());
            responses.add(SectResponse.fromEntity(sect, memberCount, isJoined));
        }

        return responses;
    }

    @Override
    public SectMemberResponse getCharacterSect(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        SectMember member = getByCharacterId(characterId);
        if (member == null) {
            return null;
        }

        Sect sect = sectService.getById(member.getSectId());
        return SectMemberResponse.fromEntity(member, sect, character.getCharacterName());
    }

    @Override
    @Transactional
    public SectMemberResponse joinSect(JoinSectRequest request) {
        Long characterId = request.getCharacterId();
        Long sectId = request.getSectId();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证宗门
        Sect sect = sectService.getById(sectId);
        if (sect == null) {
            throw new BusinessException(8001, "宗门不存在");
        }

        // 3. 检查境界要求
        if (character.getRealmLevel() < sect.getRequiredRealmLevel()) {
            throw new BusinessException(8002, "境界不足，需要境界等级: " + sect.getRequiredRealmLevel());
        }

        // 4. 检查是否已加入宗门
        SectMember existing = getByCharacterId(characterId);
        if (existing != null) {
            throw new BusinessException(8003, "已加入宗门，需先退出当前宗门");
        }

        // 5. 加入宗门
        SectMember member = new SectMember();
        member.setSectId(sectId);
        member.setCharacterId(characterId);
        member.setPosition("弟子");
        member.setContribution(0);
        member.setWeeklyContribution(0);
        member.setJoinedAt(LocalDateTime.now());
        this.save(member);

        logger.info("加入宗门: characterId={}, sectId={}, sectName={}",
                characterId, sectId, sect.getSectName());

        return SectMemberResponse.fromEntity(member, sect, character.getCharacterName());
    }

    @Override
    @Transactional
    public boolean leaveSect(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        SectMember member = getByCharacterId(characterId);
        if (member == null) {
            throw new BusinessException(8004, "未加入任何宗门");
        }

        this.removeById(member.getMemberId());
        logger.info("退出宗门: characterId={}, sectId={}", characterId, member.getSectId());

        return true;
    }

    @Override
    public List<SectShopItemResponse> getShopItems(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        SectMember member = getByCharacterId(characterId);
        if (member == null) {
            throw new BusinessException(8004, "未加入任何宗门");
        }

        int memberPosition = POSITION_LEVELS.getOrDefault(member.getPosition(), 1);

        LambdaQueryWrapper<SectShopItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectShopItem::getSectId, member.getSectId())
                .orderByAsc(SectShopItem::getRequiredPosition)
                .orderByAsc(SectShopItem::getPrice);
        List<SectShopItem> items = shopItemMapper.selectList(wrapper);

        List<SectShopItemResponse> responses = new ArrayList<>();
        for (SectShopItem item : items) {
            responses.add(SectShopItemResponse.fromEntity(item, memberPosition));
        }

        return responses;
    }

    @Override
    @Transactional
    public String buyShopItem(SectShopBuyRequest request) {
        Long characterId = request.getCharacterId();
        Long itemId = request.getItemId();
        int quantity = request.getQuantity();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证宗门成员
        SectMember member = getByCharacterId(characterId);
        if (member == null) {
            throw new BusinessException(8004, "未加入任何宗门");
        }

        // 3. 验证商品
        SectShopItem item = shopItemMapper.selectById(itemId);
        if (item == null || !item.getSectId().equals(member.getSectId())) {
            throw new BusinessException(8005, "商品不存在");
        }

        // 4. 验证职位
        int memberPosition = POSITION_LEVELS.getOrDefault(member.getPosition(), 1);
        if (memberPosition < item.getRequiredPosition()) {
            throw new BusinessException(8006, "职位不足，需要职位等级: " + item.getRequiredPosition());
        }

        // 5. 验证库存
        Integer currentStock = item.getCurrentStock();
        if (currentStock == null || currentStock < quantity) {
            int stockValue = (currentStock == null) ? 0 : currentStock;
            throw new BusinessException(8007, "库存不足，当前库存: " + stockValue);
        }

        // 6. 验证贡献值
        int totalPrice = item.getPrice() * quantity;
        if (member.getContribution() < totalPrice) {
            throw new BusinessException(8008, "贡献值不足，需要: " + totalPrice + "，当前: " + member.getContribution());
        }

        // 7. 扣除贡献值
        member.setContribution(member.getContribution() - totalPrice);
        this.updateById(member);

        // 8. 扣除库存
        item.setCurrentStock(item.getCurrentStock() - quantity);
        shopItemMapper.updateById(item);

        // 9. 添加物品到背包
        inventoryService.addItem(characterId, item.getItemType(), item.getRefItemId(), quantity);

        logger.info("宗门商店购买: characterId={}, itemId={}, itemName={}, quantity={}, cost={}",
                characterId, itemId, item.getItemName(), quantity, totalPrice);

        return String.format("成功购买 %s x%d，消耗贡献值 %d", item.getItemName(), quantity, totalPrice);
    }

    @Override
    @Transactional
    public void addContribution(Long characterId, int contribution) {
        if (contribution <= 0) {
            return;
        }

        SectMember member = getByCharacterId(characterId);
        if (member != null) {
            member.setContribution(member.getContribution() + contribution);
            member.setWeeklyContribution(member.getWeeklyContribution() + contribution);
            this.updateById(member);
            logger.info("增加宗门贡献: characterId={}, added={}, total={}",
                    characterId, contribution, member.getContribution());
        }
    }

    @Override
    public List<SectMemberResponse> getSectMembers(Long sectId) {
        Sect sect = sectService.getById(sectId);
        if (sect == null) {
            throw new BusinessException(8001, "宗门不存在");
        }

        LambdaQueryWrapper<SectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectMember::getSectId, sectId)
                .orderByDesc(SectMember::getContribution);
        List<SectMember> members = this.list(wrapper);

        List<SectMemberResponse> responses = new ArrayList<>();
        for (SectMember member : members) {
            PlayerCharacter character = characterService.getById(member.getCharacterId());
            String charName = character != null ? character.getCharacterName() : "未知";
            responses.add(SectMemberResponse.fromEntity(member, sect, charName));
        }

        return responses;
    }

    private SectMember getByCharacterId(Long characterId) {
        LambdaQueryWrapper<SectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectMember::getCharacterId, characterId);
        return this.getOne(wrapper);
    }

    private int countSectMembers(Long sectId) {
        LambdaQueryWrapper<SectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectMember::getSectId, sectId);
        return (int) this.count(wrapper);
    }
}
