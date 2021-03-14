package ruina.cards.EGO.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ModifyDamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.actions.CallbackExhaustAction;
import ruina.cards.EGO.AbstractEgoCard;

import java.util.ArrayList;
import java.util.function.Consumer;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class Lumber extends AbstractEgoCard {
    public final static String ID = makeID(Lumber.class.getSimpleName());

    public static final int DAMAGE = 10;
    public static final int EXHAUST = 2;

    public Lumber() {
        super(ID, 1, CardType.ATTACK, CardTarget.ENEMY);
        baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = EXHAUST;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_HEAVY);
        AbstractCard thisCard = this;
        Consumer<ArrayList<AbstractCard>> consumer = abstractCards -> {
            for (AbstractCard card : abstractCards) {
                if (card.type == CardType.ATTACK) {
                    if (card.baseDamage > 0) {
                        atb(new ModifyDamageAction(thisCard.uuid, card.baseDamage));
                    }
                }
            }
        };
        atb(new CallbackExhaustAction(magicNumber, false, true, true, consumer));
    }

    @Override
    public void upp() {
        selfRetain = true;
    }
}