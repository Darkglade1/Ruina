package ruina.monsters.uninvitedGuests.philip.malkuthCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.philip.Malkuth;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Emotional extends AbstractRuinaCard {
    public final static String ID = makeID(Emotional.class.getSimpleName());

    public Emotional(Malkuth parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.emotionalEmotions;
        baseBlock = parent.SELF_BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}