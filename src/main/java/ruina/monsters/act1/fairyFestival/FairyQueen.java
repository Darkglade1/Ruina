package ruina.monsters.act1.fairyFestival;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.CenterOfAttention;
import ruina.powers.act1.Meal;
import ruina.powers.act1.Satiation;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class FairyQueen extends AbstractRuinaMonster
{
    public static final String ID = makeID(FairyQueen.class.getSimpleName());

    public static final float MINION_X_1 = -450.0F;
    public static final float MINION_X_2 = -200.0F;
    private static final byte QUEENS_DECREE = 0;
    private static final byte PREDATION = 1;
    private static final byte RAVENOUSNESS = 2;

    private final int STRENGTH = calcAscensionSpecial(1);
    private final int RAVENOUSNESS_STR = calcAscensionSpecial(4);
    private final float ENRAGE_THRESHOLD = 0.5f;
    private final int SELF_DEBUFF = 1;
    private final int ENRAGE_PHASE = 2;

    public FairyQueen(final float x, final float y) {
        super(ID, ID, 180, 0.0F, 0, 250.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("FairyQueen/Spriter/FairyQueen.scml"));
        setHp(calcAscensionTankiness(180));
        addMove(QUEENS_DECREE, Intent.UNKNOWN);
        addMove(PREDATION, Intent.ATTACK, calcAscensionDamage(6), 2);
        addMove(RAVENOUSNESS, Intent.BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Angela2");
        applyToTarget(this, this, new Satiation(this, SELF_DEBUFF));
        applyToTarget(this, this, new CenterOfAttention(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case QUEENS_DECREE: {
                specialAnimation();
                Summon();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
            case PREDATION: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case RAVENOUSNESS: {
                enrageAnimation();
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if (mo instanceof FairyMass && !mo.isDeadOrEscaped()) {
                        AbstractPower meal = mo.getPower(Meal.POWER_ID);
                        if (meal != null && mo.currentHealth <= meal.amount) {
                            applyToTarget(this, this, new WeakPower(this, SELF_DEBUFF, true));
                            applyToTarget(this, this, new VulnerablePower(this, SELF_DEBUFF, true));
                        }
                        atb(new AbstractGameAction() {
                            @Override
                            public void update() {
                                AbstractDungeon.effectList.add(new StrikeEffect(mo, mo.hb.cX, mo.hb.cY, 999));
                                this.isDone = true;
                            }
                        });
                        atb(new InstantKillAction(mo));
                    }
                }
                applyToTarget(this, this, new StrengthPower(this, RAVENOUSNESS_STR));
                resetIdle(1.0f);
                setPhase(ENRAGE_PHASE);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (currentHealth <= maxHealth * ENRAGE_THRESHOLD && this.phase != ENRAGE_PHASE) {
            setMoveShortcut(RAVENOUSNESS);
        } else if (!areMinionsAlive() && !firstMove && this.phase != ENRAGE_PHASE) {
            setMoveShortcut(QUEENS_DECREE);
        } else {
            setMoveShortcut(PREDATION);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case QUEENS_DECREE: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.SUMMON);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case RAVENOUSNESS: {
                DetailedIntent detail = new DetailedIntent(this, RAVENOUSNESS_STR, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    public void Summon() {
        AbstractMonster minion = new FairyMass(MINION_X_1, 0.0f);
        atb(new SpawnMonsterAction(minion, true));
        atb(new UsePreBattleActionAction(minion));
        AbstractMonster minion2 = new FairyMass(MINION_X_2, 0.0f);
        atb(new SpawnMonsterAction(minion2, true));
        atb(new UsePreBattleActionAction(minion2));
    }

    private boolean areMinionsAlive() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof FairyMass && !mo.isDeadOrEscaped()) {
                return true;
            }
        }
        return false;
    }

    public void consumeMinion(AbstractMonster minion) {
        consumeAnimation(minion);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.effectList.add(new StrikeEffect(minion, minion.hb.cX, minion.hb.cY, 999));
                this.isDone = true;
            }
        });
        atb(new InstantKillAction(minion));
        applyToTarget(this, this, new WeakPower(this, SELF_DEBUFF, false));
        applyToTarget(this, this, new VulnerablePower(this, SELF_DEBUFF, false));
        applyToTarget(this, this, new StrengthPower(this, -SELF_DEBUFF));
        resetIdle();
        atb(new RollMoveAction(this));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                createIntent();
                this.isDone = true;
            }
        });
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof FairyMass) {
                atb(new SuicideAction(mo));
            }
        }
        onBossVictoryLogic();
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "FairyQueenAtk", enemy, this);
    }

    private void consumeAnimation(AbstractCreature enemy) {
        animationAction("Attack", "FairyQueenEat", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "FairySpecial", this);
    }

    private void enrageAnimation() {
        animationAction("Special", "FairyQueenChange", this);
    }

}