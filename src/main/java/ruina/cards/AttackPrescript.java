package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanAttack;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class AttackPrescript extends AbstractRuinaCard {
    public final static String ID = makeID(AttackPrescript.class.getSimpleName());

    public AttackPrescript() {
        super(ID, 0, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.COLORLESS, makeImagePath("cards/" + CHRBOSS_yanAttack.class.getSimpleName() + ".png"));
        baseDamage = 6;
        selfRetain = true;
        exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
    }

    public void upp() {
        upgradeDamage(3);
    }
}