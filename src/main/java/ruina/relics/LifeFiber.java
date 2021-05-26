package ruina.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnPlayerDeathRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.unique.IncreaseMaxHpAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.powers.GoodbyePower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class LifeFiber extends AbstractEasyRelic implements OnPlayerDeathRelic {
    public static final String ID = makeID(LifeFiber.class.getSimpleName());

    public LifeFiber() {
        super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    public void onEquip(){
            counter = (adp().maxHealth / 2);
            AbstractDungeon.player.decreaseMaxHealth(adp().maxHealth / 2);
    }

    @Override
    public boolean onPlayerDeath(AbstractPlayer abstractPlayer, DamageInfo damageInfo) {
        if(counter == -2){ return true; }
        else {
            flash();
            AbstractDungeon.player.heal(adp().maxHealth);
            AbstractDungeon.player.increaseMaxHp(counter, true);
            counter = -2;
            return false;
        }
    }
}
