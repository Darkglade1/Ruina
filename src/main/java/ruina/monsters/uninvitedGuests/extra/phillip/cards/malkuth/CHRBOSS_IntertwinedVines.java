package ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;
import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_IntertwinedVines extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_IntertwinedVines.class.getSimpleName());
    public static final int DRAW = 1;

    public CHRBOSS_IntertwinedVines() {
        super(ID, 0, CardType.SKILL, CardRarity.RARE, CardTarget.SELF, RuinaMod.Enums.EGO);
        selfRetain = true;
        magicNumber = baseMagicNumber = DRAW;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!AbstractDungeon.player.hand.isEmpty()) {
            addToBot(new GamblingChipAction(AbstractDungeon.player, true));
        }
    }

    @Override
    public void upp() {
        rawDescription = languagePack.getCardStrings(cardID).UPGRADE_DESCRIPTION;
        initializeDescription();
    }

    @Override
    public void triggerWhenDrawn() {
        if (upgraded) { addToBot(new DrawCardAction(magicNumber)); }
    }

    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_IntertwinedVines(); }
}