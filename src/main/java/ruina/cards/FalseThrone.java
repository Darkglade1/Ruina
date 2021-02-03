package ruina.cards;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.cardmods.EtherealMod;
import ruina.powers.AbstractLambdaPower;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class FalseThrone extends AbstractEgoCard {
    public final static String ID = makeID(FalseThrone.class.getSimpleName());

    public FalseThrone() {
        super(ID, 2, CardType.SKILL, CardRarity.SPECIAL, CardTarget.NONE);
        purgeOnUse = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(p, p, new AbstractLambdaPower(name, ID, AbstractPower.PowerType.BUFF, false, p, -1) {
            @Override
            public void atStartOfTurn() {
                ArrayList<AbstractCard> cardsToReturn = new ArrayList<>();
                for (AbstractCard card : adp().exhaustPile.group) {
                    if (card.type != CardType.STATUS && card.color != CardColor.CURSE && card.type != CardType.CURSE) {
                        cardsToReturn.add(card);
                    }
                }
                AbstractCardModifier etherealMod = new EtherealMod();
                for (AbstractCard card : cardsToReturn) {
                    adp().exhaustPile.removeCard(card);
                    CardModifierManager.addModifier(card, etherealMod.makeCopy());
                    makeInHand(card);
                }
                atb(new RemoveSpecificPowerAction(owner, owner, this));
            }

            @Override
            public void updateDescription() {
                description = rawDescription;
            }
        });
    }

    @Override
    public void upp() {
        selfRetain = true;
    }
}