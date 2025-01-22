package ruina.cards;

import basemod.AutoAdd;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cardmods.DrawReductionMod;
import ruina.cardmods.EnergyLossMod;
import ruina.cardmods.VulnerableMod;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

@AutoAdd.Ignore
public class Emerald extends AbstractRuinaCard {
    public final static String ID = makeID(Emerald.class.getSimpleName());
    private static final int COST = 0;

    public Emerald() {
        super(ID, COST, CardType.SKILL, CardRarity.SPECIAL, CardTarget.NONE);
        selfRetain = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractCard card : adp().hand.group) {
            if (card instanceof FalsePresent) {
                if (CardModifierManager.hasModifier(card, EnergyLossMod.ID)) {
                    CardModifierManager.removeModifiersById(card, EnergyLossMod.ID, false);
                    card.flash();
                }
                if (CardModifierManager.hasModifier(card, DrawReductionMod.ID)) {
                    CardModifierManager.removeModifiersById(card, DrawReductionMod.ID, false);
                    card.flash();
                }
                if (CardModifierManager.hasModifier(card, VulnerableMod.ID)) {
                    CardModifierManager.removeModifiersById(card, VulnerableMod.ID, false);
                    card.flash();
                }
            }
        }
    }

    @Override
    public void upp() {
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }
}