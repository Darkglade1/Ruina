package ruina.monsters.theHead;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;

public class RolandHead extends Roland {

    private boolean usedPreBattleAction = false;

    public RolandHead() {
        this(0.0f, 0.0f);
    }

    public RolandHead(final float x, final float y) {
        super(x, y);
    }

    @Override
    public void usePreBattleAction() {
        if (!usedPreBattleAction) {
            usedPreBattleAction = true;
            super.usePreBattleAction();
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (mo instanceof Baral) { enemyBoss = mo; }
            }
        }
    }

    public void dialogue() {
    }

}
