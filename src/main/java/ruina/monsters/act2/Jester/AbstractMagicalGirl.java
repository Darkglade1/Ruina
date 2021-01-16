package ruina.monsters.act2.Jester;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.AbstractAllyMonster;
import ruina.util.Wiz;

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

    public void usePreBattleAction() {
        rollMove();
        createIntent();
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
}