package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import ruina.actions.CallbackExhaustAction;
import ruina.cards.EGO.AbstractEgoCard;

import java.util.ArrayList;
import java.util.function.Consumer;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class Wingbeat extends AbstractEgoCard {
    public final static String ID = makeID(Wingbeat.class.getSimpleName());

    public Wingbeat() {
        super(ID, -1, CardType.SKILL, CardTarget.SELF);
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                int effect = EnergyPanel.totalCount;
                if (energyOnUse != -1) {
                    effect = energyOnUse;
                }

                if (adp().hasRelic(ChemicalX.ID)) {
                    effect += 2;
                    adp().getRelic(ChemicalX.ID).flash();
                }

                if (upgraded) {
                    effect++;
                }

                if (effect > 0) {
                    Consumer<ArrayList<AbstractCard>> consumer = abstractCards -> {
                        int totalCost = 0;
                        for (AbstractCard card : abstractCards) {
                            if (card.costForTurn >= 0) {
                                totalCost += card.costForTurn;
                            }
                        }
                        if (totalCost > 0) {
                            atb(new HealAction(p, p, totalCost));
                            applyToTarget(p, p, new DexterityPower(p, totalCost));
                        }
                    };
                    att(new CallbackExhaustAction(effect, false, true, true, consumer));
                    if (!freeToPlayOnce) {
                        adp().energy.use(EnergyPanel.totalCount);
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upp() {
        uDesc();
    }
}