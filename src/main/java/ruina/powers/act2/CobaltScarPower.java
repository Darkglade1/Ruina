package ruina.powers.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.atb;

public class CobaltScarPower extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("CobaltScarPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int damage;
    private int threshold;

    public CobaltScarPower(AbstractCreature owner, int amount, int threshold) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.damage = amount;
        this.amount = 0;
        this.threshold = threshold;
        updateDescription();
    }

    @Override
    public void onAfterCardPlayed(AbstractCard card) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            amount++;
            if (amount % threshold == 0) {
                amount = 0;
                this.flash();
                this.addToBot(new SFXAction("ATTACK_HEAVY"));
                if (Settings.FAST_MODE) {
                    atb(new VFXAction(new CleaveEffect()));
                } else {
                    atb(new VFXAction(this.owner, new CleaveEffect(), 0.2F));
                }
                atb(new DamageAllEnemiesAction(this.owner, DamageInfo.createDamageMatrix(damage, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE, true));
            } else {
                flashWithoutSound();
            }
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        damage += stackAmount;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + threshold + DESCRIPTIONS[1] + damage + DESCRIPTIONS[2];
    }
}
