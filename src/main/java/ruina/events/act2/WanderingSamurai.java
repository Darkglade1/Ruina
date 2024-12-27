package ruina.events.act2;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.Shame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.monsters.eventboss.kim.Kim;

import static ruina.util.Wiz.adp;

public class WanderingSamurai extends AbstractEvent {

    public static final String ID = RuinaMod.makeID(WanderingSamurai.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final int CARD_REMOVAL = 2;
    private static final int NUM_RELICS = 2;
    AbstractCard curse = CardLibrary.getCopy(Shame.ID);

    private int screenNum = 0;

    public WanderingSamurai() {
        this.body = DESCRIPTIONS[0];
        this.roomEventText.addDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "r") + " " + FontHelper.colorString(OPTIONS[3] + NUM_RELICS + OPTIONS[4], "g"));
        this.roomEventText.addDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[5] + CARD_REMOVAL + OPTIONS[6], "g") + " " + FontHelper.colorString(OPTIONS[7] + curse.name + OPTIONS[10], "r"), curse);
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.EVENT;
        this.hasDialog = true;
        this.hasFocus = true;
        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(Kim.ID);
    }

    @Override
    public void update() {
        super.update();
        if (!RoomEventDialog.waitForInput) {
            this.buttonEffect(this.roomEventText.getSelectedOption());
        }
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
            for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                AbstractDungeon.effectList.add(new PurgeCardEffect(c));
                AbstractDungeon.player.masterDeck.removeCard(c);
                AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
            }
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.enterCombat();
                        AbstractDungeon.lastCombatMetricKey = Kim.ID;
                        for (int i = 0; i < NUM_RELICS; i++) {
                            AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.UNCOMMON);
                            AbstractDungeon.getCurrRoom().addRelicToRewards(relic);
                        }
                        AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.miscRng.random(25, 35));
                        break;
                    case 1:
                        this.roomEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 2;
                        this.roomEventText.updateDialogOption(0, OPTIONS[8]);
                        this.roomEventText.clearRemainingOptions();
                        if (adp().masterDeck.getPurgeableCards().size() >= CARD_REMOVAL) {
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(adp().masterDeck.getPurgeableCards()), CARD_REMOVAL, OPTIONS[9], false);
                        }
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
