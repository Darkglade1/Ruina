package ruina.monsters.act2.mountain;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.multiplayer.MultiplayerAllyBuff;
import ruina.powers.act2.Corpse;
import ruina.util.TexLoader;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class MeltedCorpses extends AbstractRuinaMonster
{
    public static final String ID = makeID(MeltedCorpses.class.getSimpleName());

    private static final byte NONE = 0;
    private final int HEAL = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionSpecial(30));

    public MeltedCorpses() {
        this(0.0f, 0.0f);
    }

    public MeltedCorpses(final float x, final float y) {
        super(ID, ID, 30, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Corpse/Spriter/Corpse.scml"));
        setHp(40);
        this.icon = TexLoader.getTexture(makeUIPath("CorpseIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        addPower(new MinionPower(this));
        applyToTarget(this, this, new Corpse(this, HEAL));
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
        setMove(NONE, Intent.NONE);
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.isDead || this.isDying || this.currentHealth <= 0) {
            if (info.owner instanceof Mountain) {
                atb(new HealAction(info.owner, info.owner, HEAL));
            }
        }
        AbstractDungeon.onModifyPower();
    }
}