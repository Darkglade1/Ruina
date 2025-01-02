package ruina.cards.EGO.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.combat.MindblastEffect;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class LoveAndHate extends AbstractEgoCard {
    public final static String ID = makeID(LoveAndHate.class.getSimpleName());

    public static final int DAMAGE = 10;
    public static final int FIRST_THRESHOLD = 4;
    public static final int SECOND_THRESHOLD = 6;
    public static final float FIRST_THRESHOLD_BONUS = 1.5f;
    public static final float SECOND_THRESHOLD_BONUS = 2.0f;

    public LoveAndHate() {
        super(ID, -1, CardType.ATTACK, CardTarget.ALL_ENEMY);
        baseDamage = DAMAGE;
        baseMagicNumber = magicNumber = FIRST_THRESHOLD;
        baseSecondMagicNumber = secondMagicNumber = SECOND_THRESHOLD;
        isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int effect = EnergyPanel.totalCount;
        if (energyOnUse != -1) {
            effect = energyOnUse;
        }

        if (adp().hasRelic(ChemicalX.ID)) {
            effect += 2;
            adp().getRelic(ChemicalX.ID).flash();
        }

        if (effect > 0) {
            float bonusMultiplier = 1;
            if (effect >= FIRST_THRESHOLD) {
                bonusMultiplier *= FIRST_THRESHOLD_BONUS;
            }
            if (upgraded && effect >= SECOND_THRESHOLD) {
                bonusMultiplier *= SECOND_THRESHOLD_BONUS;
            }
            for (int i = 0; i < multiDamage.length; i++) {
                multiDamage[i] *= effect * bonusMultiplier;
            }

            if (!freeToPlayOnce) {
                adp().energy.use(EnergyPanel.totalCount);
            }
            atb(new SFXAction("ATTACK_HEAVY"));
            atb(new VFXAction(p, new MindblastEffect(p.dialogX, p.dialogY, p.flipHorizontal), 0.1F));
            allDmg(AbstractGameAction.AttackEffect.NONE);
        }
    }

    @Override
    public float getTitleFontSize()
    {
        return 11;
    }

    @Override
    public void upp() {
        uDesc();
    }
}