package ruina.multiplayer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.act1.AllAroundHelper;
import ruina.monsters.act2.QueenOfHate;
import ruina.monsters.act2.knight.Sword;
import ruina.monsters.act3.punishingBird.PunishingBird;
import ruina.monsters.uninvitedGuests.normal.clown.Tiph;
import ruina.monsters.uninvitedGuests.normal.eileen.Yesod;
import ruina.monsters.uninvitedGuests.normal.elena.Binah;
import ruina.monsters.uninvitedGuests.normal.greta.Hod;
import ruina.powers.act1.Pattern;
import ruina.powers.act2.Hysteria;
import ruina.powers.act2.Worthless;
import ruina.powers.act3.PunishingBirdPunishmentPower;
import ruina.powers.act4.DarkBargain;
import ruina.powers.act4.PurpleTearStance;
import spireTogether.networkcore.objects.entities.NetworkIntent;
import spireTogether.networkcore.objects.entities.NetworkMonster;
import spireTogether.networkcore.objects.rooms.NetworkLocation;
import spireTogether.other.RoomDataManager;
import spireTogether.subscribers.TiSNetworkMessageSubscriber;
import spireTogether.util.NetworkMessage;
import spireTogether.util.SpireHelp;

import java.util.ArrayList;

public class MessengerListener implements TiSNetworkMessageSubscriber {

