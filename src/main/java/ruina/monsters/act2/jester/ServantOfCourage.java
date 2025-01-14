package ruina.monsters.act2.jester;

import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.powers.Erosion;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class ServantOfCourage extends AbstractMagicalGirl
{
    public static final String ID = RuinaMod.makeID(ServantOfCourage.class.getSimpleName());

    private static final byte HELP = 0;
    private static final byte PROTECT_FRIEND = 1;

    private static final int DEBUFF_AMT = 4;

    public ServantOfCourage(final float x, final float y) {
        super(ID, ID, 120, -5.0F, 0, 170.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ServantOfCourage/Spriter/ServantOfCourage.scml"));
        this.animation.setFlip(true, false);
        if (AbstractDungeon.ascensionLevel >= 9) {
            this.setHp(120);
        } else {
            this.setHp(140);
        }

        addMove(HELP, Intent.ATTACK, 7, 2);
        addMove(PROTECT_FRIEND, Intent.STRONG_DEBUFF);

        this.icon = TexLoader.getTexture(makeUIPath("CourageIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void takeTurn() {
        if (firstMove) {
            atb(new TalkAction(this, this.getSummonDialog()));
        }
        super.takeTurn();
        switch (this.nextMove) {
            case HELP: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        attack1Animation(target);
                    } else {
                        attack2Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case PROTECT_FRIEND: {
                debuffAnimation(target);
                applyToTarget(target, this, new Erosion(target, DEBUFF_AMT));
                resetIdle(0.5f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.currentHealth <= this.maxHealth * 0.30f) {
            setMoveShortcut(HELP);
        } else {
            if (this.lastMove(HELP)) {
                setMoveShortcut(PROTECT_FRIEND);
            } else {
                setMoveShortcut(HELP);
            }
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case PROTECT_FRIEND: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF_AMT, DetailedIntent.EROSION_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "WoodStrike", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "WoodFinish", enemy, this);
    }

    private void debuffAnimation(AbstractCreature enemy) {
        animationAction("Attack3", "GreedGetPower", enemy, this);
    }

    public String getSummonDialog() {
        return DIALOG[0];
    }

    public String getVictoryDialog() {
        return DIALOG[1];
    }

    public String getDeathDialog() {
        return DIALOG[2];
    }

}