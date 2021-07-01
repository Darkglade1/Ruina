package ruina.events.act1;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class Funeral extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(Funeral.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Funeral.png");

    private final int maxHpLoss;
    private static final float MAX_HP_LOSS = 0.10F;
    private static final float A15_MAX_HP_LOSS = 0.13F;

    private final int hpLoss;
    private static final float HP_LOSS = 0.14F;
    private static final float A15_HP_LOSS = 0.18F;

    private static final int REMOVE_CARD_NUM = 2;

    private int screenNum = 0;

    public Funeral() {
        super(NAME, DESCRIPTIONS[0], IMG);
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.maxHpLoss = (int)((float)adp().maxHealth * A15_MAX_HP_LOSS);
            this.hpLoss = (int)((float)adp().maxHealth * A15_HP_LOSS);
        } else {
            this.maxHpLoss = (int)((float)adp().maxHealth * MAX_HP_LOSS);
            this.hpLoss = (int)((float)adp().maxHealth * HP_LOSS);
        }
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + hpLoss + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[5], "g"));
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[2] + maxHpLoss + OPTIONS[4], "r") + " " + FontHelper.colorString(OPTIONS[6] + REMOVE_CARD_NUM + OPTIONS[7], "g"));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().damage(new DamageInfo(null, hpLoss));
                        AbstractCard card = AbstractEgoCard.getRandomEgoCards(1).get(0);
                        card.upgrade();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");
                        adp().decreaseMaxHealth(maxHpLoss);
                        if (adp().masterDeck.getPurgeableCards().size() >= REMOVE_CARD_NUM) {
                            AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(adp().masterDeck.getPurgeableCards()), REMOVE_CARD_NUM, OPTIONS[9], false);
                        }
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
                AbstractDungeon.effectList.add(new PurgeCardEffect(c));
                AbstractDungeon.player.masterDeck.removeCard(c);
                AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
            }
        }
    }
}
