package ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth;

import basemod.AutoAdd;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FlameBarrierPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.att;

@AutoAdd.Ignore
public class CHRBOSS_RaveningHunger extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_RaveningHunger.class.getSimpleName());
    public static final int DAMAGE = 3;
    public static final int UPG_DAMAGE = 2;

    public static final int HEAL = 3;
    public static final int TIMES = 5;
    public static final int TEMP_THORNS = 6;

    public CHRBOSS_RaveningHunger() {
        super(ID, 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = TIMES;
        secondMagicNumber = baseSecondMagicNumber = HEAL;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for(int i = 0; i < magicNumber; i += 1){
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
        }
        applyToSelf(new FlameBarrierPower(p, secondMagicNumber));
    }

    @Override
    public void upp() {
        upgradeDamage(UPG_DAMAGE);
    }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_RaveningHunger(); }
}