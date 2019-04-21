#version 330

layout(location = 0) out vec4 outColor;

in float v_height;

void main() {
	outColor = vec4(vec3(0.3, 0.1, 0.6) * (v_height / 10.0f * 7.0f + 0.3f), 1.0);
}