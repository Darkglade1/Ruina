package ruina.events.act3;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.dungeons.AbstractRuinaDungeon;
import ruina.events.act2.NothingThere;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.relics.Strongest;

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
    public static int dialogueN;

    static {
        if (CardCrawlGame.dungeon instanceof AbstractRuinaDungeon) { dialogueN = getDialogueSwitchPerFloor(((AbstractRuinaDungeon) CardCrawlGame.dungeon).floor, (AbstractDungeon.miscRng.random(1, 24) == 1));
        } else { dialogueN = getDialogueSwitchPerFloor(null, true); }
    }
    public PatronLibrarian() {
        super(NAME, DESCRIPTIONS[dialogueN], IMG);
        imageEventText.setDialogOption(OPTIONS[11]);
        imageEventText.setDialogOption(OPTIONS[12]);
        title = OPTIONS[dialogueN];
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screen) {
            case PRE_CARD:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.clearAllDialogs();
                        imageEventText.updateBodyText(DESCRIPTIONS[11]);
                        imageEventText.setDialogOption(OPTIONS[12]);
                        screen = CurScreen.LEAVE;
                        this.pickCard = true;
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (int i = 0; i < 20; i++) {
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
                        AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[4], false);
                        break;
                    case 1:
                        this.imageEventText.clearAllDialogs();
                        imageEventText.updateBodyText(DESCRIPTIONS[12]);
                        imageEventText.setDialogOption(OPTIONS[12]);
                        screen = CurScreen.LEAVE;
                        break;
                }
                break;
            case LEAVE:
                openMap();
                break;
        }
    }

    public static int getDialogueSwitchPerFloor(AbstractRuinaDungeon.Floor floor, boolean isAngela) {
        if (isAngela) {
            return 10;
        }
        switch (floor) {
            case MAKUTH:
                return 0;
            case YESOD:
                return 1;
            case HOD:
                return 2;
            case NETZACH:
                return 3;
            case TIPHERETH:
                return 4;
            case GEBURA:
                return 5;
            case CHESED:
                return 6;
            case BINAH:
                return 7;
            case HOKMA:
                return 8;
            case ROLAND:
                return 9;
            default:
                return 10;
        }
    }

    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = (AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeCopy();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }
}