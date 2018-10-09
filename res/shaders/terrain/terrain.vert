#version 430

layout (location = 0) in vec4 a_position;

uniform mat4 u_camera;
uniform mat4 u_model;
uniform mat4 u_projection;

void main() {
    vec4 position = u_projection * u_camera * u_model * a_position;
	
	gl_Position = position;
}