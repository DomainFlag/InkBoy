#version 430

// 256 invocations
layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 1, rgba32f) uniform writeonly image2D u_normal_map;

uniform sampler2D u_height_map;

uniform float u_normal_strength;
uniform int u_size;

void main() {
    ivec2 coordinate = ivec2(gl_GlobalInvocationID.xy);
    vec2 texture_coordinate = gl_GlobalInvocationID.xy / float(u_size);

    float texture_size = 1.0 / u_size;

    float zs[8];
    for(int g = -1; g <= 1; g++) {
        for (int h = -1; h <= 1; h++) {
            zs[(g + 1) * 3 + h + 1] = texture(u_height_map, texture_coordinate + vec2(h * texture_size, g * texture_size)).r;
        }
    }

    vec3 normal;

    // sobel filter
    normal.z = 1.0 / u_normal_strength;
    normal.x = zs[0] + 2 * zs[3] + zs[5] - zs[2] - 2 * zs[4] - zs[7];
    normal.y = zs[0] + 2 * zs[1] + zs[2] - zs[5] - 2 * zs[6] - zs[7];

    // data​ will be written into the image at the given coordinate, using format conversion glBindImageTexture(GL_RGBA32F)
    imageStore(u_normal_map, coordinate, vec4((normalize(normal) + 1) / 2, 1));
}