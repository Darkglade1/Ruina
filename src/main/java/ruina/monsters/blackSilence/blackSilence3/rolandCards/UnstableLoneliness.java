package ruina.monsters.blackSilence.blackSilence3.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence3.BlackSilence3;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class UnstableLoneliness extends AbstractRuinaCard {
    public final static String ID = makeID(UnstableLoneliness.class.getSimpleName());
    private BlackSilence3 parent;

    public UnstableLoneliness(BlackSilence3 parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.lonelyDamage;
        magicNumber = baseMagicNumber = parent.lonelyDebuff;
        this.parent = parent;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new UnstableLoneliness(parent); }
}