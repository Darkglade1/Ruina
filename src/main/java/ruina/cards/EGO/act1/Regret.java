package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class Regret extends AbstractEgoCard {
    public final static String ID = makeID(Regret.class.getSimpleName());
    public static final int DAMAGE = 13;
    public static final int UP_DAMAGE = 3;

    public Regret() {
        super(ID, 2, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
        selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractCard card : adp().hand.group) {
                    if (card.type == CardType.STATUS || card.color == CardColor.CURSE || card.type == CardType.CURSE) {
                        dmgTop(m, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upp() { upgradeDamage(UP_DAMAGE); }
}