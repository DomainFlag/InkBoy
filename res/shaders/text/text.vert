#version 430

layout (location = 0) in vec4 a_position;
layout (location = 1) in vec2 a_texture;

out vec2 v_texture;

uniform sampler2D u_texture;
uniform mat4 u_projection;

void main() {
    v_texture = a_texture.xy;

    gl_Position = u_projection * a_position;
}