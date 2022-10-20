#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 v_textureCoors;
uniform samplerExternalOES u_texture;
uniform mat4 u_transformMatrix;

void main() {
    vec2 p = (u_transformMatrix * vec4(v_textureCoors, 0.0, 1.0)).xy;
    gl_FragColor = texture2D(u_texture, p);
}