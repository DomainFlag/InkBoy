#version 430

layout(vertices = 3) out;

in vec3 v_color[];
out vec3 v_tecolor[];

uniform float[10] u_morphing_thresholds;
uniform int u_lod;
uniform float u_max_tess_factor;
uniform float u_min_tess_factor;
uniform float u_camera_position;

const int AB = 0;
const int BC = 1;
const int CD = 2;

const float TESSELATION_OUTER_FACTOR = 1;
const float TESSELATION_INNER_FACTOR = 1;

float calculateTesselationFactor() {
    float tessFactor = (u_camera_position - u_morphing_thresholds[u_lod])
        / (u_morphing_thresholds[u_lod - 1] - u_morphing_thresholds[u_lod]);

    float tessLowerBand = mix(u_min_tess_factor, u_max_tess_factor, (u_lod) / 9.0f);
    float tessHigherBand = mix(u_min_tess_factor, u_max_tess_factor, (u_lod - 1) / 9.0f);

    return mix(tessLowerBand, tessHigherBand, tessFactor);
}

void main() {
	if(gl_InvocationID == 0) {
        gl_TessLevelOuter[AB] = TESSELATION_OUTER_FACTOR;
        gl_TessLevelOuter[BC] = TESSELATION_OUTER_FACTOR;
        gl_TessLevelOuter[CD] = TESSELATION_OUTER_FACTOR;

        gl_TessLevelInner[0] = calculateTesselationFactor();
	}

	v_tecolor[gl_InvocationID] = v_color[gl_InvocationID];

	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}