package ruina.events.act2;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import ruina.RuinaMod;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class SocialSciences extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(SocialSciences.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Chesed.png");

    private static final float HEAL = 0.30F;
    private static final float A15_HEAL = 0.20F;
    private int heal;

    private static final int COMMON_MAX_HP = 8;
    private static final int UNCOMMON_MAX_HP = 12;
    private static final int RARE_MAX_HP = 16;
    private int maxHP;

    private AbstractPotion potion;

    private int screenNum = 0;

    public SocialSciences() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.noCardsInRewards = true;
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.heal = (int)((float)adp().maxHealth * A15_HEAL);
        } else {
            this.heal = (int)((float)adp().maxHealth * HEAL);
        }
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + heal + OPTIONS[3], "g"));
        this.potion = adp().getRandomPotion();
        if (potion != null) {
            if (potion.rarity == AbstractPotion.PotionRarity.COMMON) {
                maxHP = COMMON_MAX_HP;
            } else if (potion.rarity == AbstractPotion.PotionRarity.UNCOMMON) {
                maxHP = UNCOMMON_MAX_HP;
            } else {
                maxHP = RARE_MAX_HP;
            }
            imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + potion.name + OPTIONS[5], "r") + " " + FontHelper.colorString(OPTIONS[6] + maxHP + OPTIONS[7], "g"));
        } else {
            imageEventText.setDialogOption(OPTIONS[9], true);
        }
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
                        adp().heal(this.heal, true);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[8]);
                        this.imageEventText.clearRemainingOptions();
                        AbstractDungeon.player.removePotion(this.potion);
                        adp().increaseMaxHp(maxHP, true);
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }
}
