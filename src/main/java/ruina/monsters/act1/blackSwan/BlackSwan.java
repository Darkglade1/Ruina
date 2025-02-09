package ruina.monsters.act1.blackSwan;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Erosion;
import ruina.powers.act1.Dream;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BlackSwan extends AbstractRuinaMonster
{
    public static final String ID = makeID(BlackSwan.class.getSimpleName());
    private static final byte WRITHE = 0;
    private static final byte PARASOL = 1;
    private static final byte REALITY = 2;
    private static final byte SHRIEK = 3;

    private final int BLOCK = calcAscensionTankiness(7);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int EROSION = calcAscensionSpecial(3);
    private final int STRENGTH = calcAscensionSpecial(1);
    private static final int DEAD_BROTHERS_THRESHOLD = 4;

    public BlackSwan() {
        this(0.0f, 0.0f);
    }

    public BlackSwan(final float x, final float y) {
        super(ID, ID, 160, 0.0F, 0, 170.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSwan/Spriter/BlackSwan.scml"));
        setHp(calcAscensionTankiness(160));
        addMove(WRITHE, Intent.ATTACK, calcAscensionDamage(5), 2);
        addMove(PARASOL, Intent.DEFEND_BUFF);
        addMove(REALITY, Intent.ATTACK_DEBUFF, calcAscensionDamage(7));
        addMove(SHRIEK, Intent.ATTACK_DEBUFF, calcAscensionDamage(16));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Angela1");
        applyToTarget(this, this, new Dream(this, DEAD_BROTHERS_THRESHOLD));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case WRITHE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(adp());
                    } else {
                        slashAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case PARASOL: {
                blockAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (!mo.isDeadOrEscaped()) {
                        block(mo, BLOCK);
                        if (mo == this) {
                            applyToTarget(this, this, new StrengthPower(mo, STRENGTH));
                        }
                    }
                }
                resetIdle();
                break;
            }
            case REALITY: {
                pierceAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                resetIdle();
                break;
            }
            case SHRIEK: {
                specialAnimation();
                dmg(adp(), info);
                applyToTargetNextTurn(adp(), this, new Erosion(adp(), EROSION));
                resetIdle(1.5f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (getNumDeadBrothers() >= DEAD_BROTHERS_THRESHOLD) {
            if (lastMove(SHRIEK)) {
                setMoveShortcut(WRITHE);
            } else {
                setMoveShortcut(SHRIEK);
            }
        } else {
            if (lastMove(REALITY)) {
                setMoveShortcut(WRITHE);
            } else if (lastMove(WRITHE)) {
                setMoveShortcut(PARASOL);
            } else {
                setMoveShortcut(REALITY);
            }
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case PARASOL: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case REALITY: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case SHRIEK: {
                DetailedIntent detail = new DetailedIntent(this, EROSION, DetailedIntent.EROSION_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private int getNumDeadBrothers() {
        int count = 0;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Brother && (mo.isDead || mo.isDying)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Brother && !mo.isDead) {
                atb(new SuicideAction(mo));
            }
        }
        onBossVictoryLogic();
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "SwanVertDown", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "SwanPierce", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "SwanShout", 0.8f, this);
    }

    private void blockAnimation() {
        animationAction("Block", "SwanGuard", this);
    }

}