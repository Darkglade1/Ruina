package ruina.monsters.eventboss.lulu.monster;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.eventboss.lulu.cards.CHRBOSS_FlamingBat;
import ruina.monsters.eventboss.lulu.cards.CHRBOSS_PreparedMind;
import ruina.monsters.eventboss.lulu.cards.CHRBOSS_SetAblaze;
import ruina.powers.act1.FlamingBat;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Lulu extends AbstractCardMonster {
    public static final String ID = RuinaMod.makeID(Lulu.class.getSimpleName());

    private static final byte PREPARED_MIND = 0;
    private static final byte FLAMING_BAT = 1;
    private static final byte SET_ABLAZE = 2;

    public final int PREPARED_MIND_STRENGTH = calcAscensionSpecial(2);
    public final int FLAMING_BAT_DAMAGE = calcAscensionDamage(7);
    public final int FLAMING_BAT_VULNERABLE = calcAscensionSpecial(1);
    public final int SET_ABLAZE_DAMAGE = calcAscensionDamage(6);
    public final int SET_ABLAZE_HITS = 2;
    private final int FLAMING_BAT_BURN_AMOUNT = calcAscensionSpecial(2);

    public Lulu() {
        this(0.0f, 0.0f);
    }

    public Lulu(final float x, final float y) {
        super(ID, ID, 105, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lulu/Spriter/Lulu.scml"));
        this.setHp(calcAscensionTankiness(105));

        addMove(PREPARED_MIND, Intent.BUFF);
        addMove(FLAMING_BAT, Intent.ATTACK_DEBUFF, FLAMING_BAT_DAMAGE);
        addMove(SET_ABLAZE, Intent.ATTACK, SET_ABLAZE_DAMAGE, SET_ABLAZE_HITS);

        cardList.add(new CHRBOSS_PreparedMind(this));
        cardList.add(new CHRBOSS_FlamingBat(this));
        cardList.add(new CHRBOSS_SetAblaze(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new FlamingBat(this, FLAMING_BAT_BURN_AMOUNT));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (nextMove) {
            case PREPARED_MIND: {
                guardAnimation();
                applyToTarget(this, this, new StrengthPower(this, PREPARED_MIND_STRENGTH));
                resetIdle();
                break;
            }
            case FLAMING_BAT: {
                pierceAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new VulnerablePower(adp(), FLAMING_BAT_VULNERABLE, true));
                resetIdle();
                break;
            }
            case SET_ABLAZE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(adp());
                    } else {
                        bluntAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(FLAMING_BAT)) {
            setMoveShortcut(SET_ABLAZE, MOVES[SET_ABLAZE], cardList.get(SET_ABLAZE).makeStatEquivalentCopy());
        } else if (this.lastMove(PREPARED_MIND)) {
            setMoveShortcut(FLAMING_BAT, MOVES[FLAMING_BAT], cardList.get(FLAMING_BAT).makeStatEquivalentCopy());
        } else {
            setMoveShortcut(PREPARED_MIND, MOVES[PREPARED_MIND], cardList.get(PREPARED_MIND).makeStatEquivalentCopy());
        }
    }

    public void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "FireStab", enemy, this);
    }

    public void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "FireHori", enemy, this);
    }

    public void guardAnimation() {
        animationAction("Block", "FireGuard", this);
    }

}