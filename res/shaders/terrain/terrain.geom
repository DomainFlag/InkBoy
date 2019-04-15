#version 430

layout(triangles) in;
//layout(line_strip, max_vertices = 4) out;
layout(triangle_strip, max_vertices = 3) out;

in vec3 v_gcolor[];
in float v_gheight[];
out vec3 v_fcolor;
out float v_height;

void main() {
    v_fcolor = v_gcolor[0];
    v_height = v_gheight[0];

    for(int i = 0; i < gl_in.length(); i++) {
        gl_Position = gl_in[i].gl_Position;

        EmitVertex();
    }

//    gl_Position = gl_in[0].gl_Position;
//
//    EmitVertex();

    EndPrimitive();
}