package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.RuinaMod;
import ruina.monsters.act2.QueenOfHate;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class Hysteria extends TwoAmountPower {
    public static final String POWER_ID = RuinaMod.makeID("Hysteria");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int threshold;
    private final int weak;
    private final int frail;
    private int attackCount = 0;
    private int skillCount = 0;
    private QueenOfHate queen;

    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("Hysteria84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("Hysteria32.png"));

    public Hysteria(AbstractCreature owner, int weak, int frail, int threshold, QueenOfHate queen) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.weak = weak;
        this.frail = frail;
        this.threshold = threshold;
        this.queen = queen;
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            attackCount++;
            if (attackCount >= threshold) {
                this.flash();
                applyToTarget(adp(), owner, new WeakPower(adp(), weak, true));
                attackCount = 0;
                queen.hysteriaTriggered = true;
            } else {
                this.flashWithoutSound();
            }
            amount2 = attackCount;
        }
        if (card.type == AbstractCard.CardType.SKILL) {
            skillCount++;
            if (skillCount >= threshold) {
                this.flash();
                applyToTarget(adp(), owner, new FrailPower(adp(), frail, true));
                skillCount = 0;
                queen.hysteriaTriggered = true;
            } else {
                this.flashWithoutSound();
            }
            amount = skillCount;
        }
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        attackCount = 0;
        skillCount = 0;
        amount = 0;
        amount2 = 0;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + threshold + DESCRIPTIONS[1] + weak + DESCRIPTIONS[2] + threshold + DESCRIPTIONS[3] + frail + DESCRIPTIONS[4];
    }
}
