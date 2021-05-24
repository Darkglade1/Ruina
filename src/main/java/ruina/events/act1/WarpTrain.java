package ruina.events.act1;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.relics.Overexertion;

import static ruina.RuinaMod.makeEventPath;

public class WarpTrain extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(WarpTrain.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Train.png");

    private int screenNum = 0;
    private int GOLD_THRESHOLD = 50;
    private int GOLD_GAIN_AMOUNT = 75;
    private boolean transformCard = false;

    public WarpTrain() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if(AbstractDungeon.ascensionLevel >= 15){
            GOLD_GAIN_AMOUNT /= 1.25f;
            GOLD_THRESHOLD *= 1.2f;
        }
        imageEventText.setDialogOption(String.format(OPTIONS[0], GOLD_GAIN_AMOUNT));
        imageEventText.setDialogOption(AbstractDungeon.player.gold < GOLD_THRESHOLD ? String.format(OPTIONS[2],GOLD_THRESHOLD) : String.format(OPTIONS[1], GOLD_THRESHOLD), AbstractDungeon.player.gold < GOLD_THRESHOLD);
        imageEventText.setDialogOption(OPTIONS[3]);

    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 2;
                        AbstractDungeon.effectList.add(new RainingGoldEffect(GOLD_GAIN_AMOUNT));
                        AbstractDungeon.player.gainGold(GOLD_GAIN_AMOUNT);
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[4]);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 2;
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[4]);
                        transform();
                        break;
                    case 2:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        screenNum = 1;
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[4]);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    public void update() {
        super.update();
        if (transformCard) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                this.transformCard = false;
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);
                    AbstractCard transformedCard = AbstractDungeon.getTransformedCard();
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(transformedCard, c.current_x, c.current_y));
                }
                AbstractDungeon.player.loseGold(GOLD_THRESHOLD);
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
        }
    }

    private void transform() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        transformCard = true;
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 1,true, OPTIONS[5]);
    }

}