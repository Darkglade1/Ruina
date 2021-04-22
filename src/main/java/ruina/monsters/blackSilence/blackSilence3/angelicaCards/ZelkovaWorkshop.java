package ruina.monsters.blackSilence.blackSilence3.angelicaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence3.Angelica;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class ZelkovaWorkshop extends AbstractRuinaCard {
    public final static String ID = makeID(ZelkovaWorkshop.class.getSimpleName());

    public ZelkovaWorkshop(Angelica parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.waltzDamage;
        magicNumber = baseMagicNumber = parent.waltzDamage;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}