package ruina.monsters.act2.greed;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.CenterOfAttention;
import ruina.powers.act2.Road;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

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
        applyToTarget(this, this, new Road(this, 2));
        applyToTarget(this, this, new CenterOfAttention(this));

        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        if (move.nextMove == ROAD_OF_KING) {
            playSound("GreedStrAtkReady");
            runAnim("SpecialIdle");
        } else {
            playSound("GreedDiamond");
        }
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
        AbstractPower road = getPower(Road.POWER_ID);
        if (road != null) {
            road.amount = turn;
            road.updateDescription();
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case FIXATION: {
                DetailedIntent detail = new DetailedIntent(this, VULNERABLE, DetailedIntent.VULNERABLE_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case EDACITY: {
                DetailedIntent detail = new DetailedIntent(this, FRAIL, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
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