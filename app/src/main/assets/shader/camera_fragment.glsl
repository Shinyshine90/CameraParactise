#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 v_texturePosition;
uniform samplerExternalOES u_texture;
uniform mat4 u_rotateMatrix;
uniform int u_previewMode;

void main() {
    float x = v_texturePosition.x;
    float y = v_texturePosition.y;
    vec2 reflectPosition = vec2(x, y);

    //正常预览模式
    if (u_previewMode == 0) {
        reflectPosition = vec2(x, y);
    }
    //TOP视角
    else if (u_previewMode == 1) {
        reflectPosition = vec2(x /2.0, y /2.0 + 0.5);
    }
    //BOTTOM视角
    else if (u_previewMode == 2) {
        reflectPosition = vec2(x /2.0 + 0.5, y /2.0 + 0.5);
    }
    //LEFT视角
    else if (u_previewMode == 3) {
        reflectPosition = vec2(x / 2.0, y /2.0);
    }
    //RIGHT视角
    else if (u_previewMode == 4) {
        reflectPosition = vec2(x / 2.0 + 0.5, y /2.0);
    }
    //H宫格视角
    else if (u_previewMode == 5) {
        //left
        if (0.0 <= x && x < 0.25) {
            reflectPosition = vec2(x * 2.0, y / 2.0);
        }
        //right
        else if (0.75 <= x && x < 1.0) {
            reflectPosition = vec2(x * 2.0 - 1.0, y / 2.0);
        }
        //top
        else if (0.25 <= x && x < 0.75 && 0.5 <= y) {
            reflectPosition = vec2(x - 0.25, y);
        }
        //bottom
        else if (0.25 <= x && x < 0.75 && y < 0.5) {
            reflectPosition = vec2(x + 0.25, y + 0.5);
        }
    }
    vec2 transform = (u_rotateMatrix * vec4(reflectPosition, 1.0, 1.0)).xy;
    gl_FragColor = texture2D(u_texture, transform);
}