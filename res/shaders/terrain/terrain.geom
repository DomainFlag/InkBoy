#version 430

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

in vec3 v_gcolor[];
out vec3 v_fcolor;

void main() {
    v_fcolor = v_gcolor[0];

    for(int i = 0; i < gl_in.length(); i++) {
        gl_Position = gl_in[i].gl_Position;

        EmitVertex();
    }

    gl_Position = gl_in[0].gl_Position;

    EmitVertex();

    EndPrimitive();
}