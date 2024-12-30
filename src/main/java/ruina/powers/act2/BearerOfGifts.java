package ruina.powers.act2;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cardmods.*;
import ruina.cards.FalsePresent;
import ruina.powers.AbstractUnremovablePower;

import java.util.ArrayList;

import static ruina.util.Wiz.*;

public class BearerOfGifts extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(BearerOfGifts.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int strGain;
    public AbstractCard nextGift;
    private boolean playedGift = true;

    private static int ENERGY_LOSS_AMT;
    private static int DRAW_REDUCTION_AMT;
    private static final int VULNERABLE_AMT = 1;

    private final ArrayList<PresentUpside> upsides = new ArrayList<>();
    private final ArrayList<PresentDownside> downsides = new ArrayList<>();

    public BearerOfGifts(AbstractCreature owner, int amount, int strGain) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.strGain = strGain;
        updateDescription();
        if (AbstractDungeon.ascensionLevel >= 19) {
            ENERGY_LOSS_AMT = 2;
            DRAW_REDUCTION_AMT = 2;
        } else {
            ENERGY_LOSS_AMT = 1;
            DRAW_REDUCTION_AMT = 1;
        }
    }

    @Override
    public void onInitialApplication() {
        generatePresent();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (!playedGift && card instanceof FalsePresent && amount == 0) {
            flash();
            playedGift = true;
        }
    }

    @Override
    public void atEndOfRound() {
        if (amount == 0 && !playedGift) {
            flash();
            applyToTarget(owner, owner, new StrengthPower(owner, strGain));
        }
        playedGift = false;
        owner.addPower(new BearerOfGifts(owner, 1, strGain));
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (amount == 2) {
            amount = 0;
            if (nextGift == null) {
                generatePresent();
            }
            flash();
            makeInHand(nextGift, 1);
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    generatePresent();
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + strGain + POWER_DESCRIPTIONS[1];
    }

    public void generatePresent() {
        if (upsides.isEmpty() || downsides.isEmpty()) {
            refillLists();
        }
        PresentUpside chosenUpside = upsides.remove(AbstractDungeon.miscRng.random(upsides.size() - 1));
        PresentDownside chosenDownside = downsides.remove(AbstractDungeon.miscRng.random(downsides.size() - 1));
        nextGift = new FalsePresent();
        CardModifierManager.addModifier(nextGift, chosenUpside.associatedMod);
        CardModifierManager.addModifier(nextGift, chosenDownside.associatedMod);
    }

    private void refillLists() {
        upsides.clear();
        downsides.clear();

        upsides.add(PresentUpside.STR);
        upsides.add(PresentUpside.DEX);
        upsides.add(PresentUpside.METALLICIZE);

        downsides.add(PresentDownside.ENERGY_LOSS);
        downsides.add(PresentDownside.DRAW_REDUCTION);
        downsides.add(PresentDownside.VULNERABLE);
    }

    public enum PresentUpside {
        STR(new StrMod(1)), DEX(new DexMod(1)), METALLICIZE(new MetallicizeMod(2));
        final AbstractCardModifier associatedMod;
        PresentUpside(AbstractCardModifier associatedMod) {
            this.associatedMod = associatedMod;
        }
    }

    public enum PresentDownside {
        ENERGY_LOSS(new EnergyLossMod(ENERGY_LOSS_AMT)), DRAW_REDUCTION(new DrawReductionMod(DRAW_REDUCTION_AMT)), VULNERABLE(new VulnerableMod(VULNERABLE_AMT));

        final AbstractCardModifier associatedMod;
        PresentDownside(AbstractCardModifier associatedMod) {
            this.associatedMod = associatedMod;
        }
    }
}
