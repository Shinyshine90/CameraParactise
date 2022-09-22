#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 v_textureCoors;
uniform int u_renderMode;
uniform mat4 u_transformMatrix;
uniform samplerExternalOES u_texture;

/** F format **/
vec2 getSingleSideFront(in float x, in float y) {
    //front fullscreeen
    return vec2(x /2.0, y /2.0 + 0.5);
}

/** B format **/
vec2 getSingleSideBack(in float x, in float y) {
    //back fullscreeen
    return vec2(x /2.0 + 0.5, y /2.0 + 0.5);
}

/** L format **/
vec2 getSingleSideLeft(in float x, in float y) {
    //left fullscreen
    return vec2(x / 2.0, y /2.001);
}

/** R format **/
vec2 getSingleSideRight(in float x, in float y) {
    //right fullscreen
    return vec2(x / 2.0 + 0.5, y /2.001);
}

/** LRF format **/
vec2 getDoubleSideLRF(in float x, in float y) {
    //left right device equaly
    if (0.0 <= x && x < 0.5) {
        //reflect range x [0.0, 0.5) to [0.125, 0.375), y [0.0, 1.0] to [0.0, 0.5]
        return vec2(x / 2.0 + 0.125, y / 2.001);
    } else {
        //reflect range x [0.5, 1.0) to [0.625, 0.875), y [0.0, 1.0] to [0.0, 0.5]
        return vec2(0.625 + (x - 0.5) / 2.0, y / 2.001);
    }
}

/** LR LW format **/
vec2 getDoubleSideLRLW(in float x, in float y) {
    //left fullscreen right attached, devide pos (0.7, 0.7)
    if (0.7 < x && 0.7 < y) {
        return vec2(x * 1.667 - 0.667, y * 1.667 - 1.167);
    } else {
        return vec2(x / 2.0, y / 2.001);
    }
}

/** LR RW format **/
vec2 getDoubleSideLRRW(in float x, in float y) {
    //right fullscreen left attached
    if (x < 0.3 && 0.7 <= y) {
        return vec2(x * 1.667, y * 1.667 - 1.167);
    } else {
        return vec2(x * 0.5 + 0.5, y / 2.001);
    }
}

/** LRB format **/
vec2 getTripleSideLRB(in float x, in float y) {
    // left right position top, back view position bottom
    if (0.0 <= x && x < 0.3 && 0.7 < y) {
        return vec2(x * 5.0 / 3.0, y * 5.0 / 3.0 - 7.0 / 6.0);
    } else if (0.7 <= x && 0.7 <= y) {
        return vec2(x * 5.0 / 3.0 - 2.0 / 3.0, y * 5.0 / 3.0 - 7.0 / 6.0);
    } else {
        return vec2(x /2.0 + 0.5, y /2.0 + 0.5);
    }
}

/** T format, do not transform**/
vec2 getFourSideH(in float x, in float y) {
    //left, reflect x range [0, 0.25) to [3 /16, 5 /16)
    if (0.0 <= x && x < 0.25) {
        return vec2(3.0 / 16.0 + x / 2.0, y / 2.001);
    }
    //right, reflect x range [0.75, 1) to [11/ 16, 13/ 16)
    else if (0.75 <= x && x < 1.0) {
        return vec2(5.0 / 16.0 + x / 2.0, y / 2.001);
    }
    //front
    else if (0.25 <= x && x < 0.75 && 0.5 <= y) {
        return vec2(x - 0.25, y);
    }
    //back
    else if (0.25 <= x && x < 0.75 && y < 0.5) {
        return vec2(x + 0.25, y + 0.5);
    }
    return vec2(x, y);
}

/** T format **/
vec2 getFourSideT(in float x, in float y) {
    return vec2(x, y);
}

void main() {
    float x = v_textureCoors.x;
    float y = v_textureCoors.y;
    vec2 reflectTextureCoors;
    if (u_renderMode == 0x01) {
        reflectTextureCoors = getSingleSideFront(x, y);
    } else if (u_renderMode == 0x02) {
        reflectTextureCoors = getSingleSideBack(x, y);
    } else if (u_renderMode == 0x03) {
        reflectTextureCoors = getSingleSideLeft(x, y);
    } else if (u_renderMode == 0x04) {
        reflectTextureCoors = getSingleSideRight(x, y);
    } else if (u_renderMode == 0x11) {
        reflectTextureCoors = getDoubleSideLRF(x, y);
    } else if (u_renderMode == 0x12) {
        reflectTextureCoors = getDoubleSideLRLW(x, y);
    } else if (u_renderMode == 0x13) {
        reflectTextureCoors = getDoubleSideLRRW(x, y);
    } else if (u_renderMode == 0x21) {
        reflectTextureCoors = getTripleSideLRB(x, y);
    } else if (u_renderMode == 0x32) {
        reflectTextureCoors = getFourSideH(x, y);
    } else if (u_renderMode == 0x31) {
        reflectTextureCoors = getFourSideT(x, y);
    }
    vec2 texturePosition = (u_transformMatrix * vec4(reflectTextureCoors, 1.0, 1.0)).xy;
    gl_FragColor = texture2D(u_texture, texturePosition);
}