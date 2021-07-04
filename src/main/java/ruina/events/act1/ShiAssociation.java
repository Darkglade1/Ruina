package ruina.events.act1;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import ruina.RuinaMod;
import ruina.relics.Overexertion;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class ShiAssociation extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(ShiAssociation.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Shi.png");

    private int screenNum = 0;
    private Overexertion relic = new Overexertion();
    private boolean upgradeCard = false;

    public ShiAssociation() {
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
                        this.img = new Texture(makeEventPath("Shi2.png"));
                        imageEventText.setDialogOption(OPTIONS[1], relic);
                        imageEventText.setDialogOption(OPTIONS[2]);
                        break;
                }
                break;
            case 1:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 2;
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relic);
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[3]);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        screenNum = 2;
                        this.imageEventText.clearAllDialogs();
                        imageEventText.setDialogOption(OPTIONS[3]);
                        upgrade();
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    public void update() {
        super.update();
        if (upgradeCard) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                this.upgradeCard = false;
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    c.upgrade();
                    adp().bottledCardUpgradeCheck(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
                    AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                    AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    c.stopGlowing();
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
        }
    }

    private void upgrade() {
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        upgradeCard = true;
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getUpgradableCards(), 1, OPTIONS[4], true, false, false, false);
    }

}
