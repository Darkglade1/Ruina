package ruina.monsters.act3.whiteNight;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class StaffApostle extends AbstractRuinaMonster {
    public static final String ID = makeID(StaffApostle.class.getSimpleName());

    private static final byte THY_WORDS = 0;
    private static final byte GIVE_US_REST = 1;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final WhiteNight whiteNight;

    public StaffApostle(final float x, final float y, WhiteNight whiteNight) {
        super(ID, ID, 50, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("StaffApostle/Spriter/StaffApostle.scml"));
        setHp(calcAscensionTankiness(52), calcAscensionTankiness(56));
        addMove(THY_WORDS, Intent.ATTACK_BUFF, calcAscensionDamage(7));
        addMove(GIVE_US_REST, Intent.ATTACK_DEBUFF, calcAscensionDamage(9));
        this.whiteNight = whiteNight;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (nextMove) {
            case THY_WORDS:
                attackAnimation(adp());
                dmg(adp(), info);
                for (AbstractMonster mo : monsterList()) {
                    applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                }
                resetIdle();
                break;
            case GIVE_US_REST:
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                resetIdle();
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(THY_WORDS)) {
            setMoveShortcut(GIVE_US_REST);
        } else {
            setMoveShortcut(THY_WORDS);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case THY_WORDS: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail);
                break;
            }
            case GIVE_US_REST: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.VULNERABLE_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "ApostleWand", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", null, this);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (int i = 0; i < whiteNight.minions.length; i++) {
            if (whiteNight.minions[i] == this) {
                whiteNight.minions[i] = null;
                break;
            }
        }
    }
}