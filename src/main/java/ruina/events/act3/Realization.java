package ruina.events.act3;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import ruina.RuinaMod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static ruina.RuinaMod.makeEventPath;

public class Realization extends AbstractEvent {

    public static final String ID = RuinaMod.makeID(Realization.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Realization.png");

    private enum AbnoStory {
        GREED(1), DESPAIR(7), HATE(13);

        private int start;
        ArrayList<String> story1;
        ArrayList<String> story2;

        AbnoStory(int start) {
            this.start = start;
            story1 = new ArrayList<>();
            story1.add(DESCRIPTIONS[start + 1]);
            story1.add(DESCRIPTIONS[start + 2]);
            story1.add(DESCRIPTIONS[start + 3]);
            story2 = new ArrayList<>();
            story2.add(DESCRIPTIONS[start + 4]);
            story2.add(DESCRIPTIONS[start + 5]);
        }
    }

    private enum AbnoOptions {
        GREED(0), DESPAIR(5), HATE(10);

        private int start;
        ArrayList<String> option1;
        ArrayList<String> option2;

        AbnoOptions(int start) {
            this.start = start;
            option1 = new ArrayList<>();
            option1.add(DESCRIPTIONS[start + 1]);
            option1.add(DESCRIPTIONS[start + 2]);
            option2 = new ArrayList<>();
            option2.add(DESCRIPTIONS[start + 3]);
            option2.add(DESCRIPTIONS[start + 4]);
        }
    }

    AbstractCard curse = CardLibrary.getCopy(Shame.ID);

    private int screenNum = 0;
    private AbnoStory abnoStory;
    private AbnoOptions abnoOptions;

    private AbnoOptions option1;
    private AbnoOptions option2;

    public Realization() {
        this.body = DESCRIPTIONS[0];
        AbnoOptions greed = AbnoOptions.GREED;
        AbnoOptions despair = AbnoOptions.DESPAIR;
        AbnoOptions hate = AbnoOptions.HATE;
        ArrayList<AbnoOptions> shuffle = new ArrayList<>();
        shuffle.add(greed);
        shuffle.add(despair);
        shuffle.add(hate);
        Collections.shuffle(shuffle, AbstractDungeon.eventRng.random);
        option1 = shuffle.get(0);
        option2 = shuffle.get(1);
        this.roomEventText.addDialogOption(OPTIONS[option1.start]);
        this.roomEventText.addDialogOption(OPTIONS[option2.start]);
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.EVENT;
        this.hasDialog = true;
        this.hasFocus = true;
        initializeImage(IMG, 0.0f, -50.0f * Settings.scale);
    }

    @Override
    public void onEnterRoom() {
        CustomDungeon.playTempMusicInstantly("Space");
    }

    @Override
    public void update() {
        super.update();
        if (!RoomEventDialog.waitForInput) {
            this.buttonEffect(this.roomEventText.getSelectedOption());
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        abnoOptions = option1;
                        abnoStory = getStoryFromOption(option1);
                        break;
                    case 1:
                        abnoOptions = option2;
                        abnoStory = getStoryFromOption(option2);
                        break;
                }
                this.roomEventText.updateBodyText(DESCRIPTIONS[abnoStory.start]);
                screenNum = 1;
                break;
            default:
                this.openMap();
        }
    }

    private AbnoStory getStoryFromOption(AbnoOptions option) {
        if (option == AbnoOptions.GREED) {
            return AbnoStory.GREED;
        }
        if (option == AbnoOptions.DESPAIR) {
            return AbnoStory.DESPAIR;
        } else {
            return AbnoStory.HATE;
        }
    }
}
