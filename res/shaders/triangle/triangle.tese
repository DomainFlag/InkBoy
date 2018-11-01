#version 410 core

// Triangles is the domain the PG will work on.
// Segments of equal lengths
// Counter-clockwise order
layout(triangles, fractional_odd_spacing, cw) in;

void main() {
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;
    float w = gl_TessCoord.z;

    vec4 position = u * gl_in[0].gl_Position +
                          v * gl_in[1].gl_Position +
                          w * gl_in[2].gl_Position;

    gl_Position = position;
}