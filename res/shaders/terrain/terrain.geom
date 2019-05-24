#version 430

layout(triangles) in;
// layout(line_strip, max_vertices = 4) out;
layout(triangle_strip, max_vertices = 3) out;

// number of materials allowed
const int MAX_MATERIALS = 5;

in vec2 normal_map_coord_gs[3];
out vec2 normal_map_coord_fs;

out vec3 tangent_fs;
out vec4 position_fs;

uniform mat4 u_projection;
uniform mat4 u_camera;

struct MaterialTexture {
    sampler2D diffuse;
    sampler2D normal;
    sampler2D displacement;

    float vertical_scale;
    float horizontal_scale;
};

uniform MaterialTexture u_material_textures[MAX_MATERIALS];

uniform sampler2D u_normal_map;

uniform int u_materials_length;
uniform float u_distance;
//uniform float u_attenuation;
uniform float u_factor;

/**
 * edge1 = edgeUV1.x * T + edgeUV1.y * B
 * edge2 = edgeUV2.x * T + edgeUV2.y * B
 *
 *         edge1 * edgeUV2.y - edge2 * edgeUV1.y
 * T = -----------------------------------------------
 *      edgeUV1.x * edgeUV2.y - edgeUV2.x * edgeUV2.x
 */
vec3 computeTangent() {
    // triangle delta edges with the respect of v0
    vec3 edge1 = (gl_in[1].gl_Position.xyz - gl_in[0].gl_Position.xyz);
    vec3 edge2 = (gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz);

    // triangle delta uv(texture coordinates) with the respect of uv0
    vec2 edgeUV1 = (normal_map_coord_gs[1] - normal_map_coord_gs[0]);
    vec2 edgeUV2 = (normal_map_coord_gs[2] - normal_map_coord_gs[0]);

    return normalize((edge1 * edgeUV2.y - edge2 * edgeUV1.y) / (edgeUV1.x * edgeUV2.y - edgeUV1.y * edgeUV2.x));
}

void main() {
    // vertex displacement value
    float displacements[3] = float[3](0, 0, 0);

    // compute the tangent; bitagent is the cross product of two vectors
    vec3 tangent = computeTangent();

    // get the size of available materials
    int size = min(MAX_MATERIALS, u_materials_length);

    float offset = 1.0f / size;

    for(int h = 0; h < gl_in.length(); h++) {
        float distance = length(u_camera * gl_in[h].gl_Position);

        if(distance < u_distance) {
            for(int g = 0; g < size; g++) {
                vec3 normal = texture(u_normal_map, normal_map_coord_gs[h]).rgb;

                int index = int(normal.y / offset);

                vec2 texture_coord_displaced = normal_map_coord_gs[h] * u_material_textures[index].horizontal_scale;

                float factor = clamp(1.0 - distance / u_distance, 0, 1.0) * u_factor;
                float displacement = texture(u_material_textures[index].displacement, texture_coord_displaced).r
                    * u_material_textures[index].vertical_scale;

                displacements[h] = factor * displacement;
            }
        }
    }


    for(int i = 0; i < gl_in.length(); i++) {
        tangent_fs = tangent;

        position_fs = u_camera * (gl_in[i].gl_Position + vec4(0, displacements[i], 0, 0));

        normal_map_coord_fs = normal_map_coord_gs[i];

        gl_Position = u_projection * position_fs;

        EmitVertex();
    }

//    gl_Position = u_projection * u_camera * gl_in[0].gl_Position;
//
//    EmitVertex();

    EndPrimitive();
}