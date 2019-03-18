#version 430

layout (location = 0) in vec4 a_position;
layout (location = 1) in vec3 a_texture;
layout (location = 2) in vec3 a_normals;

uniform mat4 u_camera;
uniform mat4 u_projection;
uniform mat4 u_model;

varying vec3 v_texture;
varying vec3 v_normals;

void main() {
    vec4 pos = a_position;
    pos.xyz *= 30;

    gl_Position = u_projection * u_camera * u_model * pos;

    v_texture = a_texture;
    v_normals = a_normals;
}