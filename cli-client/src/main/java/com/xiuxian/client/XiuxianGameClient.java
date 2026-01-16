package com.xiuxian.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xiuxian.client.model.*;
import com.xiuxian.client.util.ApiClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 凡人修仙文字游戏命令行客户端
 */
public class XiuxianGameClient {

    private static final Scanner scanner = new Scanner(System.in, "UTF-8");
    private static Long currentCharacterId = null;
    private static CharacterResponse currentCharacter = null;
    // 使用ApiClient的Gson实例，它已配置LocalDateTime支持
    private static final Gson gson = ApiClient.getGson();

    // 会话文件路径（用户主目录下的.xiuxian_session.json）
    private static final String SESSION_FILE = System.getProperty("user.home") + File.separator + ".xiuxian_session.json";

    /**
     * 根据境界等级获取境界名称
     */
    private static String getRealmNameByLevel(Integer realmLevel) {
        if (realmLevel == null) {
            return "未知境界";
        }
        switch (realmLevel) {
            case 1: return "凡人";
            case 2: return "炼气期";
            case 3: return "筑基期";
            case 4: return "结丹期";
            case 5: return "元婴期";
            case 6: return "化神期";
            case 7: return "炼虚期";
            case 8: return "合体期";
            case 9: return "大乘期";
            case 10: return "渡劫期";
            case 11: return "仙人";
            case 12: return "金仙";
            case 13: return "大罗金仙";
            case 14: return "道祖之境";
            default: return "境界" + realmLevel;
        }
    }

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

        // 尝试加载保存的会话并自动登录
        loadSavedSession();

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
        System.out.println("│ 11. 🛡️ 装备管理                        │");
        System.out.println("│ 12. 🎒 背包管理                        │");
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
                case "12": showInventory(); break;
                case "0":
                    currentCharacterId = null;
                    currentCharacter = null;
                    // 清除保存的会话
                    clearSession();
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
            // 保存会话以便下次自动登录
            saveSession();
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
                // 保存会话以便下次自动登录
                saveSession();
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
        System.out.printf("║ 物理: %-3s 冰系: %-3s 火系: %-3s 雷系: %-3s        ║\n",
                formatValue(currentCharacter.getPhysicalResist(), "0"),
                formatValue(currentCharacter.getIceResist(), "0"),
                formatValue(currentCharacter.getFireResist(), "0"),
                formatValue(currentCharacter.getLightningResist(), "0"));
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
            System.out.print("\n请选择 (直接回车返回主菜单): ");

            String choice = readMenuChoice();

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

                // 刷新角色信息
                refreshCharacter();
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
            System.out.println("  恢复气血: " + result.getHealthRecovered());
            System.out.println("  当前气血: " + result.getCurrentHealth() + "/" + result.getMaxHealth());
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

                // 刷新角色信息
                refreshCharacter();
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
            System.out.print("\n请选择 (直接回车返回主菜单): ");

