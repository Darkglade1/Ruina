package ruina.monsters.blackSilence.blackSilence3.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence3.BlackSilence3;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class WaltzInBlack extends AbstractRuinaCard {
    public final static String ID = makeID(WaltzInBlack.class.getSimpleName());
    private BlackSilence3 parent;

    public WaltzInBlack(BlackSilence3 parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + "Waltz" + ".png"));
        damage = baseDamage = parent.waltzDamage;
        magicNumber = baseMagicNumber = parent.waltzHits;
        this.parent = parent;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { uDesc();}

    @Override
    public AbstractCard makeCopy() { return new WaltzInBlack(parent); }
}