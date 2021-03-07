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
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import ruina.RuinaMod;
import ruina.cardmods.BlockUpMod;
import ruina.cardmods.EtherealMod;
import ruina.cards.EGO.AbstractEgoCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class Philosophy extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(Philosophy.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Binah.png");

    private static final int COMMON_HEAL = 20;
    private static final int UNCOMMON_HEAL = 35;
    private static final int RARE_HEAL = 60;
    private static final float UPGRADE_BONUS = 1.2f;
    private static final float HIGH_ASC_PENALTY = 0.75f;

    private static final int BLOCK_INCREASE = 4;
    private final AbstractCardModifier mod1 = new BlockUpMod(BLOCK_INCREASE);
    private final AbstractCardModifier mod2 = new EtherealMod();

    private final AbstractCard chosenCard;
    private int heal;

    private int screenNum = 0;

    public Philosophy() {
        super(NAME, DESCRIPTIONS[0], IMG);
        chosenCard = getRandomNonBasicCard();
        if (chosenCard != null) {
            if (chosenCard.rarity == AbstractCard.CardRarity.COMMON) {
                heal = COMMON_HEAL;
            } else if (chosenCard.rarity == AbstractCard.CardRarity.UNCOMMON) {
                heal = UNCOMMON_HEAL;
            } else if (chosenCard.rarity == AbstractCard.CardRarity.RARE) {
                heal = RARE_HEAL;
            } else {
                heal = UNCOMMON_HEAL;
            }
            if (chosenCard.upgraded) {
                heal = (int)(heal * UPGRADE_BONUS);
            }
            if (AbstractDungeon.ascensionLevel >= 15) {
                heal = (int)(heal * HIGH_ASC_PENALTY);
            }
            imageEventText.setDialogOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[2] + chosenCard.name + OPTIONS[3], "r") + " " + FontHelper.colorString(OPTIONS[4] + heal + OPTIONS[5],"g"), chosenCard.makeStatEquivalentCopy());
        } else {
            imageEventText.setDialogOption(OPTIONS[11], true);
        }
        imageEventText.setDialogOption(OPTIONS[1] + FontHelper.colorString(OPTIONS[6] + BLOCK_INCREASE + OPTIONS[7], "g") + " " + FontHelper.colorString(OPTIONS[8], "r"));
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();
                        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(chosenCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.player.masterDeck.removeCard(chosenCard);
                        adp().heal(heal, true);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        screenNum = 1;
                        this.imageEventText.updateDialogOption(0, OPTIONS[9]);
                        this.imageEventText.clearRemainingOptions();
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                            if (card.baseBlock > 0) {
                                group.addToBottom(card);
                            }
                        }
                        if (!group.isEmpty()) {
                            AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[10], false);
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
                CardModifierManager.addModifier(c, mod1.makeCopy());
                CardModifierManager.addModifier(c, mod2.makeCopy());
                AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
            }
        }
    }

    private AbstractCard getRandomNonBasicCard() {
        ArrayList<AbstractCard> list = new ArrayList<>();

        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity != AbstractCard.CardRarity.BASIC && c.type != AbstractCard.CardType.CURSE && !(c instanceof AbstractEgoCard)) {
                list.add(c);
            }
        }

        if (list.isEmpty()) {
            return null;
        } else {
            Collections.shuffle(list, new Random(AbstractDungeon.miscRng.randomLong()));
            return list.get(0);
        }
    }
}
