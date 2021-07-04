package ruina.monsters.eventboss.lulu.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.lulu.monster.Lulu;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_SetAblaze extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_SetAblaze.class.getSimpleName());
    private Lulu parent;

    public CHRBOSS_SetAblaze(Lulu parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.SET_ABLAZE_DAMAGE;
        magicNumber = baseMagicNumber = parent.SET_ABLAZE_HITS;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_SetAblaze(parent); }
}