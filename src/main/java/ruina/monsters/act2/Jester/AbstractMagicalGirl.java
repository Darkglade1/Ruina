package ruina.monsters.act2.Jester;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.AbstractAllyMonster;
import ruina.util.Wiz;

import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public abstract class AbstractMagicalGirl extends AbstractAllyMonster {
    protected JesterOfNihil jester;

    public AbstractMagicalGirl(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractMagicalGirl(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractMagicalGirl(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    @Override
    public void usePreBattleAction() {
        rollMove();
        createIntent();
        AbstractDungeon.onModifyPower();
        super.usePreBattleAction();
    }

    @Override
    public void applyPowers() {
        AbstractCreature target = jester;
        if (target != null) {
            applyPowers(target);
        }
    }

    @Override
    public void takeTurn() {
        for (AbstractMonster mo : Wiz.monsterList()) {
            if (mo instanceof AbstractMagicalGirl && mo.halfDead) {
                this.addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        mo.halfDead = false;
                        this.isDone = true;
                    }
                });
            }
        }
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