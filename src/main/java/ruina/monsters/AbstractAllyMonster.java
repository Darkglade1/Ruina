package ruina.monsters;

import com.megacrit.cardcrawl.actions.AbstractGameAction;

public abstract class AbstractAllyMonster extends AbstractRuinaMonster {

    public boolean isAlly = true;

    public AbstractAllyMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
    }

    public AbstractAllyMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
    }

    public AbstractAllyMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        if (isAlly) {
            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    halfDead = true;
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void takeTurn() {
        if (isAlly) {
            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    halfDead = false;
                    this.isDone = true;
                }
            });
        }
    }
}