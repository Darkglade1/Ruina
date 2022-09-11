package ruina.monsters.day49.angelaCards.aspiration;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.act3.WristCutter;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class Heartbeat extends AbstractRuinaCard {
    public final static String ID = makeID(Heartbeat.class.getSimpleName());

    private static final int CARD_DRAW = 1;
    private static final int COST = 0;

    public Heartbeat() {
        super(ID, COST, CardType.CURSE, CardRarity.CURSE, CardTarget.NONE, CardColor.CURSE, makeImagePath("cards/" + Pulsation.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = CARD_DRAW;
        selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new DrawCardAction(p, magicNumber));
    }

    @Override
    public void upp() { }

    @Override
    public boolean canUpgrade() { return false; }

    @Override
    public void onRetained() { atb(new MakeTempCardInHandAction(this.makeStatEquivalentCopy())); }

    @Override
    public AbstractCard makeCopy() { return new Heartbeat(); }
}