package ruina.events.act3;

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
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.cardmods.CostChangeMod;
import ruina.cardmods.ExhaustMod;

import static ruina.RuinaMod.makeEventPath;

public class TheThumb extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(TheThumb.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Thumb.png");

    private static final int UNCOMMON_CARDS = 30;
    private static final int COST_REDUCTION = -1;

    private boolean pickCard = false;
    private boolean pickMods = false;

    private final AbstractCardModifier mod1 = new CostChangeMod(COST_REDUCTION);
    private final AbstractCardModifier mod2 = new ExhaustMod();

    private int screenNum = 0;

    public TheThumb() {
        super(NAME, DESCRIPTIONS[0], IMG);
        imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + UNCOMMON_CARDS + OPTIONS[3], "g"));
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[4] + COST_REDUCTION + OPTIONS[5], "g") + " " + FontHelper.colorString(OPTIONS[6], "r"));
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
                        int minUncommonSize = Math.min(UNCOMMON_CARDS, AbstractDungeon.uncommonCardPool.group.size());
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        pickCard = true;
                        for(int i = 0; i < minUncommonSize; ++i) {
                            AbstractCard card = AbstractDungeon.getCard(AbstractCard.CardRarity.UNCOMMON).makeCopy();
                            boolean containsDupe = true;

                            while(true) {
                                while(containsDupe) {
                                    containsDupe = false;

                                    for (AbstractCard c : group.group) {
                                        if (c.cardID.equals(card.cardID)) {
                                            containsDupe = true;
                                            card = AbstractDungeon.getCard(AbstractCard.CardRarity.UNCOMMON).makeCopy();
                                            break;
                                        }
                                    }
                                }

                                if (group.contains(card)) {
                                    --i;
                                } else {
                                    group.addToBottom(card);
                                }
                                break;
                            }
                        }
                        for (AbstractCard card : group.group) {
                            UnlockTracker.markCardAsSeen(card.cardID);
                        }
                        AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[9], false);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[7]);
                        this.imageEventText.clearRemainingOptions();
                        group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        pickMods = true;
                        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                            if (card.costForTurn > 0) {
                                group.addToBottom(card);
                            }
                        }
                        if (!group.isEmpty()) {
                            AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[8], false);
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
        if (pickMods) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                    AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                    CardModifierManager.addModifier(c, mod1.makeCopy());
                    CardModifierManager.addModifier(c, mod2.makeCopy());
                    AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
                }
            }
        }
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0).makeStatEquivalentCopy();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }

    }
}
