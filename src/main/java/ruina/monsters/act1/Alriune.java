package ruina.monsters.act1;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.WintersInception;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Alriune extends AbstractRuinaMonster
{
    public static final String ID = makeID(Alriune.class.getSimpleName());

    private static final byte SPRINGS_GENESIS = 0;
    private static final byte FULL_BLOOM = 1;
    private static final byte MAGNIFICENT_END = 2;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEX_DOWN = calcAscensionSpecial(1);
    private final int STATUS = calcAscensionSpecial(1);
    private final int DAMAGE_REDUCTION = calcAscensionSpecial(50);

    public Alriune() {
        this(0.0f, 0.0f);
    }

    public Alriune(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 250.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Alriune/Spriter/Alriune.scml"));
        setHp(calcAscensionTankiness(77), calcAscensionTankiness(81));
        addMove(SPRINGS_GENESIS, Intent.DEBUFF);
        addMove(FULL_BLOOM, Intent.ATTACK_DEBUFF, calcAscensionDamage(11));
        addMove(MAGNIFICENT_END, Intent.ATTACK, calcAscensionDamage(16));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning2");
        applyToTarget(this, this, new WintersInception(this, DAMAGE_REDUCTION));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case SPRINGS_GENESIS: {
                blockAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(adp(), this, new DexterityPower(adp(), -DEX_DOWN));
                resetIdle(1.0f);
                break;
            }
            case FULL_BLOOM: {
                attackAnimation(adp());
                dmg(adp(), info);
                intoDiscardMo(new Slimed(), STATUS, this);
                resetIdle();
                break;
            }
            case MAGNIFICENT_END: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (moveHistory.size() >= 2 && !lastMove(SPRINGS_GENESIS) && !lastMoveBefore(SPRINGS_GENESIS)) {
            setMoveShortcut(SPRINGS_GENESIS);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(FULL_BLOOM)) {
                possibilities.add(FULL_BLOOM);
            }
            if (!this.lastMove(MAGNIFICENT_END)) {
                possibilities.add(MAGNIFICENT_END);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case SPRINGS_GENESIS: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, -DEX_DOWN, DetailedIntent.DEXTERITY_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case FULL_BLOOM: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, DetailedIntent.SLIMED_TEXTURE, DetailedIntent.TargetType.DISCARD_PILE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "AlriuneHori", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Special", "AlriuneGuard", this);
    }

}