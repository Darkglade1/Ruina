package ruina.monsters.day49.act5;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.atb;

public class Act5Hammer extends AbstractRuinaMonster
{
    public Act5Hammer() {
        this(0.0f, 0.0f);
    }

    public Act5Hammer(final float x, final float y) {
        super("", "", 70, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Hammer/Spriter/Hammer.scml"));
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
    }

    public void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "SilentHammer", enemy, this);
    }

    public void deadAnimation() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                animation.setFlip(true, false);
                this.isDone = true;
            }
        });
        animationAction("Dead", null, this);
    }


}