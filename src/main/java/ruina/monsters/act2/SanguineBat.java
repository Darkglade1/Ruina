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
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SanguineBat extends AbstractRuinaMonster
{
    public static final String ID = makeID(SanguineBat.class.getSimpleName());

    private static final byte BLOODSUCKING = 0;
    private static final byte DIGGING_TEETH = 1;
    private static final byte AVID_THIRST = 2;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);

    public SanguineBat() {
        this(0.0f, 0.0f);
    }

    public SanguineBat(final float x, final float y) {
        super(ID, ID, 40, -5.0F, 50.0f, 230.0f, 155.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Bat/Spriter/Bat.scml"));
        setHp(calcAscensionTankiness(31), calcAscensionTankiness(37));
        addMove(BLOODSUCKING, Intent.ATTACK_BUFF, calcAscensionDamage(5), 2);
        addMove(DIGGING_TEETH, Intent.ATTACK_DEBUFF, calcAscensionDamage(7));
        addMove(AVID_THIRST, Intent.BUFF);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case BLOODSUCKING: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    atb(new VampireDamageActionButItCanFizzle(adp(), info, AbstractGameAction.AttackEffect.NONE));
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case DIGGING_TEETH: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Bleed(adp(), DEBUFF));
                resetIdle();
                break;
            }
            case AVID_THIRST: {
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(BLOODSUCKING) && !firstMove) {
            possibilities.add(BLOODSUCKING);
        }
        if (!this.lastMove(DIGGING_TEETH)) {
            possibilities.add(DIGGING_TEETH);
        }
        if (!this.lastMove(AVID_THIRST) && !this.lastMoveBefore(AVID_THIRST)) {
            possibilities.add(AVID_THIRST);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case BLOODSUCKING: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.LIFESTEAL);
                detailsList.add(detail);
                break;
            }
            case DIGGING_TEETH: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.BLEED_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case AVID_THIRST: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BatAttack", 0.5f, enemy, this);
    }

}