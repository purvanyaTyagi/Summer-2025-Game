#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 resolution; // Pass your screen resolution

void main() {
    vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);
    float len = length(position);

    // Adjust vignette parameters for softness/strength:
    float vignette = smoothstep(0.5, 0.3, len);

    vec4 color = texture2D(u_texture, v_texCoords);
    color.rgb *= vignette; // Darken edges
    gl_FragColor = color;
}
