uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;

varying vec4 v_color;
varying vec2 v_texCoord;

#define PI 3.1415927
#define TWO_PI 6.283185

#define ANIMATION_SPEED 1.5
#define MOVEMENT_SPEED 0.6
#define MOVEMENT_DIRECTION vec2(0.7, -1.0)

#define PARTICLE_SIZE 0.006

#define PARTICLE_SCALE (vec2(0.9, 1.0))
#define PARTICLE_SCALE_VAR (vec2(0.25, 0.2))

#define PARTICLE_BLOOM_SCALE (vec2(0.5, 0.8))
#define PARTICLE_BLOOM_SCALE_VAR (vec2(0.3, 0.1))

#define SPARK_COLOR vec3(0.8, 0.9, 1.0) * 1.5
#define BLOOM_COLOR vec3(0.1, 0.9, 1.0) * 0.8

#define SIZE_MOD 1.15
#define ALPHA_MOD 0.9
#define LAYERS_COUNT 2


float hash1_2(in vec2 x){
 	return fract(sin(dot(x, vec2(52.127, 61.2871))) * 521.582);
}

vec2 hash2_2(in vec2 x){
    return fract(sin(x * mat2(20.52, 24.1994, 70.291, 80.171)) * 492.194);
}

vec2 noise2_2(vec2 uv){
    vec2 f = smoothstep(0.0, 1.0, fract(uv));

 	vec2 uv00 = floor(uv);
    vec2 uv01 = uv00 + vec2(0,1);
    vec2 uv10 = uv00 + vec2(1,0);
    vec2 uv11 = uv00 + 1.0;
    vec2 v00 = hash2_2(uv00);
    vec2 v01 = hash2_2(uv01);
    vec2 v10 = hash2_2(uv10);
    vec2 v11 = hash2_2(uv11);

    vec2 v0 = mix(v00, v01, f.y);
    vec2 v1 = mix(v10, v11, f.y);
    vec2 v = mix(v0, v1, f.x);

    return v;
}

float noise1_2(in vec2 uv){
    vec2 f = fract(uv);

 	vec2 uv00 = floor(uv);
    vec2 uv01 = uv00 + vec2(0,1);
    vec2 uv10 = uv00 + vec2(1,0);
    vec2 uv11 = uv00 + 1.0;

    float v00 = hash1_2(uv00);
    float v01 = hash1_2(uv01);
    float v10 = hash1_2(uv10);
    float v11 = hash1_2(uv11);

    float v0 = mix(v00, v01, f.y);
    float v1 = mix(v10, v11, f.y);
    float v = mix(v0, v1, f.x);

    return v;
}

vec2 rotate(in vec2 point, in float deg){
 	float s = sin(deg);
    float c = cos(deg);
    return mat2(s, c, -c, s) * point;
}

vec2 voronoiPointFromRoot(in vec2 root, in float deg){
  	vec2 point = hash2_2(root) - 0.5;
    float s = sin(deg);
    float c = cos(deg);
    point = mat2(s, c, -c, s) * point * 0.66;
    point += root + 0.5;
    return point;
}

float degFromRootUV(in vec2 uv){
 	return u_time * ANIMATION_SPEED * (hash1_2(uv) - 0.5) * 2.0;
}

vec2 randomAround2_2(in vec2 point, in vec2 range, in vec2 uv){
 	return point + (hash2_2(uv) - 0.5) * range;
}


vec3 fireParticles(in vec2 uv, in vec2 originalUV){
    vec3 particles = vec3(0.0);
    vec2 rootUV = floor(uv);
    float deg = degFromRootUV(rootUV);
    vec2 pointUV = voronoiPointFromRoot(rootUV, deg);
    float dist = 2.0;
    float distBloom = 0.0;
    vec2 tempUV = uv + (noise2_2(uv * 2.0) - 0.5) * 0.1;
    tempUV += -(noise2_2(uv * 3.0 + u_time) - 0.5) * 0.07;
    dist = length(rotate(tempUV - pointUV, 0.7) * randomAround2_2(PARTICLE_SCALE, PARTICLE_SCALE_VAR, rootUV));
    distBloom = length(rotate(tempUV - pointUV, 0.7) * randomAround2_2(PARTICLE_BLOOM_SCALE, PARTICLE_BLOOM_SCALE_VAR, rootUV));
    particles += (1.0 - smoothstep(PARTICLE_SIZE * 0.6, PARTICLE_SIZE * 3.0, dist)) * SPARK_COLOR;
    particles += pow((1.0 - smoothstep(0.0, PARTICLE_SIZE * 6.0, distBloom)) * 1.0, 3.0) * BLOOM_COLOR;
    float border = (hash1_2(rootUV) - 0.5) * 2.0;
 	float disappear = 1.0 - smoothstep(border, border + 0.5, originalUV.y);
    border = (hash1_2(rootUV + 0.214) - 1.8) * 0.7;
    float appear = smoothstep(border, border + 0.4, originalUV.y);
    return particles * disappear * appear;
}

vec3 layeredParticles(in vec2 uv, in float sizeMod, in float alphaMod, in float smoke) {
    const int layers = LAYERS_COUNT;
    vec3 particles = vec3(0);
    float size = 1.0;
    float alpha = 1.0;
    vec2 offset = vec2(0.0);
    vec2 noiseOffset;
    vec2 bokehUV;

    for (int i = 0; i < layers; i++){
        noiseOffset = (noise2_2(uv * size * 2.0 + 0.5) - 0.5) * 0.15;
        bokehUV = (uv * size + u_time * MOVEMENT_DIRECTION * MOVEMENT_SPEED) + offset + noiseOffset;
		particles += fireParticles(bokehUV, uv) * alpha * (1.0 - smoothstep(0.0, 1.0, smoke) * (float(i) / float(layers)));
        offset += hash2_2(vec2(alpha, alpha)) * 10.0;
        alpha *= alphaMod;
        size *= sizeMod;
    }

    return particles;
}

void main() {
        vec2 uv = (2.0 * v_texCoord.xy - vec2(1.0, 1.0)) / 1.0;
        uv.x *= 1.5;
        uv.y *= -1.;
        float vignette = 1.0 - smoothstep(0.4, 1.4, length(uv + vec2(0.0, 0.3)));
        uv *= 1.8;
        vec3 particles = layeredParticles(uv, SIZE_MOD, ALPHA_MOD, 0.);
        vec3 col = particles;
    	col *= vignette;
        col = smoothstep(-0.08, 1.0, col);
        gl_FragColor = vec4(col, 1.0);
        gl_FragColor += texture2D(u_texture, v_texCoord);
}
