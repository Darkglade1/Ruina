package ruina.vfx.TerminationProtocol;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class TerminationCore extends AbstractGameEffect {

    public TerminationCore(){ }

    public void update(){
        AbstractDungeon.topLevelEffectsQueue.add(new TerminationWarningText());
        AbstractDungeon.topLevelEffectsQueue.add(new TerminationRabbitImage());
        AbstractDungeon.topLevelEffectsQueue.add(new TerminationPrimaryMessage());
        this.isDone = true;
    }

    @Override
    public void render(SpriteBatch sb) {
    }

    public void dispose(){}
}