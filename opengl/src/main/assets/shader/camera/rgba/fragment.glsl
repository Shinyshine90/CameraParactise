#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 v_textureCoors;
uniform int u_renderMode;
uniform mat4 u_transformMatrix;
uniform samplerExternalOES u_texture;

void main() {
    float x = v_textureCoors.x;
    float y = v_textureCoors.y;
    vec2 reflectTextureCoors;
    if (u_renderMode == 0x01) {
        //front fullscreeen
        reflectTextureCoors = vec2(x /2.0, y /2.0 + 0.5);
    } else if (u_renderMode == 0x02) {
        //back fullscreeen
        reflectTextureCoors = vec2(x /2.0 + 0.5, y /2.0 + 0.5);
    } else if (u_renderMode == 0x03) {
        //left fullscreen
        reflectTextureCoors = vec2(x / 2.0, y /2.0);
    } else if (u_renderMode == 0x04) {
        //right fullscreen
        reflectTextureCoors = vec2(x / 2.0 + 0.5, y /2.0);
    } else if (u_renderMode == 0x11) {
        //left right device equaly
        if (0.0 <= x && x < 0.5) {
            //reflect range x [0.0, 0.5) to [0.125, 0.375), y [0.0, 1.0] to [0.0, 0.5]
            reflectTextureCoors = vec2(x / 2.0 + 0.125, y / 2.0);
        } else {
            //reflect range x [0.5, 1.0) to [0.625, 0.875), y [0.0, 1.0] to [0.0, 0.5]
            reflectTextureCoors = vec2(0.625 + (x - 0.5) / 2.0, y / 2.0);
        }
    } else if (u_renderMode == 0x12) {
        //left fullscreen right attached, devide pos (0.7, 0.7)
        if (0.7 < x && 0.7 < y) {
            reflectTextureCoors = vec2(x * 1.667 - 0.667, y * 1.667 - 1.167);
        } else {
            reflectTextureCoors = vec2(x / 2.0, y / 2.0);
        }
    } else if (u_renderMode == 0x13) {
        //right fullscreen left attached
        if (x < 0.3 && 0.7 <= y) {
            reflectTextureCoors = vec2(x * 1.667, y * 1.667 - 1.167);
        } else {
            reflectTextureCoors = vec2(x * 0.5 + 0.5, y / 2.0);
        }
    } else if (u_renderMode == 0x21) {
        // left right position top, back view position bottom
        if (0.75 <= y) {
            if (0.0 <= x && x < 0.5) {
                //left
                reflectTextureCoors = vec2(x, y * 2.0 - 1.5);
            } else {
                //right
                reflectTextureCoors = vec2(x, y * 2.0 - 1.5);
            }
        } else {
            reflectTextureCoors = vec2(x / 2.0 + 0.5, y * 0.667 + 0.5);
        }
    } else if (u_renderMode == 0x32) {
        // H format
        //left, reflect x range [0, 0.25) to [3 /16, 5 /16)
        if (0.0 <= x && x < 0.25) {
            reflectTextureCoors = vec2(3.0 / 16.0 + x / 2.0, y / 2.0);
        }
        //right, reflect x range [0.75, 1) to [11/ 16, 13/ 16)
        else if (0.75 <= x && x < 1.0) {
            reflectTextureCoors = vec2(5.0 / 16.0 + x / 2.0, y / 2.0);
        }
        //front
        else if (0.25 <= x && x < 0.75 && 0.5 <= y) {
            reflectTextureCoors = vec2(x - 0.25, y);
        }
        //back
        else if (0.25 <= x && x < 0.75 && y < 0.5) {
            reflectTextureCoors = vec2(x + 0.25, y + 0.5);
        }
    } else if (u_renderMode == 0x31) {
        // T format do not transform
        reflectTextureCoors = vec2(x, y);
    }
    vec2 texturePosition = (u_transformMatrix * vec4(reflectTextureCoors, 1.0, 1.0)).xy;
    gl_FragColor = texture2D(u_texture, texturePosition);
}