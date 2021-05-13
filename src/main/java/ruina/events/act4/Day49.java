package ruina.events.act4;

import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import ruina.RuinaMod;
import ruina.chr.chr_aya;
import ruina.relics.Book;

import static ruina.RuinaMod.*;
import static ruina.chr.chr_aya.characterStrings;
import static ruina.util.Wiz.adp;

public class Day49 extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(Day49.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Ensemble.png");

    AbstractRelic relic = new Book();

    private int screenNum = 0;

    public Day49() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        screenNum = 1;
                        // i hope this works LOL
                        AbstractDungeon.player = new chr_aya(characterStrings.NAMES[0], chr_aya.Enums.ANGELA_LOR);
                        adp().initializeStarterDeck();
                        adp().title = "Angela";
                        // if not, write an awful patch to remove that other guy's render code.
                        adp().maxHealth = 106;
                        adp().currentHealth = adp().maxHealth;
                        adp().relics.clear();
                        adp().potions.clear();
                        adp().masterDeck.clear();
                        // add cards here
                        for(int i = 0; i <= 4; i+= 1){
                            adp().masterDeck.addToBottom(new Madness());
                            adp().masterDeck.addToBottom(new Madness());
                        }
                        adp().energy.energy = 4;
                        // Draw 2 relic and Draw 5 relics.
                        adp().gameHandSize = 0;

                        // add the two gamer relics
                        if (adp().hasRelic(relic.relicId)) {
                            relic = new Circlet();
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        break;
                }
                break;
            default:
                // Prevent the previous button sequence to happen again
                openMap();
                break;
        }
    }

}
