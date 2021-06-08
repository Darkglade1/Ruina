package ruina.cards.EGO.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.vfx.BloodSplatter;
import ruina.vfx.ThirstEffect;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class Thirst extends AbstractEgoCard {
    public final static String ID = makeID(Thirst.class.getSimpleName());

    public static final int BLEED = 9;
    public static final int STRENGTH = 1;

    public static final String POWER_ID = makeID("Thirst");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Thirst() {
        super(ID, 1, CardType.SKILL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = BLEED;
        secondMagicNumber = baseSecondMagicNumber = STRENGTH;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        final AbstractGameEffect[] vfx = {null};
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(vfx[0] == null){
                    vfx[0] = new ThirstEffect();
                    AbstractDungeon.effectsQueue.add(vfx[0]);
                }
                else {
                    isDone = vfx[0].isDone;
                }
            }
        });
        applyToTarget(m, p, new Bleed(m, magicNumber));
        applyToTarget(p, p, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, p, secondMagicNumber) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner == this.owner) {
                    if (target.hasPower(Bleed.POWER_ID)) {
                        this.flash();
                        applyToTarget(owner, owner, new StrengthPower(owner, amount));
                    }

                }
            }

            @Override
            public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
                atb(new RemoveSpecificPowerAction(owner, owner, this));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void upp() {
        selfRetain = true;
    }
}