package ruina.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.RunicDome;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.act3.bigBird.BigBird;
import ruina.multiplayer.NetworkMultiIntentMonster;
import ruina.powers.multiplayer.MultiplayerEnemyBuff;
import ruina.util.AdditionalIntent;
import ruina.util.DetailedIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import spireTogether.networkcore.P2P.P2PManager;
import spireTogether.networkcore.objects.entities.NetworkIntent;
import spireTogether.networkcore.objects.entities.NetworkMonster;
import spireTogether.other.RoomDataManager;
import spireTogether.util.SpireHelp;

import java.util.ArrayList;
import java.util.Map;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;
import static spireTogether.patches.monster.MonsterIntentPatch.pauseIntentSync;

public abstract class AbstractMultiIntentMonster extends AbstractRuinaMonster {
    public ArrayList<EnemyMoveInfo> additionalMoves = new ArrayList<>();
    public ArrayList<ArrayList<Byte>> additionalMovesHistory = new ArrayList<>();
    public ArrayList<AdditionalIntent> additionalIntents = new ArrayList<>();
    protected int numAdditionalMoves = 0;
    protected int maxAdditionalMoves = 0;
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MultiIntentStrings"));
    protected static final String[] TEXT = uiStrings.TEXT;
    public boolean attackingMonsterWithPrimaryIntent;

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractMultiIntentMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        for (AdditionalIntent additionalIntent : this.additionalIntents) {
            //that way they don't fade out after the monster takes its primary intent
            additionalIntent.usePrimaryIntentsColor = false;
        }
        if (attackingMonsterWithPrimaryIntent) {
            takeCustomTurn(this.moves.get(nextMove), target, -1);
        } else {
            takeCustomTurn(this.moves.get(nextMove), adp(), -1);
        }
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            if (!this.halfDead) {
                String moveName;
                if (additionalIntent.enemyCard != null) {
                    moveName = additionalIntent.enemyCard.name;
                } else {
                    moveName = MOVES[additionalMove.nextMove];
                }
                atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, moveName)));
                atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            }
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp(), i);
            } else {
                takeCustomTurn(additionalMove, additionalIntent.target, i);
            }
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
    }

    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        multiplier = move.multiplier;
        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        if (RuinaMod.isMultiplayerConnected() && hasPower(MultiplayerEnemyBuff.POWER_ID) && target instanceof AbstractMonster && !(target instanceof AbstractAllyMonster)) {
            int amount = getPower(MultiplayerEnemyBuff.POWER_ID).amount;
            info.output = (int)(info.output * (1.0f + ((float)amount / 100)));
        }
    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    protected void applyPowersToAdditionalIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, AbstractCreature target, Texture targetTexture, int whichMove) {
        boolean justDied = false;
        if (target instanceof AbstractRuinaMonster) {
            justDied = ((AbstractRuinaMonster) target).justDiedThisTurn;
        }
        if (additionalMove.nextMove == -1 || ((target.isDead || target.isDying) && !justDied)) {
            target = adp();
        }
        if (additionalMove.baseDamage > -1) {
            int dmg = additionalMove.baseDamage;
            DamageInfo info = new DamageInfo(this, dmg);
            info.applyPowers(this, target);

            dmg = info.output;
            dmg = applySpecialMultiplier(additionalMove, additionalIntent, target, whichMove, dmg);

            additionalIntent.updateDamage(dmg);
            if (target != adp()) {
                PowerTip intentTip = additionalIntent.intentTip;
                if (additionalIntent.numHits > 0) {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + additionalIntent.damage + TEXT[3] + additionalIntent.numHits + TEXT[4];
                } else {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + additionalIntent.damage + TEXT[2];
                }
                if (targetTexture != null) {
                    additionalIntent.setTargetTexture(targetTexture, target);
                }
            } else {
                additionalIntent.clearTargetTexture();
            }
        } else {
            if (additionalIntent.intent == Intent.DEBUFF || additionalIntent.intent == Intent.STRONG_DEBUFF || additionalIntent.intent == Intent.DEFEND_DEBUFF) {
                if (target != adp()) {
                    PowerTip intentTip = additionalIntent.intentTip;
                    intentTip.body = TEXT[5] + FontHelper.colorString(target.name, "y") + TEXT[6];
                    if (targetTexture != null) {
                        additionalIntent.setTargetTexture(targetTexture, target);
                    }
                } else {
                    additionalIntent.clearTargetTexture();
                }
            }
        }
    }

    protected int applySpecialMultiplier(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, AbstractCreature target, int whichMove, int dmg) {
        if (RuinaMod.isMultiplayerConnected() && hasPower(MultiplayerEnemyBuff.POWER_ID) && target instanceof AbstractMonster) {
            int amount = getPower(MultiplayerEnemyBuff.POWER_ID).amount;
            return (int)(dmg * (1.0f + ((float)amount / 100)));
        }
        return dmg;
    }

    @Override
    public void rollMove() {
        additionalIntents.clear();
        additionalMoves.clear();
        Random seedToUse;
        if (RuinaMod.isMultiplayerConnected()) {
            seedToUse = generateMultiplayerRandom();
        } else {
            seedToUse = AbstractDungeon.aiRng;
        }
        this.getMove(seedToUse.random(99));
        for (int i = 0; i < numAdditionalMoves; i++) {
            getAdditionalMoves(seedToUse.random(99), i);
        }
        notifyMainIntentUpdateMultiplayer();
        notifyAdditionalIntentUpdateMultiplayer();
    }
    public void notifyAdditionalIntentUpdateMultiplayer() {
        if (RuinaMod.isMultiplayerConnected() && !pauseIntentSync.get() && P2PManager.GetSelf().isGameStatusInCombat()) {
            ArrayList<NetworkIntent> additionalNetworkMoves = new ArrayList<>();
            for (EnemyMoveInfo info : this.additionalMoves) {
                additionalNetworkMoves.add(new NetworkIntent(info));
            }
            P2PManager.SendData(NetworkMultiIntentMonster.request_monsterUpdateAdditionalIntents, additionalNetworkMoves, this.additionalMovesHistory, SpireHelp.Gameplay.CreatureToUID(this), SpireHelp.Gameplay.GetMapLocation());
            NetworkMonster m = RoomDataManager.GetMonsterForCurrentRoom(this);
            if (m instanceof NetworkMultiIntentMonster) {
                ((NetworkMultiIntentMonster)m).additionalMoves = additionalNetworkMoves;
                ((NetworkMultiIntentMonster)m).additionalMovesHistory = this.additionalMovesHistory;
            }
        }
    }

    public void getAdditionalMoves(int num, int whichMove) {

    }

    public void setAdditionalMoveShortcut(byte next, ArrayList<Byte> moveHistory) {
        EnemyMoveInfo info = this.moves.get(next);
        AdditionalIntent additionalIntent = new AdditionalIntent(this, info);
        additionalIntents.add(additionalIntent);
        additionalMoves.add(info);
        moveHistory.add(next);
    }

    protected boolean lastMove(byte move, ArrayList<Byte> moveHistory) {
        if (moveHistory.isEmpty()) {
            return false;
        } else {
            return moveHistory.get(moveHistory.size() - 1) == move;
        }
    }

    protected boolean lastMoveBefore(byte move, ArrayList<Byte> moveHistory) {
        if (moveHistory.isEmpty()) {
            return false;
        } else if (moveHistory.size() < 2) {
            return false;
        } else {
            return moveHistory.get(moveHistory.size() - 2) == move;
        }
    }

    protected boolean lastTwoMoves(byte move, ArrayList<Byte> moveHistory) {
        if (moveHistory.size() < 2) {
            return false;
        } else {
            return moveHistory.get(moveHistory.size() - 1) == move && (Byte)moveHistory.get(moveHistory.size() - 2) == move;
        }
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        int position = 1;
        for (AdditionalIntent additionalIntent : additionalIntents) {
            if (additionalIntent != null && !this.hasPower(StunMonsterPower.POWER_ID)) {
                additionalIntent.update(position);
                if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic(RunicDome.ID) && this.intent != AbstractMonster.Intent.NONE && !Settings.hideCombatElements) {
                    additionalIntent.render(sb, position);
                }
            }
            position++;
        }
        if (attackingMonsterWithPrimaryIntent) {
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(target.icon, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        if (!adp().hasRelic(RunicDome.ID) && !this.hasPower(StunMonsterPower.POWER_ID)) {
            if (tips.size() > 0) {
                for (int i = 0; i < additionalIntents.size(); i++) {
                    AdditionalIntent additionalIntent = additionalIntents.get(i);
                    if (!additionalIntent.transparent) {
                        this.tips.add(i + 1, additionalIntent.intentTip);
                    }
                }
            }
        }
    }

    //returns the highest damaging intent for compatibility with spot weakness/go for the eyes, etc.
    @Override
    public int getIntentBaseDmg() {
        int original = super.getIntentBaseDmg();
        if (original >= 0) {
            return original;
        }
        int maxDamage = original;
        for (AdditionalIntent additionalIntent : this.additionalIntents) {
            if (additionalIntent.baseDamage > maxDamage) {
                maxDamage = additionalIntent.baseDamage;
            }
        }
        return maxDamage;
    }

    public int getRealIntentBaseDmg() {
        return super.getIntentBaseDmg();
    }

    //returns the highest damaging intent for compatibility with spot weakness/go for the eyes, etc.
    @Override
    public int getIntentDmg() {
        int original = super.getIntentDmg();
        if (original >= 0) {
            return original;
        }
        int maxDamage = original;
        for (AdditionalIntent additionalIntent : this.additionalIntents) {
            if (additionalIntent.damage > maxDamage) {
                maxDamage = additionalIntent.damage;
            }
        }
        if (maxDamage == BigBird.INSTANT_KILL_NUM && this instanceof BigBird) {
            //stop cards that deal damage based on intent from trivializing this fight
            return 16;
        }
        return maxDamage;
    }

    public int getRealIntentDmg() {
        return super.getIntentDmg();
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        if (attackingMonsterWithPrimaryIntent && this.nextMove != -1 && target != null) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            if (info.base > -1) {
                info.applyPowers(this, target);
                if (RuinaMod.isMultiplayerConnected() && hasPower(MultiplayerEnemyBuff.POWER_ID)) {
                    int amount = getPower(MultiplayerEnemyBuff.POWER_ID).amount;
                    info.output = (int)(info.output * (1.0f + ((float)amount / 100)));
                }
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                PowerTip intentTip = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                int multiplier = moves.get(this.nextMove).multiplier;
                if (multiplier > 0) {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[3] + multiplier + TEXT[4];
                } else {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[2];
                }
                Texture attackImg = getAttackIntent(info.output);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            }
        }
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                handleTargetingForIntent(additionalMove, additionalIntent, i);
            }
        }
    }

    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (target != null) {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, target.icon, index);
        } else {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null, index);
        }
    }

    public void setNumAdditionalMoves(int num) {
        numAdditionalMoves = num;
        maxAdditionalMoves = num;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
    }

    @Override
    protected Map<Integer, ArrayList<DetailedIntent>> getDetailedIntents() {
        Map<Integer, ArrayList<DetailedIntent>> detailsMap = super.getDetailedIntents();
        for (int i = 0; i < additionalMoves.size(); i++) {
            ArrayList<DetailedIntent> details = getDetails(additionalMoves.get(i), i);
            if (details != null) {
                detailsMap.put(i, details);
            }
        }
        return detailsMap;
    }
}