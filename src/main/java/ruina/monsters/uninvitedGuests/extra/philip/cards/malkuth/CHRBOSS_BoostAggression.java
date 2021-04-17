package ruina.monsters.uninvitedGuests.extra.philip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.powers.NextTurnPowerPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class CHRBOSS_BoostAggression extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_BoostAggression.class.getSimpleName());
    public static final int DAMAGE = 6;
    public static final int BLOCK = 8;
    public static final int STR = 5;
    public static final int UPG_BLOCK = 2;
    public static final int UPG_DAMAGE = 3;

    public CHRBOSS_BoostAggression() {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        block = baseBlock = BLOCK;
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = STR;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new GainBlockAction(p, block));
        dmg(m, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(m.lastDamageTaken > 0){ applyToSelfTop(new NextTurnPowerPower(p, new StrengthPower(p, magicNumber))); }
                isDone = true;
            }
        });
    }

    @Override
    public void upp() {
        upgradeDamage(UPG_DAMAGE);
        upgradeBlock(UPG_BLOCK);
    }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_BoostAggression(); }
}