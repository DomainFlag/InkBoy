#version 430

layout(vertices = 3) out;

in vec2 normal_map_coord_tc[];
out vec2 normal_map_coord_te[];

const int AB = 0;
const int BC = 1;
const int CD = 2;

uniform mat4 u_camera;

float calcTessFactor(float dist) {
    float tessFactor = 540 / pow(dist, 2.1) + 0.2;

    return mix(1, gl_MaxTessGenLevel, tessFactor);
}

void main() {
	if(gl_InvocationID == 0) {
        vec4 midAB = (gl_in[1].gl_Position + gl_in[2].gl_Position) / 2.0f;
        vec4 midBC = (gl_in[0].gl_Position + gl_in[2].gl_Position) / 2.0f;
        vec4 midCD = (gl_in[0].gl_Position + gl_in[1].gl_Position) / 2.0f;

        gl_TessLevelOuter[AB] = calcTessFactor(length(u_camera * midAB));
        gl_TessLevelOuter[BC] = calcTessFactor(length(u_camera * midBC));
        gl_TessLevelOuter[CD] = calcTessFactor(length(u_camera * midCD));

        float innerTessFactor = 0.0f;
        for(int i = 0; i < 3; i++) {
            innerTessFactor += gl_TessLevelOuter[i];
        }

        gl_TessLevelInner[0] = innerTessFactor / 3.0f;
	}

    normal_map_coord_te[gl_InvocationID] = normal_map_coord_tc[gl_InvocationID];

	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}