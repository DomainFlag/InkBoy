#version 330

layout(location = 0) out vec4 outputColor;

in vec3 v_fcolor;
in float v_height;

void main() {
	outputColor = vec4(v_fcolor * (v_height / 10.0f * 7.0f + 0.3f), 1.0);
}