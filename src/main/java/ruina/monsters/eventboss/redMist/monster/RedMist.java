package ruina.monsters.eventboss.redMist.monster;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import com.sun.org.apache.bcel.internal.generic.FALOAD;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.cards.Dazzled;
import ruina.cards.EGO.act2.Mimicry;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractDeckMonster;
import ruina.monsters.eventboss.redMist.cards.*;
import ruina.powers.Bleed;
import ruina.powers.NextTurnPowerPower;
import ruina.powers.RedMistPower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

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

    private static final byte SALVATION = 0;
    private static final byte DAZZLE_ENEMY = 1;
    private static final byte DAZZLE_PLAYER = 2;
    private static final byte ILLUMINATE = 3;

    private final int STATUS = calcAscensionSpecial(3);
    private final int DEBUFF = calcAscensionSpecial(1);

    public static final String Salvation_POWER_ID = makeID("Salvation");
    public static final PowerStrings SalvationPowerStrings = CardCrawlGame.languagePack.getPowerStrings(Salvation_POWER_ID);
    public static final String Salvation_POWER_NAME = SalvationPowerStrings.NAME;
    public static final String[] Salvation_POWER_DESCRIPTIONS = SalvationPowerStrings.DESCRIPTIONS;

    private final int focusSpiritBlock = 12;
    private final int focusSpiritStr = 1;
    private final int GSVBleed = 3;
    private final int GSHBleed = 5;

    private final int upstanding_threshold = 7;
    private final int level_threshold = 7;

    private boolean EGO = false;
    private boolean EGORECENTTRIGGER = false;
    private int baseExtraActions = 1;
    private int egoExtraActions = 1;
    private int levelSlashExtraActions = 0;
    private int turn = 0;

    public RedMist() {
        this(100.0f, 0.0f);
    }
    public RedMist(final float x, final float y) {
        super(NAME, ID, 300, -5.0F, 0, 300.0f, 355.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BigBird/Spriter/BigBird.scml"));
        this.type = EnemyType.NORMAL;
        this.setHp(calcAscensionTankiness(300));
        // Not a bug - I just don't want to crash the game with null movehistory.
        numAdditionalMoves = 99;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        numAdditionalMoves = baseExtraActions;
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(FOCUS_SPIRIT, Intent.DEFEND_BUFF);
        addMove(UPSTANDING_SLASH, Intent.ATTACK_BUFF, 7, 2, true);
        addMove(LEVEL_SLASH, Intent.ATTACK_BUFF, 5, 2, true);
        addMove(SPEAR, Intent.ATTACK, 3, 3, true);
        addMove(GSV, Intent.ATTACK_DEBUFF, calcAscensionDamage(30));
        addMove(GSH, Intent.ATTACK_DEBUFF, calcAscensionDamage(40));

        initializeDeck();
    }

    @Override
    public void usePreBattleAction() {
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
                block(this, focusSpiritBlock);
                applyToTarget(this, this, new NextTurnPowerPower(this, new StrengthPower(this, focusSpiritStr)));
                applyToTarget(this, this, new NextTurnPowerPower(this, new LoseStrengthPower(this, focusSpiritStr)));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        turn += 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case UPSTANDING_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += adp().lastDamageTaken;
                            isDone = true;
                        }
                    });
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(threshold[0] >= upstanding_threshold){
                            AbstractPower P = RedMist.this.getPower(RedMistPower.POWER_ID);
                            if(P != null){ amount += ((RedMist.this.maxHealth / 100) * 5); }
                        }
                        isDone = true;
                    }
                });
                break;
            }
            case LEVEL_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += adp().lastDamageTaken;
                            isDone = true;
                        }
                    });
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(threshold[0] >= level_threshold){ levelSlashExtraActions += 1; }
                        isDone = true;
                    }
                });
            }
            case SPEAR: {
                for (int i = 0; i < multiplier; i++) { dmg(adp(), info); }
                break;
            }
            case GSV: {
                dmg(adp(), info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(adp().lastDamageTaken > 0){ applyToTargetTop(adp(), RedMist.this, new Bleed(adp(), GSVBleed)); }
                        this.isDone = true;
                    }
                });
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        turn += 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case GSH: {
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
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        turn += 1;
                        this.isDone = true;
                    }
                });
                break;
            }
        }
    }

    private void salvation1Animation(AbstractCreature enemy) {
        animationAction("Salvation1", "BigBirdOpen", enemy, this);
    }

    private void salvation2Animation(AbstractCreature enemy) {
        animationAction("Salvation2", "BigBirdCrunch", enemy, this);
    }

    private void dazzleAnimation(AbstractCreature enemy) {
        animationAction("Lamp", "BigBirdLamp", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Lamp", "BigBirdEyes", enemy, this);
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
                calculateAllocatedMoves();
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(turn != 0 && turn % 3 == 0){
            if(EGO){ setMoveShortcut(GSH, MOVES[GSH], new CHRBOSS_GreaterSplitHorizontal()); }
            else { setMoveShortcut(GSV, MOVES[GSV], new CHRBOSS_GreaterSplitVertical()); }
        }
        else{
            if(EGORECENTTRIGGER){ setMoveShortcut(GSH, MOVES[GSH], new CHRBOSS_GreaterSplitHorizontal()); }
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
    public void getAdditionalMoves(int num, int whichMove) { createMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove)); }

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
            masterDeck.addToBottom(new CHRBOSS_LevelSlash());
            masterDeck.addToBottom(new CHRBOSS_Spear());
            masterDeck.addToBottom(new CHRBOSS_UpstandingSlash());
        }
    }

    @Override
    protected void createMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {
        System.out.println(c.cardID);
        switch (c.cardID){
            case "ruina:CHRBOSS_LevelSlash":
                setAdditionalMoveShortcut(LEVEL_SLASH, moveHistory, c);
                break;
            case "ruina:CHRBOSS_Spear":
                setAdditionalMoveShortcut(SPEAR, moveHistory, c);
                break;
            case "ruina:CHRBOSS_UpstandingSlash":
                setAdditionalMoveShortcut(UPSTANDING_SLASH, moveHistory, c);
                break;
            default:
                break;

        }
    }

    public void calculateAllocatedMoves(){
        if(turn != 0 && turn % 3 == 0){
            numAdditionalMoves = 0;
            levelSlashExtraActions = 0;
        }
        else {
            numAdditionalMoves = baseExtraActions;
            if (EGO) { numAdditionalMoves += egoExtraActions; }
            numAdditionalMoves += levelSlashExtraActions;
            levelSlashExtraActions = 0;
        }
    }

    public void activateEGO(){
        // vfx here or something
        EGO = true;
        EGORECENTTRIGGER = true;
    }

    protected AbstractCard getMoveCardFromByte(Byte move) {
        switch (move){
            case FOCUS_SPIRIT: return new Madness();
            case UPSTANDING_SLASH: return new CHRBOSS_UpstandingSlash();
            case LEVEL_SLASH: return new CHRBOSS_LevelSlash();
            case SPEAR: return new CHRBOSS_Spear();
            default: return new Madness();
        }
    }
}