//package ruina.monsters.act3.seraphim;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
//
//import static ruina.RuinaMod.makeMonsterPath;
//
//public class WhiteNightAura extends AbstractGameEffect {
//    private float x;
//    private float y;
//    private float lineTimer;
//    private float rotation2;
//    private float rotation3;
//
//    private static final TextureAtlas atlas = new TextureAtlas(makeMonsterPath("WhiteNight/backEffect.atlas"));
//    public static final TextureAtlas.AtlasRegion Ring = atlas.findRegion("0407");
//    public static final TextureAtlas.AtlasRegion LightAura = atlas.findRegion("0408");
//    public static final TextureAtlas.AtlasRegion AuraPin = atlas.findRegion("0400");
//
//    public WhiteNightAura(float hbx, float hby) {
//        this.color = Color.WHITE.cpy();
//        this.color.a = 0.8F;
//        this.renderBehind = true;
//        this.rotation2 = 0.0F;
//        this.rotation3 = 90.0F;
//        this.scale = 0.6666667F;
//        this.x = hbx;
//        this.y = hby;
//        this.lineTimer = 0.6666667F;
//    }
//
//    public void update() {
//        this.lineTimer -= Gdx.graphics.getDeltaTime();
//        this.rotation2 -= Gdx.graphics.getDeltaTime() * 60.0F;
//        this.rotation3 += Gdx.graphics.getDeltaTime() * 60.0F;
//        if (this.lineTimer <= 0.0F) {
//            this.lineTimer += 6.0F;
//            for (int i = 0; i < 7; i++) { AbstractDungeon.effectsQueue.add(new WhiteNightAuraPin(this.x - 10.0F * Settings.scale, this.y, i)); }
//        }
//    }
//
//    public void render(SpriteBatch sb) {
//        sb.setBlendFunction(770, 1);
//        sb.setColor(this.color);
//        sb.draw(Ring, this.x - Ring.packedWidth / 2.0F, this.y - Ring.packedHeight / 2.0F, Ring.packedWidth / 2.0F, Ring.packedHeight / 2.0F, Ring.packedWidth, Ring.packedHeight, this.scale / 1.1F * Settings.scale, this.scale / 1.1F * Settings.scale, this.rotation);
//        sb.draw(LightAura, this.x - LightAura.packedWidth / 2.0F, this.y - LightAura.packedHeight / 2.0F, LightAura.packedWidth / 2.0F, LightAura.packedHeight / 2.0F, LightAura.packedWidth, LightAura.packedHeight, this.scale * Settings.scale, this.scale * Settings.scale, this.rotation2);
//        sb.draw(LightAura, this.x - LightAura.packedWidth / 2.0F, this.y - LightAura.packedHeight / 2.0F, LightAura.packedWidth / 2.0F, LightAura.packedHeight / 2.0F, LightAura.packedWidth, LightAura.packedHeight, this.scale * Settings.scale, this.scale * Settings.scale, this.rotation3);
//        sb.setBlendFunction(770, 771);
//    }
//
//    public void dispose() { }
//}