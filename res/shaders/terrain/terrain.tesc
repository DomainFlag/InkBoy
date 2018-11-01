#version 430

layout(vertices = 3) out;

in vec3 v_color[];
out vec3 v_tecolor[];

const int AB = 0;
const int BC = 1;
const int CD = 2;

const int TESSELATION_OUTER_FACTOR = 1;
const int TESSELATION_INNER_FACTOR = 1;

void main() {
	if(gl_InvocationID == 0) {
        gl_TessLevelOuter[AB] = TESSELATION_OUTER_FACTOR;
        gl_TessLevelOuter[BC] = TESSELATION_OUTER_FACTOR;
        gl_TessLevelOuter[CD] = TESSELATION_OUTER_FACTOR;

        gl_TessLevelInner[0] = TESSELATION_INNER_FACTOR;
	}

	v_tecolor[gl_InvocationID] = v_color[gl_InvocationID];

	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}