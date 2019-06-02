#version 330

layout(location = 0) out vec4 outColor;

in vec2 normal_map_coord_fs;
in vec3 tangent_fs;
in vec4 position_fs;

uniform sampler2D u_normal_map;

uniform mat4 u_camera;

// max lighting
const int MAX_LIGHTS = 6;

// number of materials allowed
const int MAX_MATERIALS = 5;

// directional lighting, light coming from a direction with constant intensity
struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
};

// texture materials for terrain
struct MaterialTexture {
    sampler2D diffuse;
    sampler2D normal;
    sampler2D displacement;

    float vertical_scale;
    float horizontal_scale;
};

// fog effect
struct Fog {
    int activate;
    float density;
    float exponent;
    vec3 colour;
};

uniform Fog u_fog;
uniform MaterialTexture u_material_textures[MAX_MATERIALS];

uniform int u_materials_length;
uniform float u_distance;
uniform float u_attenuation;
uniform float u_exponent;

// ambient dominant colour light
uniform vec3 u_ambient_light;

/* Directional Lights */
uniform DirectionalLight u_directional_light[MAX_LIGHTS];

vec4 setupLightColor(vec3 to_light_direction, vec3 normal, vec3 light_colour, float light_intensity) {

    // retain factor when both normal and the direction from position to light source are in the same direction -90 <-> 90
    float diffuseFactor = max(0, dot(to_light_direction, normal));

    // diffuse light
    vec4 diffuse_color = vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

    return diffuse_color;
}

vec4 setDirectionalLight(DirectionalLight directional_light, vec3 normal) {

    // similar to no light
    if(directional_light.intensity == 0)
        return vec4(0, 0, 0, 0);

    /* Directional light direction */
    vec3 directional_light_direction = normalize((u_camera * vec4(directional_light.direction.xyz, 0)).xyz);

    return setupLightColor(directional_light_direction, normal, directional_light.colour, directional_light.intensity);
}

vec3 computeMaterialColor(vec3 normal, int index) {

    // compute current texels
    vec2 texture_coordinate = normal_map_coord_fs * u_material_textures[index].horizontal_scale;

    // diffuse material color
    vec3 material_color = texture(u_material_textures[index].diffuse, texture_coordinate).rgb;

    return material_color;
}

vec4 computeFogEffect(vec4 colour, float distance) {

    if(u_fog.activate != 1)
        return colour;

    // fog factor
    float factor = 1.0 / exp(pow(u_fog.density * distance, u_fog.exponent));

    // normalize
    factor = clamp(factor, 0.0, 1.0);

    // fog colour
    vec3 fog_colour = mix(u_fog.colour, colour.rgb, factor);

    return vec4(fog_colour, colour.a);
}

void main() {

    // normal
    vec3 normal = normalize(texture(u_normal_map, normal_map_coord_fs).rgb);

    // size of available materials
    int size = min(MAX_MATERIALS, u_materials_length);

    // steepness
    int index = int(abs(normal.y) / (1.0 / size));

    // diffuse material color
    vec3 material_color = computeMaterialColor(normal, index);

    float distance = length(position_fs);
    if(distance < u_distance) {

        // bump
        vec3 bump_normal = texture(u_material_textures[index].normal, normal_map_coord_fs).rgb * 2.0 - 1.0;

        // bitangent one of the two possible vectors
        vec3 bitangent = normalize(cross(tangent_fs, normal));

        // tbn matrix
        mat3 tbn = mat3(tangent_fs, bitangent, normal);

        // update normal direction
        normal = normalize(tbn * bump_normal);
    }

    // lighting
    vec4 diffuseSpecularComp = vec4(0, 0, 0, 0);

    for(int g = 0; g < MAX_LIGHTS; g++) {

        // directional lighting
        diffuseSpecularComp += setDirectionalLight(u_directional_light[g], normal);
    }

    // light colour
    vec4 light_colour = vec4(u_ambient_light + material_color, 1) + diffuseSpecularComp;

    outColor = computeFogEffect(light_colour, distance);
}