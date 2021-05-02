package ruina.rooms;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import ruina.monsters.theHead.dialogue.HeadEnding;

public class RuinaVictoryRoom extends AbstractRoom {

    public RuinaVictoryRoom() {
        this.phase = RoomPhase.INCOMPLETE;
        AbstractDungeon.overlayMenu.proceedButton.hideInstantly();
    }

    @Override
    public void onPlayerEntry() {
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.topLevelEffectsQueue.add(new HeadEnding(0, 6));
    }

}