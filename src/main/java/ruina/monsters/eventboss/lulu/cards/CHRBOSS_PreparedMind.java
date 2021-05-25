package ruina.monsters.eventboss.lulu.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.lulu.monster.Lulu;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_PreparedMind extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_PreparedMind.class.getSimpleName());
    private Lulu parent;

    public CHRBOSS_PreparedMind(Lulu parent) {
        super(ID, 1, CardType.SKILL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = parent.PREPARED_MIND_STRENGTH;
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_PreparedMind(parent); }
}