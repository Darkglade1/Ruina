package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Bleed;
import ruina.powers.RuinaIntangible;
import ruina.powers.act2.Hunter;
import ruina.util.DetailedIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BadWolf extends AbstractRuinaMonster
{
    public static final String ID = makeID(BadWolf.class.getSimpleName());

    private static final byte CLAW = 0;
    private static final byte BITE = 1;
    private static final byte HUNT = 2;

    public static final float HP_THRESHOLD = 0.5f;
    private final int INTANGIBLE_TURNS = calcAscensionSpecial(1);
    private final int BLEED = calcAscensionSpecial(2);
    private final int STRENGTH = calcAscensionSpecial(4);

    public static final int HUNT_PHASE = 2;
    public static final int POST_HUNT_PHASE = 3;

    public BadWolf() {
        this(0.0f, 0.0f);
    }

    public BadWolf(final float x, final float y) {
        super(ID, ID, 40, -5.0F, 0, 300.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BadWolf/Spriter/BadWolf.scml"));
        setHp(calcAscensionTankiness(80), calcAscensionTankiness(84));
        addMove(CLAW, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(BITE, Intent.ATTACK_BUFF, calcAscensionDamage(14));
        addMove(HUNT, Intent.ATTACK, calcAscensionDamage(18));
    }

    @Override
    public void usePreBattleAction() {
        playSound("WolfPhase");
        applyToTarget(this, this, new Hunter(this, INTANGIBLE_TURNS, STRENGTH, HP_THRESHOLD));
        if (this.phase == HUNT_PHASE) {
            runAnim("Idle" + getAnimFromPhase());
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case CLAW: {
                slashAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Bleed(adp(), BLEED));
                resetIdle();
                break;
            }
            case BITE: {
                biteAnimation(adp());
                atb(new VampireDamageActionButItCanFizzle(adp(), info, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                break;
            }
            case HUNT: {
                slashAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.phase >= HUNT_PHASE) {
            setMoveShortcut(HUNT);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(CLAW)) {
                possibilities.add(CLAW);
            }
            if (!this.lastMove(BITE)) {
                possibilities.add(BITE);
            }
            if (!this.lastMove(HUNT)) {
                possibilities.add(HUNT);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case CLAW: {
                DetailedIntent detail = new DetailedIntent(this, BLEED, DetailedIntent.BLEED_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case BITE: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.LIFESTEAL);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void setPhase(int newPhase) {
        super.setPhase(newPhase);
        if (phase == HUNT_PHASE) {
            playSound("Fog", 0.9f);
        }
        runAnim("Idle" + getAnimFromPhase());
    }

    private int getAnimFromPhase() {
        if (phase == HUNT_PHASE) {
            return HUNT_PHASE;
        } else {
            return 1;
        }
    }

    private void biteAnimation(AbstractCreature enemy) {
        animationAction("Bite" + getAnimFromPhase(), "Bite", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash" + getAnimFromPhase(), "Claw", enemy, this);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + getAnimFromPhase());
                this.isDone = true;
            }
        });
    }

    public void checkSkulkTrigger() {
        if (this.currentHealth < (int)(this.maxHealth * HP_THRESHOLD) && phase == DEFAULT_PHASE) {
            applyToTarget(this, this, new StrengthPower(this, STRENGTH));
            applyToTarget(this, this, new RuinaIntangible(this, INTANGIBLE_TURNS, false));
            setPhase(HUNT_PHASE);
            rollMove();
            createIntent();
        }
    }

}