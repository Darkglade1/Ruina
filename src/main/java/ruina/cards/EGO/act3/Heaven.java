package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.BurrowingHeavenEffect;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class Heaven extends AbstractEgoCard {
    public final static String ID = makeID(Heaven.class.getSimpleName());
    public static final int BLOCK = 8;
    public static final int UP_BLOCK = 2;

    public static final String POWER_ID = makeID("Heaven");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Heaven() {
        super(ID, -1, CardType.SKILL, CardTarget.SELF);
        baseBlock = BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        final AbstractGameEffect[] vfx = {null};
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(vfx[0] == null){
                    vfx[0] = new BurrowingHeavenEffect();
                    AbstractDungeon.effectsQueue.add(vfx[0]);
                }
                else {
                    isDone = vfx[0].isDone;
                }
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                int effect = EnergyPanel.totalCount;
                if (energyOnUse != -1) {
                    effect = energyOnUse;
                }

                if (adp().hasRelic(ChemicalX.ID)) {
                    effect += 2;
                    adp().getRelic(ChemicalX.ID).flash();
                }

                if (effect > 0) {
                    for(int i = 0; i < effect; ++i) {
                        att(new GainBlockAction(adp(), adp(), block));
                    }

                    if (!freeToPlayOnce) {
                        adp().energy.use(EnergyPanel.totalCount);
                    }
                }
                this.isDone = true;
            }
        });
        applyToTarget(adp(), adp(), new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, adp(), -1) {
            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS && info.owner != null && info.owner != this.owner) {
                    if (owner.currentBlock > 0) {
                        this.flash();
                        att(new DamageAction(info.owner, new DamageInfo(this.owner, owner.currentBlock, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL, true));
                    }
                }
                return damageAmount;
            }

            @Override
            public void atEndOfRound() {
                atb(new RemoveSpecificPowerAction(owner, owner, this));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void upp() {
        upgradeBlock(UP_BLOCK);
    }
}