            String choice = readMenuChoice();

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
     * 获取妖兽列表（辅助方法）
     */
    private static List<Monster> getMonstersList() throws IOException, InterruptedException {
        String response = ApiClient.get("/combat/monsters?characterId=" + currentCharacterId);
        Type listType = new TypeToken<List<Monster>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            return gson.fromJson(array, listType);
        }
        return null;
    }

    /**
     * 显示妖兽列表
     */
    private static void showMonsters() throws IOException, InterruptedException {
        System.out.println("\n--- 可挑战妖兽列表 ---");

        List<Monster> monsters = getMonstersList();

        if (monsters != null && !monsters.isEmpty()) {
            System.out.println("\n┌────┬──────────────────┬──────────┬──────┬──────┬──────────┐");
            System.out.println("│ ID │ 妖兽名称          │ 境界     │ 攻击 │ 防御 │ 经验奖励 │");
            System.out.println("├────┼──────────────────┼──────────┼──────┼──────┼──────────┤");
            for (Monster m : monsters) {
                System.out.printf("│ %2d │ %-16s │ %-8s │ %4d │ %4d │ %8d │%n",
                        m.getMonsterId(), m.getMonsterName(), m.getRealmName(),
                        m.getAttack(), m.getDefense(), m.getExpReward());
            }
            System.out.println("└────┴──────────────────┴──────────┴──────┴──────┴──────────┘");
            System.out.println("\n💡 提示：输入妖兽ID开始战斗（例如：输入1挑战毒蛇）");
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

        // 先显示可挑战的妖兽列表
        List<Monster> monsters = getMonstersList();

        if (monsters == null || monsters.isEmpty()) {
            System.out.println("\n暂无可挑战的妖兽！");
            pressEnterToContinue();
            return;
        }

        // 显示妖兽列表
        System.out.println("\n可挑战妖兽列表:");
        System.out.println("┌────┬──────────────────┬──────────┬──────┬──────┬──────────┐");
        System.out.println("│ ID │ 妖兽名称          │ 境界     │ 攻击 │ 防御 │ 经验奖励 │");
        System.out.println("├────┼──────────────────┼──────────┼──────┼──────┼──────────┤");
        for (Monster m : monsters) {
            System.out.printf("│ %2d │ %-16s │ %-8s │ %4d │ %4d │ %8d │%n",
                    m.getMonsterId(), m.getMonsterName(), m.getRealmName(),
                    m.getAttack(), m.getDefense(), m.getExpReward());
        }
        System.out.println("└────┴──────────────────┴──────────┴──────┴──────┴──────────┘");

        // 提示用户输入
        System.out.print("\n请输入妖兽ID (直接回车返回): ");
        String monsterIdStr = scanner.nextLine();

        // 空输入直接返回
        if (monsterIdStr.isEmpty()) {
            return;
        }

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

        // 先显示妖兽列表
        List<Monster> monsters = getMonstersList();
        if (monsters == null || monsters.isEmpty()) {
            System.out.println("\n暂无可挑战的妖兽！");
            pressEnterToContinue();
            return;
        }

        // 显示妖兽列表
        System.out.println("\n可挑战妖兽列表:");
        System.out.println("┌────┬──────────────────┬──────────┬──────┬──────┬──────────┐");
        System.out.println("│ ID │ 妖兽名称          │ 境界     │ 攻击 │ 防御 │ 经验奖励 │");
        System.out.println("├────┼──────────────────┼──────────┼──────┼──────┼──────────┤");
        for (Monster m : monsters) {
            System.out.printf("│ %2d │ %-16s │ %-8s │ %4d │ %4d │ %8d │%n",
                    m.getMonsterId(), m.getMonsterName(), m.getRealmName(),
                    m.getAttack(), m.getDefense(), m.getExpReward());
        }
        System.out.println("└────┴──────────────────┴──────────┴──────┴──────┴──────────┘");
        System.out.println("\n💡 提示：挂机将自动重复战斗，直到体力耗尽或战斗失败");

        System.out.print("\n请输入妖兽ID: ");
        String monsterIdStr = scanner.nextLine();

        try {
            Long monsterId = Long.parseLong(monsterIdStr);

            // 获取挂机配置
            int maxBattles = 30; // 默认值
            try {
                String configResponse = ApiClient.get("/combat/idle-config");
                JsonObject configJson = gson.fromJson(configResponse, JsonObject.class);
                if (configJson.has("code") && configJson.get("code").getAsInt() == 200) {
                    if (configJson.has("data")) {
                        JsonObject configData = configJson.getAsJsonObject("data");
                        if (configData.has("maxBattles")) {
                            maxBattles = configData.get("maxBattles").getAsInt();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("获取挂机配置失败，使用默认值30轮");
            }

            // 统计信息
            int totalBattles = 0;
            int victories = 0;
            int defeats = 0;
            int totalExpGained = 0;
            int totalSpiritStonesGained = 0;
            List<String> allItemsDropped = new ArrayList<>();

            System.out.println("\n🤖 挂机开始！战斗中...");
            System.out.printf("提示：挂机将持续到体力耗尽、战斗失败或达到最大战斗轮数(%d轮)\n", maxBattles);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

            boolean continueAuto = true;
            while (continueAuto && totalBattles < maxBattles) {
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

                            // 构建基本战斗信息
                            StringBuilder battleInfo = new StringBuilder();
                            battleInfo.append(String.format("第%d战 ✅ 胜利！经验+%d 灵石+%d",
                                    totalBattles,
                                    result.getExpGained(),
                                    result.getSpiritStonesGained()));

                            // 显示装备掉落
                            if (result.getItemsDropped() != null && !result.getItemsDropped().isEmpty()) {
                                allItemsDropped.addAll(result.getItemsDropped());
                                battleInfo.append(" 📦掉落: ");
                                battleInfo.append(String.join(", ", result.getItemsDropped()));
                            }

                            battleInfo.append(String.format(" | 体力:%d 气血:%d 灵力:%d",
                                    result.getCharacterStaminaRemaining(),
                                    result.getCharacterHpRemaining(),
                                    result.getCharacterSpiritualPowerRemaining()));

                            System.out.println(battleInfo.toString());

                            // 检查是否达到最大战斗轮数
                            if (totalBattles >= maxBattles) {
                                System.out.printf("\n⏰ 已达到最大战斗轮数(%d轮)，挂机结束\n", maxBattles);
                                continueAuto = false;
                            }
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
            System.out.printf("│ 掉落装备：  %-4d件                  │\n", allItemsDropped.size());
            System.out.println("└─────────────────────────────────────┘");

            if (victories > 0) {
                double winRate = (double) victories / totalBattles * 100;
                System.out.printf("胜率：%.1f%%\n", winRate);
            }

            // 显示所有装备掉落详情
            if (!allItemsDropped.isEmpty()) {
                System.out.println("\n📦 装备掉落详情：");
                System.out.println("┌─────────────────────────────────────┐");
                for (int i = 0; i < allItemsDropped.size(); i++) {
                    System.out.printf("│ %2d. %-31s │\n", i + 1, allItemsDropped.get(i));
                }
                System.out.println("└─────────────────────────────────────┘");
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

        int currentPage = 1;
        int totalPages = 1;

        while (true) {
            String response = ApiClient.get("/combat/records?characterId=" + currentCharacterId + "&page=" + currentPage);
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

            if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 200) {
                if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
                    JsonObject data = jsonObject.get("data").getAsJsonObject();

                    // 获取总数和总页数
                    long total = data.has("total") ? data.get("total").getAsLong() : 0;
                    int pageSize = data.has("pageSize") ? data.get("pageSize").getAsInt() : 20;
                    totalPages = (int) Math.ceil((double) total / pageSize);
                    if (totalPages == 0) totalPages = 1;

                    // 获取当前页
                    int current = data.has("page") ? data.get("page").getAsInt() : 1;

                    if (data.has("items") && data.get("items").isJsonArray()) {
                        JsonArray items = data.get("items").getAsJsonArray();

                        // 清屏并显示标题
                        for (int i = 0; i < 50; i++) System.out.println();

                        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
                        System.out.println("║                         战 斗 记 录                                 ║");
                        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");

                        if (items.size() == 0) {
                            System.out.println("\n暂无战斗记录！");
                            pressEnterToContinue();
                            return;
                        }

                        System.out.println("\n┌──────┬──────────┬──────────┬──────┬────────┬────────┬──────┬────────┐");
                        System.out.println("│ ID   │ 妖兽ID   │ 战斗模式 │ 结果 │ 回合数 │ 造成伤害│ 经验 │ 时间   │");
                        System.out.println("├──────┼──────────┼──────────┼──────┼────────┼────────┼──────┼────────┤");

                        for (int i = 0; i < items.size(); i++) {
                            JsonObject record = items.get(i).getAsJsonObject();
                            int combatId = record.has("combatId") ? record.get("combatId").getAsInt() : 0;
                            int monsterId = record.has("monsterId") ? record.get("monsterId").getAsInt() : 0;
                            String mode = record.has("combatMode") ? record.get("combatMode").getAsString() : "未知";
                            boolean isVictory = record.has("isVictory") && record.get("isVictory").getAsInt() == 1;
                            int turns = record.has("turns") ? record.get("turns").getAsInt() : 0;
                            int damage = record.has("damageDealt") ? record.get("damageDealt").getAsInt() : 0;
                            int exp = record.has("expGained") ? record.get("expGained").getAsInt() : 0;
                            String time = record.has("combatTime") ? record.get("combatTime").getAsString() : "";

                            // 格式化时间（只显示日期时间部分）
                            if (time.length() > 16) {
                                time = time.substring(0, 16);
                            }

                            System.out.printf("│ %4d │ %8d │ %-8s │ %4s │ %6d │ %6d │ %4d │ %s │%n",
                                    combatId, monsterId, mode, isVictory ? "胜利" : "失败",
                                    turns, damage, exp, time);
                        }

                        System.out.println("└──────┴──────────┴──────────┴──────┴────────┴────────┴──────┴────────┘");

                        // 显示分页信息
                        System.out.println("\n┌────────────────────────────────────────────────────────────────────┐");
                        System.out.printf("│  第 %d 页 / 共 %d 页    总记录数: %d                              │%n",
                                current, totalPages, total);
                        System.out.println("├────────────────────────────────────────────────────────────────────┤");
                        System.out.println("│ 操作: a/A上一页 | d/D下一页 | 回车退出                          │");
                        System.out.println("└────────────────────────────────────────────────────────────────────┘");
                        System.out.print("\n> ");
                    }
                }
            } else {
                System.out.println("\n❌ 查询战斗记录失败！");
                pressEnterToContinue();
                return;
            }

            // 读取用户输入
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                // 回车键退出
                return;
            } else if (input.equals("a") || input.equals("A")) {
                // 左方向键或A键 - 上一页
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("\n已经是第一页了！");
                    Thread.sleep(500);
                }
            } else if (input.equals("d") || input.equals("D")) {
                // 右方向键或D键 - 下一页
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("\n已经是最后一页了！");
                    Thread.sleep(500);
                }
            }
        }
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
            System.out.print("\n请选择 (直接回车返回主菜单): ");

            String choice = readMenuChoice();

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
                System.out.println("\n序号  丹方名称              成功率  需要炼丹等级  需要材料");
                System.out.println("─────────────────────────────────────────────────────────");
                for (int i = 0; i < recipes.size(); i++) {
                    PillRecipeResponse r = recipes.get(i);

                    // 构建材料列表字符串
                    StringBuilder materialsStr = new StringBuilder();
                    if (r.getMaterials() != null && !r.getMaterials().isEmpty()) {
                        for (int j = 0; j < r.getMaterials().size(); j++) {
                            PillRecipeResponse.MaterialRequirement m = r.getMaterials().get(j);
                            materialsStr.append(m.getMaterialName())
                                    .append("×")
                                    .append(m.getRequiredQuantity());
                            if (j < r.getMaterials().size() - 1) {
                                materialsStr.append(", ");
                            }
                        }
                    } else {
                        materialsStr.append("无");
                    }

                    System.out.printf("%-4d  %-20s  %-6d  %-12d  %s\n",
                            i + 1, r.getRecipeName(), r.getBaseSuccessRate(), r.getAlchemyLevelRequired(), materialsStr.toString());
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
            System.out.print("\n请选择 (直接回车返回主菜单): ");

            String choice = readMenuChoice();

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
            System.out.println("│  6. 查看所有技能                     │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择 (直接回车返回主菜单): ");

            String choice = readMenuChoice();

            switch (choice) {
                case "1": showAvailableSkills(); break;
                case "2": showLearnedSkills(); break;
                case "3": learnSkill(); break;
                case "4": equipSkill(); break;
                case "5": upgradeSkill(); break;
                case "6": showAllSkills(); break;
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
     * 显示所有技能
     */
    private static void showAllSkills() throws IOException, InterruptedException {
        System.out.println("\n--- 所有技能列表 ---");

        String response = ApiClient.get("/skill/all");
        Type listType = new TypeToken<List<SkillResponse>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
            JsonArray array = jsonObject.get("data").getAsJsonArray();
            List<SkillResponse> skills = gson.fromJson(array, listType);

            if (skills != null && !skills.isEmpty()) {
                System.out.println("\n序号  技能名称              类型      元素  阶位  基础伤害  消耗灵力  描述");
                System.out.println("──────────────────────────────────────────────────────────────────────────────────────");
                for (int i = 0; i < skills.size(); i++) {
                    SkillResponse s = skills.get(i);
                    String desc = s.getDescription() != null && s.getDescription().length() > 20
                        ? s.getDescription().substring(0, 20) + "..."
                        : (s.getDescription() != null ? s.getDescription() : "");
                    System.out.printf("%-4d  %-20s  %-8s  %-4s  %-4d  %-8d  %-8d  %s\n",
                            i + 1, s.getSkillName(), s.getFunctionType(),
                            s.getElementType(), s.getTier(),
                            s.getBaseDamage() != null ? s.getBaseDamage() : 0,
                            s.getSpiritualCost() != null ? s.getSpiritualCost() : 0,
                            desc);
                }
            } else {
                System.out.println("\n暂无技能数据！");
            }
        }

        pressEnterToContinue();
    }

    /**
     * 学习技能
     */
    private static void learnSkill() throws IOException, InterruptedException {
        System.out.println("\n--- 学习技能 ---");

        // 1. 显示已学习的技能（避免重复学习）
        System.out.println("\n📚 已学习的技能:");
        String learnedResponse = ApiClient.get("/skill/learned/" + currentCharacterId);
        JsonObject learnedJson = gson.fromJson(learnedResponse, JsonObject.class);

        java.util.Set<Long> learnedSkillIds = new java.util.HashSet<>();

        if (learnedJson.has("code") && learnedJson.get("code").getAsInt() == 200) {
            if (learnedJson.has("data") && learnedJson.get("data").isJsonArray()) {
                JsonArray array = learnedJson.get("data").getAsJsonArray();
                if (array.size() > 0) {
                    System.out.println("┌──────┬──────────────────┬──────────┬──────────┐");
                    System.out.println("│ 技能ID │ 技能名称        │ 等级     │ 装备     │");
                    System.out.println("├──────┼──────────────────┼──────────┼──────────┤");
                    for (JsonElement element : array) {
                        JsonObject skillObj = element.getAsJsonObject();
                        Long id = skillObj.has("skillId") ? skillObj.get("skillId").getAsLong() : 0L;
                        String name = skillObj.has("skillName") ? skillObj.get("skillName").getAsString() : "未知";
                        int level = skillObj.has("skillLevel") ? skillObj.get("skillLevel").getAsInt() : 1;
                        boolean equipped = skillObj.has("isEquipped") && skillObj.get("isEquipped").getAsBoolean();

                        learnedSkillIds.add(id);
                        System.out.printf("│ %-6d │ %-16s │ %-8d │ %-8s │\n",
                                id, name, level, equipped ? "✓" : "");
                    }
                    System.out.println("└──────┴──────────────────┴──────────┴──────────┘");
                    System.out.println("\n⚠️  以上技能已学习，请勿重复学习！");
                } else {
                    System.out.println("  暂未学习任何技能");
                }
            }
        }

        // 2. 显示背包中的技能物品
        System.out.println("\n📦 背包中的技能物品:");
        System.out.println("正在加载技能物品...");
        String inventoryResponse = ApiClient.get("/inventory/character/" + currentCharacterId + "?itemType=skill");

        JsonObject inventoryJson = gson.fromJson(inventoryResponse, JsonObject.class);
        java.util.List<JsonObject> skillItems = new java.util.ArrayList<>();

        if (inventoryJson.has("code") && inventoryJson.get("code").getAsInt() == 200) {
            if (inventoryJson.has("data") && inventoryJson.get("data").isJsonArray()) {
                JsonArray array = inventoryJson.get("data").getAsJsonArray();
                for (JsonElement element : array) {
                    skillItems.add(element.getAsJsonObject());
                }
            }
        }

        if (!skillItems.isEmpty()) {
            System.out.println("\n┌──────┬──────────────────┬─────────────────────────────┬──────────┐");
            System.out.println("│ 序号 │ 技能名称         │ 类型 | 元素 | 属性          │ 数量     │");
            System.out.println("├──────┼──────────────────┼─────────────────────────────┼──────────┤");

            for (int i = 0; i < skillItems.size(); i++) {
                JsonObject item = skillItems.get(i);
                String itemName = item.has("itemName") && !item.get("itemName").getAsString().isEmpty() ?
                                 item.get("itemName").getAsString() : "未知技能";

                // 获取技能详情
                String detail = item.has("itemDetail") ? item.get("itemDetail").getAsString() : "技能秘籍";

                int quantity = item.has("quantity") ? item.get("quantity").getAsInt() : 1;
                Long itemId = item.has("itemId") ? item.get("itemId").getAsLong() : 0L;

                // 标记已学习的技能
                String status = "";
                if (learnedSkillIds.contains(itemId)) {
                    status = " [已学]";
                }

                // 截断过长的详情
                if (detail.length() > 25) {
                    detail = detail.substring(0, 22) + "...";
                }

                System.out.printf("│ %-4d │ %-16s │ %-25s │ %-8d │\n",
                        i + 1, itemName + status, detail, quantity);
            }
            System.out.println("└──────┴──────────────────┴─────────────────────────────┴──────────┘");

            // 显示技能ID列表
            System.out.println("\n📋 可学习的技能ID:");
            System.out.println("─────────────────────────────────");
            for (int i = 0; i < skillItems.size(); i++) {
                JsonObject item = skillItems.get(i);
                Long itemId = item.has("itemId") ? item.get("itemId").getAsLong() : 0L;
                String itemName = item.has("itemName") && !item.get("itemName").getAsString().isEmpty() ?
                                 item.get("itemName").getAsString() : "未知技能";

                String status = learnedSkillIds.contains(itemId) ? " [已学]" : "";
                System.out.printf("  [%d] %s%s\n", itemId, itemName, status);
            }
            System.out.println("\n💡 提示：请输入技能ID（方括号中的数字）进行学习");
        } else {
            System.out.println("\n背包中没有技能物品！");
            pressEnterToContinue();
            return;
        }

        // 显示当前角色境界信息（调试用）
        System.out.println("\n📊 当前角色信息:");
        System.out.println("  境界等级: " + currentCharacter.getRealmLevel() + " (" + getRealmNameByLevel(currentCharacter.getRealmLevel()) + ")");

        System.out.print("\n请输入技能ID: ");
        String skillIdStr = scanner.nextLine();

        try {
            Long skillId = Long.parseLong(skillIdStr);

            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("skillId", skillId);

            String response = ApiClient.post("/skill/learn", request);

            // 解析响应
            JsonObject responseObj = gson.fromJson(response, JsonObject.class);
            if (responseObj.has("code")) {
                int code = responseObj.get("code").getAsInt();
                if (code == 200) {
                    // 学习成功
                    if (responseObj.has("data") && !responseObj.get("data").isJsonNull()) {
                        JsonObject data = responseObj.get("data").getAsJsonObject();
                        String skillName = data.has("skillName") ? data.get("skillName").getAsString() : "未知技能";
                        System.out.println("\n✅ 学习成功！");
                        System.out.println("技能: " + skillName);
                    }
                } else {
                    // 学习失败，显示错误信息
                    String message = responseObj.has("message") ?
                            responseObj.get("message").getAsString() : "学习失败";

                    // 将境界等级替换为中文境界
                    message = message.replaceAll("需要境界等级: (\\d+)", "需要境界: $1");
                    // 提取数字并转换为中文
                    Pattern pattern = Pattern.compile("需要境界: (\\d+)");
                    Matcher matcher = pattern.matcher(message);
                    if (matcher.find()) {
                        int level = Integer.parseInt(matcher.group(1));
                        message = message.replaceFirst("需要境界: \\d+", "需要境界: " + getRealmNameByLevel(level));
                    }

                    System.out.println("\n❌ " + message);
                }
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

        // 1. 获取已装备的技能
        String equippedResponse = ApiClient.get("/skill/equipped/" + currentCharacterId);
        JsonObject equippedJson = gson.fromJson(equippedResponse, JsonObject.class);
        java.util.Map<Integer, JsonObject> equippedSkills = new java.util.HashMap<>();

        if (equippedJson.has("code") && equippedJson.get("code").getAsInt() == 200) {
            if (equippedJson.has("data") && equippedJson.get("data").isJsonArray()) {
                JsonArray array = equippedJson.get("data").getAsJsonArray();
                for (JsonElement element : array) {
                    JsonObject skillObj = element.getAsJsonObject();
                    Integer slotIndex = skillObj.has("slotIndex") && !skillObj.get("slotIndex").isJsonNull() ?
                                        skillObj.get("slotIndex").getAsInt() : null;
                    if (slotIndex != null) {
                        equippedSkills.put(slotIndex, skillObj);
                    }
                }
            }
        }

        // 2. 显示技能槽位状态
        System.out.println("\n📊 技能槽位状态:");
        if (equippedSkills.isEmpty()) {
            System.out.println("  所有槽位空闲");
        } else {
            for (int i = 1; i <= 8; i++) {
                String slotType = (i <= 5) ? "[攻击]" : "[防御/辅助]";
                if (equippedSkills.containsKey(i)) {
                    JsonObject skill = equippedSkills.get(i);
                    String name = skill.has("skillName") ? skill.get("skillName").getAsString() : "未知";
                    int level = skill.has("skillLevel") ? skill.get("skillLevel").getAsInt() : 1;
                    System.out.printf("  槽位%d %s: %s (Lv.%d)\n", i, slotType, name, level);
                } else {
                    System.out.printf("  槽位%d %s: [空闲]\n", i, slotType);
                }
            }
        }

        // 3. 获取已学习的技能
        String learnedResponse = ApiClient.get("/skill/learned/" + currentCharacterId);
        JsonObject learnedJson = gson.fromJson(learnedResponse, JsonObject.class);
        java.util.List<JsonObject> learnedSkills = new java.util.ArrayList<>();

        if (learnedJson.has("code") && learnedJson.get("code").getAsInt() == 200) {
            if (learnedJson.has("data") && learnedJson.get("data").isJsonArray()) {
                JsonArray array = learnedJson.get("data").getAsJsonArray();
                for (JsonElement element : array) {
                    learnedSkills.add(element.getAsJsonObject());
                }
            }
        }

        // 4. 显示已学习的技能
        if (learnedSkills.isEmpty()) {
            System.out.println("\n❌ 你还没有学习任何技能！");
            pressEnterToContinue();
            return;
        }

        System.out.println("\n📚 已学习的技能:");
        System.out.println("┌──────┬──────────────────┬──────────┬──────────┬──────────┬──────────┐");
        System.out.println("│ 序号 │ 技能名称         │ 类型     │ 等级     │ 熟练度   │ 状态     │");
        System.out.println("├──────┼──────────────────┼──────────┼──────────┼──────────┼──────────┤");

        java.util.Map<Long, JsonObject> skillMap = new java.util.HashMap<>();
        for (int i = 0; i < learnedSkills.size(); i++) {
            JsonObject skill = learnedSkills.get(i);
            Long charSkillId = skill.has("characterSkillId") ? skill.get("characterSkillId").getAsLong() : 0L;
            String name = skill.has("skillName") ? skill.get("skillName").getAsString() : "未知";
            String functionType = skill.has("functionType") ? skill.get("functionType").getAsString() : "未知";
            int level = skill.has("skillLevel") ? skill.get("skillLevel").getAsInt() : 1;
            int proficiency = skill.has("proficiency") ? skill.get("proficiency").getAsInt() : 0;
            boolean isEquipped = skill.has("isEquipped") && skill.get("isEquipped").getAsBoolean();
            Integer slotIndex = skill.has("slotIndex") && !skill.get("slotIndex").isJsonNull() ?
                               skill.get("slotIndex").getAsInt() : null;

            String status = isEquipped ? (slotIndex != null ? "已装备(槽" + slotIndex + ")" : "已装备") : "未装备";

            System.out.printf("│ %-4d │ %-16s │ %-8s │ %-8d │ %-8d │ %-8s │\n",
                    i + 1, name, functionType, level, proficiency, status);

            skillMap.put(charSkillId, skill);
        }
        System.out.println("└──────┴──────────────────┴──────────┴──────────┴──────────┴──────────┘");

        // 5. 用户输入
        System.out.print("\n请输入要装备的技能序号 (直接回车返回): ");
        String skillSeqStr = scanner.nextLine().trim();

        if (skillSeqStr.isEmpty()) {
            return;
        }

        try {
            int skillSeq = Integer.parseInt(skillSeqStr);
            if (skillSeq < 1 || skillSeq > learnedSkills.size()) {
                System.out.println("\n❌ 无效的技能序号！");
                pressEnterToContinue();
                return;
            }

            JsonObject selectedSkill = learnedSkills.get(skillSeq - 1);
            Long charSkillId = selectedSkill.has("characterSkillId") ?
                               selectedSkill.get("characterSkillId").getAsLong() : 0L;
            String skillName = selectedSkill.has("skillName") ?
                              selectedSkill.get("skillName").getAsString() : "未知";

            // 检查是否已装备
            boolean currentlyEquipped = selectedSkill.has("isEquipped") &&
                                        selectedSkill.get("isEquipped").getAsBoolean();
            Integer currentSlot = selectedSkill.has("slotIndex") && !selectedSkill.get("slotIndex").isJsonNull() ?
                                   selectedSkill.get("slotIndex").getAsInt() : null;

            if (currentlyEquipped && currentSlot != null) {
                System.out.printf("\n⚠️  技能 [%s] 当前已装备在槽位%d\n", skillName, currentSlot);
                System.out.print("是否要更换槽位？(y/n): ");
                String confirm = scanner.nextLine().trim();
                if (!confirm.equalsIgnoreCase("y")) {
                    return;
                }
            }

            System.out.print("\n请输入目标槽位 (1-8, 直接回车卸下): ");
            String slotStr = scanner.nextLine().trim();

            if (slotStr.isEmpty()) {
                // 卸下技能
                unequipSkill(charSkillId);
                return;
            }

            Integer slot = Integer.parseInt(slotStr);
            if (slot < 1 || slot > 8) {
                System.out.println("\n❌ 槽位索引必须在1-8之间！");
                pressEnterToContinue();
                return;
            }

            // 装备技能
            JsonObject request = new JsonObject();
            request.addProperty("characterId", currentCharacterId);
            request.addProperty("characterSkillId", charSkillId);
            request.addProperty("slotIndex", slot);

            String response = ApiClient.post("/skill/equip", request);
            JsonObject resultObj = gson.fromJson(response, JsonObject.class);

            if (resultObj.has("code") && resultObj.get("code").getAsInt() == 200) {
                System.out.println("\n✅ 装备成功！");
                System.out.printf("技能 [%s] 已装备到槽位 %d\n", skillName, slot);
            } else {
                String message = resultObj.has("message") ? resultObj.get("message").getAsString() : "装备失败";
                System.out.println("\n❌ " + message);
            }

        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的输入！");
        }

        pressEnterToContinue();
    }

    /**
     * 卸下技能（内部调用）
     */
    private static void unequipSkill(Long characterSkillId) throws IOException, InterruptedException {
        JsonObject request = new JsonObject();
        request.addProperty("characterId", currentCharacterId);
        request.addProperty("characterSkillId", characterSkillId);

        String response = ApiClient.post("/skill/unequip", request);
        JsonObject resultObj = gson.fromJson(response, JsonObject.class);

        if (resultObj.has("code") && resultObj.get("code").getAsInt() == 200) {
            System.out.println("\n✅ 卸下成功！");
        } else {
            String message = resultObj.has("message") ? resultObj.get("message").getAsString() : "卸下失败";
            System.out.println("\n❌ " + message);
        }
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
            System.out.print("\n请选择 (直接回车返回主菜单): ");

            String choice = readMenuChoice();

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
            System.out.println("│  5. 宗门任务                         │");
            System.out.println("│  6. 职位管理                         │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择 (直接回车返回主菜单): ");

            String choice = readMenuChoice();

            switch (choice) {
                case "1": showSectList(); break;
                case "2": showMySect(); break;
                case "3": joinSect(); break;
                case "4": showSectShop(); break;
                case "5": showSectTasks(); break;
                case "6": showPositionManagement(); break;
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

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
            JsonObject data = jsonObject.get("data").getAsJsonObject();
            SectMemberResponse member = gson.fromJson(data, SectMemberResponse.class);

            if (member != null) {
                System.out.println("\n┌──────────────────────────────────────┐");
                System.out.println("│            我的宗门信息                │");
                System.out.println("├──────────────────────────────────────┤");
                System.out.printf("│ 宗门名称: %-28s │\n", member.getSectName());
                System.out.printf("│ 职位: %-32s │\n", member.getPosition());
                System.out.printf("│ 总贡献: %-30d │\n", member.getContribution());
                System.out.printf("│ 本周贡献: %-28d │\n", member.getWeeklyContribution());
                System.out.printf("│ 声望: %-32d │\n", member.getReputation());
                System.out.printf("│ 加入时间: %-28s │\n", member.getJoinedAt());
                System.out.println("└──────────────────────────────────────┘");
            } else {
                System.out.println("\n未加入任何宗门！");
            }
        } else {
            System.out.println("\n未加入任何宗门！");
        }

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
                System.out.println("\n序号  物品名称              类型      价格    库存    所需职位");
                System.out.println("─────────────────────────────────────────────────────────");
                for (int i = 0; i < items.size(); i++) {
                    SectShopItemResponse item = items.get(i);
                    // 库存为null时显示为0
                    String stockDisplay = (item.getStock() == null) ? "0" : String.valueOf(item.getStock());
                    // 获取职位名称
                    String positionDisplay = getPositionName(item.getRequiredPosition());
                    System.out.printf("%-4d  %-20s  %-8s  %-6d  %-6s  %-8s\n",
                            i + 1, item.getItemName(), item.getItemType(),
                            item.getPrice(), stockDisplay, positionDisplay);
                }

                // 显示后续操作菜单
                System.out.println("\n请选择操作：");
                System.out.println("1. 查看物品明细");
                System.out.println("2. 购买物品");
                System.out.println("0. 返回");
                System.out.print("\n请选择: ");

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        viewSectShopItemDetail(items);
                        break;
                    case "2":
                        buyFromSectShop(items);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("\n无效选择！");
                }
            } else {
                System.out.println("\n商店暂无物品！");
                pressEnterToContinue();
            }
        } else {
            System.out.println("\n商店暂无物品！");
            pressEnterToContinue();
        }
    }

    /**
     * 查看宗门商店物品明细
     */
    private static void viewSectShopItemDetail(List<SectShopItemResponse> items) throws IOException, InterruptedException {
        System.out.print("\n请输入要查看的物品序号: ");
        String indexStr = scanner.nextLine();

        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index >= 0 && index < items.size()) {
                SectShopItemResponse item = items.get(index);
                String itemType = item.getItemType();

                // 根据物品类型显示不同的标题
                String title = getItemTypeDisplayName(itemType);

                System.out.println("\n┌──────────────────────────────────────┐");
                System.out.printf("│          %s                │\n", title);
                System.out.println("├──────────────────────────────────────┤");
                System.out.printf("│ 物品名称: %-28s │\n", item.getItemName());
                System.out.printf("│ 物品类型: %-28s │\n", itemType);
                if (item.getItemTier() != null) {
                    System.out.printf("│ 物品阶位: %-28d │\n", item.getItemTier());
                }
                System.out.printf("│ 价格: %-32d │\n", item.getPrice());
                System.out.printf("│ 库存: %-32s │\n",
                    (item.getStock() == null ? "0" : String.valueOf(item.getStock())));

                // 根据物品类型显示特有属性
                switch (itemType) {
                    case "skill":
                        if (item.getBaseDamage() != null) {
                            System.out.printf("│ 基础伤害: %-28d │\n", item.getBaseDamage());
                        }
                        if (item.getSpiritualCost() != null) {
                            System.out.printf("│ 灵力消耗: %-28d │\n", item.getSpiritualCost());
                        }
                        if (item.getSkillType() != null) {
                            System.out.printf("│ 技能类型: %-28s │\n", item.getSkillType());
                        }
                        if (item.getElementType() != null) {
                            System.out.printf("│ 元素类型: %-28s │\n", item.getElementType());
                        }
                        break;
                    case "equipment":
                        if (item.getEquipmentSlot() != null) {
                            System.out.printf("│ 装备部位: %-28s │\n", item.getEquipmentSlot());
                        }
                        if (item.getAttackBonus() != null && item.getAttackBonus() > 0) {
                            System.out.printf("│ 攻击加成: %-28d │\n", item.getAttackBonus());
                        }
                        if (item.getDefenseBonus() != null && item.getDefenseBonus() > 0) {
                            System.out.printf("│ 防御加成: %-28d │\n", item.getDefenseBonus());
                        }
                        if (item.getHealthBonus() != null && item.getHealthBonus() > 0) {
                            System.out.printf("│ 生命加成: %-28d │\n", item.getHealthBonus());
                        }
                        break;
                    case "pill":
                        if (item.getHealAmount() != null && item.getHealAmount() > 0) {
                            System.out.printf("│ 治疗量: %-30d │\n", item.getHealAmount());
                        }
                        if (item.getExpBonus() != null && item.getExpBonus() > 0) {
                            System.out.printf("│ 经验加成: %-28d │\n", item.getExpBonus());
                        }
                        if (item.getBuffDuration() != null && item.getBuffDuration() > 0) {
                            System.out.printf("│ Buff时长: %-27d秒│\n", item.getBuffDuration());
                        }
                        break;
                    case "material":
                        // 材料类型只显示基础信息
                        System.out.println("│ 用途: 炼丹/锻造材料                 │");
                        break;
                }

                // 显示描述
                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    // 分行显示描述，每行最多26个字符
                    String desc = item.getDescription();
                    int maxLength = 26;
                    for (int i = 0; i < desc.length(); i += maxLength) {
                        int end = Math.min(i + maxLength, desc.length());
                        String line = desc.substring(i, end);
                        if (i == 0) {
                            System.out.printf("│ 描述: %-29s │\n", line);
                        } else {
                            System.out.printf("│       %-29s │\n", line);
                        }
                    }
                } else {
                    System.out.printf("│ 描述: %-29s │\n", "暂无描述");
                }
                System.out.println("└──────────────────────────────────────┘");
            } else {
                System.out.println("\n❌ 无效的序号！");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的输入！");
        }

        pressEnterToContinue();
    }

    /**
     * 获取物品类型的显示名称
     */
    private static String getItemTypeDisplayName(String itemType) {
        switch (itemType) {
            case "skill": return "技能详细信息";
            case "equipment": return "装备详细信息";
            case "pill": return "丹药详细信息";
            case "material": return "材料详细信息";
            default: return "物品详细信息";
        }
    }

    /**
     * 购买物品
     */
    private static void buyFromSectShop(List<SectShopItemResponse> items) throws IOException, InterruptedException {
        System.out.println("\n--- 购买物品 ---");
        System.out.print("请输入要购买的物品序号: ");
        String indexStr = scanner.nextLine();

        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index >= 0 && index < items.size()) {
                SectShopItemResponse item = items.get(index);

                // 检查库存
                if (item.getStock() != null && item.getStock() <= 0) {
                    System.out.println("\n❌ 该物品库存不足！");
                    pressEnterToContinue();
                    return;
                }

                System.out.printf("\n物品: %s\n", item.getItemName());
                System.out.printf("价格: %d 贡献/个\n", item.getPrice());
                System.out.print("请输入购买数量: ");
                String quantityStr = scanner.nextLine();

                try {
                    Integer quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        System.out.println("\n❌ 购买数量必须大于0！");
                        pressEnterToContinue();
                        return;
                    }

                    // 检查库存是否足够
                    if (item.getStock() != null && quantity > item.getStock()) {
                        System.out.printf("\n❌ 库存不足！当前库存: %d\n", item.getStock());
                        pressEnterToContinue();
                        return;
                    }

                    JsonObject request = new JsonObject();
                    request.addProperty("characterId", currentCharacterId);
                    request.addProperty("itemId", item.getShopItemId());
                    request.addProperty("quantity", quantity);

                    String response = ApiClient.post("/sect/shop/buy", request);

                    // 解析响应
                    JsonObject result = gson.fromJson(response, JsonObject.class);
                    if (result.has("code") && result.get("code").getAsInt() == 200) {
                        System.out.println("\n✅ 购买成功！");
                    } else {
                        System.out.println("\n❌ " + result.get("message").getAsString());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n❌ 无效的数量！");
                }
            } else {
                System.out.println("\n❌ 无效的序号！");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 无效的输入！");
        }

        pressEnterToContinue();
    }

    /**
     * 宗门任务菜单
     */
    private static void showSectTasks() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              宗 门 任 务              │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看任务列表                     │");
            System.out.println("│  2. 接取任务                         │");
            System.out.println("│  3. 提交任务                         │");
            System.out.println("│  4. 领取奖励                         │");
            System.out.println("│  0. 返回宗门菜单                     │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = readMenuChoice();

            switch (choice) {
                case "1": showTaskList(); break;
                case "2": acceptTask(); break;
                case "3": submitTask(); break;
                case "4": claimTaskReward(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 查看任务列表
     */
    private static void showTaskList() throws IOException, InterruptedException {
        System.out.println("\n--- 任务列表 ---");

        String response = ApiClient.get("/sect/tasks/my/" + currentCharacterId);
        JsonObject result = gson.fromJson(response, JsonObject.class);

        if (result.has("code") && result.get("code").getAsInt() == 200) {
            DailyTaskSummaryResponse summary = gson.fromJson(result.get("data"), DailyTaskSummaryResponse.class);

            System.out.printf("\n今日剩余接取次数: %d/%d\n", summary.getRemainingAccepts(), summary.getTotalDailyLimit());
            System.out.printf("今日已完成: %d\n\n", summary.getCompletedCount());

            // 显示进行中任务
            if (summary.getInProgressTasks() != null && !summary.getInProgressTasks().isEmpty()) {
                System.out.println("┌──────────────────────────────────────┐");
                System.out.println("│            进行中任务                 │");
                System.out.println("├──────────────────────────────────────┤");
                for (TaskProgressResponse task : summary.getInProgressTasks()) {
                    System.out.printf("│ [%d] %s\n", task.getProgressId(), task.getTaskName());
                    System.out.printf("│     类型: %s\n", task.getTaskTypeDisplay());
                    System.out.printf("│     进度: %s\n", task.getProgressDisplay());
                    System.out.printf("│     状态: %s\n", task.getStatusDisplay());
                    System.out.printf("│     奖励: %d贡献 + %d声望\n",
                            task.getContributionReward(), task.getReputationReward());
                    System.out.println("├──────────────────────────────────────┤");
                }
                System.out.println("└──────────────────────────────────────┘");
            }

            // 显示可接取任务
            if (summary.getAvailableTasks() != null && !summary.getAvailableTasks().isEmpty()) {
                System.out.println("\n┌──────────────────────────────────────┐");
                System.out.println("│            可接取任务                 │");
                System.out.println("├──────────────────────────────────────┤");
                for (SectTaskResponse task : summary.getAvailableTasks()) {
                    System.out.printf("│ [%d] %s", task.getTemplateId(), task.getTaskName());
                    if (!task.getCanAccept()) {
                        System.out.print(" (无法接取)");
                    }
                    System.out.println();
                    System.out.printf("│     类型: %s\n", task.getTaskTypeDisplay());
                    System.out.printf("│     目标: %s\n", task.getTargetDisplay());
                    System.out.printf("│     奖励: %d贡献 + %d声望\n",
                            task.getContributionReward(), task.getReputationReward());
                    System.out.println("├──────────────────────────────────────┤");
                }
                System.out.println("└──────────────────────────────────────┘");
            } else {
                System.out.println("\n暂无可接取任务");
            }
        } else {
            System.out.println("\n❌ " + result.get("message").getAsString());
        }

        pressEnterToContinue();
    }

    /**
     * 接取任务
     */
    private static void acceptTask() throws IOException, InterruptedException {
        System.out.println("\n--- 接取任务 ---");

        // 先获取可接取任务列表
        String response = ApiClient.get("/sect/tasks/available/" + currentCharacterId);
        JsonObject result = gson.fromJson(response, JsonObject.class);

        if (result.has("code") && result.get("code").getAsInt() == 200) {
            Type listType = new TypeToken<List<SectTaskResponse>>(){}.getType();
            List<SectTaskResponse> tasks = gson.fromJson(result.get("data").getAsJsonArray(), listType);

            if (tasks == null || tasks.isEmpty()) {
                System.out.println("\n暂无可接取任务");
                pressEnterToContinue();
                return;
            }

            System.out.println("\n可接取任务:");
            for (SectTaskResponse task : tasks) {
                System.out.printf("[%d] %s - %s\n", task.getTemplateId(), task.getTaskName(), task.getTargetDisplay());
                if (!task.getCanAccept()) {
                    System.out.println("    (无法接取：职位不足或次数限制)");
                }
            }

            System.out.print("\n请输入要接取的任务ID: ");
            String taskIdStr = scanner.nextLine();
            try {
                Long taskId = Long.parseLong(taskIdStr);

                JsonObject request = new JsonObject();
                request.addProperty("characterId", currentCharacterId);
                request.addProperty("templateId", taskId);

                String acceptResponse = ApiClient.post("/sect/tasks/accept", request);
                JsonObject acceptResult = gson.fromJson(acceptResponse, JsonObject.class);

                if (acceptResult.has("code") && acceptResult.get("code").getAsInt() == 200) {
                    System.out.println("\n✅ 接取任务成功！");
                } else {
                    System.out.println("\n❌ " + acceptResult.get("message").getAsString());
                }
            } catch (NumberFormatException e) {
                System.out.println("\n❌ 无效的任务ID！");
            }
        } else {
            System.out.println("\n❌ " + result.get("message").getAsString());
        }

        pressEnterToContinue();
    }

    /**
     * 提交任务
     */
    private static void submitTask() throws IOException, InterruptedException {
        System.out.println("\n--- 提交任务 ---");

        // 获取进行中任务
        String response = ApiClient.get("/sect/tasks/my/" + currentCharacterId);
        JsonObject result = gson.fromJson(response, JsonObject.class);

        if (result.has("code") && result.get("code").getAsInt() == 200) {
            DailyTaskSummaryResponse summary = gson.fromJson(result.get("data"), DailyTaskSummaryResponse.class);

            if (summary.getInProgressTasks() == null || summary.getInProgressTasks().isEmpty()) {
                System.out.println("\n暂无进行中任务");
                pressEnterToContinue();
                return;
            }

            // 筛选已完成的任务
            List<TaskProgressResponse> completedTasks = new ArrayList<>();
            List<TaskProgressResponse> inProgressTasks = new ArrayList<>();

            for (TaskProgressResponse task : summary.getInProgressTasks()) {
                if ("completed".equals(task.getStatus())) {
                    completedTasks.add(task);
                } else if ("accepted".equals(task.getStatus())) {
                    inProgressTasks.add(task);
                }
            }

            // 显示任务列表
            if (!inProgressTasks.isEmpty()) {
                System.out.println("\n进行中任务:");
                for (TaskProgressResponse task : inProgressTasks) {
                    System.out.printf("[%d] %s - 进度: %s\n",
                            task.getProgressId(), task.getTaskName(), task.getProgressDisplay());
                }
            }

            if (!completedTasks.isEmpty()) {
                System.out.println("\n✨ 已完成任务（可提交）:");
                for (TaskProgressResponse task : completedTasks) {
                    System.out.printf("[%d] %s - 进度: %s\n",
                            task.getProgressId(), task.getTaskName(), task.getProgressDisplay());
                }
            }

            if (completedTasks.isEmpty()) {
                System.out.println("\n暂无可提交的任务");
                pressEnterToContinue();
                return;
            }

            System.out.print("\n请输入要提交的任务进度ID（直接回车提交所有已完成任务）: ");
            String progressIdStr = scanner.nextLine().trim();

            // 如果输入为空，自动提交所有已完成任务
            if (progressIdStr.isEmpty()) {
                System.out.println("\n开始自动提交所有已完成任务...\n");
                int successCount = 0;
                int failCount = 0;

                for (TaskProgressResponse task : completedTasks) {
                    try {
                        JsonObject request = new JsonObject();
                        request.addProperty("characterId", currentCharacterId);
                        request.addProperty("progressId", task.getProgressId());

                        String submitResponse = ApiClient.post("/sect/tasks/submit", request);
                        JsonObject submitResult = gson.fromJson(submitResponse, JsonObject.class);

                        if (submitResult.has("code") && submitResult.get("code").getAsInt() == 200) {
                            System.out.printf("✅ [%d] %s - 提交成功\n", task.getProgressId(), task.getTaskName());
                            successCount++;
                        } else {
                            String errorMsg = submitResult.has("message") ?
                                    submitResult.get("message").getAsString() : "未知错误";
                            System.out.printf("❌ [%d] %s - 提交失败: %s\n",
                                    task.getProgressId(), task.getTaskName(), errorMsg);
                            failCount++;
                        }
                    } catch (Exception e) {
                        System.out.printf("❌ [%d] %s - 提交异常: %s\n",
                                task.getProgressId(), task.getTaskName(), e.getMessage());
                        failCount++;
                    }
                }

                System.out.println("\n" + "─".repeat(40));
                System.out.printf("\n提交完成！成功: %d，失败: %d\n", successCount, failCount);

                if (successCount > 0) {
                    System.out.println("\n💡 提示：可以前往「领取奖励」菜单领取任务奖励");
                }
            } else {
                // 手动提交单个任务
                try {
                    Long progressId = Long.parseLong(progressIdStr);

                    JsonObject request = new JsonObject();
                    request.addProperty("characterId", currentCharacterId);
                    request.addProperty("progressId", progressId);

                    String submitResponse = ApiClient.post("/sect/tasks/submit", request);
                    JsonObject submitResult = gson.fromJson(submitResponse, JsonObject.class);

                    if (submitResult.has("code") && submitResult.get("code").getAsInt() == 200) {
                        System.out.println("\n✅ 任务提交成功！可以领取奖励了");
                    } else {
                        System.out.println("\n❌ " + submitResult.get("message").getAsString());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n❌ 无效的进度ID！");
                }
            }
        } else {
            System.out.println("\n❌ " + result.get("message").getAsString());
        }

        pressEnterToContinue();
    }

    /**
     * 领取任务奖励
     */
    private static void claimTaskReward() throws IOException, InterruptedException {
        System.out.println("\n--- 领取奖励 ---");

        // 获取可领取奖励的任务（已完成状态）
        String response = ApiClient.get("/sect/tasks/my/" + currentCharacterId);
        JsonObject result = gson.fromJson(response, JsonObject.class);

        if (result.has("code") && result.get("code").getAsInt() == 200) {
            DailyTaskSummaryResponse summary = gson.fromJson(result.get("data"), DailyTaskSummaryResponse.class);

            if (summary.getInProgressTasks() == null || summary.getInProgressTasks().isEmpty()) {
                System.out.println("\n暂无任务");
                pressEnterToContinue();
                return;
            }

            // 筛选已提交待领奖的任务
            List<TaskProgressResponse> completedTasks = new ArrayList<>();
            for (TaskProgressResponse task : summary.getInProgressTasks()) {
                // 只显示已提交但未领奖的任务
                if ("submitted".equals(task.getStatus())) {
                    completedTasks.add(task);
                }
            }

            if (completedTasks.isEmpty()) {
                System.out.println("\n暂无可领取奖励的任务（需要先提交任务）");
                pressEnterToContinue();
                return;
            }

            System.out.println("\n可领取奖励的任务:");
            for (TaskProgressResponse task : completedTasks) {
                System.out.printf("[%d] %s - 奖励: %d贡献 + %d声望\n",
                        task.getProgressId(), task.getTaskName(),
                        task.getContributionReward(), task.getReputationReward());
            }

            System.out.print("\n请输入要领取奖励的任务进度ID（直接回车领取所有可领取奖励）: ");
            String progressIdStr = scanner.nextLine().trim();

            // 如果输入为空，自动领取所有可领取奖励
            if (progressIdStr.isEmpty()) {
                System.out.println("\n开始自动领取所有可领取奖励...\n");
                int successCount = 0;
                int failCount = 0;
                int totalContribution = 0;
                int totalReputation = 0;

                for (TaskProgressResponse task : completedTasks) {
                    try {
                        String claimResponse = ApiClient.post("/sect/tasks/claim/" + task.getProgressId(), new com.google.gson.JsonObject());
                        JsonObject claimResult = gson.fromJson(claimResponse, com.google.gson.JsonObject.class);

                        if (claimResult.has("code") && claimResult.get("code").getAsInt() == 200) {
                            int contribution = task.getContributionReward();
                            int reputation = task.getReputationReward();
                            totalContribution += contribution;
                            totalReputation += reputation;

                            System.out.printf("✅ [%d] %s - 获得 %d贡献 + %d声望\n",
                                    task.getProgressId(), task.getTaskName(), contribution, reputation);
                            successCount++;
                        } else {
                            String errorMsg = claimResult.has("message") ?
                                    claimResult.get("message").getAsString() : "未知错误";
                            System.out.printf("❌ [%d] %s - 领取失败: %s\n",
                                    task.getProgressId(), task.getTaskName(), errorMsg);
                            failCount++;
                        }
                    } catch (Exception e) {
                        System.out.printf("❌ [%d] %s - 领取异常: %s\n",
                                task.getProgressId(), task.getTaskName(), e.getMessage());
                        failCount++;
                    }
                }

                System.out.println("\n" + "─".repeat(40));
                System.out.printf("\n领取完成！成功: %d，失败: %d\n", successCount, failCount);
                if (successCount > 0) {
                    System.out.printf("\n📊 总计获得: %d贡献值，%d声望\n", totalContribution, totalReputation);
                    System.out.println("\n💡 提示：可以使用「我的宗门」菜单查看新的声望和贡献值");
                }
            } else {
                // 手动领取单个任务奖励
                try {
                    Long progressId = Long.parseLong(progressIdStr);

                    String claimResponse = ApiClient.post("/sect/tasks/claim/" + progressId, new com.google.gson.JsonObject());
                    JsonObject claimResult = gson.fromJson(claimResponse, com.google.gson.JsonObject.class);

                    if (claimResult.has("code") && claimResult.get("code").getAsInt() == 200) {
                        // 安全地获取data字段
                        if (claimResult.has("data") && !claimResult.get("data").isJsonNull()) {
                            System.out.println("\n✅ " + claimResult.get("data").getAsString());
                        } else {
                            System.out.println("\n✅ 奖励领取成功！");
                        }
                    } else {
                        System.out.println("\n❌ " + claimResult.get("message").getAsString());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n❌ 无效的进度ID！");
                }
            }
        } else {
            System.out.println("\n❌ " + result.get("message").getAsString());
        }

        pressEnterToContinue();
    }

    /**
     * 职位管理菜单
     */
    private static void showPositionManagement() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────┐");
            System.out.println("│              职 位 管 理              │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. 查看职位信息                     │");
            System.out.println("│  2. 申请职位升级                     │");
            System.out.println("│  3. 查看所有职位                     │");
            System.out.println("│  0. 返回宗门菜单                     │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择: ");

            String choice = readMenuChoice();

            switch (choice) {
                case "1": showPositionInfo(); break;
                case "2": applyForPromotion(); break;
                case "3": showAllPositions(); break;
                case "0": return;
                default: System.out.println("\n无效选择！");
            }
        }
    }

    /**
     * 显示职位信息
     */
    private static void showPositionInfo() throws IOException, InterruptedException {
        System.out.println("\n--- 职位升级信息 ---");

        com.xiuxian.client.model.PositionUpgradeInfo info =
                ApiClient.getPositionUpgradeInfo(currentCharacterId);

        if (info == null) {
            System.out.println("\n❌ 无法获取职位信息");
            pressEnterToContinue();
            return;
        }

        if (!info.getAvailable()) {
            System.out.printf("\n当前职位: %s\n", info.getCurrentPosition());
            System.out.printf("\n%s\n", info.getUnavailableReason());
            pressEnterToContinue();
            return;
        }

        System.out.println("\n┌──────────────────────────────────────┐");
        System.out.printf("│          职 位 升 级 信 息          │\n");
        System.out.println("├──────────────────────────────────────┤");
        System.out.printf("│ 当前职位: %-26s │\n", info.getCurrentPosition());
        System.out.printf("│ 下一职位: %-26s │\n", info.getNextPosition());
        System.out.println("├──────────────────────────────────────┤");
        System.out.println("│ 升级要求:                            │");
        System.out.println("├──────────────────────────────────────┤");

        // 显示声望值
        String reputationStatus = info.getCurrentReputation() >= info.getRequiredReputation() ? "✅" : "❌";
        System.out.printf("│   %s 声望值: %4d / %-4d              │\n",
                reputationStatus, info.getCurrentReputation(), info.getRequiredReputation());

        // 显示贡献值
        String contributionStatus = info.getCurrentContribution() >= info.getRequiredContribution() ? "✅" : "❌";
        System.out.printf("│   %s 贡献值: %4d / %-4d              │\n",
                contributionStatus, info.getCurrentContribution(), info.getRequiredContribution());

        // 显示灵石
        String stonesStatus = info.getCurrentSpiritStones() >= info.getRequiredSpiritStones() ? "✅" : "❌";
        System.out.printf("│   %s 灵石:   %4d / %-4d              │\n",
                stonesStatus, info.getCurrentSpiritStones(), info.getRequiredSpiritStones());

        System.out.println("├──────────────────────────────────────┤");

        if (info.getCanUpgrade()) {
            System.out.println("│ 状态: ✅ 满足升级条件               │");
        } else {
            System.out.println("│ 状态: ❌ 不满足升级条件             │");
        }

        System.out.println("└──────────────────────────────────────┘");

        pressEnterToContinue();
    }

    /**
     * 申请职位升级
     */
    private static void applyForPromotion() throws IOException, InterruptedException {
        System.out.println("\n--- 申请职位升级 ---");

        // 先获取职位信息确认
        com.xiuxian.client.model.PositionUpgradeInfo info =
                ApiClient.getPositionUpgradeInfo(currentCharacterId);

        if (info == null) {
            System.out.println("\n❌ 无法获取职位信息");
            pressEnterToContinue();
            return;
        }

        if (!info.getAvailable()) {
            System.out.printf("\n当前职位: %s\n", info.getCurrentPosition());
            System.out.println("\n" + info.getUnavailableReason());
            pressEnterToContinue();
            return;
        }

        if (!info.getCanUpgrade()) {
            System.out.println("\n❌ 不满足升级条件：");
            if (info.getCurrentReputation() < info.getRequiredReputation()) {
                System.out.printf("   声望值不足 (需要 %d，当前 %d)\n",
                        info.getRequiredReputation(), info.getCurrentReputation());
            }
            if (info.getCurrentContribution() < info.getRequiredContribution()) {
                System.out.printf("   贡献值不足 (需要 %d，当前 %d)\n",
                        info.getRequiredContribution(), info.getCurrentContribution());
            }
            if (info.getCurrentSpiritStones() < info.getRequiredSpiritStones()) {
                System.out.printf("   灵石不足 (需要 %d，当前 %d)\n",
                        info.getRequiredSpiritStones(), info.getCurrentSpiritStones());
            }
            pressEnterToContinue();
            return;
        }

        System.out.printf("\n当前职位: %s\n", info.getCurrentPosition());
        System.out.printf("目标职位: %s\n", info.getNextPosition());
        System.out.printf("\n升级消耗:\n");
        System.out.printf("  贡献值: %d\n", info.getRequiredContribution());
        System.out.printf("  灵石: %d\n", info.getRequiredSpiritStones());
        System.out.print("\n确认申请升级？(y/n): ");

        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!"y".equals(confirm) && !"yes".equals(confirm)) {
            System.out.println("\n已取消升级申请");
            pressEnterToContinue();
            return;
        }

        try {
            String result = ApiClient.promotePosition(currentCharacterId);

            if (result != null) {
                System.out.println("\n✅ " + result);
            } else {
                System.out.println("\n❌ 升级申请失败：服务器未返回响应");
            }
        } catch (Exception e) {
            System.out.println("\n❌ 升级申请失败：" + e.getMessage());
        }

        pressEnterToContinue();
    }

    /**
     * 显示所有职位信息
     */
    private static void showAllPositions() throws IOException, InterruptedException {
        System.out.println("\n┌──────────────────────────────────────────────────────────┐");
        System.out.println("│                    宗门职位体系                          │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│ 职位等级       声望要求    贡献消耗    灵石消耗          │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│ 【1】弟子                                              │");
        System.out.println("│   → 初始职位，加入宗门即可获得                            │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│ 【2】内门弟子                                          │");
        System.out.println("│   声望: 100    贡献: 500     灵石: 1,000                │");
        System.out.println("│   → 解锁更多宗门商店物品                                 │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│ 【3】核心弟子                                          │");
        System.out.println("│   声望: 300    贡献: 1,500   灵石: 3,000                │");
        System.out.println("│   → 可接取更高难度的宗门任务                              │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│ 【4】长老                                              │");
        System.out.println("│   声望: 800    贡献: 5,000   灵石: 10,000               │");
        System.out.println("│   → 可管理宗门事务，享受最高待遇                          │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│ 【5】掌门                                              │");
        System.out.println("│   → 需通过宗门战/竞选获得                                 │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        System.out.println("\n💡 职位说明:");
        System.out.println("  • 职位越高，宗门商店可购买的物品越丰富");
        System.out.println("  • 职位越高，可接取的宗门任务奖励越丰厚");
        System.out.println("  • 升级需要同时满足声望、贡献值和灵石要求");
        System.out.println("  • 贡献值通过完成宗门任务获得");
        System.out.println("  • 声望通过完成宗门任务奖励获得");

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
            System.out.println("│  5. 一键装备                         │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择 (直接回车返回主菜单): ");

            String choice = readMenuChoice();

            switch (choice) {
                case "1": showEquippedItems(); break;
                case "2": equipItem(); break;
                case "3": unequipItem(); break;
                case "4": showEquipmentBonus(); break;
                case "5": autoEquip(); break;
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
                for (EquipmentInfo e : equipments) {
                    if (e.isEquipped()) {
                        // 显示装备基本信息
                        System.out.println("\n┌────────────────────────────────────────────────────────────────────┐");
                        System.out.printf("│ 槽位: %-8s │ 装备名称: %-38s │%n", e.getEquipmentSlot(), e.getEquipmentName());
                        System.out.printf("│ 品质: %-8s │ 基础评分: %-6d │ 强化等级: %-6d │%n",
                                e.getQuality(),
                                e.getBaseScore() != null ? e.getBaseScore() : 0,
                                e.getEnhancementLevel() != null ? e.getEnhancementLevel() : 0);
                        System.out.println("├────────────────────────────────────────────────────────────────────┤");

                        // 显示基础属性
                        System.out.println("│ 基础属性                                                              │");
                        System.out.println("├────────────────────────────────────────────────────────────────────┤");

                        String attack = e.getAttack() != null ? String.valueOf(e.getAttack()) : "-";
                        String defense = e.getDefense() != null ? String.valueOf(e.getDefense()) : "-";
                        String health = e.getHealthBonus() != null ? String.valueOf(e.getHealthBonus()) : "-";
                        String crit = e.getCriticalRate() != null ? String.valueOf(e.getCriticalRate()) : "-";
                        String speed = e.getSpeedBonus() != null ? String.valueOf(e.getSpeedBonus()) : "-";

                        System.out.printf("│ 攻击力: %6s │ 防御力: %6s │ 气血: %6s │ 暴击: %6s │ 速度: %6s │%n",
                                attack, defense, health, crit, speed);

                        // 显示抗性
                        System.out.println("├────────────────────────────────────────────────────────────────────┤");
                        System.out.println("│ 抗性属性                                                              │");
                        System.out.println("├────────────────────────────────────────────────────────────────────┤");

                        String physical = e.getPhysicalResist() != null ? String.valueOf(e.getPhysicalResist()) : "-";
                        String ice = e.getIceResist() != null ? String.valueOf(e.getIceResist()) : "-";
                        String fire = e.getFireResist() != null ? String.valueOf(e.getFireResist()) : "-";
                        String lightning = e.getLightningResist() != null ? String.valueOf(e.getLightningResist()) : "-";

                        System.out.printf("│ 物理抗性: %4s │ 冰系抗性: %4s │ 火系抗性: %4s │ 电系抗性: %4s │%n",
                                physical, ice, fire, lightning);

                        System.out.println("└────────────────────────────────────────────────────────────────────┘");
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

        // 定义所有槽位（9个槽位，戒指可装备2个）
        String[] slots = {"武器", "头盔", "铠甲", "护手", "护腿", "靴子", "戒指1", "戒指2", "项链"};

        // 1. 获取已装备的装备
        System.out.println("\n正在加载装备信息...");
        String equippedResponse = ApiClient.get("/equipment/character/" + currentCharacterId);
        java.util.Map<String, JsonObject> equippedMap = new java.util.HashMap<>();

        JsonObject equippedJson = gson.fromJson(equippedResponse, JsonObject.class);
        if (equippedJson.has("code") && equippedJson.get("code").getAsInt() == 200) {
            if (equippedJson.has("data") && equippedJson.get("data").isJsonArray()) {
                JsonArray array = equippedJson.get("data").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    JsonObject item = array.get(i).getAsJsonObject();
                    String slot = item.has("equipmentSlot") ? item.get("equipmentSlot").getAsString() : "";
                    equippedMap.put(slot, item);
                }
            }
        }

        // 2. 显示所有槽位及已装备的装备
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("                      当前装备状态                          ");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        for (int i = 0; i < slots.length; i++) {
            String slot = slots[i];
            System.out.printf("%2d. %-8s: ", i + 1, slot);

            if (equippedMap.containsKey(slot)) {
                JsonObject equipped = equippedMap.get(slot);
                String name = equipped.has("equipmentName") ? equipped.get("equipmentName").getAsString() : "未知";
                int score = equipped.has("baseScore") ? equipped.get("baseScore").getAsInt() : 0;
                String quality = equipped.has("quality") ? equipped.get("quality").getAsString() : "普通";
                System.out.printf("%s (评分:%d, %s)%n", name, score, quality);
            } else {
                System.out.println("[空]");
            }
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // 3. 获取背包中的装备
        String inventoryResponse = ApiClient.get("/inventory/character/" + currentCharacterId + "?itemType=equipment");
        java.util.List<JsonObject> inventoryItems = new java.util.ArrayList<>();

        JsonObject inventoryJson = gson.fromJson(inventoryResponse, JsonObject.class);
        if (inventoryJson.has("code") && inventoryJson.get("code").getAsInt() == 200) {
            if (inventoryJson.has("data") && inventoryJson.get("data").isJsonArray()) {
                JsonArray array = inventoryJson.get("data").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    inventoryItems.add(array.get(i).getAsJsonObject());
                }
            }
        }

        if (inventoryItems.isEmpty()) {
            System.out.println("\n背包中没有装备！");
            pressEnterToContinue();
            return;
        }

        // 4. 显示背包中的装备列表
        System.out.println("\n可装备列表:");
        System.out.println("┌──────┬──────────────────┬─────────────────────────────┬──────────┐");
        System.out.println("│ 序号 │ 装备名称        │ 类型 | 品质 | 评分           │ 数量     │");
        System.out.println("├──────┼──────────────────┼─────────────────────────────┼──────────┤");

        for (int i = 0; i < inventoryItems.size(); i++) {
            JsonObject item = inventoryItems.get(i);
            int index = i + 1;
            String name = item.has("itemName") ? item.get("itemName").getAsString() : "未知";
            String detail = item.has("itemDetail") ? item.get("itemDetail").getAsString() : "";
            int quantity = item.has("quantity") ? item.get("quantity").getAsInt() : 1;
            long itemId = item.has("itemId") ? item.get("itemId").getAsLong() : 0;

            // 截断过长的字符串
            String displayName = name.length() > 16 ? name.substring(0, 14) + ".." : name;
            String displayDetail = detail.length() > 25 ? detail.substring(0, 23) + ".." : detail;

            System.out.printf("│ %4d │ %-16s │ %-25s │ %8d │ (ID:%d)%n",
                    index, displayName, displayDetail, quantity, itemId);
        }

        System.out.println("└──────┴──────────────────┴─────────────────────────────┴──────────┘");

        // 5. 选择槽位
        System.out.print("\n请选择槽位 (1-" + slots.length + ", 直接回车返回): ");
        String slotStr = scanner.nextLine().trim();

        if (slotStr.isEmpty()) {
            return;
        }

        int slotIndex;
        try {
            slotIndex = Integer.parseInt(slotStr) - 1;
            if (slotIndex < 0 || slotIndex >= slots.length) {
                System.out.println("\n❌ 无效的槽位序号！");
                pressEnterToContinue();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 请输入有效的数字！");
            pressEnterToContinue();
            return;
        }

        String selectedSlot = slots[slotIndex];
        System.out.println("\n已选择槽位: " + selectedSlot);

        // 6. 选择装备
        System.out.print("\n请选择装备序号 (直接回车返回): ");
        String itemStr = scanner.nextLine().trim();

        if (itemStr.isEmpty()) {
            return;
        }

        int itemIndex;
        try {
            itemIndex = Integer.parseInt(itemStr) - 1;
            if (itemIndex < 0 || itemIndex >= inventoryItems.size()) {
                System.out.println("\n❌ 无效的装备序号！");
                pressEnterToContinue();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 请输入有效的数字！");
            pressEnterToContinue();
            return;
        }

        JsonObject selectedItem = inventoryItems.get(itemIndex);
        String itemName = selectedItem.has("itemName") ? selectedItem.get("itemName").getAsString() : "未知";
        long equipmentId = selectedItem.has("itemId") ? selectedItem.get("itemId").getAsLong() : 0;

        // 7. 检查装备类型是否匹配槽位
        String detail = selectedItem.has("itemDetail") ? selectedItem.get("itemDetail").getAsString() : "";
        String equipmentType = extractEquipmentType(detail);

        if (!isSlotMatchEquipmentType(selectedSlot, equipmentType)) {
            System.out.printf("\n❌ 装备类型不匹配！%s 不能装备到 %s 槽位%n", equipmentType, selectedSlot);
            pressEnterToContinue();
            return;
        }

        // 8. 显示将要被替换的装备
        if (equippedMap.containsKey(selectedSlot)) {
            JsonObject oldEquipment = equippedMap.get(selectedSlot);
            String oldName = oldEquipment.has("equipmentName") ? oldEquipment.get("equipmentName").getAsString() : "未知";
            System.out.printf("\n⚠️  槽位已有装备: %s (将被替换)%n", oldName);
        }

        // 9. 确认装备
        System.out.printf("\n确认将 %s 装备到 %s 槽位？ (y/n): ", itemName, selectedSlot);
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("\n已取消装备。");
            pressEnterToContinue();
            return;
        }

        // 10. 调用API装备
        JsonObject request = new JsonObject();
        request.addProperty("characterId", currentCharacterId);
        request.addProperty("equipmentId", equipmentId);
        request.addProperty("equipmentSlot", selectedSlot);

        System.out.println("\n正在装备...");
        String response = ApiClient.post("/equipment/equip", request);

        JsonObject resultJson = gson.fromJson(response, JsonObject.class);
        if (resultJson.has("code") && resultJson.get("code").getAsInt() == 200) {
            System.out.println("\n✅ 装备成功！");
            System.out.printf("已将 %s 装备到 %s 槽位%n", itemName, selectedSlot);
        } else {
            String errorMsg = resultJson.has("message") ? resultJson.get("message").getAsString() : "装备失败";
            System.out.println("\n❌ " + errorMsg);
        }

        pressEnterToContinue();
    }

    /**
     * 从detail字符串中提取装备类型
     * detail格式: "武器 | 稀有 | 评分:300"
     */
    private static String extractEquipmentType(String detail) {
        try {
            if (detail.contains("|")) {
                String type = detail.substring(0, detail.indexOf("|")).trim();
                return type;
            }
        } catch (Exception e) {
            // 解析失败
        }
        return "";
    }

    /**
     * 验证装备类型与槽位是否匹配
     */
    private static boolean isSlotMatchEquipmentType(String slot, String equipmentType) {
        if (equipmentType == null || equipmentType.isEmpty()) {
            return false;
        }

        switch (slot) {
            case "戒指1":
            case "戒指2":
                return "戒指".equals(equipmentType);
            default:
                return slot.equals(equipmentType);
        }
    }

    /**
     * 卸下装备
     */
    private static void unequipItem() throws IOException, InterruptedException {
        System.out.println("\n--- 卸下装备 ---");

        // 1. 获取已装备的装备
        System.out.println("\n正在加载装备信息...");
        String response = ApiClient.get("/equipment/character/" + currentCharacterId);
        Type listType = new TypeToken<List<EquipmentInfo>>(){}.getType();

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 200) {
            if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
                JsonArray array = jsonObject.get("data").getAsJsonArray();
                List<EquipmentInfo> equipments = gson.fromJson(array, listType);

                // 过滤出已装备的物品
                java.util.List<EquipmentInfo> equippedItems = new java.util.ArrayList<>();
                for (EquipmentInfo e : equipments) {
                    if (e.isEquipped()) {
                        equippedItems.add(e);
                    }
                }

                if (equippedItems.isEmpty()) {
                    System.out.println("\n当前没有装备任何物品！");
                    pressEnterToContinue();
                    return;
                }

                // 2. 显示已装备列表
                System.out.println("\n当前已装备的物品:");
                System.out.println("┌──────┬──────────────────┬────────┬──────┬──────┐");
                System.out.println("│ 序号 │ 装备名称        │ 品质   │ 攻击 │ 防御 │");
                System.out.println("├──────┼──────────────────┼────────┼──────┼──────┤");

                for (int i = 0; i < equippedItems.size(); i++) {
                    EquipmentInfo e = equippedItems.get(i);
                    int index = i + 1;
                    String attack = e.getAttack() != null ? String.valueOf(e.getAttack()) : "-";
                    String defense = e.getDefense() != null ? String.valueOf(e.getDefense()) : "-";

                    System.out.printf("│ %4d │ %-16s │ %-6s │ %4s │ %4s │%n",
                            index, e.getEquipmentName(), e.getQuality(), attack, defense);
                }

                System.out.println("└──────┴──────────────────┴────────┴──────┴──────┘");

                // 3. 选择要卸下的装备
                System.out.print("\n请输入要卸下的装备序号 (直接回车返回): ");
                String indexStr = scanner.nextLine().trim();

                if (indexStr.isEmpty()) {
                    return;
                }

                try {
                    int index = Integer.parseInt(indexStr) - 1;
                    if (index < 0 || index >= equippedItems.size()) {
                        System.out.println("\n❌ 无效的序号！");
                        pressEnterToContinue();
                        return;
                    }

                    EquipmentInfo selected = equippedItems.get(index);
                    String slot = selected.getEquipmentSlot();
                    String name = selected.getEquipmentName();

                    // 4. 确认卸下
                    System.out.printf("\n确认卸下 %s (槽位: %s)？ (y/n): ", name, slot);
                    String confirm = scanner.nextLine().trim();

                    if (!confirm.equalsIgnoreCase("y")) {
                        System.out.println("\n已取消卸下。");
                        pressEnterToContinue();
                        return;
                    }

                    // 5. 调用卸下API
                    System.out.println("\n正在卸下装备...");
                    String queryParams = String.format("characterId=%d&equipmentSlot=%s",
                            currentCharacterId, slot);
                    String deleteResponse = ApiClient.delete("/equipment/unequip", queryParams);

                    JsonObject deleteJson = gson.fromJson(deleteResponse, JsonObject.class);
                    if (deleteJson.has("code") && deleteJson.get("code").getAsInt() == 200) {
                        System.out.println("\n✅ 卸下成功！");
                        System.out.printf("已将 %s 从 %s 槽位卸下%n", name, slot);
                    } else {
                        String errorMsg = deleteJson.has("message") ? deleteJson.get("message").getAsString() : "卸下失败";
                        System.out.println("\n❌ " + errorMsg);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("\n❌ 请输入有效的数字！");
                }
            } else {
                System.out.println("\n当前没有装备任何物品！");
            }
        } else {
            String errorMsg = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "加载失败";
            System.out.println("\n❌ " + errorMsg);
        }

        pressEnterToContinue();
    }

    /**
     * 查看装备加成
     */
    private static void showEquipmentBonus() throws IOException, InterruptedException {
        System.out.println("\n--- 装备加成 ---");

        String response = ApiClient.get("/equipment/bonus/" + currentCharacterId);

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 200) {
            if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
                JsonObject bonus = jsonObject.get("data").getAsJsonObject();

                int attackBonus = bonus.has("attackBonus") ? bonus.get("attackBonus").getAsInt() : 0;
                int defenseBonus = bonus.has("defenseBonus") ? bonus.get("defenseBonus").getAsInt() : 0;
                int healthBonus = bonus.has("healthBonus") ? bonus.get("healthBonus").getAsInt() : 0;
                int staminaBonus = bonus.has("staminaBonus") ? bonus.get("staminaBonus").getAsInt() : 0;
                int spiritualPowerBonus = bonus.has("spiritualPowerBonus") ? bonus.get("spiritualPowerBonus").getAsInt() : 0;
                int criticalRateBonus = bonus.has("criticalRateBonus") ? bonus.get("criticalRateBonus").getAsInt() : 0;
                int speedBonus = bonus.has("speedBonus") ? bonus.get("speedBonus").getAsInt() : 0;
                int physicalResistBonus = bonus.has("physicalResistBonus") ? bonus.get("physicalResistBonus").getAsInt() : 0;
                int iceResistBonus = bonus.has("iceResistBonus") ? bonus.get("iceResistBonus").getAsInt() : 0;
                int fireResistBonus = bonus.has("fireResistBonus") ? bonus.get("fireResistBonus").getAsInt() : 0;
                int lightningResistBonus = bonus.has("lightningResistBonus") ? bonus.get("lightningResistBonus").getAsInt() : 0;

                System.out.println("\n┌────────────────────────────────────────────────────────────────────┐");
                System.out.println("│                      装备总加成                                   │");
                System.out.println("├────────────────────────────────────────────────────────────────────┤");

                System.out.printf("│ 攻击力加成: %6d              暴击率加成: %6d                   │%n", attackBonus, criticalRateBonus);
                System.out.printf("│ 防御力加成: %6d              速度加成:   %6d                   │%n", defenseBonus, speedBonus);
                System.out.printf("│ 气血加成:   %6d                                                       │%n", healthBonus);
                System.out.printf("│ 体力加成:   %6d              灵力加成:   %6d                   │%n", staminaBonus, spiritualPowerBonus);

                System.out.println("├────────────────────────────────────────────────────────────────────┤");
                System.out.println("│                       抗性加成                                     │");
                System.out.println("├────────────────────────────────────────────────────────────────────┤");
                System.out.printf("│ 物理抗性: %6d              冰系抗性: %6d                   │%n", physicalResistBonus, iceResistBonus);
                System.out.printf("│ 火系抗性: %6d              电系抗性: %6d                   │%n", fireResistBonus, lightningResistBonus);

                System.out.println("└────────────────────────────────────────────────────────────────────┘");

                // 如果所有加成都是0，显示提示
                if (attackBonus == 0 && defenseBonus == 0 && healthBonus == 0
                        && staminaBonus == 0 && spiritualPowerBonus == 0
                        && criticalRateBonus == 0 && speedBonus == 0
                        && physicalResistBonus == 0 && iceResistBonus == 0
                        && fireResistBonus == 0 && lightningResistBonus == 0) {
                    System.out.println("\n💡 提示：当前未装备任何物品，或装备未提供属性加成");
                }
            }
        } else {
            String errorMsg = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "加载失败";
            System.out.println("\n❌ " + errorMsg);
        }

        pressEnterToContinue();
    }

    /**
     * 一键装备
     */
    private static void autoEquip() throws IOException, InterruptedException {
        System.out.println("\n--- 一键装备 ---");

        // 1. 选择优先属性
        System.out.println("\n请选择优先装备属性：");
        System.out.println("┌──────────────────────────────────────┐");
        System.out.println("│  1. 物理抗性                         │");
        System.out.println("│  2. 冰系抗性                         │");
        System.out.println("│  3. 火系抗性                         │");
        System.out.println("│  4. 雷系抗性                         │");
        System.out.println("│  0. 不指定（按基础评分）             │");
        System.out.println("└──────────────────────────────────────┘");
        System.out.print("\n请选择 (直接回车默认不指定): ");

        String attrChoice = scanner.nextLine().trim();
        String priorityAttribute = null;

        switch (attrChoice) {
            case "1":
                priorityAttribute = "physical";
                break;
            case "2":
                priorityAttribute = "ice";
                break;
            case "3":
                priorityAttribute = "fire";
                break;
            case "4":
                priorityAttribute = "lightning";
                break;
            case "0":
            case "":
                priorityAttribute = null;
                break;
            default:
                System.out.println("\n无效选择，将按基础评分装备");
                priorityAttribute = null;
        }

        // 2. 获取预览方案
        System.out.println("\n正在计算最优装备方案...");

        com.google.gson.JsonObject previewRequest = new com.google.gson.JsonObject();
        previewRequest.addProperty("characterId", currentCharacterId);
        if (priorityAttribute != null) {
            previewRequest.addProperty("priorityAttribute", priorityAttribute);
        }

        String previewResponse = ApiClient.post("/equipment/auto-equip/preview", previewRequest);
        com.google.gson.JsonObject previewJson = gson.fromJson(previewResponse, com.google.gson.JsonObject.class);

        if (previewJson.has("code") && previewJson.get("code").getAsInt() != 200) {
            String errorMsg = previewJson.has("message") ? previewJson.get("message").getAsString() : "获取预览失败";
            System.out.println("\n❌ " + errorMsg);
            pressEnterToContinue();
            return;
        }

        // 解析预览结果
        com.google.gson.JsonArray changesArray = previewJson.getAsJsonObject("data").getAsJsonArray("changes");

        if (changesArray.size() == 0) {
            System.out.println("\n✅ 当前装备已是最优配置，无需更换！");
            pressEnterToContinue();
            return;
        }

        // 3. 显示预览方案
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("                    装备更换预览                          ");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        java.util.List<com.xiuxian.client.model.EquipmentChangeInfo> changes = new java.util.ArrayList<>();
        for (int i = 0; i < changesArray.size(); i++) {
            com.google.gson.JsonObject changeObj = changesArray.get(i).getAsJsonObject();
            com.xiuxian.client.model.EquipmentChangeInfo change =
                gson.fromJson(changeObj, com.xiuxian.client.model.EquipmentChangeInfo.class);
            changes.add(change);
        }

        for (com.xiuxian.client.model.EquipmentChangeInfo change : changes) {
            System.out.printf("\n【%s】%s%n", change.getEquipmentSlot(), change.getReason());
            System.out.println("  旧装备: " + (change.getOldEquipment() != null ?
                String.format("%s (评分:%d)",
                    change.getOldEquipment().getEquipmentName(),
                    change.getOldEquipment().getBaseScore()) :
                "[空]"));
            System.out.println("  新装备: " + String.format("%s (评分:%d)",
                change.getNewEquipment().getEquipmentName(),
                change.getNewEquipment().getBaseScore()));
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("共将更换 %d 件装备%n", changes.size());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // 4. 用户确认
        System.out.print("\n是否确认装备？(y/n): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("y") && !confirm.equalsIgnoreCase("yes")) {
            System.out.println("\n已取消装备");
            pressEnterToContinue();
            return;
        }

        // 5. 执行装备
        System.out.println("\n正在执行装备...");

        String equipResponse = ApiClient.post("/equipment/auto-equip", previewRequest);
        com.google.gson.JsonObject equipJson = gson.fromJson(equipResponse, com.google.gson.JsonObject.class);

        if (equipJson.has("code") && equipJson.get("code").getAsInt() == 200) {
            String message = equipJson.getAsJsonObject("data").get("message").getAsString();
            System.out.println("\n✅ " + message);

            // 显示详细变更
            com.google.gson.JsonArray resultChanges = equipJson.getAsJsonObject("data")
                .getAsJsonArray("changes");
            if (resultChanges.size() > 0) {
                System.out.println("\n装备变更详情：");
                for (int i = 0; i < resultChanges.size(); i++) {
                    com.google.gson.JsonObject changeObj = resultChanges.get(i).getAsJsonObject();
                    String slot = changeObj.get("equipmentSlot").getAsString();
                    String reason = changeObj.get("reason").getAsString();
                    System.out.printf("  • %s: %s%n", slot, reason);
                }
            }
        } else {
            String errorMsg = equipJson.has("message") ? equipJson.get("message").getAsString() : "装备失败";
            System.out.println("\n❌ " + errorMsg);
        }

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
            System.out.print("\n体质加点 (输入0跳过，直接回车默认为0): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                constitutionPoints = 0;
                break;
            }
            try {
                constitutionPoints = Integer.parseInt(input);
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
            System.out.print("精神加点 (输入0跳过，直接回车默认为0): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                spiritPoints = 0;
                break;
            }
            try {
                spiritPoints = Integer.parseInt(input);
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
            System.out.print("悟性加点 (输入0跳过，直接回车默认为0): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                comprehensionPoints = 0;
                break;
            }
            try {
                comprehensionPoints = Integer.parseInt(input);
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
            System.out.print("机缘加点 (输入0跳过，直接回车默认为0): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                luckPoints = 0;
                break;
            }
            try {
                luckPoints = Integer.parseInt(input);
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
            System.out.print("气运加点 (输入0跳过，直接回车默认为0): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                fortunePoints = 0;
                break;
            }
            try {
                fortunePoints = Integer.parseInt(input);
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
            System.out.println("│  6. 💰 一键售出                       │");
            System.out.println("│  0. 返回主菜单                       │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.print("\n请选择 (直接回车返回主菜单): ");

            String choice = readMenuChoice();

            switch (choice) {
                case "1": showInventoryItems(null); break;
                case "2": showInventoryItems("equipment"); break;
                case "3": showInventoryItems("material"); break;
                case "4": showInventoryItems("pill"); break;
                case "5": showInventorySummary(); break;
                case "6": sellInventoryItem(); break;
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

                    // 根据物品类型显示不同的表头
                    boolean showEquipmentDetail = itemType == null || itemType.equals("equipment");
                    if (showEquipmentDetail) {
                        System.out.println("┌────┬──────────────────┬──────────────────────────────────────────────────────────────────────┬──────┐");
                        System.out.println("│ ID │ 物品名称          │ 详细信息                                                         │ 数量 │");
                        System.out.println("├────┼──────────────────┼──────────────────────────────────────────────────────────────────────┼──────┤");
                    } else {
                        System.out.println("────────────────────────────────────────────────────────────");
                        System.out.printf("%-4s  %-20s  %-20s  %-6s\n", "ID", "物品名称", "详细信息", "数量");
                        System.out.println("────────────────────────────────────────────────────────────");
                    }

                    for (int i = 0; i < array.size(); i++) {
                        JsonObject item = array.get(i).getAsJsonObject();
                        Long id = item.has("inventoryId") ? item.get("inventoryId").getAsLong() : 0L;
                        String type = item.has("itemType") ? item.get("itemType").getAsString() : "";
                        Integer quantity = item.has("quantity") ? item.get("quantity").getAsInt() : 0;

                        // 获取物品名称，优先级：itemName > skillName > equipmentName > 物品类型
                        String name = item.has("itemName") && !item.get("itemName").getAsString().isEmpty() ?
                                     item.get("itemName").getAsString() :
                                     "skill".equals(type) && item.has("skillName") ?
                                     item.get("skillName").getAsString() :
                                     "equipment".equals(type) && item.has("equipmentName") ?
                                     item.get("equipmentName").getAsString() :
                                     "pill".equals(type) && item.has("pillName") ?
                                     item.get("pillName").getAsString() :
                                     "material".equals(type) && item.has("materialName") ?
                                     item.get("materialName").getAsString() :
                                     item.has("refItemName") ? item.get("refItemName").getAsString() :
                                     "未知物品";

                        String detail;
                        if ("equipment".equals(type)) {
                            // 装备类型，显示详细属性
                            detail = formatEquipmentDetail(item);
                        } else if ("skill".equals(type)) {
                            // 技能类型，显示技能详细信息
                            detail = formatSkillDetail(item);
                        } else {
                            // 其他类型，显示简单信息
                            detail = item.has("itemDetail") ? item.get("itemDetail").getAsString() :
                                   item.has("description") ? item.get("description").getAsString() : type;
                        }

                        if (showEquipmentDetail) {
                            System.out.printf("│ %2d │ %-16s │ %-64s │ %4d │%n",
                                    (i + 1), name, detail, quantity);
                        } else {
                            System.out.printf("%-4d  %-20s  %-20s  %-6d\n",
                                    (i + 1), name, detail, quantity);
                        }
                    }

                    if (showEquipmentDetail) {
                        System.out.println("└────┴──────────────────┴──────────────────────────────────────────────────────────────────────┴──────┘");
                    } else {
                        System.out.println("────────────────────────────────────────────────────────────");
                    }
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
     * 计算物品出售单价
     */
    private static long calculateSellPrice(String itemType, String detail) {
        if (detail == null || detail.isEmpty()) {
            return 0L;
        }

        try {
            switch (itemType) {
                case "equipment":
                    // 装备: 评分 × 10
                    // detail格式: "武器 | 稀有 | 评分:300"
                    if (detail.contains("评分:")) {
                        int scoreIndex = detail.indexOf("评分:");
                        String scoreStr = detail.substring(scoreIndex + 3).trim();
                        // 提取数字，去除可能的非数字字符
                        scoreStr = scoreStr.replaceAll("[^0-9]", "");
                        if (!scoreStr.isEmpty()) {
                            int score = Integer.parseInt(scoreStr);
                            return score * 10L;
                        }
                    }
                    return 0L;

                case "material":
                    // 材料: 阶数 × 50
                    // detail格式: "3阶 | 普通"
                    if (detail.contains("阶")) {
                        String tierStr = detail.substring(0, detail.indexOf("阶")).trim();
                        int tier = Integer.parseInt(tierStr);
                        return tier * 50L;
                    }
                    return 0L;

                case "pill":
                    // 丹药: 阶数 × 80
                    // detail格式: "2阶 | 精良"
                    if (detail.contains("阶")) {
                        String tierStr = detail.substring(0, detail.indexOf("阶")).trim();
                        int tier = Integer.parseInt(tierStr);
                        return tier * 80L;
                    }
                    return 0L;

                default:
                    return 10L; // 默认价格
            }
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 读取菜单选择，如果输入为空则返回"0"（返回上级）
     */
    private static String readMenuChoice() {
        String choice = scanner.nextLine().trim();
        if (choice.isEmpty()) {
            return "0";  // 空输入视为返回上级
        }
        return choice;
    }

    /**
     * 根据职位等级获取职位名称
     * @param level 职位等级 (1-5)
     * @return 职位名称
     */
    private static String getPositionName(Integer level) {
        if (level == null) {
            return "不限";
        }
        switch (level) {
            case 1: return "弟子";
            case 2: return "内门弟子";
            case 3: return "核心弟子";
            case 4: return "长老";
            case 5: return "掌门";
            default: return "未知";
        }
    }

    /**
     * 售出背包物品
     */
    private static void sellInventoryItem() throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n--- 💰 一键售出 ---");
            System.out.println("1. 单件出售");
            System.out.println("2. 批量出售（按评分筛选）");
            System.out.println("0. 返回");
            System.out.print("\n请选择出售方式: ");

            String choice = readMenuChoice();
            if (choice.equals("0")) {
                return;
            }

            if (choice.equals("1")) {
                // 单件出售
                sellSingleItem();
            } else if (choice.equals("2")) {
                // 批量出售
                sellBatchItems();
            } else {
                System.out.println("\n❌ 无效的选择！");
                pressEnterToContinue();
            }
        }
    }

    /**
     * 从detail字符串中提取评分
     * detail格式: "武器 | 稀有 | 评分:300"
     */
    private static int extractScoreFromDetail(String detail) {
        try {
            if (detail.contains("评分:")) {
                int scoreIndex = detail.indexOf("评分:");
                String scoreStr = detail.substring(scoreIndex + 3).trim();
                // 提取数字部分
                scoreStr = scoreStr.replaceAll("[^0-9]", "");
                if (!scoreStr.isEmpty()) {
                    return Integer.parseInt(scoreStr);
                }
            }
        } catch (Exception e) {
            // 解析失败返回0
        }
        return 0;
    }

    /**
     * 单件出售
     */
    private static void sellSingleItem() throws IOException, InterruptedException {
        System.out.println("\n--- 单件出售 ---");
        System.out.println("💡 提示：当前仅支持出售装备");

        // 先显示背包物品
        System.out.println("\n正在加载背包物品...");
        String response = ApiClient.get("/inventory/character/" + currentCharacterId);

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 200) {
            if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
                JsonArray allItems = jsonObject.get("data").getAsJsonArray();

                // 过滤只显示装备
                java.util.List<JsonObject> equipmentItems = new java.util.ArrayList<>();
                for (int i = 0; i < allItems.size(); i++) {
                    JsonObject item = allItems.get(i).getAsJsonObject();
                    String itemType = item.has("itemType") ? item.get("itemType").getAsString() : "";
                    if ("equipment".equals(itemType)) {
                        equipmentItems.add(item);
                    }
                }

                if (equipmentItems.isEmpty()) {
                    System.out.println("\n背包中没有装备，无法出售！");
                    pressEnterToContinue();
                    return;
                }

                // 显示装备列表
                System.out.println("\n可出售装备列表:");
                System.out.println("┌──────┬──────────────────┬─────────────────────────────┬──────┬──────────┐");
                System.out.println("│ 序号 │ 物品名称        │ 详细信息                   │ 数量 │ 单价(灵石)│");
                System.out.println("├──────┼──────────────────┼─────────────────────────────┼──────┼──────────┤");

                for (int i = 0; i < equipmentItems.size(); i++) {
                    JsonObject item = equipmentItems.get(i).getAsJsonObject();
                    int index = i + 1;
                    String name = item.has("itemName") ? item.get("itemName").getAsString() : "未知";
                    String detail = item.has("itemDetail") ? item.get("itemDetail").getAsString() : "";
                    int quantity = item.has("quantity") ? item.get("quantity").getAsInt() : 1;
                    String itemType = item.has("itemType") ? item.get("itemType").getAsString() : "";

                    // 计算单价
                    long unitPrice = calculateSellPrice(itemType, detail);

                    // 截断过长的字符串
                    if (name.length() > 16) name = name.substring(0, 14) + "..";
                    if (detail.length() > 25) detail = detail.substring(0, 23) + "..";

                    System.out.printf("│ %4d │ %-16s │ %-25s │ %4d │ %8d │%n",
                            index, name, detail, quantity, unitPrice);
                }

                System.out.println("└──────┴──────────────────┴─────────────────────────────┴──────┴──────────┘");

                // 输入序号
                System.out.print("\n请输入要出售的装备序号 (直接回车返回): ");
                String indexStr = scanner.nextLine().trim();

                // 空输入直接返回
                if (indexStr.isEmpty()) {
                    return;
                }

                if (indexStr.equals("0")) {
                    return;
                }

                try {
                    int index = Integer.parseInt(indexStr);
                    if (index < 1 || index > equipmentItems.size()) {
                        System.out.println("\n❌ 无效的序号！");
                        pressEnterToContinue();
                        return;
                    }

                    JsonObject selectedItem = equipmentItems.get(index - 1).getAsJsonObject();
                    long inventoryId = selectedItem.get("inventoryId").getAsLong();
                    String itemName = selectedItem.get("itemName").getAsString();
                    int maxQuantity = selectedItem.has("quantity") ? selectedItem.get("quantity").getAsInt() : 1;

                    // 输入数量
                    System.out.printf("\n已选择: %s (拥有数量: %d)\n", itemName, maxQuantity);
                    System.out.print("请输入出售数量 (直接回车返回): ");
                    String quantityStr = scanner.nextLine().trim();

                    // 空输入直接返回
                    if (quantityStr.isEmpty()) {
                        return;
                    }

                    if (quantityStr.equals("0")) {
                        return;
                    }

                    int quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        System.out.println("\n❌ 出售数量必须大于0！");
                        pressEnterToContinue();
                        return;
                    }

                    if (quantity > maxQuantity) {
                        System.out.printf("\n❌ 出售数量超过拥有数量！(拥有: %d)\n", maxQuantity);
                        pressEnterToContinue();
                        return;
                    }

                    // 确认出售
                    System.out.printf("\n确认出售 %s x%d ？ (y/n): ", itemName, quantity);
                    String confirm = scanner.nextLine().trim();

                    if (!confirm.equalsIgnoreCase("y")) {
                        System.out.println("\n已取消出售。");
                        pressEnterToContinue();
                        return;
                    }

                    // 调用出售API
                    JsonObject sellRequest = new JsonObject();
                    sellRequest.addProperty("characterId", currentCharacterId);
                    sellRequest.addProperty("inventoryId", inventoryId);
                    sellRequest.addProperty("quantity", quantity);

                    System.out.println("\n正在出售...");
                    String sellResponse = ApiClient.post("/inventory/sell", sellRequest);

                    JsonObject sellJson = gson.fromJson(sellResponse, JsonObject.class);
                    if (sellJson.has("code") && sellJson.get("code").getAsInt() == 200) {
                        if (sellJson.has("data")) {
                            JsonObject data = sellJson.get("data").getAsJsonObject();
                            long totalStones = data.has("totalSpiritStones") ? data.get("totalSpiritStones").getAsLong() : 0;
                            long remainingStones = data.has("remainingSpiritStones") ? data.get("remainingSpiritStones").getAsLong() : 0;
                            String message = data.has("message") ? data.get("message").getAsString() : "出售成功";

                            System.out.println("\n✅ " + message);
                            System.out.printf("获得灵石: %d | 当前灵石: %d%n", totalStones, remainingStones);
                        }
                    } else {
                        String errorMsg = sellJson.has("message") ? sellJson.get("message").getAsString() : "出售失败";
                        System.out.println("\n❌ " + errorMsg);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("\n❌ 请输入有效的数字！");
                }

            } else {
                System.out.println("\n背包为空！");
            }
        } else {
            String errorMsg = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "加载失败";
            System.out.println("\n❌ " + errorMsg);
        }

        pressEnterToContinue();
    }

    /**
     * 批量出售（按评分筛选）
     */
    private static void sellBatchItems() throws IOException, InterruptedException {
        System.out.println("\n--- 批量出售 ---");
        System.out.println("💡 提示：将出售评分低于指定值的所有装备");

        // 输入评分阈值
        System.out.print("\n请输入评分阈值（低于此评分的装备将被出售）: ");
        String thresholdStr = scanner.nextLine().trim();

        if (thresholdStr.isEmpty()) {
            System.out.println("\n已取消批量出售。");
            return;
        }

        int threshold;
        try {
            threshold = Integer.parseInt(thresholdStr);
            if (threshold < 0) {
                System.out.println("\n❌ 评分不能为负数！");
                pressEnterToContinue();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ 请输入有效的数字！");
            pressEnterToContinue();
            return;
        }

        // 加载背包物品
        System.out.println("\n正在加载背包物品...");
        String response = ApiClient.get("/inventory/character/" + currentCharacterId);

        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 200) {
            if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
                JsonArray allItems = jsonObject.get("data").getAsJsonArray();

                // 过滤装备并按评分筛选
                java.util.List<JsonObject> itemsToSell = new java.util.ArrayList<>();
                for (int i = 0; i < allItems.size(); i++) {
                    JsonObject item = allItems.get(i).getAsJsonObject();
                    String itemType = item.has("itemType") ? item.get("itemType").getAsString() : "";
                    if ("equipment".equals(itemType)) {
                        String detail = item.has("itemDetail") ? item.get("itemDetail").getAsString() : "";
                        int score = extractScoreFromDetail(detail);
                        if (score < threshold) {
                            itemsToSell.add(item);
                        }
                    }
                }

                if (itemsToSell.isEmpty()) {
                    System.out.printf("\n没有找到评分低于 %d 的装备！%n", threshold);
                    pressEnterToContinue();
                    return;
                }

                // 显示待出售装备列表
                System.out.printf("\n找到 %d 件评分低于 %d 的装备:%n", itemsToSell.size(), threshold);
                System.out.println("┌──────┬──────────────────┬─────────────────────────────┬──────┬──────────┐");
                System.out.println("│ 序号 │ 物品名称        │ 详细信息                   │ 数量 │ 单价(灵石)│");
                System.out.println("├──────┼──────────────────┼─────────────────────────────┼──────┼──────────┤");

                long totalStones = 0;
                for (int i = 0; i < itemsToSell.size(); i++) {
                    JsonObject item = itemsToSell.get(i).getAsJsonObject();
                    int index = i + 1;
                    String name = item.has("itemName") ? item.get("itemName").getAsString() : "未知";
                    String detail = item.has("itemDetail") ? item.get("itemDetail").getAsString() : "";
                    int quantity = item.has("quantity") ? item.get("quantity").getAsInt() : 1;
                    String itemType = item.has("itemType") ? item.get("itemType").getAsString() : "";

                    // 计算单价
                    long unitPrice = calculateSellPrice(itemType, detail);
                    long itemTotal = unitPrice * quantity;
                    totalStones += itemTotal;

                    // 截断过长的字符串
                    String displayName = name.length() > 16 ? name.substring(0, 14) + ".." : name;
                    String displayDetail = detail.length() > 25 ? detail.substring(0, 23) + ".." : detail;

                    System.out.printf("│ %4d │ %-16s │ %-25s │ %4d │ %8d │%n",
                            index, displayName, displayDetail, quantity, unitPrice);
                }

                System.out.println("└──────┴──────────────────┴─────────────────────────────┴──────┴──────────┘");
                System.out.printf("\n总计将获得: %d 灵石%n", totalStones);

                // 确认批量出售
                System.out.printf("\n确认批量出售以上 %d 件装备？ (y/n): ", itemsToSell.size());
                String confirm = scanner.nextLine().trim();

                if (!confirm.equalsIgnoreCase("y")) {
                    System.out.println("\n已取消批量出售。");
                    pressEnterToContinue();
                    return;
                }

                // 批量出售
                System.out.println("\n正在批量出售...");
                int successCount = 0;
                int failCount = 0;
                long actualStones = 0;

                for (JsonObject item : itemsToSell) {
                    long inventoryId = item.get("inventoryId").getAsLong();
                    int quantity = item.has("quantity") ? item.get("quantity").getAsInt() : 1;

                    JsonObject sellRequest = new JsonObject();
                    sellRequest.addProperty("characterId", currentCharacterId);
                    sellRequest.addProperty("inventoryId", inventoryId);
                    sellRequest.addProperty("quantity", quantity);

                    try {
                        String sellResponse = ApiClient.post("/inventory/sell", sellRequest);
                        JsonObject sellJson = gson.fromJson(sellResponse, JsonObject.class);
                        if (sellJson.has("code") && sellJson.get("code").getAsInt() == 200) {
                            if (sellJson.has("data")) {
                                JsonObject data = sellJson.get("data").getAsJsonObject();
                                long stones = data.has("totalSpiritStones") ? data.get("totalSpiritStones").getAsLong() : 0;
                                actualStones += stones;
                            }
                            successCount++;
                        } else {
                            failCount++;
                        }
                    } catch (Exception e) {
                        failCount++;
                    }
                }

                // 显示结果
                System.out.println("\n✅ 批量出售完成！");
                System.out.printf("成功: %d 件 | 失败: %d 件%n", successCount, failCount);
                System.out.printf("实际获得灵石: %d%n", actualStones);

            } else {
                System.out.println("\n背包为空！");
            }
        } else {
            String errorMsg = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "加载失败";
            System.out.println("\n❌ " + errorMsg);
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

    /**
     * 格式化装备详细属性为字符串
     */
    private static String formatEquipmentDetail(JsonObject item) {
        StringBuilder sb = new StringBuilder();

        // 基础信息
        String type = item.has("equipmentType") ? item.get("equipmentType").getAsString() :
                      item.has("itemDetail") && item.get("itemDetail").getAsString().contains("|") ?
                      item.get("itemDetail").getAsString().split("\\|")[0].trim() : "";
        String quality = item.has("quality") ? item.get("quality").getAsString() :
                        item.has("itemDetail") && item.get("itemDetail").getAsString().contains("|") ?
                        item.get("itemDetail").getAsString().split("\\|")[1].trim() : "";
        int baseScore = item.has("baseScore") && !item.get("baseScore").isJsonNull() ?
                       item.get("baseScore").getAsInt() : 0;

        // 检查是否有装备属性字段（新格式）
        boolean hasDetailedFields = item.has("attackPower") || item.has("defensePower");

        if (hasDetailedFields) {
            // 新格式：显示详细属性
            int attack = item.has("attackPower") && !item.get("attackPower").isJsonNull() ?
                        item.get("attackPower").getAsInt() : 0;
            int defense = item.has("defensePower") && !item.get("defensePower").isJsonNull() ?
                         item.get("defensePower").getAsInt() : 0;
            int health = item.has("healthBonus") && !item.get("healthBonus").isJsonNull() ?
                        item.get("healthBonus").getAsInt() : 0;
            int crit = item.has("criticalRate") && !item.get("criticalRate").isJsonNull() ?
                      item.get("criticalRate").getAsInt() : 0;
            int speed = item.has("speedBonus") && !item.get("speedBonus").isJsonNull() ?
                        item.get("speedBonus").getAsInt() : 0;

            sb.append(type).append("|").append(quality).append("|").append("评分:").append(baseScore).append(" ");

            // 构建属性字符串
            java.util.List<String> attrs = new java.util.ArrayList<>();
            if (attack > 0) attrs.add("攻" + attack);
            if (defense > 0) attrs.add("防" + defense);
            if (health > 0) attrs.add("血" + health);
            if (crit > 0) attrs.add("暴" + crit);
            if (speed > 0) attrs.add("速" + speed);

            if (attrs.isEmpty()) {
                sb.append("(无属性)");
            } else {
                sb.append(String.join(" ", attrs));
            }
        } else {
            // 旧格式：显示原始 itemDetail
            sb.append(item.has("itemDetail") ? item.get("itemDetail").getAsString() : "");
        }

        return sb.toString();
    }

    /**
     * 格式化技能物品详细信息
     */
    private static String formatSkillDetail(JsonObject item) {
        StringBuilder sb = new StringBuilder();

        // 技能名称（如果有）
        String skillName = item.has("skillName") ? item.get("skillName").getAsString() : "";

        // 技能类型
        String skillType = item.has("skillType") ? item.get("skillType").getAsString() : "";

        // 基础伤害
        int baseDamage = item.has("baseDamage") && !item.get("baseDamage").isJsonNull() ?
                       item.get("baseDamage").getAsInt() : 0;

        // 灵力消耗
        int spiritualCost = item.has("spiritualCost") && !item.get("spiritualCost").isJsonNull() ?
                          item.get("spiritualCost").getAsInt() : 0;

        // 元素类型
        String element = item.has("elementType") ? item.get("elementType").getAsString() : "";

        // 构建技能描述
        if (!skillType.isEmpty()) {
            sb.append(skillType);
            if (!element.isEmpty()) {
                sb.append("(").append(element).append(")");
            }
        }

        if (baseDamage > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append("伤害:").append(baseDamage);
        }

        if (spiritualCost > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append("灵力:").append(spiritualCost);
        }

        // 如果没有任何信息，返回默认描述
        if (sb.length() == 0) {
            return "技能秘籍";
        }

        return sb.toString();
    }

    // ==================== 会话管理 ====================

    /**
     * 保存当前登录会话到文件
     */
    private static void saveSession() {
        if (currentCharacterId == null || currentCharacter == null) {
            System.err.println("[系统] 保存会话失败: 角色信息为空");
            return;
        }

        try {
            System.out.println("[系统] 正在保存会话...");
            System.out.println("[系统] 会话文件路径: " + SESSION_FILE);

            JsonObject sessionData = new JsonObject();
            sessionData.addProperty("characterId", currentCharacterId);
            sessionData.addProperty("characterName", currentCharacter.getPlayerName());
            sessionData.addProperty("savedAt", java.time.LocalDateTime.now().toString());

            File sessionFile = new File(SESSION_FILE);
            // 确保父目录存在
            File parentDir = sessionFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                System.out.println("[系统] 创建父目录: " + parentDir.getAbsolutePath());
            }

            try (FileWriter writer = new FileWriter(sessionFile)) {
                gson.toJson(sessionData, writer);
            }

            System.out.println("[系统] ✅ 会话已保存到: " + sessionFile.getAbsolutePath());
            System.out.println("[系统] 下次启动将自动登录");
        } catch (IOException e) {
            System.err.println("[系统] ❌ 保存会话失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 加载保存的会话并尝试自动登录
     * @return true表示成功加载并自动登录，false表示没有保存的会话或加载失败
     */
    private static boolean loadSavedSession() {
        File sessionFile = new File(SESSION_FILE);
        if (!sessionFile.exists()) {
            System.out.println("[系统] 未找到保存的会话文件: " + SESSION_FILE);
            return false;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(SESSION_FILE)));
            JsonObject sessionData = gson.fromJson(content, JsonObject.class);

            if (sessionData != null && sessionData.has("characterId")) {
                Long savedCharacterId = sessionData.get("characterId").getAsLong();
                String characterName = sessionData.get("characterName").getAsString();

                System.out.println("[系统] 发现保存的会话: " + characterName + " (ID: " + savedCharacterId + ")");
                System.out.println("[系统] 正在验证会话...");

                // 尝试从服务器获取角色信息
                String response = ApiClient.get("/characters/" + savedCharacterId);
                CharacterResponse character = ApiClient.parseResponse(response, CharacterResponse.class);

                if (character != null) {
                    currentCharacterId = character.getCharacterId();
                    currentCharacter = character;
                    System.out.println("\n[系统] ✅ 自动登录成功！欢迎回来，" + characterName + "！");
                    pressEnterToContinue();
                    return true;
                } else {
                    System.out.println("\n[系统] ⚠️ 保存的会话已失效（角色不存在），已清除旧会话");
                    clearSession();
                    pressEnterToContinue();
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("[系统] 加载会话失败: " + e.getMessage());
            // 如果加载失败，清除损坏的会话文件
            clearSession();
        }
        return false;
    }

    /**
     * 清除保存的会话文件
     */
    private static void clearSession() {
        File sessionFile = new File(SESSION_FILE);
        if (sessionFile.exists()) {
            if (sessionFile.delete()) {
                System.out.println("[系统] 会话已清除");
            } else {
                System.err.println("[系统] 清除会话失败");
            }
        }
    }
}
