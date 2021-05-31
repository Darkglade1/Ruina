package ruina.cards.EGO.act1.orchestra;

import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static com.evacipated.cardcrawl.mod.stslib.StSLib.getMasterDeckEquivalent;
import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class FirstMovement extends AbstractEgoCard {
    public final static String ID = makeID(FirstMovement.class.getSimpleName());
    public static final int DAMAGE = 8;
    public static final int UP_DAMAGE = 3;

    public FirstMovement() {
        super(ID, 1, CardType.ATTACK, CardTarget.ALL_ENEMY);
        damage = baseDamage = DAMAGE;
        isMultiDamage = true;
        cardsToPreview = new SecondMovement();
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // vfx this later.
        allDmg(AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractCard c = getMasterDeckEquivalent(FirstMovement.this);
                if (c != null) {
                    AbstractCard nm = new SecondMovement();
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    att(new AddCardToDeckAction(nm));
                }
                isDone = true;
            }
        });
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
    }
}