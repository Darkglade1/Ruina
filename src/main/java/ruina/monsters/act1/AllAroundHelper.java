package ruina.monsters.act1;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.FlexibleLoseStrengthPower;
import ruina.powers.act1.Pattern;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class AllAroundHelper extends AbstractRuinaMonster
{
    public static final String ID = makeID(AllAroundHelper.class.getSimpleName());

    private static final byte CHARGE = 0;
    private static final byte CLEAN = 1;

    private int DAMAGE_THRESHOLD = 9;
    private final int STRENGTH = calcAscensionSpecial(2);
    private final boolean attackFirst;

    public AllAroundHelper() {
        this(0.0f, 0.0f, false);
    }

    public AllAroundHelper(final float x, final float y, boolean attackFirst) {
        super(ID, ID, 140, 0.0F, 0, 250.0f, 215.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Helper/Spriter/Helper.scml"));
        setHp(calcAscensionTankiness(50), calcAscensionTankiness(54));
        addMove(CHARGE, Intent.BUFF);
        addMove(CLEAN, Intent.ATTACK, calcAscensionDamage(6), 2);
        this.attackFirst = attackFirst;

        if (AbstractDungeon.ascensionLevel >= 3) {
            DAMAGE_THRESHOLD += 1;
        }
        if (AbstractDungeon.ascensionLevel >= 18) {
            DAMAGE_THRESHOLD += 1;
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        if (attackFirst) {
            CustomDungeon.playTempMusicInstantly("Warning1");
            playSound("HelperOn", 8.0f);
        }
        applyToTarget(this, this, new Pattern(this, DAMAGE_THRESHOLD));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case CHARGE: {
                specialAnimation(adp());
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(this, this, new FlexibleLoseStrengthPower(this, STRENGTH - 1, true));
                resetIdle(1.0f);
                break;
            }
            case CLEAN: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (attackFirst && firstMove) {
            setMoveShortcut(CLEAN);
        } else if (lastMove(CHARGE)) {
            setMoveShortcut(CLEAN);
        } else {
            setMoveShortcut(CHARGE);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case CHARGE: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, STRENGTH - 1, DetailedIntent.FLEX_TEXTURE);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "SwordVert", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "HelperCharge", enemy, this);
    }

}