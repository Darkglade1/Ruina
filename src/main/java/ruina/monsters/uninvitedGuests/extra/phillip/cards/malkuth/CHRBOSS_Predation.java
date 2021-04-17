package ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth;

import basemod.AutoAdd;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

@AutoAdd.Ignore
public class CHRBOSS_Predation extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_Predation.class.getSimpleName());
    public static final int DAMAGE = 12;
    public static final int DRAW = 1;
    public static final int HEAL = 6;
    public static final int UPG_DAMAGE = 4;

    public CHRBOSS_Predation() {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = DRAW;
        secondMagicNumber = baseSecondMagicNumber = HEAL;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(m.lastDamageTaken > 0){
                    att(new AddTemporaryHPAction(p, p, secondMagicNumber));
                }
                isDone = true;
            }
        });
        atb(new DrawCardAction(magicNumber));
    }

    @Override
    public void upp() {
        upgradeDamage(UPG_DAMAGE);
    }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_Predation(); }
}