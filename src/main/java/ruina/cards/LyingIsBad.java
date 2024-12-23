package ruina.cards;

import basemod.AutoAdd;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.PersistFields;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.actions.CallbackExhaustAction;
import ruina.cardmods.LieMod;
import ruina.monsters.AbstractRuinaMonster;

import java.util.ArrayList;
import java.util.function.Consumer;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class LyingIsBad extends AbstractRuinaCard {
    public final static String ID = makeID(LyingIsBad.class.getSimpleName());

    private static final int STR = 2;
    private static final int UP_STR = 1;

    public LyingIsBad() {
        super(ID, 1, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF, CardColor.COLORLESS);
        baseMagicNumber = magicNumber = STR;
        PersistFields.setBaseValue(this, 99);
        selfRetain = true;
        this.cardsToPreview = new Lie();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Consumer<ArrayList<AbstractCard>> consumer = abstractCards -> {
            for (AbstractCard card : abstractCards) {
                if (CardModifierManager.hasModifier(card, LieMod.ID)) {
                    applyToTarget(adp(), adp(), new StrengthPower(adp(), magicNumber));
                    AbstractRuinaMonster.playSound("PinoSuccess");
                }
            }
        };
        atb(new CallbackExhaustAction(1, false, true, true, consumer));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_STR);
    }
}