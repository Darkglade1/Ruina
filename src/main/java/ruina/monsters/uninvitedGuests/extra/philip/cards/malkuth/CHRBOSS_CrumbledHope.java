package ruina.monsters.uninvitedGuests.extra.philip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.actions.DrawCardCallbackAction;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_CrumbledHope extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_CrumbledHope.class.getSimpleName());
    public static final int DAMAGE = 7;
    public static final int DRAW = 1;
    public static final int UPG_DAMAGE = 3;

    public CHRBOSS_CrumbledHope() {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = DRAW;
        tags.add(RuinaMod.Enums.ABNO_SG);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        DamageInfo info = new DamageInfo(p, this.damage, this.damageTypeForTurn);
        AbstractGameAction.AttackEffect effect = AbstractGameAction.AttackEffect.BLUNT_LIGHT;
        addToBot(new DamageAction(m, info, effect));
        addToBot(new DrawCardAction(magicNumber, new DrawCardCallbackAction(info, m, effect)));
    }

    @Override
    public void upp() {
        upgradeDamage(UPG_DAMAGE);
    }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_CrumbledHope(); }
}