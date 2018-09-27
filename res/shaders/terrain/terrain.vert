#version 150

in vec4 a_position;

uniform mat4 u_camera;
uniform mat4 u_projection;
uniform mat4 u_model;

varying float v_depth;
varying float v_far;

void main() {
    vec4 camera = u_camera * u_model * a_position;
    vec4 result = u_projection * camera;

    v_depth = (a_position.z + 1.0) / 2.0;
    v_far = -camera.z; //->[0.0, inf]

    gl_Position = result;
}