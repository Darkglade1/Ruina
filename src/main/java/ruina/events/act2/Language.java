package ruina.events.act2;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.cardmods.DamageUpMod;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeEventPath;

public class Language extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(Language.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Gebura.png");

    private static final int DAMAGE_BOOST = 4;
    private final AbstractCardModifier mod = new DamageUpMod(DAMAGE_BOOST);

    private int screenNum = 0;

    public Language() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + DAMAGE_BOOST + OPTIONS[3], "g"));
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4], "g"));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                            if (card.baseDamage > 0) {
                                group.addToBottom(card);
                            }
                        }
                        if (group.size() > 0) {
                            AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[6], false);
                        }
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();
                        AbstractCard card = AbstractEgoCard.getRandomEgoCards(1).get(0);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
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
                CardModifierManager.addModifier(c, mod.makeCopy());
                AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
            }
        }
    }
}
