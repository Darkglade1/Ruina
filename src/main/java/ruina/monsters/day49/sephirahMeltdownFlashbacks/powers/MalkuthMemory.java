package ruina.monsters.day49.sephirahMeltdownFlashbacks.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.DarkShackles;
import com.megacrit.cardcrawl.cards.red.Disarm;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.EnergyDownPower;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class MalkuthMemory extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID(MalkuthMemory.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int ENERGY_DRAW_REDUCTION = 1;
    private static int STRENGTH = 2;

    public MalkuthMemory(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, STRENGTH);
        updateDescription();
        amount2 = ENERGY_DRAW_REDUCTION;
        loadRegion("combust");
    }

    @Override
    public void atEndOfRound() {
        super.atEndOfRound();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for(AbstractCard c: adp().masterDeck.group){
                    c.type = returnRandomType();
                }
                for(AbstractCard c: adp().drawPile.group){
                    c.type = returnRandomType();
                }
                for(AbstractCard c: adp().discardPile.group){
                    c.type = returnRandomType();
                }
                for(AbstractCard c: adp().exhaustPile.group){
                    c.type = returnRandomType();
                }
                isDone = true;
            }
        });
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        switch (card.type){
            case ATTACK:
                atb(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount)));
                break;
            case SKILL:
                atb(new ApplyPowerAction(adp(), owner, new StrengthPower(owner, -amount), -amount));
                if (!adp().hasPower(ArtifactPower.POWER_ID)) {
                    atb(new ApplyPowerAction(adp(), owner, new GainStrengthPower(adp(), amount), amount));
                }
                break;
            case POWER:
                atb(new ApplyPowerAction(adp(), adp(), new EnergyDownPower(adp(), amount2)));
                atb(new ApplyPowerAction(adp(), adp(), new DrawReductionPower(adp(), amount2)));
                break;
        }
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount, amount, amount2);
    }

    public AbstractCard.CardType returnRandomType() {
        switch (AbstractDungeon.miscRng.random(0, 2)) {
            case 0:
                return AbstractCard.CardType.ATTACK;
            case 1:
                return AbstractCard.CardType.SKILL;
            case 2:
                return AbstractCard.CardType.POWER;
        }
        return AbstractCard.CardType.CURSE;
    }

    @Override
    public void onRemove() {
        super.onRemove();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for(AbstractCard c: adp().masterDeck.group){ c.type = c.makeCopy().type; }
                for(AbstractCard c: adp().drawPile.group){ c.type = c.makeCopy().type; }
                for(AbstractCard c: adp().discardPile.group){ c.type = c.makeCopy().type; }
                for(AbstractCard c: adp().exhaustPile.group){ c.type = c.makeCopy().type; }
                isDone = true;
            }
        });
    }
}