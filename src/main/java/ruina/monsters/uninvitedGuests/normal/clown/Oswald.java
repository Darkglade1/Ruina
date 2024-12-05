package ruina.monsters.uninvitedGuests.normal.clown;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.clown.oswaldCards.*;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Oswald extends AbstractCardMonster
{
    public static final String ID = makeID(Oswald.class.getSimpleName());

    private static final byte CLIMAX = 0;
    private static final byte FUN = 1;
    private static final byte CATCH = 2;
    private static final byte POW = 3;
    private static final byte BRAINWASH = 4;

    public final int funHits = 2;
    public final int climaxDamage = calcAscensionDamage(5);
    public int climaxHits = 4;
    public final int climaxHitIncrease = calcAscensionSpecial(1);

    public final int STATUS = calcAscensionSpecial(3);
    public final int STRENGTH = calcAscensionSpecial(1);
    public final int WEAK = calcAscensionSpecial(2);
    public final int BRAINWASH_LENGTH = 2;
    public final int BRAINWASH_DAMAGE = calcAscensionTankiness(40);
    public final int BRAINWASH_DAMAGE_INCREASE = calcAscensionTankiness(20);
    public int brainwashDamage = BRAINWASH_DAMAGE;

    public static final String POWER_ID = makeID("Brainwash");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Oswald() {
        this(0.0f, 0.0f);
    }

    public Oswald(final float x, final float y) {
        super(ID, ID, 700, -5.0F, 0, 200.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Oswald/Spriter/Oswald.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(700));

        addMove(CLIMAX, Intent.ATTACK, climaxDamage, climaxHits);
        addMove(FUN, Intent.ATTACK, calcAscensionDamage(9), funHits);
        addMove(CATCH, Intent.DEBUFF);
        addMove(POW, Intent.BUFF);
        addMove(BRAINWASH, Intent.STRONG_DEBUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble2");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Tiph) {
                target = (Tiph)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        super.takeCustomTurn(move, target);
        switch (move.nextMove) {
            case CLIMAX: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == multiplier - 1) {
                        specialAttackAnimation(target);
                    } else if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    waitAnimation();
                }
                resetIdle();
                climaxHits += climaxHitIncrease;
                addMove(CLIMAX, Intent.ATTACK, climaxDamage, climaxHits, true);
                break;
            }
            case FUN: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        bluntAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case CATCH: {
                buffAnimation();
                intoDrawMo(new Wound(), STATUS, this);
                resetIdle(1.0f);
                break;
            }
            case POW: {
                buffAnimation();
                applyToTarget(target, this, new WeakPower(target, WEAK, true));
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
            case BRAINWASH: {
                specialAnimation();
                applyToTarget(target, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.DEBUFF, false, target, BRAINWASH_LENGTH) {
                    boolean justApplied = true;

                    @Override
                    public void onInitialApplication() {
                        if (owner instanceof AbstractAllyMonster) {
                            owner.halfDead = false;
                            ((AbstractAllyMonster) owner).isAlly = false;
                            ((AbstractAllyMonster) owner).setAnimationFlip(false, false);
                            Color color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
                            ReflectionHacks.setPrivate(owner, AbstractMonster.class, "intentColor", color);
                        }
                        if (owner instanceof Tiph) {
                            ((Tiph) owner).onBrainwashed();
                        }
                        amount2 = brainwashDamage;
                        brainwashDamage += BRAINWASH_DAMAGE_INCREASE;
                        updateDescription();
                        AbstractDungeon.onModifyPower();
                    }

                    @Override
                    public int onAttacked(DamageInfo info, int damageAmount) {
                        flash();
                        amount2 -= damageAmount;
                        if (amount2 <= 0) {
                            atb(new RemoveSpecificPowerAction(owner, owner, this));
                        }
                        updateDescription();
                        return damageAmount;
                    }

                    @Override
                    public void onRemove() {
                        if (owner instanceof AbstractAllyMonster) {
                            owner.halfDead = true;
                            ((AbstractAllyMonster) owner).isAlly = true;
                            ((AbstractAllyMonster) owner).setAnimationFlip(true, false);
                        }
                    }

                    @Override
                    public void atEndOfRound() {
                        if (justApplied) {
                            justApplied = false;
                        } else {
                            atb(new ReducePowerAction(owner, owner, this, 1));
                        }
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + amount2 + POWER_DESCRIPTIONS[2];
                    }
                });
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "OswaldHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "OswaldStab", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "OswaldVert", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "OswaldAttract", this);
    }

    private void specialAttackAnimation(AbstractCreature enemy) {
        animationAction("Special", "OswaldFinish", enemy, this);
    }

    private void buffAnimation() {
        animationAction("Block", "OswaldLaugh", this);
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(CLIMAX)) {
            setMoveShortcut(FUN, MOVES[FUN], getMoveCardFromByte(FUN));
        } else if (this.lastMove(FUN)) {
            setMoveShortcut(CATCH, MOVES[CATCH], getMoveCardFromByte(CATCH));
        } else {
            setMoveShortcut(CLIMAX, MOVES[CLIMAX], getMoveCardFromByte(CLIMAX));
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (this.lastMove(BRAINWASH, moveHistory) || this.lastMove(CATCH, moveHistory)) {
            setAdditionalMoveShortcut(FUN, moveHistory, getMoveCardFromByte(FUN));
        } else if (this.lastMove(FUN, moveHistory)) {
            setAdditionalMoveShortcut(POW, moveHistory, getMoveCardFromByte(POW));
        } else {
            if (firstMove || (target != null && !target.isDead && !target.isDying)) {
                setAdditionalMoveShortcut(BRAINWASH, moveHistory, getMoveCardFromByte(BRAINWASH));
            } else {
                setAdditionalMoveShortcut(CATCH, moveHistory, getMoveCardFromByte(CATCH));
            }
        }
    }

    protected AbstractCard getMoveCardFromByte(Byte move) {
        ArrayList<AbstractCard> list = new ArrayList<>();
        list.add(new Climax(this));
        list.add(new Fun(this));
        list.add(new Catch(this));
        list.add(new Pow(this));
        list.add(new Brainwash(this));
        return list.get(move);
    }

    @Override
    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (additionalMove.nextMove == BRAINWASH) {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, target.icon);
        } else {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (target instanceof Tiph) {
            ((Tiph) target).onBossDeath();
        }
    }

}