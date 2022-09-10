package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.atb;

public class PlayerAngela extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(PlayerAngela.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final int ENERGY = 1;
    public static final int DRAW = 1;
    public Roland parent;

    public PlayerAngela(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        amount = ENERGY;
        amount2 = DRAW;
    }

    @Override
    public void atStartOfTurnPostDraw() {
        flash();
        atb(new GainEnergyAction(amount));
        atb(new DrawCardAction(amount2));
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], DRAW);
    }
}