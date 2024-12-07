package ruina.events.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import ruina.RuinaMod;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.lulu.monster.Lulu;

import static ruina.RuinaMod.makeEventPath;

public class StreetlightOffice extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(StreetlightOffice.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Streetlight.png");

    private int screenNum = 0;
    private static final int GOLD_REWARD = 250;

    public StreetlightOffice() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(String.format(OPTIONS[1], GOLD_REWARD));
                        imageEventText.setDialogOption(OPTIONS[2]);
                        break;
                }
                break;
            case 1:
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new Lulu());
                        AbstractDungeon.getCurrRoom().addGoldToRewards(GOLD_REWARD);
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;
                        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom()); //switches bg
                        this.enterCombatFromImage();
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 2;
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[3]);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

}
