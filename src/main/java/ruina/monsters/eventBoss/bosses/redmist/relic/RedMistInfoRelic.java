package ruina.monsters.eventBoss.bosses.redmist.relic;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.monsters.eventBoss.core.AbstractRuinaBossRelic;
import ruina.powers.GoodbyePower;
import ruina.relics.AbstractEasyRelic;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeRelicPath;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class RedMistInfoRelic extends AbstractRuinaBossRelic {
    public static final String ID = makeID(ruina.relics.Goodbye.class.getSimpleName());
    public RedMistInfoRelic() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL, new Texture(makeRelicPath("RedMist.png")));
        largeImg = null;
    }

    @Override
    public void updateDescription(final AbstractPlayer.PlayerClass c) {
        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public String getUpdatedDescription() { return DESCRIPTIONS[0]; }

    @Override
    public AbstractRelic makeCopy() { return new RedMistInfoRelic(); }
}
