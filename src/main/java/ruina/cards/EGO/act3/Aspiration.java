package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class Aspiration extends AbstractEgoCard {
    public final static String ID = makeID(Aspiration.class.getSimpleName());

    public static final int COST = 0;
    public static final int STRENGTH = 4;
    public static final int UPG_STRENGTH = 2;

    public Aspiration() {
        super(ID, COST, CardType.SKILL, CardTarget.SELF);
        magicNumber = baseMagicNumber = STRENGTH;
        isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber)));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                att(new LoseHPAction(p, p, p.currentHealth / 2));
                att(new ApplyPowerAction(p, p, new ruina.powers.Aspiration(p, p.currentHealth / 2)));
                isDone = true;
            }
        });
    }

    @Override
    public void upp() { upgradeMagicNumber(UPG_STRENGTH); }
}