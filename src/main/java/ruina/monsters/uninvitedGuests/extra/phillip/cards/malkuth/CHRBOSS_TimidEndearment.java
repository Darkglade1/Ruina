package ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.actions.DrawCardCallbackAction;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_TimidEndearment extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_TimidEndearment.class.getSimpleName());
    public static final int BLOCK = 8;
    public static final int DRAW = 1;
    public static final int UPG_BLOCK = 4;

    public CHRBOSS_TimidEndearment() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        block = baseBlock = BLOCK;
        magicNumber = baseMagicNumber = DRAW;
        tags.add(RuinaMod.Enums.ABNO_HTB);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, block));
        addToBot(new DrawCardAction(magicNumber, new DrawCardCallbackAction(block)));
    }

    @Override
    public void upp() {
        upgradeBlock(UPG_BLOCK);
    }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_FourthMatchFlame(); }
}