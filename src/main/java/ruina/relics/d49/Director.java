package ruina.relics.d49;

import basemod.AutoAdd;
import basemod.DevConsole;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import ruina.relics.AbstractEasyRelic;

import static ruina.RuinaMod.DisableConsole;
import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

@AutoAdd.Ignore
public class Director extends AbstractEasyRelic {
    public static final String ID = makeID(Director.class.getSimpleName());

    private static final int HEAL = 35;

    public Director() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onVictory() {
        this.flash();
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        if (adp().currentHealth > 0) {
            adp().heal((int) (adp().maxHealth * ((float)HEAL / 100)));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + HEAL + DESCRIPTIONS[1];
    }

    @Override
    public void instantObtain()
    {
        super.instantObtain();
        DisableConsole();
    }

    @Override
    public void obtain()
    {
        super.obtain();
        DisableConsole();
    }

    @Override
    public void instantObtain(AbstractPlayer p, int slot, boolean callOnEquip)
    {
        super.instantObtain(p, slot, callOnEquip);
        DisableConsole();
    }

    @Override
    public void update()
    {
        super.update();
        DisableConsole();
    }
}