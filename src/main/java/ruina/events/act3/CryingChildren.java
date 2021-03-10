package ruina.events.act3;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class CryingChildren extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(CryingChildren.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("CryingChildren.png");

    private static final float HP_LOSS = 0.15f;
    private static final float HIGH_ASC_HP_LOSS = 0.20f;
    private static final int MAX_HP_BOOST = 6;
    private final int hpLoss;

    private int screenNum = 0;

    public CryingChildren() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.hpLoss = (int)((float)adp().maxHealth * HIGH_ASC_HP_LOSS);
        } else {
            this.hpLoss = (int)((float)adp().maxHealth * HP_LOSS);
        }
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + hpLoss + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4], "g"));
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[5] + MAX_HP_BOOST + OPTIONS[6], "g"));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().damage(new DamageInfo(null, hpLoss));
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 1, OPTIONS[8], false, false, false, false);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        adp().increaseMaxHp(MAX_HP_BOOST, true);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                AbstractDungeon.player.masterDeck.removeCard(c);
                AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
                AbstractCard card = AbstractEgoCard.getRandomEgoCards(1).get(0);
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
            }
        }
    }
}
