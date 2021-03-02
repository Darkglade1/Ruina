package ruina.monsters.eventBoss.core.actions.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.monsters.AbstractRuinaCardMonster;
import ruina.monsters.eventBoss.core.AbstractRuinaBossCard;

public class EnemyUseCardAction extends AbstractGameAction {
    private static final float DUR = 0.15f;
    public AbstractCreature target;
    public boolean exhaustCard;
    public boolean returnToHand;
    public boolean reboundCard;
    private AbstractCard targetCard;

    public EnemyUseCardAction(final AbstractCard card, final AbstractCreature target) {
        this.target = null;
        this.reboundCard = false;
        this.targetCard = card;
        this.target = target;
        if (card.exhaustOnUseOnce || card.exhaust) {
            this.exhaustCard = true;
        }
        this.setValues(AbstractRuinaCardMonster.boss, null, 1);
        this.duration = 0.15f;
        for (final AbstractPower p : AbstractRuinaCardMonster.boss.powers) {
            if (!card.dontTriggerOnUseCard && p.type != AbstractPower.PowerType.DEBUFF) {
                p.onUseCard(card, this.makeNormalCardAction());
            }
        }
        for (final AbstractRelic r : AbstractRuinaCardMonster.boss.relics) {
            if (!card.dontTriggerOnUseCard) {
                r.onUseCard(card, this.makeNormalCardAction());
            }
        }
        for (final AbstractCard c : AbstractRuinaCardMonster.boss.hand.group) {
            if (!card.dontTriggerOnUseCard) {
                c.triggerOnCardPlayed(card);
            }
        }
        if (this.exhaustCard) {
            this.actionType = ActionType.EXHAUST;
        } else {
            this.actionType = ActionType.USE;
        }
    }

    public EnemyUseCardAction(final AbstractCard targetCard) {
        this(targetCard, null);
    }

    public UseCardAction makeNormalCardAction() {
        AbstractCard cc = this.targetCard.makeStatEquivalentCopy();
        cc.dontTriggerOnUseCard = true;
        return new UseCardAction(cc, AbstractRuinaCardMonster.boss);
    }

    @Override
    public void update() {
        if (this.duration == 0.15f) {
            //SlimeboundMod.logger.info("using card" + this.reboundCard);
            if(AbstractRuinaCardMonster.boss != null){
                for (final AbstractPower p : AbstractRuinaCardMonster.boss.powers) {
                    if (!this.targetCard.dontTriggerOnUseCard && p.type != AbstractPower.PowerType.DEBUFF) { p.onAfterUseCard(this.targetCard, this.makeNormalCardAction()); }
                }

                this.targetCard.freeToPlayOnce = false;
                this.targetCard.isInAutoplay = false;
                if (this.targetCard.purgeOnUse) {
                    this.addToTop(new EnemyShowCardAndPoofAction(this.targetCard));
                    this.isDone = true;
                    AbstractRuinaCardMonster.boss.cardInUse = null;
                    return;
                }
                if (this.targetCard.type == AbstractCard.CardType.POWER) {
                    this.addToTop(new EnemyShowCardAction(this.targetCard));
                    if (Settings.FAST_MODE) {
                        this.addToTop(new WaitAction(0.1f));
                    } else {
                        this.addToTop(new WaitAction(0.7f));
                    }
                    AbstractRuinaCardMonster.boss.hand.empower(this.targetCard);
                    this.isDone = true;
                    AbstractRuinaCardMonster.boss.hand.applyPowers();
                    AbstractRuinaCardMonster.boss.hand.glowCheck();
                    AbstractRuinaCardMonster.boss.cardInUse = null;
                    return;
                }
                AbstractRuinaCardMonster.boss.cardInUse = null;
                boolean spoonProc = false;
                if (this.exhaustCard && AbstractRuinaCardMonster.boss.hasRelic("Strange Spoon") && this.targetCard.type != AbstractCard.CardType.POWER) { spoonProc = AbstractDungeon.cardRandomRng.randomBoolean(); }
                //SlimeboundMod.logger.info("before spoon check");
                //SlimeboundMod.logger.info("using card" + this.reboundCard);
                if (!this.exhaustCard || spoonProc) {
                    if (spoonProc) {
                        AbstractRuinaCardMonster.boss.getRelic("Strange Spoon").flash();
                    }
                    if (this.reboundCard) {
                        //SlimeboundMod.logger.info("detected rebound card");
                        AbstractRuinaCardMonster.boss.hand.moveToDeck(this.targetCard, false);
                    } else if (this.targetCard.shuffleBackIntoDrawPile) {
                        AbstractRuinaCardMonster.boss.hand.moveToDeck(this.targetCard, true);
                    } else if (this.targetCard.returnToHand) {
                        AbstractRuinaCardMonster.boss.hand.moveToHand(this.targetCard);
                        AbstractRuinaCardMonster.boss.onCardDrawOrDiscard();
                    } else {
                        AbstractRuinaCardMonster.boss.chosenArchetype.moveCardIntoDiscardPile(targetCard);
                        AbstractRuinaCardMonster.boss.chosenArchetype.getHandPile().removeCard(targetCard);
                        //AbstractRuinaCardMonster.boss.hand.moveToDiscardPile(this.targetCard);
                    }
                } else {
                    AbstractRuinaCardMonster.boss.chosenArchetype.moveCardIntoExhaustPile(targetCard);
                    AbstractRuinaCardMonster.boss.chosenArchetype.getHandPile().removeCard(targetCard);
                }
                this.targetCard.exhaustOnUseOnce = false;
                this.targetCard.dontTriggerOnUseCard = false;
                this.addToBot(new EnemyHandCheckAction());
            }
        }
        this.tickDuration();
    }
}