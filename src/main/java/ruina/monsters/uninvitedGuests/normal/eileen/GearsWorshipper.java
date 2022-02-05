package ruina.monsters.uninvitedGuests.normal.eileen;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BobEffect;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.InvisibleBarricadePower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class GearsWorshipper extends AbstractRuinaMonster
{
    public static final String ID = makeID(GearsWorshipper.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MultiIntentStrings"));
    protected static final String[] TEXT = uiStrings.TEXT;

    private static final byte VAPOR = 0;

    public boolean attackingAlly = true;
    private final Eileen eileen;
    private final Yesod yesod;

    public GearsWorshipper() {
        this(0.0f, 0.0f, null);
    }

    public GearsWorshipper(final float x, final float y, Eileen eileen) {
        super(NAME, ID, 40, -5.0F, 0, 100.0f, 205.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("GearsWorshipper/Spriter/GearsWorshipper.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(132), calcAscensionTankiness(144));
        addMove(VAPOR, Intent.ATTACK, calcAscensionDamage(12));
        this.eileen = eileen;
        this.yesod = eileen.yesod;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        createIntent();
    }

    @Override
    public void takeTurn() {
        atb(new RemoveAllBlockAction(this, this));
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        AbstractCreature target;
        if (!yesod.isDead && !yesod.isDying && attackingAlly) {
            target = yesod;
        } else {
            target = adp();
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case VAPOR: {
                slashAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(VAPOR, MOVES[VAPOR]);
    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        if (yesod != null && !yesod.isDead && !yesod.isDying && attackingAlly) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            AbstractCreature target = yesod;
            if (info.base > -1) {
                info.applyPowers(this, target);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                PowerTip intentTip = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                int multiplier = moves.get(this.nextMove).multiplier;
                Texture attackImg;
                if (multiplier > 0) {
                    attackImg = getAttackIntent(info.output * multiplier);
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[3] + multiplier + TEXT[4];
                } else {
                    attackImg = getAttackIntent(info.output);
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[2];
                }
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            }
        } else {
            super.applyPowers();
        }
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "GearVert", enemy, this);
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        if (!yesod.isDead && !yesod.isDying && attackingAlly && !this.isDeadOrEscaped()) {
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(Yesod.targetTexture, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        eileen.onMinionDeath();
        for (int i = 0; i < eileen.minions.length; i++) {
            if (eileen.minions[i] == this) {
                eileen.minions[i] = null;
                break;
            }
        }
    }

}