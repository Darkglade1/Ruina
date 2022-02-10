package ruina.cards.EGO.act2.FadedMemories;

import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class FadedMemories extends AbstractEgoCard {
    public final static String ID = makeID(FadedMemories.class.getSimpleName());

    private static final int DRAW = 1;
    private static final int EXHAUST_DRAW = 2;

    protected static AbstractMonster target;

    public FadedMemories() {
        super(ID, 0, CardType.SKILL, CardTarget.NONE);
        this.rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[0] + " NL " + cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
        magicNumber = baseMagicNumber = DRAW;
        secondMagicNumber = baseSecondMagicNumber = EXHAUST_DRAW;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> options = new ArrayList<>();
        AbstractCard card1 = new FadedMemories1(this.uuid);
        AbstractCard card2 = new FadedMemories2(this.uuid);
        if (this.upgraded) {
            card1.upgrade();
            card2.upgrade();
        }
        options.add(card1);
        options.add(card2);
        atb(new ChooseOneAction(options));
    }

    @Override
    public void triggerOnExhaust() {
        exhaust = false;
    }

    @Override
    public void upp() {
        isInnate = true;
    }
}