#version 330

layout(location = 0) out vec4 outputColor;

in vec3 v_fcolor;

void main() {
	outputColor = vec4(v_fcolor, 1.0);
}