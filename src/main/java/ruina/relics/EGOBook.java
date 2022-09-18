package ruina.relics;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.cardmods.RetainMod;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class EGOBook extends AbstractEasyRelic {
    public static final String ID = makeID(EGOBook.class.getSimpleName());

    public EGOBook() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onPlayerEndTurn() {
        boolean triggered = false;
        for (AbstractCard card : adp().hand.group) {
            if (card.color == AbstractCard.CardColor.CURSE || card.type == AbstractCard.CardType.CURSE) {
                if (!triggered) {
                    this.flash();
                    this.addToTop(new RelicAboveCreatureAction(adp(), this));
                    triggered = true;
                }
                AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(card, adp().hand));
            }
        }
    }

    @Override
    public void onExhaust(AbstractCard card) {
        if (card.color == AbstractCard.CardColor.CURSE || card.type == AbstractCard.CardType.CURSE) {
            this.flash();
            this.addToTop(new RelicAboveCreatureAction(adp(), this));
            AbstractCard egoCard = AbstractEgoCard.getRandomEgoCards(1).get(0);
            CardModifierManager.addModifier(egoCard, new RetainMod());
            atb(new MakeTempCardInHandAction(egoCard));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
