package ruina.monsters.act3.priceOfSilence;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;
import ruina.BetterSpriterAnimation;
import ruina.cards.StolenTime;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class PriceOfSilence extends AbstractRuinaMonster
{
    public static final String ID = makeID(PriceOfSilence.class.getSimpleName());

    private static final byte STOLEN_TIME = 0;
    private static final byte SILENT_HOUR = 1;

    private static final int CARD_PLAY_THRESHOLD = 6;
    private final int STATUS = calcAscensionSpecial(3);

    public static final String POWER_ID = makeID("TickingTime");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PriceOfSilence() {
        this(0.0f, 0.0f);
    }

    public PriceOfSilence(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 280.0f, 390.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("PriceOfSilence/Spriter/PriceOfSilence.scml"));
        setHp(calcAscensionTankiness(82), calcAscensionTankiness(88));
        addMove(STOLEN_TIME, Intent.DEBUFF);
        addMove(SILENT_HOUR, Intent.ATTACK, calcAscensionDamage(17));
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                this.flashWithoutSound();
                this.amount++;
                if (this.amount == CARD_PLAY_THRESHOLD) {
                    this.amount = 0;
                    AbstractDungeon.actionManager.callEndTurnEarlySequence();
                    playSound("SilenceStop");
                    AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
                    AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
                }
                this.updateDescription();
            }

           //this power gets reset in the halfdead allies patch so it works with take extra turn shit LOL

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + CARD_PLAY_THRESHOLD + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case STOLEN_TIME: {
                effectAnimation(adp());
                intoDrawMo(new StolenTime(), STATUS, this);
                resetIdle(1.0f);
                break;
            }
            case SILENT_HOUR: {
                effectAnimation(adp());
                dmg(adp(), info);
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(STOLEN_TIME)) {
            setMoveShortcut(SILENT_HOUR);
        } else {
            setMoveShortcut(STOLEN_TIME);
        }
    }

    private void effectAnimation(AbstractCreature enemy) {
        animationAction("Flash", "SilenceEffect", enemy, this);
    }

}