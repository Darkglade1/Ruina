package ruina.monsters.theHead;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BobEffect;
import ruina.monsters.uninvitedGuests.normal.elena.Binah;

import static ruina.util.Wiz.atb;

public class BinahHead extends Binah {
    public AbstractMonster baral;
    public AbstractMonster zena;

    public BinahHead() {
        this(0.0f, 0.0f);
    }

    public BinahHead(final float x, final float y) {
        super(x, y);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Baral) {
                baral = mo;
                targetEnemy = mo;
            }
            if (mo instanceof Zena) {
                zena = mo;
            }
        }
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        if (targetEnemy == null) {
            targetEnemy = baral;
        }
        if (targetEnemy.isDeadOrEscaped()) {
            if (baral.isDeadOrEscaped()) {
                targetEnemy = zena;
            } else {
                targetEnemy = baral;
            }
        }
        applyPowers(targetEnemy);
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        Texture targetTexture = null;
        if (targetEnemy == baral) {
            targetTexture = Baral.targetTexture;
        } else if (targetEnemy == zena) {
            targetTexture = Zena.targetTexture;
        }
        if (targetTexture != null) {
            sb.setColor(Color.WHITE.cpy());
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(targetTexture, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

    public void onEntry() {
        animationAction("Blunt", "BinahArrive", null, this);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                CustomDungeon.playTempMusicInstantly("Binah3");
                isDone = true;
            }
        });
        waitAnimation(1.0f);
    }

    public void dialogue() {
    }

}
