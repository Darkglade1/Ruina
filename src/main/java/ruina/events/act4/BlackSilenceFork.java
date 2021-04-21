package ruina.events.act4;

import actlikeit.events.GetForked;
import actlikeit.patches.ContinueOntoHeartPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import ruina.RuinaMod;
import ruina.dungeons.BlackSilence;

import static ruina.RuinaMod.makeEventPath;

public class BlackSilenceFork extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(BlackSilenceFork.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("BlackSilenceFork.png");

    private int screenNum = 0;

    public BlackSilenceFork() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0]);
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        ContinueOntoHeartPatch.heartRoom(new ProceedButton());
                        break;
                    case 1:
                        GetForked.nextDungeon(BlackSilence.ID);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
