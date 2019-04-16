#version 330

out vec4 outColor;

in vec2 v_texture;

uniform sampler2D u_texture;
uniform vec4 u_color;

void main() {
    outColor = vec4(u_color.rgb, texture2D(u_texture, v_texture).a);
}