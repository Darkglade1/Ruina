package ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class CHRBOSS_StrikeOfRetribution extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_StrikeOfRetribution.class.getSimpleName());
    public static final int DAMAGE = 20;
    public static final int UPG_DAMAGE = 10;

    public CHRBOSS_StrikeOfRetribution() {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.SELF, RuinaMod.Enums.EGO);
        damage = baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new DamageAction(m, new DamageInfo(AbstractDungeon.player, m.currentBlock == 0 ? damage * 3 : damage, damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
    }

    @Override
    public void upp() {
        upgradeDamage(UPG_DAMAGE);
    }

    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_StrikeOfRetribution(); }
}