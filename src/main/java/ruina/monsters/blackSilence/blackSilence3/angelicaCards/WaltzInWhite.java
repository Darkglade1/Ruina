package ruina.monsters.blackSilence.blackSilence3.angelicaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence3.Angelica;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class WaltzInWhite extends AbstractRuinaCard {
    public final static String ID = makeID(WaltzInWhite.class.getSimpleName());
    private Angelica parent;

    public WaltzInWhite(Angelica parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + "Waltz" + ".png"));
        damage = baseDamage = parent.waltzDamage;
        magicNumber = baseMagicNumber = parent.waltzDamage;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new WaltzInWhite(parent); }
}