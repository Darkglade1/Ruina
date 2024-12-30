package ruina.monsters.act2.Oz;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ScowlingFace extends AbstractRuinaMonster
{
    public static final String ID = makeID(ScowlingFace.class.getSimpleName());

    private static final byte ATTACK = 0;
    private static final byte DEBUFF = 1;
    public final int STATUS = calcAscensionSpecial(1);
    private final boolean atkFirst;

    public ScowlingFace(final float x, final float y, boolean atkFirst) {
        super(ID, ID, 100, -5.0F, 0, 135.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScowlingFace/Spriter/ScowlingFace.scml"));
        setHp(calcAscensionTankiness(32), calcAscensionTankiness(36));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(6));
        addMove(DEBUFF, Intent.DEBUFF);
        this.atkFirst = atkFirst;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case ATTACK: {
                bluntAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case DEBUFF: {
                debuffAnimation(adp());
                intoDiscardMo(new Burn(), STATUS, this);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            if (atkFirst) {
                setMoveShortcut(ATTACK);
            } else {
                setMoveShortcut(DEBUFF);
            }
        } else {
            if (this.lastMove(ATTACK)) {
                setMoveShortcut(DEBUFF);
            } else {
                setMoveShortcut(ATTACK);
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "SmokeAtk", 0.7f, enemy, this);
    }

    private void debuffAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "MatchSizzle", enemy, this);
    }

}