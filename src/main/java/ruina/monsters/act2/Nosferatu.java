package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Paralysis;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Nosferatu extends AbstractRuinaMonster
{
    public static final String ID = makeID(Nosferatu.class.getSimpleName());

    private static final byte UNBEARABLE_DROUGHT = 0;
    private static final byte MERCILESS_GESTURE = 1;
    private static final byte LOOMING_PRESENCE = 2;

    private final int STRENGTH = calcAscensionSpecial(3);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int VULNERABLE = calcAscensionSpecial(1);

    public Nosferatu() {
        this(0.0f, 0.0f);
    }

    public Nosferatu(final float x, final float y) {
        super(ID, ID, 40, -5.0F, 0, 250.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Nosferatu/Spriter/Nosferatu.scml"));
        setHp(calcAscensionTankiness(85), calcAscensionTankiness(90));
        addMove(UNBEARABLE_DROUGHT, Intent.ATTACK_BUFF, calcAscensionDamage(14));
        addMove(MERCILESS_GESTURE, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
        addMove(LOOMING_PRESENCE, Intent.DEBUFF);
    }

    @Override
    public void usePreBattleAction() {
        playSound("NosChange");
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case UNBEARABLE_DROUGHT: {
                attack1Animation(adp());
                atb(new VampireDamageActionButItCanFizzle(adp(), info, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                break;
            }
            case MERCILESS_GESTURE: {
                attack2Animation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                resetIdle();
                break;
            }
            case LOOMING_PRESENCE: {
                specialAnimation();
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(MERCILESS_GESTURE)) {
            setMoveShortcut(UNBEARABLE_DROUGHT);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(UNBEARABLE_DROUGHT)) {
                possibilities.add(UNBEARABLE_DROUGHT);
            }
            if (!this.lastMove(MERCILESS_GESTURE)) {
                possibilities.add(MERCILESS_GESTURE);
            }
            if (!this.lastMove(LOOMING_PRESENCE) && !this.lastMoveBefore(LOOMING_PRESENCE)) {
                possibilities.add(LOOMING_PRESENCE);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case UNBEARABLE_DROUGHT: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.LIFESTEAL);
                detailsList.add(detail);
                break;
            }
            case MERCILESS_GESTURE: {
                DetailedIntent detail = new DetailedIntent(this, VULNERABLE, DetailedIntent.VULNERABLE_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case LOOMING_PRESENCE: {
                DetailedIntent detail = new DetailedIntent(this, PARALYSIS, DetailedIntent.PARALYSIS_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "NosBloodEat", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "NosGrab", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "NosSpecial", this);
    }

}