package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import ruina.RuinaMod;
import ruina.monsters.day49.Aspiration.LungsOfCravingD49;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makePowerPath;
import static ruina.util.Wiz.atb;

public class FerventBeats extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(FerventBeats.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FerventBeats(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.priority = 99;
        this.amount = amount;
    }

    public void updateDescription() {
        if (this.amount == 1) { this.description = DESCRIPTIONS[2];
        } else { this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1]; }
    }

    public void duringTurn() {
        if (this.amount == 1 && !this.owner.isDying) {
            atb(new VFXAction(new ExplosionSmallEffect(this.owner.hb.cX, this.owner.hb.cY), 0.1F));
            atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(owner instanceof LungsOfCravingD49){ ((LungsOfCravingD49) owner).externalDie(); }
                        isDone = true;
                    }
                });
        } else {
            atb(new ReducePowerAction(this.owner, this.owner, this, 1));
            updateDescription();
        }
    }
}
