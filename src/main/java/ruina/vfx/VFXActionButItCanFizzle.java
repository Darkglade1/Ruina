package ruina.vfx;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class VFXActionButItCanFizzle extends VFXAction {

    public VFXActionButItCanFizzle(AbstractGameEffect effect) {
        this((AbstractCreature)null, effect, 0.0F);
    }

    public VFXActionButItCanFizzle(AbstractGameEffect effect, float duration) {
        this((AbstractCreature)null, effect, duration);
    }

    public VFXActionButItCanFizzle(AbstractCreature source, AbstractGameEffect effect) {
        this(source, effect, 0.0f);
    }

    public VFXActionButItCanFizzle(AbstractCreature source, AbstractGameEffect effect, float duration) {
        super(source, effect, duration);
    }

    public VFXActionButItCanFizzle(AbstractCreature source, AbstractGameEffect effect, float duration, boolean topLevel) {
        super(source, effect, duration, topLevel);
    }

    public void update() {
        if (source != null && source.isDeadOrEscaped()) {
            isDone = true;
        } else {
            super.update();
        }
    }
}
