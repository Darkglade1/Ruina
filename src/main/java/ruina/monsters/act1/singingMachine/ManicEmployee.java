package ruina.monsters.act1.singingMachine;

import basemod.helpers.CardModifierManager;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.cardmods.SingingMachineMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ManicEmployee extends AbstractRuinaMonster
{
    public static final String ID = makeID(ManicEmployee.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte TREMBLING_MOTION = 0;
    private static final byte PINE_FOR_THE_SONG = 1;

    private final int DEBUFF = calcAscensionSpecial(2);

    public boolean forcedAttack;

    private SingingMachineMonster parent;
    private AbstractCard targetCard;

    public static final String POWER_ID = makeID("BeautifulSound");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ManicEmployee() {
        this(0.0f, 0.0f);
    }

    public ManicEmployee(final float x, final float y) {
        super(NAME, ID, 110, 0.0F, 0, 220.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("CrazedEmployee/Spriter/CrazedEmployee.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(TREMBLING_MOTION, Intent.DEBUFF);
        addMove(PINE_FOR_THE_SONG, Intent.ATTACK, calcAscensionDamage(8));
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof SingingMachineMonster) {
                parent = (SingingMachineMonster) mo;
            }
        }
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
            case TREMBLING_MOTION: {
                blockAnimation();
                if (adp().hasPower(WeakPower.POWER_ID)) {
                    applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                } else {
                    applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                }
                resetIdle();
                break;
            }
            case PINE_FOR_THE_SONG: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                forcedAttack = false;
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                atb(new RollMoveAction(ManicEmployee.this));
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        if (targetCard != null) {
            CardModifierManager.removeModifiersById(targetCard, SingingMachineMod.ID, true);
        }
        targetCard = null;
        makePowerRemovable(this, POWER_ID);
        atb(new RemoveSpecificPowerAction(this, this, POWER_ID));
        if (forcedAttack) {
            setAttack();
        } else {
            if (lastMove(PINE_FOR_THE_SONG) || firstMove) {
                setMoveShortcut(TREMBLING_MOTION, MOVES[TREMBLING_MOTION]);
            } else {
                setAttack();
            }
        }
    }

    private CardGroup findCardGroupOfCard(AbstractCard card) {
        for (AbstractCard c : adp().drawPile.group) {
            if (c == card) {
                return adp().drawPile;
            }
        }
        for (AbstractCard c : adp().discardPile.group) {
            if (c == card) {
                return adp().discardPile;
            }
        }
        for (AbstractCard c : adp().hand.group) {
            if (c == card) {
                return adp().hand;
            }
        }
        for (AbstractCard c : adp().exhaustPile.group) {
            if (c == card) {
                return adp().exhaustPile;
            }
        }
        return null;
    }

    private void setAttack() {
        ArrayList<AbstractCard> validCards = new ArrayList<>();
        for (AbstractCard c : adp().drawPile.group) {
            if ((c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL) && !c.exhaust && !CardModifierManager.hasModifier(c, SingingMachineMod.ID)) {
                validCards.add(c);
            }
        }
        for (AbstractCard c : adp().discardPile.group) {
            if ((c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL) && !c.exhaust && !CardModifierManager.hasModifier(c, SingingMachineMod.ID)) {
                validCards.add(c);
            }
        }
        for (AbstractCard c : adp().hand.group) {
            if ((c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL) && !c.exhaust && !CardModifierManager.hasModifier(c, SingingMachineMod.ID)) {
                validCards.add(c);
            }
        }
        if (!validCards.isEmpty()) {
            targetCard = validCards.get(AbstractDungeon.monsterRng.random(validCards.size() - 1));
        }
        if (targetCard != null && !parent.isDeadOrEscaped()) {
            CardModifierManager.addModifier(targetCard, new SingingMachineMod());
            applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {

                @Override
                public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                    if (info.owner == owner) {
                        CardGroup group = findCardGroupOfCard(targetCard);
                        if (group != null) {
                            atb(new ExhaustSpecificCardAction(targetCard, group));
                            atb(new AbstractGameAction() {
                                @Override
                                public void update() {
                                    adp().exhaustPile.removeCard(targetCard);
                                    CardModifierManager.removeModifiersById(targetCard, SingingMachineMod.ID, true);
                                    parent.machineCards.add(targetCard.makeStatEquivalentCopy());
                                    this.isDone = true;
                                }
                            });
                        }
                    }
                }

                @Override
                public void updateDescription() {
                    description = POWER_DESCRIPTIONS[0] + FontHelper.colorString(targetCard.name, "y") + POWER_DESCRIPTIONS[1];
                }
            });
        }
        setMoveShortcut(PINE_FOR_THE_SONG, MOVES[PINE_FOR_THE_SONG]);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (targetCard != null) {
            CardModifierManager.removeModifiersById(targetCard, SingingMachineMod.ID, true);
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            onBossVictoryLogic();
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        if (targetCard != null && hasPower(POWER_ID)) {
            tips.add(new CardPowerTip(targetCard.makeStatEquivalentCopy()));
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntVert", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

}