package ruina.monsters.act2.Jester;

import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.AbstractAllyMonster;

import static ruina.util.Wiz.att;

public abstract class AbstractMagicalGirl extends AbstractAllyMonster {

    public AbstractMagicalGirl(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof JesterOfNihil) {
                target = (JesterOfNihil) mo;
            }
        }
        rollMove();
        createIntent();
        AbstractDungeon.onModifyPower();
        super.usePreBattleAction();
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        att(new TalkAction(this, getDeathDialog()));
    }

    public String getSummonDialog() {
        return "";
    }

    public String getVictoryDialog() {
        return "";
    }

    public String getDeathDialog() {
        return "";
    }
}