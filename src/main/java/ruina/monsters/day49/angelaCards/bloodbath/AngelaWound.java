package ruina.monsters.day49.angelaCards.bloodbath;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.act3.WristCutter;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.day49.Act1Angela;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_ALLAS;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class AngelaWound extends AbstractRuinaCard {
    public final static String ID = makeID(AngelaWound.class.getSimpleName());

    private static final int SELF_DAMAGE = 12;
    private static final int UP_SELFDAMAGE = 6;
    private static final int COST = 1;

    public AngelaWound() {
        super(ID, COST, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE, CardColor.CURSE, makeImagePath("cards/" + WristCutter.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = SELF_DAMAGE;
        selfRetain = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new DamageAction(adp(), new DamageInfo(adp(), magicNumber, DamageInfo.DamageType.THORNS)));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_SELFDAMAGE);
    }

    @Override
    public void triggerOnManualDiscard() {
        atb(new ExhaustSpecificCardAction(this, adp().discardPile));
    }


    @Override
    public AbstractCard makeCopy() { return new AngelaWound(); }
}