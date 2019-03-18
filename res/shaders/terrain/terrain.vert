#version 430

layout (location = 0) in vec4 position;

uniform mat4 u_projection;
uniform mat4 u_camera;
uniform mat4 u_model;

uniform sampler2D u_texture;

uniform vec2 u_center;
uniform vec2 u_location;
uniform vec2 u_index;
uniform float u_span;
uniform float u_scale;
uniform int u_lod;
uniform float[10] u_morphing_thresholds;

out vec3 v_color;
out float height;

float morphLatitude(vec2 position, float gap) {
    float morphing = 0.0f;

    if(u_index == vec2(0, 0)) {
        // Good
        if(position == u_center + vec2(0, -gap)) {
            morphing += gap;
        }
    } else if(u_index == vec2(0, 1)) {
        // Good
        if(position == u_center + vec2(0, -gap)) {
            morphing -= gap;
        }
    } else if(u_index == vec2(1, 0)) {
        // Good
        if(position == u_center + vec2(0, gap)) {
            morphing += gap;
        }
    } else if(u_index == vec2(1, 1)) {
        if(position == u_center + vec2(0, gap)) {
            morphing -= gap;
        }
    }

    return morphing;
}

float morphLongitude(vec2 position, float gap) {
    float morphing = 0.0f;

    if(u_index == vec2(0, 0)) {
        if(position == u_center + vec2(-gap, 0)) {
            morphing += gap;
        }
    } else if(u_index == vec2(0, 1)) {
        if(position == u_center + vec2(gap, 0)) {
            morphing += gap;
        }
    } else if(u_index == vec2(1, 0)) {
        if(position == u_center + vec2(-gap, 0)) {
            morphing -= gap;
        }
    } else if(u_index == vec2(1, 1)) {
        if(position == u_center + vec2(gap, 0)) {
            morphing -= gap;
        }
    }

    return morphing;
}

vec4 morph(vec2 position, float morph_area) {
    vec2 longitude, latitude;

    float gap = u_span * 2.0f;

    v_color = vec3(0.7, 0.15, 0.21);

//    NOTE
//    left <-> right (-x, x)
//    up   <-> down  (-z, z)
    if(u_index == vec2(0, 0)) {
        // Left up corner
        longitude = u_center + vec2(0, -gap);
        latitude = u_center + vec2(-gap, 0);
    } else if(u_index == vec2(0, 1)) {
        // Right up coner
        longitude = u_center + vec2(0, -gap);
        latitude = u_center + vec2(gap, 0);
    } else if(u_index == vec2(1, 0)) {
        // Left bottom corner
        longitude = u_center + vec2(0, gap);
        latitude = u_center + vec2(-gap, 0);
    } else if(u_index == vec2(1, 1)) {
        // Right bottom corner
        longitude = u_center + vec2(0, gap);
        latitude = u_center + vec2(gap, 0);
    };

    vec4 world_position_longitude = u_camera * vec4(longitude.x * u_scale, 0, longitude.y * u_scale, 1);
    float distance_longitude = length(world_position_longitude.xyz);

    vec4 world_position_latitude = u_camera * vec4(latitude.x * u_scale, 0, latitude.y * u_scale, 1);
    float distance_latitude = length(world_position_latitude.xyz);

    float morphingLongitude = 0.0f, morphingLatitude = 0.0f;

    if(distance_longitude > morph_area) {
        morphingLongitude = morphLatitude(position, u_span);
    }

    if(distance_latitude > morph_area) {
        morphingLatitude = morphLongitude(position, u_span);
    }

    return vec4(morphingLongitude, 0, morphingLatitude, 0);
}

void main() {
    vec4 pos_scaled = position * u_span;
    vec4 pos_translated = vec4(pos_scaled.x + u_location.x, 0, pos_scaled.y + u_location.y, 1);

    vec4 pos = pos_translated + morph(pos_translated.xz, u_morphing_thresholds[u_lod - 1]);

	gl_Position = pos;
}