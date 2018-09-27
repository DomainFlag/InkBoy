#version 150

attribute vec4 a_position;
attribute vec3 a_texture;
attribute vec3 a_normals;

attribute vec3 a_lighting_pos;

uniform mat4 u_camera;
uniform mat4 u_projection;

varying vec3 v_texture;
varying vec3 v_normals;
varying vec3 v_lighting_pos;

void main() {
    gl_Position = u_projection*u_camera*a_position;

    v_texture = a_texture;
    v_normals = a_normals;
    v_lighting_pos = a_lighting_pos;
}