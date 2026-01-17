/**
 * 境界相关类型定义
 */

export interface Realm {
  id: number;
  realmName: string;
  realmLevel: number;
  subLevels: number;
  requiredExp: number;
  breakthroughRate: number;
  hpBonus: number;
  spBonus: number;
  staminaBonus: number;
  attackBonus: number;
  defenseBonus: number;
  lifespanBonus: number;
  levelUpPoints: number;
  realmStage: string;
  description?: string;
}
