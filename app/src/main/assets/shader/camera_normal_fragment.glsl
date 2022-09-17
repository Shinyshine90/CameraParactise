#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 v_texturePosition;
uniform samplerExternalOES u_texture;
uniform mat4 u_rotateMatrix;

void main() {
    vec2 transform = (u_rotateMatrix * vec4(v_texturePosition, 1.0, 1.0)).xy;
    gl_FragColor = texture2D(u_texture, transform);
}