package ruina.monsters.eventboss.yan.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.yan.monster.yanDistortion;
import ruina.monsters.eventboss.yan.monster.yanHand;

import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;
import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_FlurryOfFists extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_FlurryOfFists.class.getSimpleName());

    public CHRBOSS_FlurryOfFists(yanDistortion parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.flurryDamage;
        magicNumber = baseMagicNumber = parent.flurryHits;
        secondMagicNumber = baseSecondMagicNumber = parent.flurryStr;
        rawDescription = languagePack.getCardStrings(cardID).EXTENDED_DESCRIPTION[0];
        initializeDescription();
    }

    public CHRBOSS_FlurryOfFists(yanHand parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.flurryDamage;
        magicNumber = baseMagicNumber = parent.flurryHits;
        secondMagicNumber = baseSecondMagicNumber = parent.flurryStr;
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}