package ruina.monsters.act3.silentGirl;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.atb;

public class DummyHammer extends AbstractRuinaMonster
{
    public DummyHammer(final float x, final float y) {
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