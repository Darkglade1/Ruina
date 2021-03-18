package ruina.relics;

import basemod.abstracts.CustomSavable;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import ruina.RuinaMod;

import java.lang.reflect.Type;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public class Prescript extends AbstractEasyRelic implements CustomSavable<Integer> {

    private enum EncounterType {
        NORMAL(3), ELITE(1), EVENT(2);

        private int threshold;

        EncounterType(int threshold) {
            this.threshold = threshold;
        }
    }

    private enum CardType {
        COMMON(3), UNCOMMON(2), RARE(1);

        private int threshold;

        CardType(int threshold) {
            this.threshold = threshold;
        }
    }

    public static final String ID = RuinaMod.makeID(Prescript.class.getSimpleName());

    private EncounterType encounterType;
    private CardType cardType;
    private int encounterProgress = 0;
    private int cardTypeProgress = 0;

    public Prescript() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
        if (adp() == null) {
            encounterType = EncounterType.NORMAL;
            cardType = CardType.COMMON;
        } else {
            encounterType = EncounterType.values()[AbstractDungeon.relicRng.random(EncounterType.values().length - 1)];
            cardType = CardType.values()[AbstractDungeon.relicRng.random(CardType.values().length - 1)];
        }
        fixDescription();
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        if (room instanceof EventRoom) {
            if (this.encounterType == EncounterType.EVENT && encounterProgress < EncounterType.EVENT.threshold) {
                this.flash();
                encounterProgress++;
                fixDescription();
                checkForCompletion();
            }
        }
    }

    @Override
    public void onObtainCard(AbstractCard card) {
        if (card.rarity == AbstractCard.CardRarity.COMMON) {
            if (this.cardType == CardType.COMMON && cardTypeProgress < CardType.COMMON.threshold) {
                this.flash();
                cardTypeProgress++;
            }
        }
        if (card.rarity == AbstractCard.CardRarity.UNCOMMON) {
            if (this.cardType == CardType.UNCOMMON && cardTypeProgress < CardType.UNCOMMON.threshold) {
                this.flash();
                cardTypeProgress++;
            }
        }
        if (card.rarity == AbstractCard.CardRarity.RARE) {
            if (this.cardType == CardType.RARE && cardTypeProgress < CardType.RARE.threshold) {
                this.flash();
                cardTypeProgress++;
            }
        }
        fixDescription();
        checkForCompletion();
    }

    @Override
    public void onVictory() {
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
            if (this.encounterType == EncounterType.NORMAL && encounterProgress < EncounterType.NORMAL.threshold) {
                this.flash();
                encounterProgress++;
                fixDescription();
                checkForCompletion();
            }
        }
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
            if (this.encounterType == EncounterType.ELITE && encounterProgress < EncounterType.ELITE.threshold) {
                this.flash();
                encounterProgress++;
                fixDescription();
                checkForCompletion();
            }
        }
    }

    private void checkForCompletion() {
        if (encounterProgress == encounterType.threshold && cardTypeProgress == cardType.threshold) {
            CardCrawlGame.sound.playV(makeID("IndexUnlock"), 1.0f);
            AbstractRelic relic = RelicLibrary.getRelic(PrescriptsGrace.ID).makeCopy();
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
        }
    }

    private void fixDescription() {
        description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public String getUpdatedDescription() {
        if (encounterType == null) {
            encounterType = EncounterType.NORMAL;
        }
        if (cardType == null) {
            cardType = CardType.COMMON;
        }

        String description = DESCRIPTIONS[0] + this.encounterType.threshold;
        if (encounterType == EncounterType.NORMAL) {
            description += DESCRIPTIONS[1];
            if (this.encounterType.threshold > 1) {
                description += DESCRIPTIONS[4];
            } else {
                description += DESCRIPTIONS[3];
            }
        } else if (encounterType == EncounterType.ELITE) {
            description += DESCRIPTIONS[2];
            if (this.encounterType.threshold > 1) {
                description += DESCRIPTIONS[4];
            } else {
                description += DESCRIPTIONS[3];
            }
        } else if (encounterType == EncounterType.EVENT) {
            if (this.encounterType.threshold > 1) {
                description += DESCRIPTIONS[6];
            } else {
                description += DESCRIPTIONS[5];
            }
        }
        description += encounterProgress + DESCRIPTIONS[7] + this.encounterType.threshold;
        description += DESCRIPTIONS[8] + this.cardType.threshold;

        if (cardType == CardType.COMMON) {
            description += DESCRIPTIONS[9];
        } else if (cardType == CardType.UNCOMMON) {
            description += DESCRIPTIONS[10];
        } else if (cardType == CardType.RARE) {
            description += DESCRIPTIONS[11];
        }
        if (this.cardType.threshold > 1) {
            description += DESCRIPTIONS[13];
        } else {
            description += DESCRIPTIONS[12];
        }
        description += cardTypeProgress + DESCRIPTIONS[7] + this.cardType.threshold;
        description += DESCRIPTIONS[14];
        return description;
    }

    @Override
    public Integer onSave()
    {
        return (encounterType.ordinal() * 1000) + (encounterProgress * 100) + (cardType.ordinal() * 10) + cardTypeProgress;
    }

    @Override
    public Type savedType()
    {
        return new TypeToken<Integer>(){}.getType();
    }

    @Override
    public void onLoad(Integer value)
    {
        if (value == null) {
            return;
        }
        int index = value / 1000;
        if (index >= 0 && index < EncounterType.values().length) {
            encounterType = EncounterType.values()[index];
        } else {
            encounterType = EncounterType.NORMAL;
            System.out.println("Ruina Prescript save corrupted, resorting to default value");
        }
        value = value % 1000;
        encounterProgress = value / 100;
        if (encounterProgress < 0 || encounterProgress > encounterType.threshold) {
            encounterProgress = 0;
            System.out.println("Ruina Prescript save corrupted, resorting to default value");
        }
        value = value % 100;
        index = value / 10;
        if (index >= 0 && index < CardType.values().length) {
            cardType = CardType.values()[index];
        } else {
            cardType = CardType.COMMON;
            System.out.println("Ruina Prescript save corrupted, resorting to default value");
        }
        value = value % 10;
        cardTypeProgress = value;
        if (cardTypeProgress < 0 || cardTypeProgress > cardType.threshold) {
            cardTypeProgress = 0;
            System.out.println("Ruina Prescript save corrupted, resorting to default value");
        }
        fixDescription();
    }

    @Override
    public void setCounter(int counter) {
        super.setCounter(counter);
        fixDescription();
    }
}
