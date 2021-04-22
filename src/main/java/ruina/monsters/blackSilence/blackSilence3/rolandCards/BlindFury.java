package ruina.monsters.blackSilence.blackSilence3.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence3.BlackSilence3;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class BlindFury extends AbstractRuinaCard {
    public final static String ID = makeID(BlindFury.class.getSimpleName());

    public BlindFury(BlackSilence3 parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.furyStrength;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}