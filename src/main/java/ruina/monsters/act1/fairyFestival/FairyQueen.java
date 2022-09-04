package ruina.monsters.act1.fairyFestival;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.CenterOfAttention;
import ruina.powers.Meal;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class FairyQueen extends AbstractRuinaMonster
{
    public static final String ID = makeID(FairyQueen.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte QUEENS_DECREE = 0;
    private static final byte PREDATION = 1;
    private static final byte RAVENOUSNESS = 2;

    private final int STRENGTH = calcAscensionSpecial(1);
    private final int RAVENOUSNESS_STR = calcAscensionSpecial(4);
    private final float ENRAGE_THRESHOLD = 0.5f;
    private final int SELF_DEBUFF = 1;

    public AbstractMonster[] minions = new AbstractMonster[2];
    private boolean enraged = false;

    public static final String POWER_ID = makeID("Satiation");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FairyQueen() {
        this(100.0f, 0.0f);
    }

    public FairyQueen(final float x, final float y) {
        super(NAME, ID, 180, 0.0F, 0, 250.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("FairyQueen/Spriter/FairyQueen.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(180));
        addMove(QUEENS_DECREE, Intent.UNKNOWN);
        addMove(PREDATION, Intent.ATTACK, calcAscensionDamage(6), 2, true);
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
        Summon();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, SELF_DEBUFF) {

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
            }
        });
        applyToTarget(this, this, new CenterOfAttention(this));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        if (firstMove) {
            firstMove = false;
        }

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
                for (AbstractMonster minion : minions) {
                    if (minion != null) {
                        AbstractPower meal = minion.getPower(Meal.POWER_ID);
                        if (meal != null && minion.currentHealth <= meal.amount) {
                            applyToTarget(this, this, new WeakPower(this, SELF_DEBUFF, true));
                            applyToTarget(this, this, new VulnerablePower(this, SELF_DEBUFF, true));
                        }
                        atb(new AbstractGameAction() {
                            @Override
                            public void update() {
                                AbstractDungeon.effectList.add(new StrikeEffect(minion, minion.hb.cX, minion.hb.cY, 999));
                                this.isDone = true;
                            }
                        });
                        atb(new InstantKillAction(minion));
                    }
                }
                applyToTarget(this, this, new StrengthPower(this, RAVENOUSNESS_STR));
                resetIdle(1.0f);
                enraged = true;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (currentHealth <= maxHealth * ENRAGE_THRESHOLD && !enraged) {
            setMoveShortcut(RAVENOUSNESS, MOVES[RAVENOUSNESS]);
        } else if (minions[0] == null && minions[1] == null && !firstMove && !enraged) {
            setMoveShortcut(QUEENS_DECREE, MOVES[QUEENS_DECREE]);
        } else {
            setMoveShortcut(PREDATION, MOVES[PREDATION]);
        }
    }

    public void Summon() {
        float xPos_Farthest_L = -450.0F;
        float xPos_Middle_L = -200F;
        for (int i = 0; i < minions.length; i++) {
            if (minions[i] == null) {
                AbstractMonster minion;
                if (i == 0) {
                    minion = new FairyMass(xPos_Farthest_L, 0.0f, this);
                } else {
                    minion = new FairyMass(xPos_Middle_L, 0.0f, this);
                }
                atb(new SpawnMonsterAction(minion, true));
                atb(new UsePreBattleActionAction(minion));
                minions[i] = minion;
            }
        }
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