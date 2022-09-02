package ruina.monsters.eventboss.kim;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractCardMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Paralysis;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Kim extends AbstractCardMonster {
    public static final String ID = RuinaMod.makeID(Kim.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte YIELD = 0;
    private static final byte CLAIM = 1;
    private static final byte TAKE_ONES_LIFE = 2;
    private static final byte ACUPUNCTURE = 3;

    public final int STRENGTH = calcAscensionSpecial(2);
    public final int PARALYSIS = calcAscensionSpecial(2);
    public final int ACUPUNCTURE_HITS = 2;

    public boolean usedCounter = false;

    public static final String POWER_ID = makeID("CounterAttack");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final String R_POWER_ID = makeID("Rupture");
    public static final PowerStrings rpowerStrings = CardCrawlGame.languagePack.getPowerStrings(R_POWER_ID);
    public static final String R_POWER_NAME = rpowerStrings.NAME;
    public static final String[] R_POWER_DESCRIPTIONS = rpowerStrings.DESCRIPTIONS;

    public Kim() {
        this(0.0f, 0.0f);
    }

    public Kim(final float x, final float y) {
        super(NAME, ID, 180, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Kim/Spriter/Kim.scml"));

        this.setHp(calcAscensionTankiness(180));
        this.type = EnemyType.ELITE;

        addMove(YIELD, Intent.ATTACK_BUFF, calcAscensionDamage(10));
        addMove(CLAIM, Intent.ATTACK, calcAscensionDamage(20));
        addMove(TAKE_ONES_LIFE, Intent.ATTACK_DEBUFF, calcAscensionDamage(30));
        addMove(ACUPUNCTURE, Intent.ATTACK_DEBUFF, calcAscensionDamage(6), ACUPUNCTURE_HITS, true);

        cardList.add(new Yield(this));
        cardList.add(new Claim(this));
        cardList.add(new TakeOnesLife(this));
        cardList.add(new Acupuncture(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void takeTurn() {
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (nextMove) {
            case YIELD: {
                guardAnimation();
                dmg(adp(), info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
            case TAKE_ONES_LIFE: {
                final int[] damageDealt = {0};
                bluntAnimation(adp());
                dmg(adp(), info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        damageDealt[0] += adp().lastDamageTaken;
                        isDone = true;
                    }
                });
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(damageDealt[0] >= 0){
                            applyToTarget(adp(), Kim.this, new AbstractLambdaPower(R_POWER_NAME, R_POWER_ID, AbstractPower.PowerType.DEBUFF, false, adp(), damageDealt[0]) {
                                @Override
                                public void atEndOfTurn(boolean isPlayer) {
                                    this.flash();
                                    atb(new DamageAction(owner, new DamageInfo(owner, amount, DamageInfo.DamageType.THORNS), AttackEffect.POISON));
                                    atb(new RemoveSpecificPowerAction(owner, owner, this));
                                }

                                @Override
                                public void updateDescription() {
                                    description = R_POWER_DESCRIPTIONS[0] + amount + R_POWER_DESCRIPTIONS[1];
                                }
                            });
                        }
                        isDone = true;
                    }
                });
                resetIdle();
                break;
            }
            case ACUPUNCTURE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        guardAnimation();
                    } else {
                        bluntAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    public void CounterAttack() {
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        bluntAnimation(adp());
        dmg(adp(), info);
        resetIdle();
    }

    @Override
    protected void getMove(final int num) {
        byte move;
        if (this.lastMove(YIELD)) {
            usedCounter = false;
            applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, NeutralPowertypePatch.NEUTRAL, false, this, 0) {
                @Override
                public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                    if (card.type == AbstractCard.CardType.ATTACK && !owner.hasPower(StunMonsterPower.POWER_ID)) {
                        this.flash();
                        CounterAttack();
                        usedCounter = true;
                    }
                }

                @Override
                public void updateDescription() {
                    description = POWER_DESCRIPTIONS[0];
                }
            });
            move = CLAIM;
        } else if (this.lastMove(CLAIM) && usedCounter) {
            move = ACUPUNCTURE;
        } else if (this.lastMove(CLAIM) && !usedCounter) {
            move = TAKE_ONES_LIFE;
        } else {
            move = YIELD;
        }
        setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
    }

    public void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "FireStab", enemy, this);
    }

    public void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "FireHori", enemy, this);
    }

    public void guardAnimation() {
        animationAction("Block", "FireGuard", this);
    }

}