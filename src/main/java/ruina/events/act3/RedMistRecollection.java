package ruina.events.act3;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import ruina.RuinaMod;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.relics.Strongest;

import static ruina.RuinaMod.makeEventPath;

public class RedMistRecollection extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(RedMistRecollection.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Kali.png");

    private enum CurScreen {INTRO, INTRO2, LEAVE;}
    private CurScreen screen = CurScreen.INTRO;

    public RedMistRecollection() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0]);
        noCardsInRewards = true;
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screen){
            case INTRO:
                switch (buttonPressed){
                    case 0:
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        imageEventText.setDialogOption(OPTIONS[1], new Strongest());
                        imageEventText.setDialogOption(OPTIONS[2]);
                        screen = CurScreen.INTRO2;
                        break;
                }
                break;
            case INTRO2:
                switch (buttonPressed){
                    case 0:
                        AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new RedMist(0.0F, 0.0F));
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        AbstractDungeon.getCurrRoom().addRelicToRewards(RelicLibrary.getRelic(Strongest.ID).makeCopy());
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;
                        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom()); //switches bg
                        this.enterCombatFromImage();
                        break;
                    case 1:
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        imageEventText.setDialogOption(OPTIONS[2]);
                        screen = CurScreen.LEAVE;
                        break;
                }
                break;
            case LEAVE:
                openMap();
                break;
        }
    }
}