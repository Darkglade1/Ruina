package ruina.cards.EGO.act3;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.cardmods.ManifestMod;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.cards.ManifestCallbackInterface;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class Twilight extends AbstractEgoCard implements ManifestCallbackInterface {
    public final static String ID = makeID(Twilight.class.getSimpleName());

    public static final int DAMAGE = 15;
    public static final int UP_DAMAGE = 5;

    public Twilight() {
        super(ID, 3, CardType.ATTACK, CardTarget.ALL_ENEMY);
        baseDamage = DAMAGE;
        isMultiDamage = true;
        CardModifierManager.addModifier(this, new ManifestMod());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        allDmg(AbstractGameAction.AttackEffect.LIGHTNING);
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
    }

    @Override
    public void numCardsUsedToManifest(int num) {
        for (AbstractMonster mo : monsterList()) {
            applyToTarget(mo, adp(), new WeakPower(mo, num, false));
        }
    }
}