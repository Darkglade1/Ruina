#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform float magnitudeX;
uniform float magnitudeY;
uniform float scaleX;
uniform float scaleY;

const float PI = 3.1415926538;

varying vec2 v_texCoords;

void main()
{
    vec2 l_texCoords = v_texCoords;
    l_texCoords = v_texCoords + vec2(magnitudeX * sin((1. - v_texCoords.x) * 2. * PI + u_time * scaleX), magnitudeY * cos((1. - v_texCoords.y) * 2. * PI + u_time * scaleY));
    gl_FragColor = texture2D(u_texture, l_texCoords);

}