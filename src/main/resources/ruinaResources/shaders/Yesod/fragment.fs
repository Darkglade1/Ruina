#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;


varying vec2 v_texCoords;

void main()
{
    float dx = 2.5 * (1./u_resolution.x);
    float dy = 2.5 * (1./u_resolution.y);
    vec2 coord = vec2(dx*floor(v_texCoords.x/dx),
                   dy*floor(v_texCoords.y/dy));

    gl_FragColor = texture2D(u_texture, coord);

}