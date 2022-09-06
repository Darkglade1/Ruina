package ruina.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import ruina.actions.CallbackExhaustAction;

import java.util.ArrayList;
import java.util.function.Consumer;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class Greed extends AbstractEasyRelic {
    public static final String ID = makeID(Greed.class.getSimpleName());

    private static final int NUM_CARDS = 1;
    private static final int STARTER_GOLD = 5;
    private static final int COMMON_GOLD = 10;
    private static final int UNCOMMON_GOLD = 20;
    private static final int RARE_GOLD = 30;

    private boolean activated = false;

    public Greed() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    public void atBattleStartPreDraw() {
        this.activated = false;
    }

    public void atTurnStartPostDraw() {
        if (!this.activated) {
            this.activated = true;
            this.flash();
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            Consumer<ArrayList<AbstractCard>> consumer = abstractCards -> {
                for (AbstractCard card : abstractCards) {
                    int rarityPrice;
                    if (card.rarity == AbstractCard.CardRarity.BASIC) {
                        rarityPrice = STARTER_GOLD;
                    } else if (card.rarity == AbstractCard.CardRarity.COMMON) {
                        rarityPrice = COMMON_GOLD;
                    } else if (card.rarity == AbstractCard.CardRarity.UNCOMMON) {
                        rarityPrice = UNCOMMON_GOLD;
                    } else {
                        rarityPrice = RARE_GOLD;
                    }
                    AbstractDungeon.effectList.add(new RainingGoldEffect(rarityPrice));
                    adp().gainGold(rarityPrice);
                }
            };
            atb(new CallbackExhaustAction(NUM_CARDS, false, true, true, consumer));
        }

    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + NUM_CARDS + DESCRIPTIONS[1];
    }
}
