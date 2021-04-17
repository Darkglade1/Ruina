package ruina.monsters.uninvitedGuests.extra.philip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.unique.DiscardPileToTopOfDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_Respite extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_Respite.class.getSimpleName());
    public static final int BLOCK = 8;
    public static final int MOVE = 1;
    public static final int UPG_BLOCK = 4;

    public CHRBOSS_Respite() {
        super(ID, 0, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        block = baseBlock = BLOCK;
        magicNumber = baseMagicNumber = MOVE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, block));
        addToBot(new DiscardPileToTopOfDeckAction(p));
    }

    @Override
    public void upp() {
        upgradeBlock(UPG_BLOCK);
    }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_Respite(); }
}