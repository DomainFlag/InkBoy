#version 430

layout (location = 0) in vec3 a_position_a;

uniform mat4 u_projection;
uniform mat4 u_camera;

void main() {
    gl_Position = u_projection * u_camera  * vec4(a_position_a, 1.0);
}