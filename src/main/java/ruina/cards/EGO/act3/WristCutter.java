package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.actions.IncreaseCostAction;
import ruina.cards.EGO.AbstractEgoCard;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class WristCutter extends AbstractEgoCard {
    public final static String ID = makeID(WristCutter.class.getSimpleName());
    public static final int COST = 2;
    public static final int DAMAGE = 21;
    public static final int UPG_DAMAGE = 7;
    public WristCutter() {
        super(ID, COST, CardType.ATTACK, CardTarget.ALL_ENEMY);
        damage = baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        allDmg(AbstractGameAction.AttackEffect.NONE);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractCard> cards = new ArrayList<>();
                for(AbstractCard c : p.hand.group){ cards.add(c); }
                for(AbstractCard c: cards){
                    p.hand.removeCard(c);
                    p.hand.addToBottom(new Wound());
                }
                isDone = true;
            }
        });
    }

    @Override
    public void upp() { upgradeDamage(UPG_DAMAGE); }
}