package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import ruina.monsters.AbstractAllyMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class TransferBlockToAllyAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("AllyStrings"));
    private static final String[] TEXT = uiStrings.TEXT;
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
                addToTop(new AllyGainBlockAction(ally, blockToTransfer, true));
            } else {
                atb(new TalkAction(true, TEXT[12], 1.2F, 1.2F));
            }
        }
        this.isDone = true;
    }
}


