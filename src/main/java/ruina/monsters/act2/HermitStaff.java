package ruina.monsters.act2;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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

public class HermitStaff extends AbstractRuinaMonster
{
    public static final String ID = makeID(HermitStaff.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MultiIntentStrings"));
    protected static final String[] TEXT = uiStrings.TEXT;

    private static final byte ATTACK = 0;
    private Hermit hermit;

    public HermitStaff() {
        this(0.0f, 0.0f, null);
    }

    public HermitStaff(final float x, final float y, Hermit hermit) {
        super(NAME, ID, 40, -5.0F, 0, 150.0f, 195.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Staff/Spriter/Staff.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(44), calcAscensionTankiness(48));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(8));
        this.hermit = hermit;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeTurn() {
        atb(new RemoveAllBlockAction(this, this));
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        AbstractCreature target;
        if (!hermit.wrath.isDead && !hermit.wrath.isDying) {
            target = hermit.wrath;
        } else {
            target = adp();
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case ATTACK: {
                attackAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        hermit.staff = null;
        AbstractDungeon.onModifyPower();
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(ATTACK);
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
        if (!(hermit.wrath == null) && !hermit.wrath.isDead && !hermit.wrath.isDying) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            AbstractCreature target = hermit.wrath;
            if (info.base > -1) {
                info.applyPowers(this, target);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                PowerTip intentTip = (PowerTip) ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                int multiplier = moves.get(this.nextMove).multiplier;
                if (multiplier > 0) {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[3] + multiplier + TEXT[4];
                } else {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[2];
                }
                Texture attackImg = getAttackIntent(info.output);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            }
        } else {
            super.applyPowers();
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntBlow", enemy, this);
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        if (!hermit.wrath.isDead) {
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(ServantOfWrath.targetTexture, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

}