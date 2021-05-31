package ruina.cards.EGO.act1.orchestra;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static com.evacipated.cardcrawl.mod.stslib.StSLib.getMasterDeckEquivalent;
import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class SecondMovement extends AbstractEgoCard {
    public final static String ID = makeID(SecondMovement.class.getSimpleName());
    public static final int DAMAGE = 11;
    public static final int UP_DAMAGE = 3;

    public SecondMovement() {
        super(ID, 1, CardType.ATTACK, CardTarget.ALL_ENEMY);
        damage = baseDamage = DAMAGE;
        isMultiDamage = true;
        cardsToPreview = new ThirdMovement();
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // vfx this later.
        allDmg(AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractCard c = getMasterDeckEquivalent(SecondMovement.this);
                if (c != null) {
                    AbstractCard nm = new ThirdMovement();
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    att(new AddCardToDeckAction(nm));
                }
                isDone = true;
            }
        });
    }

    @Override
    public void upp() { upgradeDamage(UP_DAMAGE); }
}