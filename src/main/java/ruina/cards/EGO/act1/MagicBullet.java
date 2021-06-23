package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.actions.MagicBulletAction;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class MagicBullet extends AbstractEgoCard {
    public final static String ID = makeID(MagicBullet.class.getSimpleName());
    public static final int DRAW = 3;
    public static final int UP_DRAW = 1;

    public MagicBullet() {
        super(ID, 1, CardType.SKILL, CardTarget.NONE);
        magicNumber = baseMagicNumber = DRAW;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new DrawCardAction(magicNumber, new MagicBulletAction()));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_DRAW);
    }
}