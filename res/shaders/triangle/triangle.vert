#version 150

in vec3 a_position;

uniform mat2 u_matrix;

void main() {
    mat2 m;
    m[0][0] = 1;
    m[1][0] = 0;
    m[0][1] = 1;
    m[1][1] = 0;
    float a = (u_matrix * vec2(0, 1))[0];
    gl_Position = vec4(a_position, a);
}