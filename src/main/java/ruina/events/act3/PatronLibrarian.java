package ruina.events.act3;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.dungeons.AbstractRuinaDungeon;

import static ruina.RuinaMod.makeEventPath;

public class PatronLibrarian extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID(PatronLibrarian.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Kali.png");

    private enum CurScreen {PRE_CARD, LEAVE;}

    private CurScreen screen = CurScreen.PRE_CARD;
    private boolean pickCard = false;
    public int dialogueN;

    private static final int BASE_NUM_CARD_OPTIONS = 5;
    private static final int ACT_SCALE_NUM_CARD_OPTIONS = 5;
    private int numCardOptions;

    public PatronLibrarian() {
        super(NAME, DESCRIPTIONS[0], IMG);
        numCardOptions = BASE_NUM_CARD_OPTIONS + (AbstractDungeon.actNum * ACT_SCALE_NUM_CARD_OPTIONS);
        if (numCardOptions < BASE_NUM_CARD_OPTIONS) {
            numCardOptions = BASE_NUM_CARD_OPTIONS;
        }
        imageEventText.setDialogOption(OPTIONS[11] + FontHelper.colorString(OPTIONS[12] + numCardOptions + OPTIONS[13], "g"));
        imageEventText.setDialogOption(OPTIONS[14]);
        boolean isAngela = AbstractDungeon.eventRng.randomBoolean((float) 1 / 24);
        if (CardCrawlGame.dungeon instanceof AbstractRuinaDungeon && !isAngela) {
            dialogueN = getDialogueSwitchPerFloor(((AbstractRuinaDungeon) CardCrawlGame.dungeon).floor, isAngela);
        } else {
            dialogueN = getDialogueSwitchPerFloor(null, true);
        }
        title = OPTIONS[dialogueN];
        this.body = DESCRIPTIONS[dialogueN];
        imageEventText.updateBodyText(DESCRIPTIONS[dialogueN]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screen) {
            case PRE_CARD:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.clearAllDialogs();
                        imageEventText.updateBodyText(DESCRIPTIONS[11]);
                        imageEventText.setDialogOption(OPTIONS[14]);
                        screen = CurScreen.LEAVE;
                        this.pickCard = true;
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (int i = 0; i < numCardOptions; i++) {
                            AbstractCard card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();
                            card.upgrade();
                            boolean containsDupe = true;
                            while (containsDupe) {
                                containsDupe = false;
                                for (AbstractCard c : group.group) {
                                    if (c.cardID.equals(card.cardID)) {
                                        containsDupe = true;
                                        card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();
                                        card.upgrade();
                                    }
                                }
                            }
                            if (!group.contains(card)) {
                                for (AbstractRelic r : AbstractDungeon.player.relics) {
                                    r.onPreviewObtainCard(card);
                                }
                                group.addToBottom(card);
                            } else {
                                i--;
                            }
                        }
                        for (AbstractCard c : group.group) {
                            UnlockTracker.markCardAsSeen(c.cardID);
                        }
                        AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[15], false);
                        break;
                    case 1:
                        this.imageEventText.clearAllDialogs();
                        imageEventText.updateBodyText(DESCRIPTIONS[12]);
                        imageEventText.setDialogOption(OPTIONS[14]);
                        screen = CurScreen.LEAVE;
                        break;
                }
                break;
            case LEAVE:
                openMap();
                break;
        }
    }

    public int getDialogueSwitchPerFloor(AbstractRuinaDungeon.Floor floor, boolean isAngela) {
        if (isAngela) {
            this.imageEventText.loadImage(makeEventPath("Angela.png"));
            return 10;
        }
        switch (floor) {
            case MALKUTH:
                this.imageEventText.loadImage(makeEventPath("PatronMalkuth.png"));
                return 0;
            case YESOD:
                this.imageEventText.loadImage(makeEventPath("PatronYesod.png"));
                return 1;
            case HOD:
                this.imageEventText.loadImage(makeEventPath("PatronHod.png"));
                return 2;
            case NETZACH:
                this.imageEventText.loadImage(makeEventPath("PatronNetzach.png"));
                return 3;
            case TIPHERETH:
                this.imageEventText.loadImage(makeEventPath("PatronTiphereth.png"));
                return 4;
            case GEBURA:
                this.imageEventText.loadImage(makeEventPath("PatronGebura.png"));
                return 5;
            case CHESED:
                this.imageEventText.loadImage(makeEventPath("PatronChesed.png"));
                return 6;
            case BINAH:
                this.imageEventText.loadImage(makeEventPath("PatronBinah.png"));
                return 7;
            case HOKMA:
                this.imageEventText.loadImage(makeEventPath("PatronHokma.png"));
                return 8;
            case ROLAND:
                this.imageEventText.loadImage(makeEventPath("PatronRoland.png"));
                return 9;
            default:
                this.imageEventText.loadImage(makeEventPath("Angela.png"));
                return 10;
        }
    }

    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = (AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeStatEquivalentCopy();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}