#version 330

const int MAX_MATERIALS = 32;

// light attenuation, gradual extinction of photons in medium to respect of distance
struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

// point lighting, light coming from every direction from its origin
struct PointLight {
    Attenuation attenuation;
    vec3 colour;
    vec4 position;
    float intensity;
};

// color lighting and mapping to texture
struct Color {
    vec3 Kx;
    int Mx;
};

// ambient/diffuse/specular colors
struct Material {
    Color Kx[3];
    vec3 Tf;

    float Ni;
    float Ns;
};

layout(location = 0) out vec4 outColor;

uniform sampler2D u_textures[10];

in vec3 v_texture;
in vec3 v_normals;
in vec3 v_position;

uniform vec3 u_ambient_light;
uniform float u_specular_power;

uniform Material u_materials[MAX_MATERIALS];
uniform PointLight u_point_light;

uniform mat4 u_model;
uniform mat4 u_camera;

vec4 ambientColor, diffuseColor, specularColor;

vec4 getColor(Material material, int colorIndex) {
    Color color = material.Kx[colorIndex];

    if(color.Mx != -1) {
        return texture2D(u_textures[color.Mx], v_texture.xy);
    }

    return vec4(color.Kx, 1.0);
}

void setupColors(Material material) {
    ambientColor = getColor(material, 0);
    diffuseColor = getColor(material, 1);
    specularColor = getColor(material, 2);
}

vec4 setupPointLight(Material material, vec3 point_light) {

    /* Diffuse Light */
    vec3 to_light_source = point_light - v_position;
    vec3 to_light_direction = normalize(to_light_source);

    // retain factor when both normal and the direction from position to light source are in the same direction -90 <-> 90
    float diffuseFactor = max(dot(v_normals, to_light_direction), 0.0);

    // diffuse light
    vec4 diffuse_color = diffuseColor * vec4(u_point_light.colour, 1.0) * u_point_light.intensity * diffuseFactor;

    /* Specular Light */
    vec3 to_camera_position = normalize(- v_position);
    vec3 to_position_direction = - to_light_direction;

    // direction of reflected light from the surface
    vec3 reflected_light = normalize(reflect(to_position_direction, v_normals));

    // retain factor when both direction of reflected light and surface to camera are in the same direction
    float specularFactor = max(dot(to_camera_position, reflected_light), 0.0);

    // control the intensity of light as the derivative from 0 to 1 is an increasing positive function
    specularFactor = pow(specularFactor, u_specular_power);

    // specular light
    vec4 specular_color = specularColor * specularFactor * material.Ns * vec4(u_point_light.colour, 1.0);


    /* Attenuation coefficient */
    float distance = length(to_light_source);
    float attenuationInv = u_point_light.attenuation.constant + u_point_light.attenuation.linear * distance +
        u_point_light.attenuation.exponent * pow(distance, 2.0);

    return (diffuse_color + specular_color) / attenuationInv;
}

void main() {
    Material material = u_materials[int(v_texture.z)];

    setupColors(material);

    vec3 point_light = (u_camera * vec4(u_point_light.position.xyz - v_position, 0)).xyz + v_position;

    vec4 diffuseSpecularComp = setupPointLight(material, point_light);

    outColor = ambientColor * vec4(u_ambient_light, 1) + diffuseSpecularComp;
}