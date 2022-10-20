precision mediump float;
attribute vec4 a_vertexCoors;
attribute vec2 a_textureCoors;
varying vec2 v_textureCoors;

void main() {
    v_textureCoors = a_textureCoors;
    gl_Position = a_vertexCoors;
}