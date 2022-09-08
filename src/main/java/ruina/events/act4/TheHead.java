package ruina.events.act4;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import ruina.RuinaMod;
import ruina.monsters.theHead.Baral;

import static ruina.RuinaMod.makeEventPath;

public class TheHead extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(TheHead.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("BlackSilenceDone.png");

    private int screenNum = 0;

    public TheHead() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        screenNum++;
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.loadImage(makeEventPath("Head.png"));
                        imageEventText.setDialogOption(OPTIONS[0]);
                        break;
                }
                break;
            case 1:
                switch (buttonPressed) {
                    case 0:
                        savedTransitionToHeadFight();
                        /*
                        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(Baral.ID);
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;
                        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom());
                        this.enterCombatFromImage();

                         */
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    public void savedTransitionToHeadFight() {
        AbstractDungeon.bossKey = Baral.ID;
        CardCrawlGame.music.fadeOutBGM();
        CardCrawlGame.music.fadeOutTempBGM();
        MapRoomNode node = new MapRoomNode(-1, 15);
        node.room = new MonsterRoomBoss();
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.nextRoomTransitionStart();
    }
}
