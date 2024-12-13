package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act3.Unnerving;
import ruina.util.DetailedIntent;
import ruina.vfx.BurrowingHeavenEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BurrowingHeaven extends AbstractRuinaMonster
{
    public static final String ID = makeID(BurrowingHeaven.class.getSimpleName());

    private static final byte YOUR_OWN_HEAVEN = 0;
    private static final byte BLOODY_WINGS = 1;
    private static final byte GAZE_OF_OTHERS = 2;

    private final int FRAIL = calcAscensionSpecial(1);
    private final int VULNERABLE = calcAscensionSpecial(2);
    private final int STR_DOWN = calcAscensionSpecial(2);
    private final int DAMAGE_REDUCTION = 50;

    public BurrowingHeaven() {
        this(0.0f, 0.0f);
    }

    public BurrowingHeaven(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 280.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BurrowingHeaven/Spriter/BurrowingHeaven.scml"));
        setHp(calcAscensionTankiness(140));
        addMove(YOUR_OWN_HEAVEN, Intent.ATTACK, calcAscensionDamage(21));
        addMove(BLOODY_WINGS, Intent.ATTACK_DEBUFF, calcAscensionDamage(16));
        addMove(GAZE_OF_OTHERS, Intent.STRONG_DEBUFF);
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new Unnerving(this, DAMAGE_REDUCTION));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case YOUR_OWN_HEAVEN: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case BLOODY_WINGS: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), FRAIL, true));
                resetIdle();
                break;
            }
            case GAZE_OF_OTHERS: {
                specialAnimation(adp());
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
                applyToTarget(adp(), this, new StrengthPower(adp(), -STR_DOWN));
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(YOUR_OWN_HEAVEN)) {
            possibilities.add(YOUR_OWN_HEAVEN);
        }
        if (!this.lastMove(BLOODY_WINGS)) {
            possibilities.add(BLOODY_WINGS);
        }
        if (!this.lastMove(GAZE_OF_OTHERS) && !this.lastMoveBefore(GAZE_OF_OTHERS)) {
            possibilities.add(GAZE_OF_OTHERS);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case BLOODY_WINGS: {
                DetailedIntent detail = new DetailedIntent(this, FRAIL, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case GAZE_OF_OTHERS: {
                DetailedIntent detail = new DetailedIntent(this, -STR_DOWN, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, VULNERABLE, DetailedIntent.VULNERABLE_TEXTURE);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "WoodFinish", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Debuff", null, enemy, this);
    }

}