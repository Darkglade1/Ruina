package ruina.monsters.act2.greed;

import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.CenterOfAttention;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class KingOfGreed extends AbstractRuinaMonster
{
    public static final String ID = makeID(KingOfGreed.class.getSimpleName());

    private static final byte ROAD_OF_KING = 0;
    private static final byte FIXATION = 1;
    private static final byte EDACITY = 2;

    private static final int FRAIL = 2;
    private static final int VULNERABLE = 1;

    private boolean canPlaySound = true;

    public static final String POWER_ID = makeID("Road");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    AbstractPower road;

    public KingOfGreed() {
        this(0.0f, 0.0f);
    }

    public KingOfGreed(final float x, final float y) {
        super(ID, ID, 110, 10.0F, 0, 200.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Greed/Spriter/Greed.scml"));
        setHp(calcAscensionTankiness(115), calcAscensionTankiness(122));
        addMove(ROAD_OF_KING, Intent.ATTACK, calcAscensionDamage(26));
        addMove(FIXATION, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(EDACITY, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
    }

    @Override
    public void usePreBattleAction() {
        road = new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 2) {
            @Override
            public void updateDescription() {
                if (amount == 0) {
                    description = POWER_DESCRIPTIONS[3];
                } else if (amount == 1) {
                    description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[2];
                } else {
                    description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
                }
            }
        };
        applyToTarget(this, this, road);
        applyToTarget(this, this, new CenterOfAttention(this));
        playSound("GreedDiamond");
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        switch (this.nextMove) {
            case ROAD_OF_KING: {
                specialAnimation(adp());
                dmg(adp(), info);
                resetIdle(1.0f);
                break;
            }
            case FIXATION: {
                slashAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                resetIdle();
                break;
            }
            case EDACITY: {
                stabAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), FRAIL, true));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
        canPlaySound = true;
    }

    @Override
    protected void getMove(final int num) {
        int turn;
        if (this.lastMove(EDACITY)) {
            setMoveShortcut(FIXATION);
            turn = FIXATION;
        } else if (this.lastMove(FIXATION)) {
            if (canPlaySound) { //avoid spamming the sound in case someone calls rollMove a bunch
                playSound("GreedStrAtkReady");
                runAnim("SpecialIdle");
                canPlaySound = false;
            }
            setMoveShortcut(ROAD_OF_KING);
            turn = ROAD_OF_KING;
        } else {
            setMoveShortcut(EDACITY);
            turn = EDACITY;
        }
        if (road != null) {
            road.amount = turn;
            road.updateDescription();
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof BrilliantBliss) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "GreedVertChange", enemy, this);
    }

    private void stabAnimation(AbstractCreature enemy) {
        animationAction("Stab", "GreedStabChange", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "GreedStrAtkChange", enemy, this);
    }

}