    public static String request_helperClearedDebuffs = "ruina_helperClearedDebuffs";
    public static String request_swordCommittedSuicide = "ruina_swordCommittedSuicide";
    public static String request_queenTriggerHysteria = "ruina_queenTriggerHysteria";
    public static String request_punishingBirdMad = "ruina_punishingBirdMad";
    public static String request_yesodGainedDamage = "ruina_yesodGainedDamage";
    public static String request_playerDamageTiph = "ruina_playerDamageTiph";
    public static String request_binahChangeTarget = "ruina_binahChangeTarget";
    @Override
    public void onMessageReceive(NetworkMessage networkMessage, String s, Object o, Integer integer) {
        if (networkMessage.request.equals(NetworkRuinaMonster.request_monsterUpdateMainIntent)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            NetworkIntent networkMove = (NetworkIntent)dataIn[0];
            ArrayList<Byte> moveHistory = (ArrayList<Byte>)dataIn[1];
            String monsterID = (String)dataIn[2];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[3];
            NetworkRuinaMonster mo = (NetworkRuinaMonster) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.networkMove = networkMove;
                mo.moveHistory = moveHistory;
            }
        }
        if (networkMessage.request.equals(NetworkMultiIntentMonster.request_monsterUpdateAdditionalIntents)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            ArrayList<NetworkIntent> additionalMoves = (ArrayList<NetworkIntent>)dataIn[0];
            ArrayList<ArrayList<Byte>> additionalMovesHistory= (ArrayList<ArrayList<Byte>>)dataIn[1];
            String monsterID = (String)dataIn[2];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[3];
            NetworkMultiIntentMonster mo = (NetworkMultiIntentMonster) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.additionalMoves = additionalMoves;
                mo.additionalMovesHistory = additionalMovesHistory;
            }
        }
        if (networkMessage.request.equals(NetworkRuinaMonster.request_monsterUpdateFirstMove)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            boolean firstMove = (boolean)dataIn[0];
            String monsterID = (String)dataIn[1];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[2];
            NetworkRuinaMonster mo = (NetworkRuinaMonster) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.firstMove = firstMove;
            }
        }
        if (networkMessage.request.equals(NetworkRuinaMonster.request_monsterUpdatePhase)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            int phase = (int)dataIn[0];
            String monsterID = (String)dataIn[1];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[2];
            NetworkRuinaMonster mo = (NetworkRuinaMonster) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.phase = phase;
            }
        }
        if (networkMessage.request.equals(NetworkRuinaMonster.request_monsterUpdateAttackingAlly)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            boolean attackingAlly = (boolean)dataIn[0];
            String monsterID = (String)dataIn[1];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[2];
            NetworkRuinaMonster mo = (NetworkRuinaMonster) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.attackingAlly = attackingAlly;
            }
        }
        if (networkMessage.request.equals(request_helperClearedDebuffs)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            String monsterID = (String)dataIn[0];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[1];
            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof AllAroundHelper && SpireHelp.Gameplay.CreatureToUID(m).equals(monsterID)) {
                        if (m.hasPower(Pattern.POWER_ID)) {
                            m.getPower(Pattern.POWER_ID).onSpecificTrigger();
                        }
                    }
                }
            }
        }
        if (networkMessage.request.equals(request_swordCommittedSuicide)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            String monsterID = (String)dataIn[0];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[1];
            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof Sword && SpireHelp.Gameplay.CreatureToUID(m).equals(monsterID)) {
                        if (m.hasPower(Worthless.POWER_ID)) {
                            m.getPower(Worthless.POWER_ID).onSpecificTrigger();
                        }
                    }
                }
            }
        }
        if (networkMessage.request.equals(request_queenTriggerHysteria)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            String monsterID = (String)dataIn[0];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[1];
            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof QueenOfHate && SpireHelp.Gameplay.CreatureToUID(m).equals(monsterID)) {
                        if (m.hasPower(Hysteria.POWER_ID)) {
                            m.getPower(Hysteria.POWER_ID).onSpecificTrigger();
                        }
                    }
                }
            }
        }
        if (networkMessage.request.equals(request_punishingBirdMad)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            String monsterID = (String)dataIn[0];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[1];
            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof PunishingBird && SpireHelp.Gameplay.CreatureToUID(m).equals(monsterID)) {
                        if (m.hasPower(PunishingBirdPunishmentPower.POWER_ID)) {
                            m.getPower(PunishingBirdPunishmentPower.POWER_ID).onSpecificTrigger();
                        }
                    }
                }
            }
        }
        if (networkMessage.request.equals(NetworkTwilight.request_updateTwilight)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            int dmgTaken = (int)dataIn[0];
            boolean bigEggBroken = (boolean)dataIn[1];
            boolean smallEggBroken = (boolean)dataIn[2];
            boolean longEggBroken = (boolean)dataIn[3];
            String monsterID = (String)dataIn[4];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[5];
            NetworkTwilight mo = (NetworkTwilight) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.dmgTaken = dmgTaken;
                mo.bigEggBroken = bigEggBroken;
                mo.smallEggBroken = smallEggBroken;
                mo.longEggBroken = longEggBroken;
            }
        }
        if (networkMessage.request.equals(request_yesodGainedDamage)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            String monsterID = (String)dataIn[0];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[1];
            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof Yesod && SpireHelp.Gameplay.CreatureToUID(m).equals(monsterID)) {
                        if (m.hasPower(DarkBargain.POWER_ID)) {
                            m.getPower(DarkBargain.POWER_ID).onSpecificTrigger();
                        }
                    }
                }
            }
        }
        if (networkMessage.request.equals(NetworkYesod.request_updateYesod)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            float currentDamageBonus = (float)dataIn[0];
            String monsterID = (String)dataIn[1];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[2];
            NetworkYesod mo = (NetworkYesod) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.currentDamageBonus = currentDamageBonus;
            }
        }
        if (networkMessage.request.equals(NetworkHod.request_updateHod)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            int stance = (int)dataIn[0];
            byte slashMove = (byte)dataIn[1];
            byte pierceMove = (byte)dataIn[2];
            byte guardMove = (byte)dataIn[3];
            String monsterID = (String)dataIn[4];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[5];
            NetworkHod mo = (NetworkHod) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.stance = stance;
                mo.slashMove = slashMove;
                mo.pierceMove = pierceMove;
                mo.guardMove = guardMove;
            }
        }
        if (networkMessage.request.equals(NetworkHod.request_changeStanceHod)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            int stance = (int)dataIn[0];
            String monsterID = (String)dataIn[1];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[2];
            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof Hod && SpireHelp.Gameplay.CreatureToUID(m).equals(monsterID)) {
                        ((Hod) m).changeStance(stance);
                    }
                }
            }
        }
        if (networkMessage.request.equals(request_playerDamageTiph)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            int newCurrHp = (int)dataIn[0];
            int newCurrBlock = (int)dataIn[1];
            String monsterID = (String)dataIn[2];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[3];
            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof Tiph && SpireHelp.Gameplay.CreatureToUID(m).equals(monsterID)) {
                        if (m.halfDead && !((Tiph) m).isTargetableByPlayer) {
                            m.currentBlock = newCurrBlock;
                            m.currentHealth = newCurrHp;
                            m.healthBarUpdatedEvent();

                            NetworkMonster mo = RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
                            if (mo != null) {
                                mo.HP = newCurrHp;
                                mo.block = newCurrBlock;
                            }
                        }
                    }
                }
            }
        }
        if (networkMessage.request.equals(request_binahChangeTarget)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            String targetID = (String)dataIn[0];
            String monsterID = (String)dataIn[1];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[2];
            Binah binah = null;
            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof Binah && SpireHelp.Gameplay.CreatureToUID(m).equals(monsterID)) {
                        binah = (Binah) m;
                        break;
                    }
                }
                if (binah != null) {
                    for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                        if (m instanceof AbstractRuinaMonster && SpireHelp.Gameplay.CreatureToUID(m).equals(targetID)) {
                            binah.target = (AbstractRuinaMonster) m;
                            AbstractDungeon.onModifyPower();
                            break;
                        }
                    }
                }
            }
        }
    }
}
