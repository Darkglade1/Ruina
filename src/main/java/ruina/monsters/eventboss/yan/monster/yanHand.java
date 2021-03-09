package ruina.monsters.eventboss.yan.monster;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractDeckMonster;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_Spear;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.monsters.eventboss.yan.cards.*;
import ruina.powers.*;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class yanHand extends AbstractDeckMonster
{
    public static final String ID = makeID(yanHand.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte GIANT_FIST = 0;
    private static final byte COMPRESS = 1;
    private static final byte FLURRY = 2;
    private static final byte BRAND = 3;
    private static final byte LOCK = 4;
    private static final byte EMPTY = 5;

    public final int fistDMG = calcAscensionDamage(4);
    public final int fistPara = calcAscensionSpecial(2);

    public final int compressBlock = calcAscensionTankiness(15);

    public final int flurryDamage = calcAscensionDamage(4);
    public final int flurryStr = calcAscensionSpecial(2);
    public final int flurryHits = 3;

    public final int lockDmg = calcAscensionDamage(10);
    public final int drawReduction = 1;

    public final int brandDmg = calcAscensionDamage(10);
    public final int brandErosion = calcAscensionSpecial(1);

    public static final String OVERCLOCK_POWER_ID = makeID("Overclock");
    public static final PowerStrings OverclockPowerStrings = CardCrawlGame.languagePack.getPowerStrings(OVERCLOCK_POWER_ID);
    public static final String OVERCLOCK_POWER_NAME = OverclockPowerStrings.NAME;
    public static final String[] OVERCLOCK_POWER_DESCRIPTIONS = OverclockPowerStrings.DESCRIPTIONS;

    public static final String POWERSURGE_POWER_ID = makeID("PowerSurge");
    public static final PowerStrings PowerSurgePowerStrings = CardCrawlGame.languagePack.getPowerStrings(POWERSURGE_POWER_ID);
    public static final String POWERSURGE_POWER_NAME = PowerSurgePowerStrings.NAME;
    public static final String[] POWERSURGE_POWER_DESCRIPTIONS = PowerSurgePowerStrings.DESCRIPTIONS;

    public static final String FAULTYHARDWARE_POWER_ID = makeID("FaultyHardware");
    public static final PowerStrings FaultyHardwarePowerStrings = CardCrawlGame.languagePack.getPowerStrings(FAULTYHARDWARE_POWER_ID);
    public static final String FAULTYHARDWARE_POWER_NAME = FaultyHardwarePowerStrings.NAME;
    public static final String[] FAULTYHARDWARE_POWER_DESCRIPTIONS = FaultyHardwarePowerStrings.DESCRIPTIONS;

    public enum BEHAVIOUR{
        LEFT,
        RIGHT
    }
    public BEHAVIOUR currentMode;

    private final int baseExtraActions = 0;
    private final int extraActions = 1;
    private yanDistortion parent;

    public yanHand(final float x, final float y, BEHAVIOUR mode, yanDistortion parent) {
        super(NAME, ID, 40, -5.0F, 0, 250.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScytheApostle/Spriter/ScytheApostle.scml"));
        currentMode = mode;
        this.type = EnemyType.ELITE;
        this.setHp(calcAscensionTankiness(maxHealth));
        this.parent = parent;
        maxAdditionalMoves = extraActions;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        numAdditionalMoves = baseExtraActions;

        addMove(GIANT_FIST, Intent.ATTACK, fistDMG);
        addMove(COMPRESS, Intent.DEFEND);
        addMove(FLURRY, Intent.ATTACK, flurryDamage, flurryHits, true);
        addMove(BRAND, Intent.ATTACK_DEBUFF, brandDmg);
        addMove(LOCK, Intent.ATTACK_DEBUFF, lockDmg);
        addMove(EMPTY, Intent.NONE);
        initializeDeck();
    }

    @Override
    public void usePreBattleAction()
    {
        AbstractPower overclock = new AbstractLambdaPower(OVERCLOCK_POWER_NAME, OVERCLOCK_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() { description = OVERCLOCK_POWER_DESCRIPTIONS[0]; }
        };
        applyToTarget(this, this, overclock);
        AbstractPower powersurge = new AbstractLambdaPower(POWERSURGE_POWER_NAME, POWERSURGE_POWER_NAME, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() { description = POWERSURGE_POWER_DESCRIPTIONS[0]; }
        };
        applyToTarget(this, this, powersurge);
        AbstractPower faultyhardware = new AbstractLambdaPower(FAULTYHARDWARE_POWER_NAME, FAULTYHARDWARE_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() { description = FAULTYHARDWARE_POWER_DESCRIPTIONS[0]; }
        };
        applyToTarget(this, this, faultyhardware);
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, target); }
        switch (move.nextMove) {
            case GIANT_FIST:
                dmg(adp(), info);
                applyToTarget(adp(), yanHand.this, new Paralysis(adp(), fistPara));
                break;
            case COMPRESS: {
                block(this, compressBlock);
                break;
            }
            case FLURRY:
                for (int i = 0; i < multiplier; i++) { dmg(adp(), info); }
                applyToTarget(this, this, new NextTurnPowerPower(this, new StrengthPower(this, flurryStr)));
                break;
            case BRAND:
                dmg(adp(), info);
                applyToTarget(adp(), yanHand.this, new NextTurnPowerPower(adp(), new Erosion(adp(), brandErosion)));
                break;
            case LOCK:
                dmg(adp(), info);
                applyToTarget(adp(), yanHand.this, new DrawReductionPower(adp(), drawReduction));
                break;
        }
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
                for(AbstractPower p: yanHand.this.powers){
                    if(p instanceof StrengthPower){
                        att(new RemoveSpecificPowerAction(yanHand.this, yanHand.this, p));
                        break;
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) { createMoveFromCard(topDeckCardForMoveAction()); }

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
        if (currentMode.equals(BEHAVIOUR.LEFT)) { masterDeck.addToBottom(new CHRBOSS_BalefulBrand(this)); }
        else { masterDeck.addToBottom(new CHRBOSS_Lock(this)); }
        for(int i = 0; i < 2; i += 1){
            masterDeck.addToBottom(new CHRBOSS_GiantFist(this));
            masterDeck.addToBottom(new CHRBOSS_Compress(this));
            masterDeck.addToBottom(new CHRBOSS_FlurryOfFists(this));
        }
    }

    @Override
    protected void createMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {

    }

    protected void createMoveFromCard(AbstractCard c) {
        if (c.cardID.equals(CHRBOSS_GiantFist.ID)) { setMoveShortcut(GIANT_FIST, MOVES[0], c);
        } else if (c.cardID.equals(CHRBOSS_Compress.ID)) { setMoveShortcut(COMPRESS, MOVES[1], c);
        } else if (c.cardID.equals(CHRBOSS_FlurryOfFists.ID)) { setMoveShortcut(FLURRY, MOVES[2], c);
        } else if (c.cardID.equals(CHRBOSS_BalefulBrand.ID)) { setMoveShortcut(BRAND, MOVES[3], c);
        } else if (c.cardID.equals(CHRBOSS_Lock.ID)) { setMoveShortcut(LOCK, MOVES[4], c);
        } else { setMoveShortcut(COMPRESS, MOVES[1], c); }
    }

    protected void createAdditionalMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {
        if (c.cardID.equals(CHRBOSS_GiantFist.ID)) { setAdditionalMoveShortcut(GIANT_FIST, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_Compress.ID)) { setAdditionalMoveShortcut(COMPRESS, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_FlurryOfFists.ID)) { setAdditionalMoveShortcut(FLURRY, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_BalefulBrand.ID)) { setAdditionalMoveShortcut(BRAND, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_Lock.ID)) { setAdditionalMoveShortcut(LOCK, moveHistory, c);
        } else { setAdditionalMoveShortcut(COMPRESS, moveHistory, c); }
    }

    public void calculateAllocatedMoves(){
        numAdditionalMoves = 0;
        if(this.hasPower(NextTurnPowerPower.POWER_ID)){ numAdditionalMoves += 1; }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            if(currentMode == BEHAVIOUR.LEFT && !parent.rightKilledFirst){ parent.leftKilledFirst = true; }
            else if(currentMode == BEHAVIOUR.RIGHT && !parent.leftKilledFirst){ parent.rightKilledFirst = true; }
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            if (this.nextMove != EMPTY) {
                setMoveShortcut(EMPTY);
                this.createIntent();
                atb(new SetMoveAction(this, EMPTY, Intent.NONE));
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power.ID.equals(Unkillable.POWER_ID)) && !(power.ID.equals(StrengthPower.POWER_ID)) && !(power.ID.equals(GainStrengthPower.POWER_ID)) && !(power.ID.equals(RitualPower.POWER_ID)) && !(power.ID.equals(MinionPower.POWER_ID))) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
        }
    }

    @Override
    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die();
        }
    }
}
