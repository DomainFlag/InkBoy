#version 430

layout(triangles) in;
//layout(line_strip, max_vertices = 4) out;
layout(triangle_strip, max_vertices = 3) out;

in vec2 normal_map_coord_gs[];

out vec2 normal_map_coord_fs;

uniform mat4 u_projection;
uniform mat4 u_camera;

void main() {
    for(int i = 0; i < gl_in.length(); i++) {
        gl_Position = u_projection * u_camera * gl_in[i].gl_Position;

        normal_map_coord_fs = normal_map_coord_gs[i];

        EmitVertex();
    }

//    gl_Position = u_projection * u_camera * gl_in[0].gl_Position;
//
//    EmitVertex();

    EndPrimitive();
}