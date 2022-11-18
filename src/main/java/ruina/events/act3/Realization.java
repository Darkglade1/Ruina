package ruina.events.act3;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Pain;
import com.megacrit.cardcrawl.cards.curses.Pride;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.cards.curses.Writhe;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.cards.EGO.act1.Laetitia;
import ruina.cards.EGO.act2.GoldRush;
import ruina.cards.EGO.act2.LoveAndHate;
import ruina.cards.EGO.act2.SwordSharpened;
import ruina.events.FullScreenImgEvent;
import ruina.relics.BrokenHeart;
import ruina.relics.Despair;
import ruina.relics.Greed;
import ruina.relics.Hate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class Realization extends AbstractEvent implements FullScreenImgEvent {

    public static final String ID = RuinaMod.makeID(Realization.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Realization.png");

    private enum AbnoStory {
        GREED(1), DESPAIR(7), HATE(13), GIRL(19);

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
        GREED(0, new Pride(), true, 0.08f),
        DESPAIR(5, new Shame(), true, 0.10f),
        HATE(10, new Writhe(), false, 0.25f),
        GIRL(15, new Pain(), false, 0.35f);

        private int start;
        private AbstractCard curse;
        private boolean loseMaxHP;
        private float hpPercent;
        ArrayList<String> option1;
        ArrayList<String> option2;

        AbnoOptions(int start, AbstractCard curse, boolean loseMaxHP, float hpPercent) {
            this.start = start;
            option1 = new ArrayList<>();
            option1.add(OPTIONS[start + 1]);
            option1.add(OPTIONS[start + 2]);
            option2 = new ArrayList<>();
            option2.add(OPTIONS[start + 3]);
            option2.add(OPTIONS[start + 4]);
            this.curse = curse;
            this.loseMaxHP = loseMaxHP;
            this.hpPercent = hpPercent;
        }
    }

    private int screenNum = 0;
    private AbnoStory abnoStory;
    private AbnoOptions abnoOptions;

    private AbnoOptions option1;
    private AbnoOptions option2;
    private AbstractCard curse;
    private int hpLoss = 0;
    private AbstractRelic relicReward;
    private AbstractCard cardReward;

    public Realization() {
        this.body = DESCRIPTIONS[0];
        AbnoOptions greed = AbnoOptions.GREED;
        AbnoOptions despair = AbnoOptions.DESPAIR;
        AbnoOptions hate = AbnoOptions.HATE;
        AbnoOptions girl = AbnoOptions.GIRL;
        ArrayList<AbnoOptions> shuffle = new ArrayList<>();
        shuffle.add(greed);
        shuffle.add(despair);
        shuffle.add(hate);
        shuffle.add(girl);
        Collections.shuffle(shuffle, AbstractDungeon.eventRng.random);
        option1 = shuffle.get(0);
        option2 = shuffle.get(1);
        this.roomEventText.addDialogOption(OPTIONS[option1.start], getCardFromOption(option1), getRelicFromOption(option1));
        this.roomEventText.addDialogOption(OPTIONS[option2.start], getCardFromOption(option2), getRelicFromOption(option2));
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
                        cardReward = getCardFromOption(option1);
                        relicReward = getRelicFromOption(option1);
                        break;
                    case 1:
                        abnoOptions = option2;
                        abnoStory = getStoryFromOption(option2);
                        cardReward = getCardFromOption(option2);
                        relicReward = getRelicFromOption(option2);
                        break;
                }
                this.roomEventText.clear();
                this.roomEventText.updateBodyText(DESCRIPTIONS[abnoStory.start]);
                screenNum = 1;
                curse = abnoOptions.curse;
                this.hpLoss = (int)(adp().maxHealth * abnoOptions.hpPercent);
                this.roomEventText.addDialogOption(abnoOptions.option1.get(0) + " " + FontHelper.colorString(OPTIONS[20] + curse.name + OPTIONS[21], "r"), curse);
                if (abnoOptions.loseMaxHP) {
                    this.roomEventText.addDialogOption(abnoOptions.option1.get(1) + " " + FontHelper.colorString(OPTIONS[22] + hpLoss + OPTIONS[23], "r"));
                } else {
                    this.roomEventText.addDialogOption(abnoOptions.option1.get(1) + " " + FontHelper.colorString(OPTIONS[22] + hpLoss + OPTIONS[24], "r"));
                }
                break;
            case 1:
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        break;
                    case 1:
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        if (abnoOptions.loseMaxHP) {
                            adp().decreaseMaxHealth(hpLoss);
                        } else {
                            adp().damage(new DamageInfo(null, hpLoss));
                        }
                        break;
                }
                this.roomEventText.clear();
                this.roomEventText.updateBodyText(abnoStory.story1.get(buttonPressed) + " NL NL " + abnoStory.story1.get(2));
                screenNum = 2;
                this.roomEventText.addDialogOption(abnoOptions.option2.get(0) + " " + FontHelper.colorString(OPTIONS[25] + relicReward.name + OPTIONS[21], "g"), relicReward);
                this.roomEventText.addDialogOption(abnoOptions.option2.get(1) + " " + FontHelper.colorString(OPTIONS[25] + cardReward.name + OPTIONS[21], "g"), cardReward);
                break;
            case 2:
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relicReward);
                        break;
                    case 1:
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(cardReward, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        break;
                }
                this.roomEventText.clear();
                this.roomEventText.updateBodyText(abnoStory.story2.get(buttonPressed));
                this.roomEventText.addDialogOption(OPTIONS[26]);
                screenNum = 3;
                break;
            default:
                this.openMap();
        }
    }

    private AbnoStory getStoryFromOption(AbnoOptions option) {
        if (option == AbnoOptions.GREED) {
            return AbnoStory.GREED;
        } else if (option == AbnoOptions.DESPAIR) {
            return AbnoStory.DESPAIR;
        } else if (option == AbnoOptions.HATE) {
            return AbnoStory.HATE;
        } else {
            return AbnoStory.GIRL;
        }
    }

    private AbstractRelic getRelicFromOption(AbnoOptions option) {
        if (option == AbnoOptions.GREED) {
            return new Greed();
        } else if (option == AbnoOptions.DESPAIR) {
            return new Despair();
        } else if (option == AbnoOptions.HATE) {
            return new Hate();
        } else {
            return new BrokenHeart();
        }
    }

    private AbstractCard getCardFromOption(AbnoOptions option) {
        if (option == AbnoOptions.GREED) {
            return new GoldRush();
        } else if (option == AbnoOptions.DESPAIR) {
            return new SwordSharpened();
        } else if (option == AbnoOptions.HATE) {
            return new LoveAndHate();
        } else {
            return new Laetitia();
        }
    }
}
