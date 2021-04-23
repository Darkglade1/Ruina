package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;

public class BlackSilence3 extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(BlackSilence3.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean completed = false;

    private static final int CARD_AMOUNT_NEEDED = 3;

    private ruina.monsters.blackSilence.blackSilence3.BlackSilence3 roland;
    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("BlackEnergy84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("BlackEnergy32.png"));

    public BlackSilence3(ruina.monsters.blackSilence.blackSilence3.BlackSilence3 owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        roland = owner;
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
        updateDescription();
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (!completed && !roland.halfDead) {
            if (card.type.equals(AbstractCard.CardType.ATTACK)) {
                amount++;
                if (amount >= CARD_AMOUNT_NEEDED) {
                    flash();
                    amount = 0;
                    roland.setBondIntent();
                    completed = true;
                } else {
                    flashWithoutSound();
                }
            }
        }
    }

    @Override
    public void atEndOfRound() {
        amount = 0;
        completed = false;
    }

    @Override
    public void updateDescription() {
        if (roland == null) {
            return;
        }
        description = DESCRIPTIONS[0] + CARD_AMOUNT_NEEDED + DESCRIPTIONS[1] + FontHelper.colorString(roland.bond.name, "y") + DESCRIPTIONS[2];
    }
}