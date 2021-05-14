package ruina.events.act4;

import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.ShrugItOff;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Ironclad;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.IceCream;
import com.megacrit.cardcrawl.relics.RunicPyramid;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import jdk.nashorn.internal.parser.Token;
import ruina.RuinaMod;
import ruina.chr.chr_aya;
import ruina.monsters.day49.Angela.*;
import ruina.relics.Book;
import ruina.relics.d49.Director;

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
                        adp().maxHealth = 106;
                        adp().currentHealth = adp().maxHealth;
                        adp().relics.clear();
                        adp().potions.clear();
                        adp().masterDeck.clear();
                        // add cards here
                        // Draw 2 relic and Draw 5 relics.
                        // add the two gamer relics
                        if (adp().hasRelic(relic.relicId)) {
                            relic = new Circlet();
                        }
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, new Director());
                        openMap();
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
