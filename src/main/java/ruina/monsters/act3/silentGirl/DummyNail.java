package ruina.monsters.act3.silentGirl;

import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeMonsterPath;

public class DummyNail extends AbstractRuinaMonster
{
    public DummyNail(final float x, final float y) {
        super("", "", 70, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Nail/Spriter/Nail.scml"));
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
    }

    public void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "SilentNail", enemy, this);
    }

    public void deadAnimation() {
        animationAction("Dead", null, this);
    }


}