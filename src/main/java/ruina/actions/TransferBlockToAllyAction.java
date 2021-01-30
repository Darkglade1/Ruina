package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.AbstractAllyMonster;

public class TransferBlockToAllyAction extends AbstractGameAction {
    int maxBlock;
    private final AbstractAllyMonster ally;

    public TransferBlockToAllyAction(int maxBlock, AbstractAllyMonster ally) {
        this.actionType = ActionType.SPECIAL;
        this.duration = Settings.ACTION_DUR_FAST;
        this.maxBlock = maxBlock;
        this.ally = ally;
    }

    @Override
    public void update() {
        if (ally != null) {
            int blockToTransfer = maxBlock;
            if (AbstractDungeon.player.currentBlock < blockToTransfer) {
                blockToTransfer = AbstractDungeon.player.currentBlock;
            }
            if (blockToTransfer > 0) {
                int finalBlockToTransfer = blockToTransfer;
                addToTop(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.player.loseBlock(finalBlockToTransfer);
                        this.isDone = true;
                    }
                });
                addToTop(new GainBlockAction(ally, blockToTransfer));
            }
        }
        this.isDone = true;
    }
}


