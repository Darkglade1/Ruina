package ruina.monsters.eventboss.redMist.monster;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractDeckMonster;
import ruina.monsters.eventboss.redMist.cards.*;
import ruina.powers.Bleed;
import ruina.powers.NextTurnPowerPower;
import ruina.powers.RedMistPower;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class RedMist extends AbstractDeckMonster
{
    public static final String ID = makeID(RedMist.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte FOCUS_SPIRIT = 0;
    private static final byte UPSTANDING_SLASH = 1;
    private static final byte LEVEL_SLASH = 2;
    private static final byte SPEAR = 3;
    private static final byte GSV = 4;
    private static final byte GSH = 5;

    public final int focusSpiritBlock = calcAscensionTankiness(12);
    public final int focusSpiritStr = calcAscensionSpecial(1);
    public final int GSVBleed = calcAscensionSpecial(3);
    public final int GSHBleed = calcAscensionSpecial(5);
    public final int UPSTANDING_SLASH_DEBUFF = calcAscensionSpecial(1);

    public final int upstanding_damage = calcAscensionDamage(6);
    public final int upstanding_threshold = upstanding_damage;
    public final int level_damage = calcAscensionDamage(5);
    public final int level_threshold = level_damage;

    private static final int KALI_PHASE = 1;
    private static final int EGO_PHASE = 2;
    private int phase = KALI_PHASE;

    private boolean EGO = false;
    private boolean EGORECENTTRIGGER = false;
    private final int baseExtraActions = 1;
    private final int egoExtraActions = 1;
    private int levelSlashExtraActions = 0;

    private static final int GREATER_SPLIT_COOLDOWN = 3;
    private int greaterSplitCooldownCounter = GREATER_SPLIT_COOLDOWN;

    public RedMist() {
        this(0.0f, 0.0f);
    }
    public RedMist(final float x, final float y) {
        super(NAME, ID, 300, -5.0F, 0, 250.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RedMist/Spriter/RedMist.scml"));
        this.type = EnemyType.BOSS;
        this.setHp(calcAscensionTankiness(maxHealth));

        maxAdditionalMoves = 3;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        numAdditionalMoves = baseExtraActions;

        addMove(FOCUS_SPIRIT, Intent.DEFEND_BUFF);
        addMove(UPSTANDING_SLASH, Intent.ATTACK_DEBUFF, upstanding_damage, 2, true);
        addMove(LEVEL_SLASH, Intent.ATTACK_BUFF, level_damage, 2, true);
        addMove(SPEAR, Intent.ATTACK, 5, 3, true);
        addMove(GSV, Intent.ATTACK_DEBUFF, calcAscensionDamage(30));
        addMove(GSH, Intent.ATTACK_DEBUFF, calcAscensionDamage(40));

        initializeDeck();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction()
    {
        CustomDungeon.playTempMusicInstantly("Gebura2");
        applyToTarget(this, this, new RedMistPower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, target); }
        final int[] threshold = {0};
        switch (move.nextMove) {
            case FOCUS_SPIRIT: {
                blockAnimation();
                block(this, focusSpiritBlock);
                applyToTarget(this, this, new NextTurnPowerPower(this, new StrengthPower(this, focusSpiritStr)));
                applyToTarget(this, this, new NextTurnPowerPower(this, new LoseStrengthPower(this, focusSpiritStr)));
                resetIdle();
                break;
            }
            case UPSTANDING_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        upstandingAnimation(adp());
                    } else {
                        levelAnimation(adp());
                    }
                    dmg(adp(), info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += adp().lastDamageTaken;
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(threshold[0] >= upstanding_threshold){
                            applyToTargetTop(adp(), RedMist.this, new FrailPower(adp(), UPSTANDING_SLASH_DEBUFF, true));
                        }
                        isDone = true;
                    }
                });

                break;
            }
            case LEVEL_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        levelAnimation(adp());
                    } else {
                        upstandingAnimation(adp());
                    }
                    dmg(adp(), info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += adp().lastDamageTaken;
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(threshold[0] >= level_threshold){ levelSlashExtraActions += 1; }
                        isDone = true;
                    }
                });
                break;
            }
            case SPEAR: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                       spearAnimation(adp());
                    } else {
                        upstandingAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case GSV: {
                verticalUpAnimation(adp());
                atb(new VFXAction(new WaitEffect(), 0.25f));
                verticalSplitVfx();
                verticalDownAnimation(adp());
                dmg(adp(), info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(adp().lastDamageTaken > 0){ applyToTargetTop(adp(), RedMist.this, new Bleed(adp(), GSVBleed)); }
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        greaterSplitCooldownCounter = GREATER_SPLIT_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case GSH: {
                horizontalSplitVfx();
                horizontalAnimation(adp());
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(EGORECENTTRIGGER){ EGORECENTTRIGGER = false; }
                        isDone = true;
                    }
                });
                dmg(adp(), info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(adp().lastDamageTaken > 0){ applyToTargetTop(adp(), RedMist.this, new Bleed(adp(), GSHBleed)); }
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        greaterSplitCooldownCounter = GREATER_SPLIT_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
        }
    }

    private void upstandingAnimation(AbstractCreature enemy) {
        animationAction("Upstanding" + phase, "RedMistVert" + phase, enemy, this);
    }

    private void spearAnimation(AbstractCreature enemy) {
        animationAction("Spear" + phase, "RedMistStab" + phase, enemy, this);
    }

    private void levelAnimation(AbstractCreature enemy) {
        animationAction("Level" + phase, "RedMistHori" + phase, enemy, this);
    }

    private void verticalUpAnimation(AbstractCreature enemy) {
        animationAction("VerticalUp" + phase, "RedMistVertHit", enemy, this);
    }

    private void verticalDownAnimation(AbstractCreature enemy) {
        animationAction("VerticalDown" + phase, "RedMistVertFin", enemy, this);
    }

    private void horizontalAnimation(AbstractCreature enemy) {
        animationAction("Horizontal", "RedMistHoriFin", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block" + phase, null, this);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + phase);
                this.isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            takeCustomTurn(additionalMove, adp());
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                greaterSplitCooldownCounter -= 1;
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(!EGO){ EGOTrigger(); }
                /*
                AbstractPower P = RedMist.this.getPower(RedMistPower.POWER_ID);
                if(P != null){ ((RedMistPower) P).EGOTrigger(); }
                 */
                isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                calculateAllocatedMoves();
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(greaterSplitCooldownCounter <= 0){
            if(EGO){ setMoveShortcut(GSH, MOVES[GSH], new CHRBOSS_GreaterSplitHorizontal(this)); }
            else { setMoveShortcut(GSV, MOVES[GSV], new CHRBOSS_GreaterSplitVertical(this)); }
        }
        else{
            if(EGORECENTTRIGGER){ setMoveShortcut(GSH, MOVES[GSH], new CHRBOSS_GreaterSplitHorizontal(this)); }
            else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(FOCUS_SPIRIT)) { possibilities.add(FOCUS_SPIRIT); }
                if (!this.lastMove(UPSTANDING_SLASH)) { possibilities.add(UPSTANDING_SLASH); }
                if (!this.lastMove(LEVEL_SLASH)) { possibilities.add(LEVEL_SLASH); }
                if (!this.lastMove(SPEAR)) { possibilities.add(SPEAR); }
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setMoveShortcut(move, MOVES[move], getMoveCardFromByte(move));
            }
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) { createAdditionalMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove)); }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
            }
        }
    }

    @Override
    protected void createDeck() {
        for(int i = 0; i < 3; i += 1){
            masterDeck.addToBottom(new CHRBOSS_LevelSlash(this));
            masterDeck.addToBottom(new CHRBOSS_Spear());
            masterDeck.addToBottom(new CHRBOSS_UpstandingSlash(this));
            masterDeck.addToBottom(new CHRBOSS_FocusSpirit(this));
        }
    }

    protected void createAdditionalMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {
        if (c.cardID.equals(CHRBOSS_LevelSlash.ID)) {
            setAdditionalMoveShortcut(LEVEL_SLASH, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_Spear.ID)) {
            setAdditionalMoveShortcut(SPEAR, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_UpstandingSlash.ID)) {
            setAdditionalMoveShortcut(UPSTANDING_SLASH, moveHistory, c);
        } else {
            setAdditionalMoveShortcut(FOCUS_SPIRIT, moveHistory, c);
        }
    }

    public void calculateAllocatedMoves(){
        if(greaterSplitCooldownCounter <= 0){
            numAdditionalMoves = 0;
            levelSlashExtraActions = 0;
        }
        else {
            if(EGORECENTTRIGGER){
                numAdditionalMoves = 0;
                levelSlashExtraActions = 0;
            }
            else {
                numAdditionalMoves = baseExtraActions;
                if (EGO) { numAdditionalMoves += egoExtraActions; }
                numAdditionalMoves += levelSlashExtraActions;
                levelSlashExtraActions = 0;
                if (numAdditionalMoves > maxAdditionalMoves) {
                    numAdditionalMoves = maxAdditionalMoves;
                }
            }
        }
    }

    public void activateEGO(){
        playSound("RedMistChange");
        phase = EGO_PHASE;
        runAnim("Idle" + phase);
        CustomDungeon.playTempMusicInstantly("RedMistBGM");
        EGO = true;
        EGORECENTTRIGGER = true;
        //atb(new RemoveDebuffsAction(this));
    }

    protected AbstractCard getMoveCardFromByte(Byte move) {
        switch (move){
            case FOCUS_SPIRIT: return new CHRBOSS_FocusSpirit(this);
            case UPSTANDING_SLASH: return new CHRBOSS_UpstandingSlash(this);
            case LEVEL_SLASH: return new CHRBOSS_LevelSlash(this);
            case SPEAR: return new CHRBOSS_Spear();
            default: return new Madness();
        }
    }

    public static void verticalSplitVfx() {
        ArrayList<Texture> frames = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            frames.add(TexLoader.getTexture(makeMonsterPath("RedMist/Vertical/frame" + i + ".png")));
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("RedMistVertCut");
                this.isDone = true;
            }
        });
        fullScreenAnimation(frames, 0.1f, 0.9f);
    }

    public static void horizontalSplitVfx() {
        ArrayList<Texture> frames = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            frames.add(TexLoader.getTexture(makeMonsterPath("RedMist/Horizontal/frame" + i + ".png")));
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("RedMistHoriEye");
                playSound("RedMistHoriStart");
                this.isDone = true;
            }
        });
        fullScreenAnimation(frames, 0.1f, 1.2f);
    }

    private void EGOTrigger() {
        if(currentHealth <= maxHealth / 2){
            activateEGO();
            makePowerRemovable(this, RedMistPower.POWER_ID);
            att(new RemoveSpecificPowerAction(this, this, RedMistPower.POWER_ID));
        }
    }
}