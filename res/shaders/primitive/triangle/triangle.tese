#version 430 core

// Triangles this is the domain the PG will work on.
// Triangle edges will be subdivided into segments with equal lengths
// PG will emit triangles in counter-clockwise order
layout(triangles, fractional_odd_spacing, cw) in;

void main() {
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;
    float w = gl_TessCoord.z;

    // world position
    vec4 position = u * gl_in[0].gl_Position +
        v * gl_in[1].gl_Position +
        w * gl_in[2].gl_Position;

    gl_Position = position;
}