#version 430

layout (location = 0) in vec4 position;

uniform mat4 u_projection;
uniform mat4 u_camera;
uniform mat4 u_model;

uniform vec4 u_center;
uniform vec2 u_location;
uniform vec2 u_index;
uniform float u_span;
uniform int u_lod;
uniform float[10] u_morphing_thresholds;

float morphLatitude(float gap) {
    float morphing = 0.0f;

    if(u_index == vec2(0, 0)) {
        // Good
        if(u_location == u_center.xz + vec2(0, -gap)) {
            morphing += gap;
        }
    } else if(u_index == vec2(0, 1)) {
        // Good
        if(u_location == u_center.xz + vec2(0, -gap)) {
            morphing -= gap;
        }
    } else if(u_index == vec2(1, 0)) {
        // Good
        if(u_location == u_center.xz + vec2(0, gap)) {
            morphing += gap;
        }
    } else if(u_index == vec2(1, 1)) {
        if(u_location == u_center.xz + vec2(0, gap)) {
            morphing -= gap;
        }
    }

    return morphing;
}

float morphLongitude(float gap) {
    float morphing = 0.0f;

    if(u_index == vec2(0, 0)) {
        if(u_location == u_center.xz + vec2(-gap, 0)) {
            morphing += gap;
        }
    } else if(u_index == vec2(0, 1)) {
        if(u_location == u_center.xz + vec2(gap, 0)) {
            morphing += gap;
        }
    } else if(u_index == vec2(1, 0)) {
        if(u_location == u_center.xz + vec2(-gap, 0)) {
            morphing -= gap;
        }
    } else if(u_index == vec2(1, 1)) {
        if(u_location == u_center.xz + vec2(gap, 0)) {
            morphing -= gap;
        }
    }

    return morphing;
}

vec4 morph(vec4 position, float morph_area) {
    vec2 pos = u_center.xz;
    vec2 longitude, latitude;

    if(u_index == vec2(0, 0)) {
        // Left bottom corner
        longitude = pos + vec2(0, -u_span);
        latitude = pos + vec2(-u_span, 0);
    } else if(u_index == vec2(0, 1)) {
        // Right bottom coner
        longitude = pos + vec2(0, -u_span);
        latitude = pos + vec2(u_span, 0);
    } else if(u_index == vec2(1, 0)) {
        // Left up corner
        longitude = pos + vec2(0, u_span);
        latitude = pos + vec2(-u_span, 0);
    } else if(u_index == vec2(1, 1)) {
        // Right up corner vec2(1, 1)
        longitude = pos + vec2(0, u_span);
        latitude = pos + vec2(u_span, 0);
    };

    vec4 world_position_longitude = u_camera * vec4(longitude.x, 0, longitude.y, 1);
    float distance_longitude = length(world_position_longitude.xyz);

    vec4 world_position_latitude = u_camera * vec4(latitude.x, 0, latitude.y, 1);
    float distance_latitude = length(world_position_latitude.xyz);

    float morphingLongitude = 0.0f, morphingLatitude = 0.0f;

    float gap = u_span / 2.0f;

    if(distance_longitude > morph_area) {
        morphingLongitude = morphLatitude(gap);
    }

    if(distance_latitude > morph_area) {
        morphingLatitude = morphLongitude(gap);
    }

    return vec4(morphingLongitude, 0, morphingLatitude, 0);
}

void main() {
    vec4 pos_m = vec4(position.x - 0.5, 0, position.y - 0.5, 1.0f);
    vec4 pos = position - morph(pos_m, u_morphing_thresholds[u_lod - 1]);

	gl_Position = pos;
}