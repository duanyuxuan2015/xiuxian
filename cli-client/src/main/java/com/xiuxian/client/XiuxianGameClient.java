package com.xiuxian.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xiuxian.client.model.*;
import com.xiuxian.client.util.ApiClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

/**
 * 凡人修仙文字游戏命令行客户端
 */
public class XiuxianGameClient {

    private static final Scanner scanner = new Scanner(System.in, "UTF-8");
    private static Long currentCharacterId = null;
    private static CharacterResponse currentCharacter = null;
    // 使用ApiClient的Gson实例，它已配置LocalDateTime支持
    private static final Gson gson = ApiClient.getGson();

    public static void main(String[] args) {
        // 设置控制台编码为 UTF-8
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println("警告: 无法设置控制台编码为 UTF-8");
        }

        // 尝试设置 Windows 控制台代码页为 UTF-8
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "chcp", "65001");
                pb.inheritIO();
                Process p = pb.start();
                p.waitFor();
            }
        } catch (Exception e) {
            // 忽略错误，继续执行
        }
        System.out.println("\n" +
                "  ╔════════════════════════════════════════════╗\n" +
                "  ║                                          ║\n" +
                "  ║         凡 人 修 仙 文 字 游 戏           ║\n" +
                "  ║                                          ║\n" +
                "  ║           Mortal Cultivation             ║\n" +
                "  ║                                          ║\n" +
                "  ╚════════════════════════════════════════════╝\n");

        System.out.println("\n[系统] 正在连接游戏服务器...");
        System.out.println("[系统] 服务器地址: http://localhost:8080/api/v1");

        try {
            // 测试服务器连接 - 使用检查角色名接口
            ApiClient.get("/characters/check-name/test");
            System.out.println("[系统] ✅ 服务器连接成功！");
        } catch (Exception e) {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║ ❌ 无法连接到游戏服务器                                      ║");
            System.out.println("╠════════════════════════════════════════════════════════════╣");
            System.out.println("║ 错误类型: " + e.getClass().getSimpleName() + "                  ║");
            System.out.println("║ 错误信息: " + e.getMessage() + "           ║");
            System.out.println("╠════════════════════════════════════════════════════════════╣");
            System.out.println("║ 可能的原因:                                                   ║");
            System.out.println("║ 1. 后端服务未启动                                             ║");
            System.out.println("║ 2. 端口8080被占用                                            ║");
            System.out.println("║ 3. 网络连接问题                                               ║");
            System.out.println("╠════════════════════════════════════════════════════════════╣");
            System.out.println("║ 解决方案:                                                     ║");
            System.out.println("║ 1. 启动后端服务: cd backend && mvn spring-boot:run          ║");
            System.out.println("║ 2. 检查后端日志确认服务状态                                  ║");
            System.out.println("║ 3. 确认端口8080未被占用                                      ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");

            if (e.getCause() != null) {
                System.err.println("\n[详细错误] 原因: " + e.getCause().getMessage());
            }

            // 打印堆栈跟踪（调试用）
            System.err.println("\n[详细错误信息]");
            e.printStackTrace();

            System.exit(1);
            return;
        }

        while (true) {
            if (currentCharacterId == null) {
                showMainMenu();
            } else {
                showGameMenu();
            }
        }
    }

    /**
     * 显示主菜单
     */
    private static void showMainMenu() {
        System.out.println("\n┌──────────────────────────────────────┐");
        System.out.println("│              主 菜 单                 │");
        System.out.println("├──────────────────────────────────────┤");
        System.out.println("│  1. 创建角色                         │");
        System.out.println("│  2. 登录角色                         │");
        System.out.println("│  0. 退出游戏                         │");
        System.out.println("└──────────────────────────────────────┘");
        System.out.print("\n请选择: ");

        String choice = scanner.nextLine();

        try {
            switch (choice) {
                case "1": createCharacter(); break;
                case "2": loginCharacter(); break;
                case "0":
                    System.out.println("\n感谢游玩凡人修仙！再见！");
                    System.exit(0);
                    break;
                default: System.out.println("\n无效选择！");
            }
        } catch (Exception e) {
            System.out.println("\n❌ 错误: " + e.getMessage());
        }
    }

    /**
     * 显示游戏菜单
     */
    private static void showGameMenu() {
        refreshCharacter();

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  " + getCurrentCharacterInfo());
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│              游 戏 功 能                │");
        System.out.println("├────────────────────────────────────────┤");
        System.out.println("│  1. 👤 查看角色详情                    │");
        System.out.println("│  2. ⭐ 属性加点                        │");
        System.out.println("│  3. 🧘‍♂️ 打坐恢复                        │");
        System.out.println("│  4. 🧘 修炼修炼                        │");
        System.out.println("│  5. ⚔️ 战斗妖兽                        │");
        System.out.println("│  6. ⚗️ 炼制丹药                        │");
        System.out.println("│  7. 🔨 锻造装备                        │");
        System.out.println("│  8. 📜 技能管理                        │");
        System.out.println("│  9. 🗺️ 探索秘境                        │");
        System.out.println("│ 10. 🏛️ 宗门功能                        │");
        System.out.println("│ 11. 🎒 装备管理                        │");
        System.out.println("│  0. 🚪 退出登录                        │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.print("\n请选择: ");

        String choice = scanner.nextLine();

        try {
            switch (choice) {
                case "1": showCharacterDetail(); break;
                case "2": allocatePoints(); break;
                case "3": startMeditation(); break;
                case "4": showCultivationMenu(); break;
                case "5": showCombatMenu(); break;
                case "6": showAlchemyMenu(); break;
                case "7": showForgeMenu(); break;
                case "8": showSkillMenu(); break;
                case "9": showExplorationMenu(); break;
                case "10": showSectMenu(); break;
                case "11": showEquipmentMenu(); break;
                case "0":
                    currentCharacterId = null;
                    currentCharacter = null;
                    System.out.println("\n已退出登录！");
                    break;
                default: System.out.println("\n无效选择！");
            }
        } catch (Exception e) {
            System.out.println("\n❌ 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 创建角色
     */
    private static void createCharacter() throws IOException, InterruptedException {
        System.out.println("\n--- 创建角色 ---");

        System.out.print("请输入角色名: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("\n角色名不能为空！");
            return;
        }

        System.out.print("请选择性别 (男/女): ");
        String gender = scanner.nextLine().trim();

        if (!gender.equals("男") && !gender.equals("女")) {
            System.out.println("\n性别只能是'男'或'女'！(输入了: '" + gender + "')");
            return;
        }

        System.out.println("\n请选择宗门:");
        System.out.println("1. 黄枫谷");
        System.out.println("2. 青元门");
        System.out.println("3. 合欢宗");
        System.out.print("请选择 (1-3): ");
        String sectChoice = scanner.nextLine().trim();

        Long sectId;
        switch (sectChoice) {
            case "1": sectId = 1L; break;
            case "2": sectId = 2L; break;
            case "3": sectId = 3L; break;
            default:
                System.out.println("\n无效选择！");
                return;
        }

        // 属性点分配
        System.out.println("\n--- 属性点分配 ---");
        System.out.println("你有 45 点属性可以分配（每项基础5点，额外20点可自由分配）");
        System.out.println("属性包括: 体质、精神、悟性、机缘、气运");
        System.out.println("提示：平均分配的话，每项 9 点");

        int constitution = 0, spirit = 0, comprehension = 0, luck = 0, fortune = 0;

        while (true) {
            System.out.print("\n体质 (影响生命值，最小5): ");
            try {
                constitution = Integer.parseInt(scanner.nextLine().trim());
                if (constitution < 5) {
                    System.out.println("体质至少为5点！");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        while (true) {
            System.out.print("精神 (影响灵力，最小5): ");
            try {
                spirit = Integer.parseInt(scanner.nextLine().trim());
                if (spirit < 5) {
                    System.out.println("精神至少为5点！");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        while (true) {
            System.out.print("悟性 (影响修炼速度，最小5): ");
            try {
                comprehension = Integer.parseInt(scanner.nextLine().trim());
                if (comprehension < 5) {
                    System.out.println("悟性至少为5点！");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        while (true) {
            System.out.print("机缘 (影响探索收益，最小5): ");
            try {
                luck = Integer.parseInt(scanner.nextLine().trim());
                if (luck < 5) {
                    System.out.println("机缘至少为5点！");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        while (true) {
            System.out.print("气运 (影响战斗暴击，最小5): ");
            try {
                fortune = Integer.parseInt(scanner.nextLine().trim());
                if (fortune < 5) {
                    System.out.println("气运至少为5点！");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        int totalPoints = constitution + spirit + comprehension + luck + fortune;
        if (totalPoints != 45) {
            System.out.println("\n❌ 属性点分配不正确！总点数应为45，当前为" + totalPoints);
            System.out.println("请重新分配。");
            pressEnterToContinue();
            return;
        }

        // 构建请求JSON
        JsonObject request = new JsonObject();
        request.addProperty("playerName", name);
        request.addProperty("gender", gender);
        request.addProperty("sectId", sectId);
        request.addProperty("initialRealm", 1);
        request.addProperty("constitution", constitution);
        request.addProperty("spirit", spirit);
        request.addProperty("comprehension", comprehension);
        request.addProperty("luck", luck);
        request.addProperty("fortune", fortune);

        String response = ApiClient.post("/characters", request);
        CharacterResponse character = ApiClient.parseResponse(response, CharacterResponse.class);

        if (character != null) {
            System.out.println("\n✅ 角色创建成功！");
            System.out.println("欢迎 " + name + " 加入修仙界！");
            System.out.println("\n你的属性:");
            System.out.println("  体质: " + constitution);
            System.out.println("  精神: " + spirit);
            System.out.println("  悟性: " + comprehension);
            System.out.println("  机缘: " + luck);
            System.out.println("  气运: " + fortune);
            currentCharacterId = character.getCharacterId();
            currentCharacter = character;
            pressEnterToContinue();
        } else {
            System.out.println("\n❌ 角色创建失败！");
        }
    }

    /**
     * 登录角色
     */
    private static void loginCharacter() {
        System.out.println("\n--- 登录角色 ---");
        System.out.print("请输入角色ID: ");
        String idStr = scanner.nextLine();

        try {
            Long id = Long.parseLong(idStr);
            String response = ApiClient.get("/characters/" + id);
            CharacterResponse character = ApiClient.parseResponse(response, CharacterResponse.class);

            if (character != null) {
                currentCharacterId = character.getCharacterId();
                currentCharacter = character;
                System.out.println("\n✅ 登录成功！欢迎回来，" + character.getPlayerName() + "！");
                pressEnterToContinue();
            } else {
                System.out.println("\n❌ 角色不存在！");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的角色ID！");
        } catch (Exception e) {
            System.out.println("\n❌ 登录失败: " + e.getMessage());
        }
    }

    /**
     * 刷新角色信息
     */
    private static void refreshCharacter() {
        if (currentCharacterId != null) {
            try {
                String response = ApiClient.get("/characters/" + currentCharacterId);
                currentCharacter = ApiClient.parseResponse(response, CharacterResponse.class);
            } catch (Exception e) {
                System.err.println("刷新角色信息失败: " + e.getMessage());
            }
        }
    }

    /**
     * 获取角色信息字符串
     */
    private static String getCurrentCharacterInfo() {
        if (currentCharacter == null) return "未知角色";
        return String.format("%s | %s | 修为: %s | 灵力: %s/%s | 体力: %s/%s",
                formatValue(currentCharacter.getPlayerName(), "未知"),
                formatValue(currentCharacter.getRealmName(), "凡人"),
                formatValue(currentCharacter.getCultivation(), "0"),
                formatValue(currentCharacter.getSpiritualPower(), "0"),
                formatValue(currentCharacter.getSpiritualPowerMax(), "0"),
                formatValue(currentCharacter.getStamina(), "0"),
                formatValue(currentCharacter.getStaminaMax(), "0"));
    }

    /**
     * 查看角色详情
     */
    private static void showCharacterDetail() {
        refreshCharacter();
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                       角色详情                                 ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ 角色名: %-20s 性别: %-6s 宗门: %-12s ║\n",
                currentCharacter.getPlayerName(),
                currentCharacter.getGender(),
                formatValue(currentCharacter.getSectName(), "无"));
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ 境界: %-20s 等级: %-6s 炼丹: %-4s 锻造: %-4s  ║\n",
                formatValue(currentCharacter.getRealmName(), "未知"),
                formatValue(currentCharacter.getRealmLevel(), "0"),
                formatLevel(currentCharacter.getAlchemyLevel()),
                formatLevel(currentCharacter.getForgeLevel()));
        System.out.printf("║ 经验: %-20s 可用点: %-6s                       ║\n",
                formatValue(currentCharacter.getExperience(), "0"),
                formatValue(currentCharacter.getAvailablePoints(), "0"));
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ 修为: %-20s 灵力: %-15s               ║\n",
                formatValue(currentCharacter.getCultivation(), "0"),
                formatValue(currentCharacter.getSpiritualPower(), "0") + "/" + formatValue(currentCharacter.getSpiritualPowerMax(), "0"));
        System.out.printf("║ 生命: %-15s 攻击: %-15s              ║\n",
                formatValue(currentCharacter.getHealth(), "0") + "/" + formatValue(currentCharacter.getHealthMax(), "0"),
                formatValue(currentCharacter.getAttack(), "0"));
        System.out.printf("║ 体力: %-15s 防御: %-15s              ║\n",
                formatValue(currentCharacter.getStamina(), "0") + "/" + formatValue(currentCharacter.getStaminaMax(), "0"),
                formatValue(currentCharacter.getDefense(), "0"));
        System.out.printf("║ 暴击率: %-10s 暴击伤害: %-10s 速度: %-10s  ║\n",
                formatDouble(currentCharacter.getCritRate(), "0") + "%",
                formatDouble(currentCharacter.getCritDamage(), "0") + "%",
                formatDouble(currentCharacter.getSpeed(), "0"));
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ 体质: %-3s 精神: %-3s 悟性: %-3s 机缘: %-3s 气运: %-3s ║\n",
                formatValue(currentCharacter.getConstitution(), "?"),
                formatValue(currentCharacter.getSpirit(), "?"),
                formatValue(currentCharacter.getComprehension(), "?"),
                formatValue(currentCharacter.getLuck(), "?"),
                formatValue(currentCharacter.getFortune(), "?"));
        System.out.printf("║ 灵石: %-15s 贡献: %-15s               ║\n",
                formatValue(currentCharacter.getSpiritStones(), "0"),
                formatValue(currentCharacter.getContribution(), "0"));
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        pressEnterToContinue();
    }

    /**
     * 格式化值，处理 null 情况
     */
    private static String formatValue(Object value, String defaultValue) {
        return value != null ? String.valueOf(value) : defaultValue;
    }

    /**
     * 格式化 Double 值，保留一位小数
     */
    private static String formatDouble(Double value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return String.format("%.1f", value);
    }

    /**
     * 格式化等级显示
     */
    private static String formatLevel(Integer level) {
        return level != null ? level + "级" : "0级";
    }

    /**
     * 修炼菜单
     */
    private static void showCultivationMenu() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌────────────────────────────────────────┐");
            System.out.println("│              修 炼 菜 单                │");
            System.out.println("├────────────────────────────────────────┤");
            System.out.println("│  1. 🧘 开始修炼                        │");
            System.out.println("│  2. ⚡ 境界突破                        │");
            System.out.println("│  3. 📊 查看突破成功率                  │");
            System.out.println("│  0. 🔙 返回主菜单                      │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    startCultivation();
                    break;
                case "2":
                    attemptBreakthrough();
                    break;
                case "3":
                    viewBreakthroughRate();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 开始修炼
     */
    private static void startCultivation() throws IOException, InterruptedException {
        // 刷新角色信息以获取最新数据
        refreshCharacter();

        if (currentCharacter == null) {
            System.out.println("\n❌ 角色信息加载失败！");
            pressEnterToContinue();
            return;
        }

        if (currentCharacter.getStamina() < 5) {
            System.out.println("\n❌ 体力不足！(需要5点体力)");
            pressEnterToContinue();
            return;
        }

        System.out.println("\n--- 开始修炼 ---");
        System.out.println("正在修炼中...\n");

        JsonObject request = new JsonObject();
        request.addProperty("characterId", currentCharacterId);

        String response = ApiClient.post("/cultivation/start", request);
        CultivationResponse result = ApiClient.parseResponse(response, CultivationResponse.class);

        if (result != null) {
            System.out.println("✅ 修炼成功！");
            System.out.println(result.getMessage());
            System.out.println("\n修炼结果:");
            System.out.println("  获得经验: " + result.getExpGained());
            System.out.println("  消耗体力: " + result.getStaminaConsumed());
            System.out.println("  当前经验: " + result.getCurrentExperience());
            System.out.println("  当前体力: " + result.getCurrentStamina());

            if (result.getLeveledUp() != null && result.getLeveledUp()) {
                System.out.println("\n🎉 恭喜！境界提升！");
                System.out.println("  " + result.getStartRealm() + result.getStartLevel() + "层 -> "
                    + result.getEndRealm() + result.getEndLevel() + "层");
                if (result.getAvailablePointsGained() != null && result.getAvailablePointsGained() > 0) {
                    System.out.println("  获得属性点: " + result.getAvailablePointsGained());
                }
            }

            // 显示突破提示
            if (result.getNeedsBreakthrough() != null && result.getNeedsBreakthrough()) {
                System.out.println("\n⚠️ 境界已达巅峰！");
                System.out.println("  当前境界: " + result.getEndRealm() + result.getEndLevel() + "层");
                System.out.println("  下一境界: " + (result.getNextRealm() != null ? result.getNextRealm() : "未知"));
                System.out.println("  请使用「突破功能」进入下一境界！");
            }
        } else {
            System.out.println("❌ 修炼失败！");
        }

        pressEnterToContinue();
    }

    /**
     * 打坐恢复体力和灵力
     */
    private static void startMeditation() throws IOException, InterruptedException {
        // 刷新角色信息以获取最新数据
        refreshCharacter();

        if (currentCharacter == null) {
            System.out.println("\n❌ 角色信息加载失败！");
            pressEnterToContinue();
            return;
        }

        System.out.println("\n--- 打坐恢复 ---");
        System.out.println("正在打坐中...\n");

        JsonObject request = new JsonObject();
        request.addProperty("characterId", currentCharacterId);

        String response = ApiClient.post("/cultivation/meditation", request);
        MeditationResponse result = ApiClient.parseResponse(response, MeditationResponse.class);

        if (result != null) {
            System.out.println("✅ 打坐完成！");
            System.out.println(result.getMessage());
            System.out.println("\n恢复结果:");
            System.out.println("  恢复体力: " + result.getStaminaRecovered());
            System.out.println("  当前体力: " + result.getCurrentStamina() + "/" + result.getMaxStamina());
            System.out.println("  恢复灵力: " + result.getSpiritualPowerRecovered());
            System.out.println("  当前灵力: " + result.getCurrentSpiritualPower() + "/" + result.getMaxSpiritualPower());
        } else {
            System.out.println("❌ 打坐失败！");
        }

        pressEnterToContinue();
    }

    /**
     * 尝试境界突破
     */
    private static void attemptBreakthrough() throws IOException, InterruptedException {
        System.out.println("\n--- 境界突破 ---");
        System.out.println("准备突破到下一个境界...\n");

        // 刷新角色信息以获取最新境界数据
        refreshCharacter();

        if (currentCharacter == null) {
            System.out.println("❌ 角色信息加载失败！");
            pressEnterToContinue();
            return;
        }

        // 显示当前境界信息
        System.out.println("当前境界: " + currentCharacter.getRealmName() + currentCharacter.getRealmLevel() + "层");

        BreakthroughRequest request = new BreakthroughRequest();
        request.setCharacterId(currentCharacterId);
        request.setUsePill(false);  // 暂时不使用丹药

        String response = ApiClient.post("/cultivation/breakthrough", request);
        BreakthroughResponse result = ApiClient.parseResponse(response, BreakthroughResponse.class);

        if (result != null) {
            System.out.println("\n突破结果:");
            System.out.println("  突破成功率: " + result.getBreakthroughRate() + "%");
            System.out.println("  " + result.getPreviousRealm() + result.getPreviousLevel() + "层 -> "
                + result.getCurrentRealm() + result.getCurrentLevel() + "层");

            if (result.getSuccess()) {
                System.out.println("\n🎉 突破成功！");
                System.out.println("  恭喜！成功突破至" + result.getCurrentRealm() + "！");
                System.out.println("  获得属性点: " + result.getAttributePointsGained());
                if (result.getHpBonusGained() > 0) {
                    System.out.println("  生命上限提升: " + result.getHpBonusGained());
                }
                if (result.getSpBonusGained() > 0) {
                    System.out.println("  灵力上限提升: " + result.getSpBonusGained());
                }
                if (result.getAttackBonusGained() > 0) {
                    System.out.println("  攻击力提升: " + result.getAttackBonusGained());
                }
                if (result.getDefenseBonusGained() > 0) {
                    System.out.println("  防御力提升: " + result.getDefenseBonusGained());
                }
            } else {
                System.out.println("\n❌ 突破失败！");
                System.out.println("  道友请继续努力，提升境界后再尝试突破！");
            }

            System.out.println("\n" + result.getMessage());
        } else {
            System.out.println("❌ 突破失败！");
        }

        pressEnterToContinue();
    }

    /**
     * 查看突破成功率
     */
    private static void viewBreakthroughRate() throws IOException, InterruptedException {
        System.out.println("\n--- 查看突破成功率 ---");

        // 刷新角色信息以获取最新境界数据
        refreshCharacter();

        if (currentCharacter == null) {
            System.out.println("❌ 角色信息加载失败！");
            pressEnterToContinue();
            return;
        }

        String url = "/cultivation/breakthrough-rate?characterId=" + currentCharacterId;
        String response = ApiClient.get(url);

        // 解析响应
        com.google.gson.JsonObject jsonResponse = com.google.gson.JsonParser.parseString(response).getAsJsonObject();
        if (jsonResponse.has("code") && jsonResponse.get("code").getAsInt() == 200) {
            int rate = jsonResponse.get("data").getAsInt();
            System.out.println("\n当前突破成功率: " + rate + "%");
            System.out.println("境界: " + currentCharacter.getRealmName() + currentCharacter.getRealmLevel() + "层");
        } else {
            System.out.println("❌ 查询失败！");
        }

        pressEnterToContinue();
    }

    /**
     * 战斗菜单
     */
    private static void showCombatMenu() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              战 斗 系 统              │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看可挑战妖兽                   │");
            System.out.println("│  2. 开始战斗                         │");
            System.out.println("│  3. 🤖 挂机战斗                      │");
            System.out.println("│  4. 查看战斗记录                     │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": showMonsters(); break;
                case "2": startCombat(); break;
                case "3": startAutoCombat(); break;
                case "4": showCombatRecords(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 显示妖兽列表
     */
    private static void showMonsters() throws IOException, InterruptedException {
        System.out.println("\n--- 可挑战妖兽列表 ---");

        String response = ApiClient.get("/combat/monsters?characterId=" + currentCharacterId);
        Type listType = new TypeToken<List<Monster>>(){}.getType();
        List<Monster> monsters = gson.fromJson(JsonArray.class.equals(listType) ?
                new JsonObject().get("data").getAsJsonArray() :
                new JsonObject().getAsJsonArray("data"),
                listType);

        // 重新解析
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            monsters = gson.fromJson(array, listType);
        }

        if (monsters != null && !monsters.isEmpty()) {
            System.out.println("\nID    妖兽名称              境界      攻击   防御   经验奖励");
            System.out.println("────────────────────────────────────────────────────────────");
            for (int i = 0; i < monsters.size(); i++) {
                Monster m = monsters.get(i);
                System.out.printf("%-4d  %-20s  %-8s  %-6d %-6d %-8d\n",
                        m.getMonsterId(), m.getMonsterName(), m.getRealmName(),
                        m.getAttack(), m.getDefense(), m.getExpReward());
            }
            System.out.println("\n提示：输入ID开始战斗（例如：输入1挑战毒蛇）");
        } else {
            System.out.println("\n暂无可挑战的妖兽！");
        }

        pressEnterToContinue();
    }

    /**
     * 开始战斗
     */
    private static void startCombat() throws IOException, InterruptedException {
        System.out.println("\n--- 开始战斗 ---");
        System.out.print("请输入妖兽ID: ");
        String monsterIdStr = scanner.nextLine();

        try {
            Long monsterId = Long.parseLong(monsterIdStr);

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("monsterId", monsterId);

            String response = ApiClient.post("/combat/start", request);
            CombatResponse result = ApiClient.parseResponse(response, CombatResponse.class);

            if (result != null) {
                // 显示战斗日志
                if (result.getCombatLog() != null && !result.getCombatLog().isEmpty()) {
                    System.out.println("\n=== 战斗过程 ===");
                    for (String log : result.getCombatLog()) {
                        System.out.println(log);
                    }
                }

                if (result.isVictory()) {
                    System.out.println("\n✅ 战斗胜利！");
                    System.out.println("获得经验: " + result.getExpGained());
                    System.out.println("获得灵石: " + result.getSpiritStonesGained());
                    if (result.getItemsDropped() != null && !result.getItemsDropped().isEmpty()) {
                        System.out.println("掉落物品: " + String.join(", ", result.getItemsDropped()));
                    }
                } else {
                    System.out.println("\n❌ 战斗失败！");
                    System.out.println("剩余生命: " + result.getCharacterHpRemaining());
                }
            } else {
                System.out.println("\n❌ 战斗失败！可能原因：");
                System.out.println("  1. 妖兽ID不存在");
                System.out.println("  2. 体力不足");
                System.out.println("  3. 角色状态不允许战斗");
                System.out.println("\n提示：先选择「1. 查看可挑战妖兽」查看有效的妖兽ID");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的妖兽ID！请输入数字。");
        } catch (Exception e) {
            System.out.println("\n❌ 战斗出错: " + e.getMessage());
            e.printStackTrace();
        }

        pressEnterToContinue();
    }

    /**
     * 挂机战斗
     */
    private static void startAutoCombat() throws IOException, InterruptedException {
        System.out.println("\n--- 🤖 挂机战斗 ---");
        System.out.print("请输入妖兽ID: ");
        String monsterIdStr = scanner.nextLine();

        try {
            Long monsterId = Long.parseLong(monsterIdStr);

            // 统计信息
            int totalBattles = 0;
            int victories = 0;
            int defeats = 0;
            int totalExpGained = 0;
            int totalSpiritStonesGained = 0;

            System.out.println("\n🤖 挂机开始！战斗中...");
            System.out.println("提示：挂机将持续到体力耗尽或战斗失败");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

            boolean continueAuto = true;
            while (continueAuto) {
                try {
                    JsonObject request = new JsonObject();
                    request.addProperty("characterId", currentCharacterId);
                    request.addProperty("monsterId", monsterId);

                    String response = ApiClient.post("/combat/start", request);
                    CombatResponse result = ApiClient.parseResponse(response, CombatResponse.class);

                    if (result != null) {
                        totalBattles++;

                        if (result.isVictory()) {
                            victories++;
                            totalExpGained += result.getExpGained();
                            totalSpiritStonesGained += result.getSpiritStonesGained();

                            System.out.printf("第%d战 ✅ 胜利！经验+%d 灵石+%d | 体力:%d 气血:%d 灵力:%d\n",
                                    totalBattles,
                                    result.getExpGained(),
                                    result.getSpiritStonesGained(),
                                    result.getCharacterStaminaRemaining(),
                                    result.getCharacterHpRemaining(),
                                    result.getCharacterSpiritualPowerRemaining());
                        } else {
                            defeats++;
                            System.out.printf("第%d战 ❌ 失败！剩余生命: %d\n",
                                    totalBattles,
                                    result.getCharacterHpRemaining());
                            continueAuto = false;
                        }

                        // 短暂延迟，避免请求过快
                        Thread.sleep(500);
                    } else {
                        // 解析失败，可能是体力不足或其他错误
                        continueAuto = false;
                    }
                } catch (Exception e) {
                    // 任何错误都停止挂机
                    System.err.println("\n挂机中断: " + e.getMessage());
                    continueAuto = false;
                }
            }

            // 显示挂机统计
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🤖 挂机结束！战斗统计：");
            System.out.println("┌─────────────────────────────────────┐");
            System.out.printf("│ 总战斗次数：%-4d                    │\n", totalBattles);
            System.out.printf("│ 胜利次数：  %-4d                    │\n", victories);
            System.out.printf("│ 失败次数：  %-4d                    │\n", defeats);
            System.out.println("├─────────────────────────────────────┤");
            System.out.printf("│ 获得经验：  %-8d                │\n", totalExpGained);
            System.out.printf("│ 获得灵石：  %-8d                │\n", totalSpiritStonesGained);
            System.out.println("└─────────────────────────────────────┘");

            if (victories > 0) {
                double winRate = (double) victories / totalBattles * 100;
                System.out.printf("胜率：%.1f%%\n", winRate);
            }

        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的妖兽ID！请输入数字。");
        } catch (Exception e) {
            System.out.println("\n❌ 挂机出错: " + e.getMessage());
        }

        pressEnterToContinue();
    }

    /**
     * 查看战斗记录
     */
    private static void showCombatRecords() throws IOException, InterruptedException {
        System.out.println("\n--- 战斗记录 ---");
        String response = ApiClient.get("/combat/records?characterId=" + currentCharacterId + "&page=1&pageSize=10");
        System.out.println("\n" + response);
        pressEnterToContinue();
    }

    /**
     * 炼丹菜单
     */
    private static void showAlchemyMenu() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              炼 丹 系 统              │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看丹方列表                     │");
            System.out.println("│  2. 开始炼丹                         │");
            System.out.println("│  3. 查看炼丹记录                     │");
            System.out.println("│  4. 查看我的材料                     │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": showPillRecipes(); break;
                case "2": startAlchemy(); break;
                case "3": showAlchemyRecords(); break;
                case "4": showMyMaterials(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 显示丹方列表
     */
    private static void showPillRecipes() throws IOException, InterruptedException {
        System.out.println("\n--- 丹方列表 ---");

        String response = ApiClient.get("/alchemy/recipes/" + currentCharacterId);
        Type listType = new TypeToken<List<PillRecipeResponse>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<PillRecipeResponse> recipes = gson.fromJson(array, listType);

            if (recipes != null && !recipes.isEmpty()) {
                System.out.println("\n序号  丹方名称              成功率  需要炼丹等级");
                System.out.println("─────────────────────────────────────────────");
                for (int i = 0; i < recipes.size(); i++) {
                    PillRecipeResponse r = recipes.get(i);
                    System.out.printf("%-4d  %-20s  %-6d  %-8d\n",
                            i + 1, r.getRecipeName(), r.getBaseSuccessRate(), r.getAlchemyLevelRequired());
                }
            } else {
                System.out.println("\n暂无可用的丹方！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 开始炼丹
     */
    private static void startAlchemy() throws IOException, InterruptedException {
        System.out.println("\n--- 开始炼丹 ---");
        System.out.print("请输入丹方ID: ");
        String recipeIdStr = scanner.nextLine();

        try {
            Long recipeId = Long.parseLong(recipeIdStr);

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("recipeId", recipeId);

            String response = ApiClient.post("/alchemy/start", request);
            AlchemyResponse result = ApiClient.parseResponse(response, AlchemyResponse.class);

            if (result != null) {
                if (result.isSuccess()) {
                    System.out.println("\n✅ 炼丹成功！");
                    System.out.println("获得丹药: " + result.getPillName());
                    System.out.println("品质: " + result.getResultQuality());
                    System.out.println("数量: " + result.getQuantity());
                    System.out.println("获得经验: " + result.getExpGained());
                } else {
                    System.out.println("\n❌ 炼丹失败！");
                    System.out.println("获得经验: " + result.getExpGained());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的丹方ID！");
        }

        pressEnterToContinue();
    }

    /**
     * 查看炼丹记录
     */
    private static void showAlchemyRecords() throws IOException, InterruptedException {
        System.out.println("\n--- 炼丹记录 ---");
        String response = ApiClient.get("/alchemy/records/" + currentCharacterId);
        System.out.println("\n" + response);
        pressEnterToContinue();
    }

    /**
     * 查看我的材料
     */
    private static void showMyMaterials() throws IOException, InterruptedException {
        System.out.println("\n--- 我的材料 ---");

        String response = ApiClient.get("/alchemy/materials/" + currentCharacterId);
        Type listType = new TypeToken<List<MaterialResponse>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<MaterialResponse> materials = gson.fromJson(array, listType);

            if (materials != null && !materials.isEmpty()) {
                System.out.println("\n序号  材料名称              阶位  数量");
                System.out.println("───────────────────────────────────────");
                for (int i = 0; i < materials.size(); i++) {
                    MaterialResponse m = materials.get(i);
                    System.out.printf("%-4d  %-20s  %-4d  %-4d\n",
                            i + 1, m.getMaterialName(), m.getMaterialTier(), m.getQuantity());
                }
            } else {
                System.out.println("\n暂无材料！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 锻造菜单
     */
    private static void showForgeMenu() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              锻 造 系 统              │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看装备配方                     │");
            System.out.println("│  2. 开始锻造                         │");
            System.out.println("│  3. 查看锻造记录                     │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": showEquipmentRecipes(); break;
                case "2": startForge(); break;
                case "3": showForgeRecords(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 显示装备配方列表
     */
    private static void showEquipmentRecipes() throws IOException, InterruptedException {
        System.out.println("\n--- 装备配方列表 ---");

        String response = ApiClient.get("/forge/recipes/" + currentCharacterId);
        Type listType = new TypeToken<List<EquipmentRecipeResponse>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<EquipmentRecipeResponse> recipes = gson.fromJson(array, listType);

            if (recipes != null && !recipes.isEmpty()) {
                System.out.println("\n序号  配方名称              成功率  需要锻造等级");
                System.out.println("─────────────────────────────────────────────");
                for (int i = 0; i < recipes.size(); i++) {
                    EquipmentRecipeResponse r = recipes.get(i);
                    System.out.printf("%-4d  %-20s  %-6d  %-8d\n",
                            i + 1, r.getRecipeName(), r.getBaseSuccessRate(), r.getForgingLevelRequired());
                }
            } else {
                System.out.println("\n暂无可用的装备配方！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 开始锻造
     */
    private static void startForge() throws IOException, InterruptedException {
        System.out.println("\n--- 开始锻造 ---");
        System.out.print("请输入配方ID: ");
        String recipeIdStr = scanner.nextLine();

        try {
            Long recipeId = Long.parseLong(recipeIdStr);

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("recipeId", recipeId);

            String response = ApiClient.post("/forge/start", request);
            ForgeResponse result = ApiClient.parseResponse(response, ForgeResponse.class);

            if (result != null) {
                if (result.isSuccess()) {
                    System.out.println("\n✅ 锻造成功！");
                    System.out.println("获得装备: " + result.getEquipmentName());
                    System.out.println("品质: " + result.getResultQuality());
                    System.out.println("获得经验: " + result.getExpGained());
                } else {
                    System.out.println("\n❌ 锻造失败！");
                    System.out.println("获得经验: " + result.getExpGained());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的配方ID！");
        }

        pressEnterToContinue();
    }

    /**
     * 查看锻造记录
     */
    private static void showForgeRecords() throws IOException, InterruptedException {
        System.out.println("\n--- 锻造记录 ---");
        String response = ApiClient.get("/forge/records/" + currentCharacterId);
        System.out.println("\n" + response);
        pressEnterToContinue();
    }

    /**
     * 技能菜单
     */
    private static void showSkillMenu() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              技 能 系 统              │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看可学技能                     │");
            System.out.println("│  2. 查看已学技能                     │");
            System.out.println("│  3. 学习技能                         │");
            System.out.println("│  4. 装备技能                         │");
            System.out.println("│  5. 升级技能                         │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": showAvailableSkills(); break;
                case "2": showLearnedSkills(); break;
                case "3": learnSkill(); break;
                case "4": equipSkill(); break;
                case "5": upgradeSkill(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 显示可学技能
     */
    private static void showAvailableSkills() throws IOException, InterruptedException {
        System.out.println("\n--- 可学习技能 ---");

        String response = ApiClient.get("/skill/available/" + currentCharacterId);
        Type listType = new TypeToken<List<SkillResponse>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<SkillResponse> skills = gson.fromJson(array, listType);

            if (skills != null && !skills.isEmpty()) {
                System.out.println("\n序号  技能名称              类型  元素  阶位");
                System.out.println("───────────────────────────────────────────");
                for (int i = 0; i < skills.size(); i++) {
                    SkillResponse s = skills.get(i);
                    System.out.printf("%-4d  %-20s  %-4s  %-4s  %-4d\n",
                            i + 1, s.getSkillName(), s.getFunctionType(),
                            s.getElementType(), s.getTier());
                }
            } else {
                System.out.println("\n暂无可学习的技能！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 显示已学技能
     */
    private static void showLearnedSkills() throws IOException, InterruptedException {
        System.out.println("\n--- 已学技能 ---");

        String response = ApiClient.get("/skill/learned/" + currentCharacterId);
        Type listType = new TypeToken<List<SkillResponse>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<SkillResponse> skills = gson.fromJson(array, listType);

            if (skills != null && !skills.isEmpty()) {
                System.out.println("\n序号  技能名称              等级  熟练度  装备");
                System.out.println("───────────────────────────────────────────────");
                for (int i = 0; i < skills.size(); i++) {
                    SkillResponse s = skills.get(i);
                    String equipped = s.isEquipped() ? "是" : "否";
                    System.out.printf("%-4d  %-20s  %-4d  %-6d  %-4s\n",
                            i + 1, s.getSkillName(), s.getSkillLevel(),
                            s.getProficiency(), equipped);
                }
            } else {
                System.out.println("\n暂未学习任何技能！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 学习技能
     */
    private static void learnSkill() throws IOException, InterruptedException {
        System.out.println("\n--- 学习技能 ---");
        System.out.print("请输入技能ID: ");
        String skillIdStr = scanner.nextLine();

        try {
            Long skillId = Long.parseLong(skillIdStr);

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("skillId", skillId);

            String response = ApiClient.post("/skill/learn", request);
            SkillResponse result = ApiClient.parseResponse(response, SkillResponse.class);

            if (result != null) {
                System.out.println("\n✅ 学习成功！");
                System.out.println("技能: " + result.getSkillName());
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的技能ID！");
        }

        pressEnterToContinue();
    }

    /**
     * 装备技能
     */
    private static void equipSkill() throws IOException, InterruptedException {
        System.out.println("\n--- 装备技能 ---");
        System.out.print("请输入角色技能ID: ");
        String charSkillIdStr = scanner.nextLine();
        System.out.print("请输入槽位索引 (0-7): ");
        String slotStr = scanner.nextLine();

        try {
            Long charSkillId = Long.parseLong(charSkillIdStr);
            Integer slot = Integer.parseInt(slotStr);

            if (slot < 0 || slot > 7) {
                System.out.println("\n❌ 槽位索引必须在0-7之间！");
                pressEnterToContinue();
                return;
            }

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("characterSkillId", charSkillId);
            request.addProperty("slotIndex", slot);

            String response = ApiClient.post("/skill/equip", request);
            SkillResponse result = ApiClient.parseResponse(response, SkillResponse.class);

            if (result != null) {
                System.out.println("\n✅ 装备成功！");
                System.out.println("技能: " + result.getSkillName() + " 已装备到槽位 " + slot);
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的输入！");
        }

        pressEnterToContinue();
    }

    /**
     * 升级技能
     */
    private static void upgradeSkill() throws IOException, InterruptedException {
        System.out.println("\n--- 升级技能 ---");
        System.out.print("请输入角色技能ID: ");
        String charSkillIdStr = scanner.nextLine();

        try {
            Long charSkillId = Long.parseLong(charSkillIdStr);

            String response = ApiClient.post("/skill/upgrade?characterId=" +
                    currentCharacterId + "&characterSkillId=" + charSkillId, "");
            SkillResponse result = ApiClient.parseResponse(response, SkillResponse.class);

            if (result != null) {
                System.out.println("\n✅ 升级成功！");
                System.out.println("技能: " + result.getSkillName());
                System.out.println("新等级: " + result.getSkillLevel());
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的角色技能ID！");
        }

        pressEnterToContinue();
    }

    /**
     * 探索菜单
     */
    private static void showExplorationMenu() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              探 索 系 统              │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看探索区域                     │");
            System.out.println("│  2. 开始探索                         │");
            System.out.println("│  3. 查看探索记录                     │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": showExplorationAreas(); break;
                case "2": startExploration(); break;
                case "3": showExplorationRecords(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 显示探索区域
     */
    private static void showExplorationAreas() throws IOException, InterruptedException {
        System.out.println("\n--- 探索区域 ---");

        String response = ApiClient.get("/exploration/areas/" + currentCharacterId);
        Type listType = new TypeToken<List<ExplorationAreaResponse>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<ExplorationAreaResponse> areas = gson.fromJson(array, listType);

            if (areas != null && !areas.isEmpty()) {
                System.out.println("\n序号  区域名称              最小境界  最大境界");
                System.out.println("─────────────────────────────────────────────────");
                for (int i = 0; i < areas.size(); i++) {
                    ExplorationAreaResponse a = areas.get(i);
                    System.out.printf("%-4d  %-20s  %-8d  %-8d\n",
                            i + 1, a.getAreaName(), a.getMinRealmLevel(), a.getMaxRealmLevel());
                }
            } else {
                System.out.println("\n暂无可探索的区域！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 开始探索
     */
    private static void startExploration() throws IOException, InterruptedException {
        System.out.println("\n--- 开始探索 ---");
        System.out.print("请输入区域ID: ");
        String areaIdStr = scanner.nextLine();

        try {
            Long areaId = Long.parseLong(areaIdStr);

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("areaId", areaId);

            String response = ApiClient.post("/exploration/start", request);
            ExplorationResponse result = ApiClient.parseResponse(response, ExplorationResponse.class);

            if (result != null) {
                System.out.println("\n探索区域: " + result.getAreaName());
                System.out.println("事件类型: " + result.getEventType());
                System.out.println("事件描述: " + result.getEventDescription());

                if (result.getExpGained() > 0) {
                    System.out.println("获得经验: " + result.getExpGained());
                }
                if (result.getSpiritualPowerGained() > 0) {
                    System.out.println("获得灵力: " + result.getSpiritualPowerGained());
                }
                if (result.getItemFound() != null) {
                    System.out.println("获得物品: " + result.getItemFound());
                }
                if (result.getHealthLost() > 0) {
                    System.out.println("损失生命: " + result.getHealthLost());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的区域ID！");
        }

        pressEnterToContinue();
    }

    /**
     * 查看探索记录
     */
    private static void showExplorationRecords() throws IOException, InterruptedException {
        System.out.println("\n--- 探索记录 ---");
        String response = ApiClient.get("/exploration/records/" + currentCharacterId);
        System.out.println("\n" + response);
        pressEnterToContinue();
    }

    /**
     * 宗门菜单
     */
    private static void showSectMenu() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              宗 门 系 统              │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看宗门列表                     │");
            System.out.println("│  2. 查看我的宗门                     │");
            System.out.println("│  3. 加入宗门                         │");
            System.out.println("│  4. 查看宗门商店                     │");
            System.out.println("│  5. 购买物品                         │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": showSectList(); break;
                case "2": showMySect(); break;
                case "3": joinSect(); break;
                case "4": showSectShop(); break;
                case "5": buyFromSectShop(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 显示宗门列表
     */
    private static void showSectList() throws IOException, InterruptedException {
        System.out.println("\n--- 宗门列表 ---");

        String response = ApiClient.get("/sect/list?characterId=" + currentCharacterId);
        Type listType = new TypeToken<List<SectResponse>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<SectResponse> sects = gson.fromJson(array, listType);

            if (sects != null && !sects.isEmpty()) {
                System.out.println("\n序号  宗门名称              成员数  状态");
                System.out.println("───────────────────────────────────────────");
                for (int i = 0; i < sects.size(); i++) {
                    SectResponse s = sects.get(i);
                    // 处理null值
                    String memberDisplay = (s.getMemberCount() == null) ? "?" : String.valueOf(s.getMemberCount());
                    String joinedDisplay = (s.getIsJoined() != null && s.getIsJoined()) ? "[已加入]" : "";
                    System.out.printf("%-4d  %-20s  %-4s  %-4s\n",
                            i + 1, s.getSectName(), memberDisplay, joinedDisplay);
                }
            } else {
                System.out.println("\n暂无宗门！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 查看我的宗门
     */
    private static void showMySect() throws IOException, InterruptedException {
        System.out.println("\n--- 我的宗门 ---");

        String response = ApiClient.get("/sect/my/" + currentCharacterId);
        System.out.println("\n" + response);

        pressEnterToContinue();
    }

    /**
     * 加入宗门
     */
    private static void joinSect() throws IOException, InterruptedException {
        System.out.println("\n--- 加入宗门 ---");
        System.out.print("请输入宗门ID: ");
        String sectIdStr = scanner.nextLine();

        try {
            Long sectId = Long.parseLong(sectIdStr);

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("sectId", sectId);

            String response = ApiClient.post("/sect/join", request);
            System.out.println("\n" + response);
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的宗门ID！");
        }

        pressEnterToContinue();
    }

    /**
     * 查看宗门商店
     */
    private static void showSectShop() throws IOException, InterruptedException {
        System.out.println("\n--- 宗门商店 ---");

        String response = ApiClient.get("/sect/shop/" + currentCharacterId);
        Type listType = new TypeToken<List<SectShopItemResponse>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<SectShopItemResponse> items = gson.fromJson(array, listType);

            if (items != null && !items.isEmpty()) {
                System.out.println("\n序号  物品名称              类型  价格  库存");
                System.out.println("───────────────────────────────────────────────");
                for (int i = 0; i < items.size(); i++) {
                    SectShopItemResponse item = items.get(i);
                    // 库存为null时显示为0
                    String stockDisplay = (item.getStock() == null) ? "0" : String.valueOf(item.getStock());
                    System.out.printf("%-4d  %-20s  %-4s  %-6d  %-4s\n",
                            i + 1, item.getItemName(), item.getItemType(),
                            item.getPrice(), stockDisplay);
                }
            } else {
                System.out.println("\n商店暂无物品！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 购买物品
     */
    private static void buyFromSectShop() throws IOException, InterruptedException {
        System.out.println("\n--- 购买物品 ---");
        System.out.print("请输入商店物品ID: ");
        String shopItemIdStr = scanner.nextLine();
        System.out.print("请输入购买数量: ");
        String quantityStr = scanner.nextLine();

        try {
            Long shopItemId = Long.parseLong(shopItemIdStr);
            Integer quantity = Integer.parseInt(quantityStr);

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("itemId", shopItemId);  // 修改为itemId匹配后端DTO
            request.addProperty("quantity", quantity);

            String response = ApiClient.post("/sect/shop/buy", request);
            System.out.println("\n" + response);
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的输入！");
        }

        pressEnterToContinue();
    }

    /**
     * 装备菜单
     */
    private static void showEquipmentMenu() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              装 备 系 统              │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看已装备物品                   │");
            System.out.println("│  2. 装备物品                         │");
            System.out.println("│  3. 卸下装备                         │");
            System.out.println("│  4. 查看装备加成                     │");
            System.out.println("│  5. 🎒 查看背包                       │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": showEquippedItems(); break;
                case "2": equipItem(); break;
                case "3": unequipItem(); break;
                case "4": showEquipmentBonus(); break;
                case "5": showInventory(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 显示已装备物品
     */
    private static void showEquippedItems() throws IOException, InterruptedException {
        System.out.println("\n--- 已装备物品 ---");

        String response = ApiClient.get("/equipment/character/" + currentCharacterId);
        Type listType = new TypeToken<List<EquipmentInfo>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<EquipmentInfo> equipments = gson.fromJson(array, listType);

            if (equipments != null && !equipments.isEmpty()) {
                System.out.println("\n槽位  装备名称              品质  攻击  防御");
                System.out.println("───────────────────────────────────────────────");
                for (EquipmentInfo e : equipments) {
                    if (e.isEquipped()) {
                        System.out.printf("%-4s  %-20s  %-4s  %-4d  %-4d\n",
                                e.getEquipmentSlot(), e.getEquipmentName(),
                                e.getQuality(), e.getAttack(), e.getDefense());
                    }
                }
            } else {
                System.out.println("\n暂未装备任何物品！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 装备物品
     */
    private static void equipItem() throws IOException, InterruptedException {
        System.out.println("\n--- 装备物品 ---");
        System.out.print("请输入角色装备ID: ");
        String charEquipIdStr = scanner.nextLine();

        try {
            Long charEquipId = Long.parseLong(charEquipIdStr);

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("characterEquipmentId", charEquipId);

            String response = ApiClient.post("/equipment/equip", request);
            EquipmentInfo result = ApiClient.parseResponse(response, EquipmentInfo.class);

            if (result != null) {
                System.out.println("\n✅ 装备成功！");
                System.out.println("装备: " + result.getEquipmentName());
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的装备ID！");
        }

        pressEnterToContinue();
    }

    /**
     * 卸下装备
     */
    private static void unequipItem() throws IOException, InterruptedException {
        System.out.println("\n--- 卸下装备 ---");
        System.out.println("可选槽位: WEAPON, ARMOR, HELMET, BOOTS, ACCESSORY");
        System.out.print("\n请输入槽位名称: ");
        String slot = scanner.nextLine();

        // 这里需要用DELETE请求，但Java HttpClient的DELETE不支持body，需要手动构建
        System.out.println("\n功能已调用，槽位: " + slot);
        pressEnterToContinue();
    }

    /**
     * 查看装备加成
     */
    private static void showEquipmentBonus() throws IOException, InterruptedException {
        System.out.println("\n--- 装备加成 ---");

        String response = ApiClient.get("/equipment/bonus/" + currentCharacterId);
        System.out.println("\n" + response);

        pressEnterToContinue();
    }

    /**
     * 分配属性点
     */
    private static void allocatePoints() throws IOException, InterruptedException {
        System.out.println("\n--- 属性加点 ---");

        // 刷新角色信息
        refreshCharacter();

        if (currentCharacter == null) {
            System.out.println("\n❌ 角色信息加载失败！");
            pressEnterToContinue();
            return;
        }

        // 检查是否有可用点数
        Integer availablePoints = currentCharacter.getAvailablePoints();
        if (availablePoints == null || availablePoints <= 0) {
            System.out.println("\n当前没有可分配的属性点！");
            System.out.println("提升等级可以获得属性点。");
            pressEnterToContinue();
            return;
        }

        // 显示当前属性
        System.out.println("\n当前属性:");
        System.out.println("┌────────────────────────────────────────────┐");
        System.out.printf("│ 体质: %3d  │  精神: %3d  │  悟性: %3d   │\n",
                currentCharacter.getConstitution(),
                currentCharacter.getSpirit(),
                currentCharacter.getComprehension());
        System.out.printf("│ 机缘: %3d  │  气运: %3d  │              │\n",
                currentCharacter.getLuck(),
                currentCharacter.getFortune());
        System.out.println("└────────────────────────────────────────────┘");
        System.out.println("\n可用属性点: " + availablePoints);
        System.out.println("提示: 属性上限为999");

        int constitutionPoints = 0, spiritPoints = 0, comprehensionPoints = 0, luckPoints = 0, fortunePoints = 0;

        // 读取加点输入
        while (true) {
            System.out.print("\n体质加点 (输入0跳过): ");
            try {
                constitutionPoints = Integer.parseInt(scanner.nextLine().trim());
                if (constitutionPoints < 0) {
                    System.out.println("不能输入负数！");
                    continue;
                }
                if (currentCharacter.getConstitution() + constitutionPoints > 999) {
                    System.out.println("体质属性不能超过999！当前值:" + currentCharacter.getConstitution());
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        while (true) {
            System.out.print("精神加点 (输入0跳过): ");
            try {
                spiritPoints = Integer.parseInt(scanner.nextLine().trim());
                if (spiritPoints < 0) {
                    System.out.println("不能输入负数！");
                    continue;
                }
                if (currentCharacter.getSpirit() + spiritPoints > 999) {
                    System.out.println("精神属性不能超过999！当前值:" + currentCharacter.getSpirit());
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        while (true) {
            System.out.print("悟性加点 (输入0跳过): ");
            try {
                comprehensionPoints = Integer.parseInt(scanner.nextLine().trim());
                if (comprehensionPoints < 0) {
                    System.out.println("不能输入负数！");
                    continue;
                }
                if (currentCharacter.getComprehension() + comprehensionPoints > 999) {
                    System.out.println("悟性属性不能超过999！当前值:" + currentCharacter.getComprehension());
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        while (true) {
            System.out.print("机缘加点 (输入0跳过): ");
            try {
                luckPoints = Integer.parseInt(scanner.nextLine().trim());
                if (luckPoints < 0) {
                    System.out.println("不能输入负数！");
                    continue;
                }
                if (currentCharacter.getLuck() + luckPoints > 999) {
                    System.out.println("机缘属性不能超过999！当前值:" + currentCharacter.getLuck());
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        while (true) {
            System.out.print("气运加点 (输入0跳过): ");
            try {
                fortunePoints = Integer.parseInt(scanner.nextLine().trim());
                if (fortunePoints < 0) {
                    System.out.println("不能输入负数！");
                    continue;
                }
                if (currentCharacter.getFortune() + fortunePoints > 999) {
                    System.out.println("气运属性不能超过999！当前值:" + currentCharacter.getFortune());
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }

        // 计算总加点数
        int totalPoints = constitutionPoints + spiritPoints + comprehensionPoints + luckPoints + fortunePoints;

        if (totalPoints == 0) {
            System.out.println("\n未分配任何点数。");
            pressEnterToContinue();
            return;
        }

        if (totalPoints > availablePoints) {
            System.out.println("\n❌ 分配的点数超过可用点数！");
            System.out.println("可用: " + availablePoints + ", 请求: " + totalPoints);
            pressEnterToContinue();
            return;
        }

        // 构建请求JSON
        JsonObject request = new JsonObject();
        request.addProperty("characterId", currentCharacterId);
        request.addProperty("constitutionPoints", constitutionPoints);
        request.addProperty("spiritPoints", spiritPoints);
        request.addProperty("comprehensionPoints", comprehensionPoints);
        request.addProperty("luckPoints", luckPoints);
        request.addProperty("fortunePoints", fortunePoints);

        // 发送请求
        String response = ApiClient.post("/characters/allocate-points", request);
        AllocatePointsResponse result = ApiClient.parseResponse(response, AllocatePointsResponse.class);

        if (result != null) {
            System.out.println("\n✅ " + result.getMessage());
            System.out.println("\n分配后属性:");
            System.out.println("┌────────────────────────────────────────────┐");
            System.out.printf("│ 体质: %3d  │  精神: %3d  │  悟性: %3d   │\n",
                    result.getNewConstitution(),
                    result.getNewSpirit(),
                    result.getNewComprehension());
            System.out.printf("│ 机缘: %3d  │  气运: %3d  │              │\n",
                    result.getNewLuck(),
                    result.getNewFortune());
            System.out.println("└────────────────────────────────────────────┘");
            System.out.println("\n剩余可用点数: " + result.getRemainingPoints());

            // 显示衍生属性的变化
            if (result.getNewAttack() != null || result.getNewDefense() != null) {
                System.out.println("\n衍生属性:");
                System.out.println("┌─────────────────────────────────────────────────────┐");
                if (result.getNewAttack() != null && result.getNewDefense() != null) {
                    System.out.printf("│ 攻击力: %-5d │ 防御力: %-5d                    │\n",
                            result.getNewAttack(), result.getNewDefense());
                }
                if (result.getNewHealthMax() != null) {
                    System.out.printf("│ 气血上限: %-8d │ 体力上限: %-8d          │\n",
                            result.getNewHealthMax(), result.getNewStaminaMax());
                }
                if (result.getNewSpiritualPowerMax() != null) {
                    System.out.printf("│ 灵力上限: %-8d                               │\n",
                            result.getNewSpiritualPowerMax());
                }
                if (result.getNewCritRate() != null) {
                    System.out.printf("│ 暴击率: %.1f%%     │ 暴击伤害: %.1f%%              │\n",
                            result.getNewCritRate(), result.getNewCritDamage());
                }
                if (result.getNewSpeed() != null) {
                    System.out.printf("│ 速度: %.1f                                         │\n",
                            result.getNewSpeed());
                }
                System.out.println("└─────────────────────────────────────────────────────┘");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 查看背包
     */
    private static void showInventory() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              背包系统                │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看全部物品                     │");
            System.out.println("│  2. 只看装备                         │");
            System.out.println("│  3. 只看材料                         │");
            System.out.println("│  4. 只看丹药                         │");
            System.out.println("│  5. 背包统计                         │");
            System.out.println("│  0. 返回上级菜单                     │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": showInventoryItems(null); break;
                case "2": showInventoryItems("equipment"); break;
                case "3": showInventoryItems("material"); break;
                case "4": showInventoryItems("pill"); break;
                case "5": showInventorySummary(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 显示背包物品
     */
    private static void showInventoryItems(String itemType) throws IOException, InterruptedException {
        System.out.println("\n--- 背包物品 ---");

        String url = "/inventory/character/" + currentCharacterId;
        if (itemType != null) {
            url += "?itemType=" + itemType;
        }

        String response = ApiClient.get(url);
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

        if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 200) {
            if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
                JsonArray array = jsonObject.get("data").getAsJsonArray();

                if (array.size() == 0) {
                    System.out.println("\n背包是空的！");
                } else {
                    String typeLabel = itemType == null ? "全部" :
                            itemType.equals("equipment") ? "装备" :
                            itemType.equals("material") ? "材料" : "丹药";

                    System.out.println("\n" + typeLabel + "物品 (共" + array.size() + "件):");
                    System.out.println("────────────────────────────────────────────────────────────");
                    System.out.printf("%-4s  %-20s  %-20s  %-6s\n", "ID", "物品名称", "详细信息", "数量");
                    System.out.println("────────────────────────────────────────────────────────────");

                    for (int i = 0; i < array.size(); i++) {
                        JsonObject item = array.get(i).getAsJsonObject();
                        Long id = item.has("inventoryId") ? item.get("inventoryId").getAsLong() : 0L;
                        String name = item.has("itemName") ? item.get("itemName").getAsString() : "未知";
                        String detail = item.has("itemDetail") ? item.get("itemDetail").getAsString() : "";
                        Integer quantity = item.has("quantity") ? item.get("quantity").getAsInt() : 0;

                        System.out.printf("%-4d  %-20s  %-20s  %-6d\n",
                                (i + 1), name, detail, quantity);
                    }
                    System.out.println("────────────────────────────────────────────────────────────");
                }
            } else {
                System.out.println("\n背包是空的！");
            }
        } else {
            System.out.println("\n❌ " + response);
        }

        pressEnterToContinue();
    }

    /**
     * 显示背包统计
     */
    private static void showInventorySummary() throws IOException, InterruptedException {
        System.out.println("\n--- 背包统计 ---");

        String response = ApiClient.get("/inventory/character/" + currentCharacterId + "/summary");
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

        if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 200) {
            if (jsonObject.has("data")) {
                JsonObject data = jsonObject.get("data").getAsJsonObject();
                System.out.println("\n背包统计信息:");
                System.out.println("┌─────────────────────────────────────────┐");
                System.out.printf("│ 总物品数: %-5d                           │\n",
                        data.has("totalItems") ? data.get("totalItems").getAsInt() : 0);
                System.out.printf("│ 装备数量: %-5d                           │\n",
                        data.has("equipmentCount") ? data.get("equipmentCount").getAsInt() : 0);
                System.out.printf("│ 材料数量: %-5d                           │\n",
                        data.has("materialCount") ? data.get("materialCount").getAsInt() : 0);
                System.out.printf("│ 丹药数量: %-5d                           │\n",
                        data.has("pillCount") ? data.get("pillCount").getAsInt() : 0);
                System.out.println("└─────────────────────────────────────────┘");
            } else {
                System.out.println("\n暂无统计数据！");
            }
        } else {
            System.out.println("\n❌ " + response);
        }

        pressEnterToContinue();
    }

    /**
     * 按任意键继续
     */
    private static void pressEnterToContinue() {
        System.out.print("\n按回车键继续...");
        scanner.nextLine();
    }
}
