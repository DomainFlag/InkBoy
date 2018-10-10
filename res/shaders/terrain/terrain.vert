#version 430

layout (location = 0) in vec4 position;

uniform mat4 u_camera;
uniform mat4 u_model;
uniform mat4 u_projection;

void main() {
    vec4 pos = u_projection * u_camera * u_model * position;

	gl_Position = pos;
}