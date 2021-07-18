package ruina.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class SeventhBullet extends AbstractEasyRelic {
    public static final String ID = makeID(SeventhBullet.class.getSimpleName());

    private static final int DAMAGE = 3;
    private static final int STRENGTH = 2;
    private static final int CARDS_THRESHOLD = 7;

    public SeventhBullet() {
        super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
        this.counter = 0;
    }

    @Override
    public void atBattleStart() {
        this.flash();
        atb(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        applyToTarget(adp(), adp(), new StrengthPower(adp(), STRENGTH));
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            this.counter++;
            if (this.counter >= CARDS_THRESHOLD) {
                this.counter = 0;
                this.flash();
                atb(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                atb(new DamageAction(adp(), new DamageInfo(adp(), DAMAGE, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + STRENGTH + DESCRIPTIONS[1] + CARDS_THRESHOLD + DESCRIPTIONS[2] + DAMAGE + DESCRIPTIONS[3];
    }
}
