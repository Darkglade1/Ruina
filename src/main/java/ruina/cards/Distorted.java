package ruina.cards;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SoulboundField;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;


public class Distorted extends AbstractRuinaCard {
    public final static String ID = makeID(Distorted.class.getSimpleName());

    public Distorted() {
        super(ID, -2, CardType.CURSE, CardRarity.CURSE, CardTarget.NONE, CardColor.CURSE);
        SoulboundField.soulbound.set(this, true);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    public void upp() {

    }
}