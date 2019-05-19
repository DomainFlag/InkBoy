#version 430

layout(vertices = 4) out;

in vec2 normal_map_coord_tc[];
out vec2 normal_map_coord_te[];

const int AB = 2;
const int BC = 3;
const int CD = 0;
const int DA = 1;

uniform mat4 u_camera;

float calcTessFactor(float dist) {
    float tessFactor = 640 / pow(dist, 2.1) + 0.2;

    return mix(1, gl_MaxTessGenLevel, tessFactor);
}

void main() {
    //           0
    // Y (V)  3-----2
    // ^      |     |
    // |  1   |     |   3
    // |      0-----1
    // |         2
    // +-------> X (U)

    // invoke once
	if(gl_InvocationID == 0) {
        vec4 midAB = (gl_in[0].gl_Position + gl_in[1].gl_Position) / 2.0f;
        vec4 midBC = (gl_in[1].gl_Position + gl_in[3].gl_Position) / 2.0f;
        vec4 midCD = (gl_in[3].gl_Position + gl_in[2].gl_Position) / 2.0f;
        vec4 midDA = (gl_in[2].gl_Position + gl_in[0].gl_Position) / 2.0f;

        gl_TessLevelOuter[CD] = calcTessFactor(length(u_camera * midDA));
        gl_TessLevelOuter[DA] = calcTessFactor(length(u_camera * midAB));
        gl_TessLevelOuter[AB] = calcTessFactor(length(u_camera * midBC));
        gl_TessLevelOuter[BC] = calcTessFactor(length(u_camera * midCD));

        gl_TessLevelInner[0] = (gl_TessLevelOuter[BC] + gl_TessLevelOuter[DA]) / 2.0f;
        gl_TessLevelInner[1] = (gl_TessLevelOuter[AB] + gl_TessLevelOuter[BC]) / 2.0f;
	}

    normal_map_coord_te[gl_InvocationID] = normal_map_coord_tc[gl_InvocationID];

	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}