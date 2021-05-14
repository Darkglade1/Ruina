package ruina.cards.EGO.act3;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.cardmods.ManifestMod;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;

public class Remorse extends AbstractEgoCard {
    public final static String ID = makeID(Remorse.class.getSimpleName());
    public static final int COST = 4;
    public static final int DAMAGE = 25;
    public static final int DEBUFF = 3;

    public Remorse() {
        super(ID, COST, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = DEBUFF;
        CardModifierManager.addModifier(this, new ManifestMod());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SMASH);
        applyToTarget(m, p, new WeakPower(m, magicNumber, false));
        applyToTarget(m, p, new VulnerablePower(m, magicNumber, false));
    }

    @Override
    public void upp() { selfRetain = true; }
}