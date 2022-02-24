package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;
import ruina.actions.AspirationAction;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class Aspiration extends AbstractEgoCard {
    public final static String ID = makeID(Aspiration.class.getSimpleName());

    public static final int COST = 0;
    public static final int STRENGTH = 3;
    public static final int UPG_STRENGTH = 1;

    public Aspiration() {
        super(ID, COST, CardType.SKILL, CardTarget.SELF);
        magicNumber = baseMagicNumber = STRENGTH;
        isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber)));
        if (Settings.FAST_MODE) {
            atb(new VFXAction(new OfferingEffect(), 0.1F));
        } else {
            atb(new VFXAction(new OfferingEffect(), 0.5F));
        }
        int initialHP = adp().currentHealth;
        atb(new LoseHPAction(p, p, p.currentHealth / 2));
        atb(new AspirationAction(initialHP));
    }

    @Override
    public void upp() { upgradeMagicNumber(UPG_STRENGTH); }
}