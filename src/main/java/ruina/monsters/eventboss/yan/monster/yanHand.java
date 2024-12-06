package ruina.monsters.eventboss.yan.monster;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractDeckMonster;
import ruina.monsters.eventboss.yan.cards.*;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Erosion;
import ruina.powers.Paralysis;
import ruina.powers.act3.Unkillable;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class yanHand extends AbstractDeckMonster {
    public static final String ID = makeID(yanHand.class.getSimpleName());

    private static final byte GIANT_FIST = 0;
    private static final byte COMPRESS = 1;
    private static final byte FLURRY = 2;
    private static final byte BRAND = 3;
    private static final byte LOCK = 4;
    private static final byte EMPTY = 5;

    public final int fistDMG = calcAscensionDamage(12);
    public final int fistPara = calcAscensionSpecial(2);

    public final int compressBlock = calcAscensionTankiness(18);

    public final int flurryDamage = calcAscensionDamage(5);
    public final int flurryStr = calcAscensionSpecial(2);
    public final int flurryHits = 3;

    public final int lockDmg = calcAscensionDamage(15);
    public final int drawReduction = 1;

    public final int brandDmg = calcAscensionDamage(15);
    public final int brandErosion = calcAscensionSpecial(1);

    public static final String FAULTYHARDWARE_POWER_ID = makeID("FaultyHardware");
    public static final PowerStrings FaultyHardwarePowerStrings = CardCrawlGame.languagePack.getPowerStrings(FAULTYHARDWARE_POWER_ID);
    public static final String FAULTYHARDWARE_POWER_NAME = FaultyHardwarePowerStrings.NAME;
    public static final String[] FAULTYHARDWARE_POWER_DESCRIPTIONS = FaultyHardwarePowerStrings.DESCRIPTIONS;

    public enum BEHAVIOUR {
        LEFT,
        RIGHT
    }

    public BEHAVIOUR currentMode;

    private final yanDistortion parent;

    public yanHand(final float x, final float y, BEHAVIOUR mode, yanDistortion parent) {
        super(ID, ID, 60, -5.0F, 0, 250.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("YanHand/Spriter/YanHand.scml"));
        currentMode = mode;
        this.setHp(calcAscensionTankiness(60));
        this.parent = parent;

        addMove(GIANT_FIST, Intent.ATTACK_DEBUFF, fistDMG);
        addMove(COMPRESS, Intent.DEFEND);
        addMove(FLURRY, Intent.ATTACK_BUFF, flurryDamage, flurryHits);
        addMove(BRAND, Intent.ATTACK_DEBUFF, brandDmg);
        addMove(LOCK, Intent.ATTACK_DEBUFF, lockDmg);
        addMove(EMPTY, Intent.NONE);
        initializeDeck();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        AbstractPower faultyhardware = new AbstractLambdaPower(FAULTYHARDWARE_POWER_NAME, FAULTYHARDWARE_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = FAULTYHARDWARE_POWER_DESCRIPTIONS[0];
            }
        };
        applyToTarget(this, this, faultyhardware);
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case GIANT_FIST:
                punchAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), yanHand.this, new Paralysis(adp(), fistPara));
                resetIdle();
                break;
            case COMPRESS: {
                blockAnimation();
                block(this, compressBlock);
                resetIdle();
                break;
            }
            case FLURRY:
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slamAnimation(adp());
                    } else {
                        punchAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                applyToTarget(this, this, new StrengthPower(this, flurryStr));
                break;
            case BRAND:
                brandAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), yanHand.this, new Erosion(adp(), brandErosion));
                resetIdle();
                break;
            case LOCK:
                lockAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), yanHand.this, new DrawReductionPower(adp(), drawReduction));
                resetIdle();
                break;
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        createMoveFromCard(topDeckCardForMoveAction());
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        createAdditionalMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove));
    }

    @Override
    protected void createDeck() {
        if (currentMode.equals(BEHAVIOUR.LEFT)) {
            masterDeck.addToBottom(new CHRBOSS_BalefulBrand(this));
        } else {
            masterDeck.addToBottom(new CHRBOSS_Lock(this));
        }
        for (int i = 0; i < 2; i += 1) {
            masterDeck.addToBottom(new CHRBOSS_GiantFist(this));
            masterDeck.addToBottom(new CHRBOSS_Compress(this));
            masterDeck.addToBottom(new CHRBOSS_FlurryOfFists(this));
        }
    }

    protected void createMoveFromCard(AbstractCard c) {
        if (c.cardID.equals(CHRBOSS_GiantFist.ID)) {
            setMoveShortcut(GIANT_FIST, MOVES[0], c);
        } else if (c.cardID.equals(CHRBOSS_Compress.ID)) {
            setMoveShortcut(COMPRESS, MOVES[1], c);
        } else if (c.cardID.equals(CHRBOSS_FlurryOfFists.ID)) {
            setMoveShortcut(FLURRY, MOVES[2], c);
        } else if (c.cardID.equals(CHRBOSS_BalefulBrand.ID)) {
            setMoveShortcut(BRAND, MOVES[3], c);
        } else if (c.cardID.equals(CHRBOSS_Lock.ID)) {
            setMoveShortcut(LOCK, MOVES[4], c);
        } else {
            setMoveShortcut(COMPRESS, MOVES[1], c);
        }
    }

    protected void createAdditionalMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {
        if (c.cardID.equals(CHRBOSS_GiantFist.ID)) {
            setAdditionalMoveShortcut(GIANT_FIST, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_Compress.ID)) {
            setAdditionalMoveShortcut(COMPRESS, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_FlurryOfFists.ID)) {
            setAdditionalMoveShortcut(FLURRY, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_BalefulBrand.ID)) {
            setAdditionalMoveShortcut(BRAND, moveHistory, c);
        } else if (c.cardID.equals(CHRBOSS_Lock.ID)) {
            setAdditionalMoveShortcut(LOCK, moveHistory, c);
        } else {
            setAdditionalMoveShortcut(COMPRESS, moveHistory, c);
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            if (currentMode == BEHAVIOUR.LEFT && !parent.rightKilledFirst) {
                parent.leftKilledFirst = true;
            } else if (currentMode == BEHAVIOUR.RIGHT && !parent.leftKilledFirst) {
                parent.rightKilledFirst = true;
            }
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

    private void punchAnimation(AbstractCreature enemy) {
        animationAction("Punch", "YanStab", enemy, this);
    }

    private void slamAnimation(AbstractCreature enemy) {
        animationAction("Slam", "YanVert", enemy, this);
    }

    private void brandAnimation(AbstractCreature enemy) {
        animationAction("Brand", "YanBrand", enemy, this);
    }

    private void lockAnimation(AbstractCreature enemy) {
        animationAction("Lock", "YanLock", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }


}
