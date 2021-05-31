package ruina.cards.EGO.act1;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cardmods.ManifestMod;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.cards.ManifestCallbackInterface;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class SanguineDesire extends AbstractEgoCard implements ManifestCallbackInterface {
    public final static String ID = makeID(SanguineDesire.class.getSimpleName());

    public static final int DAMAGE = 18;
    public static final int UP_DAMAGE = 4;
    public static final int BLEED = 4;
    public static final int UP_BLEED = 1;
    AbstractMonster target;

    public SanguineDesire() {
        super(ID, 3, CardType.ATTACK, CardTarget.ENEMY);
        baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = BLEED;
        CardModifierManager.addModifier(this, new ManifestMod());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_HEAVY);
        target = m;
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
        upgradeMagicNumber(UP_BLEED);
    }

    @Override
    public void numCardsUsedToManifest(int num) {
        if (target != null) {
            applyToTarget(target, adp(), new Bleed(target, num * magicNumber));
        }
    }
}