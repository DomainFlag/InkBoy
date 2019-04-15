#version 430

layout (location = 0) in vec3 a_position;
layout (location = 1) in vec3 a_texture;
layout (location = 2) in vec3 a_normals;

out vec3 v_position;
out vec3 v_texture;
out vec3 v_normals;

uniform mat4 u_model;
uniform mat4 u_camera;
uniform mat4 u_projection;

void main() {
    vec4 position = u_camera * u_model * vec4(a_position, 1.0);

    v_position = position.xyz;
    v_texture = a_texture;

    // w is set to 0 as vector's direction is what counts and not it's position, translation scalars are the last row in transform matrix
    v_normals = normalize(u_camera * u_model * vec4(a_normals, 0.0)).xyz;

    gl_Position = u_projection * position;
}