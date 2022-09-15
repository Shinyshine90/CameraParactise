package cn.shawn.camerapractise.util;

import android.graphics.Bitmap;
import java.nio.IntBuffer;
import javax.microedition.khronos.opengles.GL10;

public class GlUtils {

  /**
   * 将GL中绘制的像素转换成位图
   * Attention: gl operation have to run in gl thread
   */
  public static Bitmap getBitmapFromGL(int w, int h, GL10 gl) {
    int[] b = new int[w * h];
    int[] bt = new int[w * h];
    IntBuffer ib = IntBuffer.wrap(b);
    ib.position(0);
    gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
    for (int i = 0, k = 0; i < h; i++, k++) {
      for (int j = 0; j < w; j++) {
        int pix = b[i * w + j];
        int pb = (pix >> 16) & 0xff;
        int pr = (pix << 16) & 0xffff0000;
        int pix1 = (pix & 0xff00ff00) | pr | pb;
        bt[(h - k - 1) * w + j] = pix1;
      }
    }
    return Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
  }

}
