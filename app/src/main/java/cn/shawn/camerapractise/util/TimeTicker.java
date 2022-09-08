package cn.shawn.camerapractise.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TimeTicker {

    private final TickHandler mHandler;

    public TimeTicker(int period) {
        mHandler = new TickHandler(period);
    }

    public boolean isStarted() {
        return mHandler.isStarted();
    }

    public void start(OnTickListener listener) {
        mHandler.loop(listener);
    }

    public void stop() {
        mHandler.release();
    }

    private static class TickHandler extends Handler {

        private static final int TYPE_MSG_TICK = 0x1024;

        private final int mPeriod;

        private volatile boolean mIsStarted = false;

        @Nullable
        private OnTickListener mTickListener;

        private TickHandler(int period) {
            super(Looper.getMainLooper());
            this.mPeriod = period;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (!mIsStarted) return;
            if (mTickListener != null) {
                mTickListener.onTick();
            }
            sendEmptyMessageDelayed(TYPE_MSG_TICK, mPeriod);
        }

        void loop(OnTickListener listener) {
            if (mIsStarted) return;
            mIsStarted = true;
            mTickListener = listener;
            sendEmptyMessageDelayed(TYPE_MSG_TICK, mPeriod);
        }

        void release() {
            mIsStarted = false;
            mTickListener = null;
            removeMessages(TYPE_MSG_TICK);
        }

        public boolean isStarted() {
            return mIsStarted;
        }

    }

    public interface OnTickListener {
        void onTick();
    }

}
