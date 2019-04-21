#version 430

layout(triangles) in;
//layout(line_strip, max_vertices = 4) out;
layout(triangle_strip, max_vertices = 3) out;

in float v_gheight[];
out float v_height;

uniform mat4 u_projection;
uniform mat4 u_camera;

void main() {
    v_height = v_gheight[0];
    for(int i = 0; i < gl_in.length(); i++) {
        gl_Position = u_projection * u_camera * gl_in[i].gl_Position;

        EmitVertex();
    }

//    gl_Position = u_projection * u_camera * gl_in[0].gl_Position;
//
//    EmitVertex();

    EndPrimitive();
}