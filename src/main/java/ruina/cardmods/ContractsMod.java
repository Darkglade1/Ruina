package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.interfaces.AlternateCardCostModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.actions.YeetPlayerAction;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class ContractsMod extends AbstractCardModifier implements AlternateCardCostModifier {

    public static final String ID = makeID(ContractsMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int MAX_HP_TO_ENERGY_RATIO = 2;

    @Override
    public AbstractCardModifier makeCopy() {
        return new ContractsMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[0] + rawDescription;
    }

    @Override
    public int getAlternateResource(AbstractCard card) {
        if (card.costForTurn >= 0 && card.cost >= 0) { //a failsafe because apparently bottled snecko can make cards x cost when dual wielded???
            return 999;
        } else {
            return 0;
        }
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        //To prevent the player from killing themselves with X cost cards
        return card.costForTurn >= 0 && !CardModifierManager.hasModifier(card, ID);
    }

    @Override
    public boolean canSplitCost(AbstractCard card) {
        return true;
    }

    @Override
    public int spendAlternateCost(AbstractCard card, int costToSpend) {
        if (card.costForTurn >= 0 && card.cost >= 0) {
            int maxHPToLose = costToSpend * MAX_HP_TO_ENERGY_RATIO;
            boolean shouldYeet = maxHPToLose >= adp().maxHealth;
            adp().decreaseMaxHealth(maxHPToLose);
            CardCrawlGame.sound.play("BLUNT_FAST");
            AbstractDungeon.effectList.add(new StrikeEffect(adp(), adp().hb.cX, adp().hb.cY, maxHPToLose));
            if (shouldYeet) {
                atb(new YeetPlayerAction());
            }
            costToSpend = 0;
        }
        return costToSpend;
    }
}