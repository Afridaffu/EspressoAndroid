package com.greenbox.coyni.intro_slider;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.greenbox.coyni.utils.SwipeDirection;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class OneDirectionViewPager extends ViewPager {
  public static final int DEFAULT_INTERVAL = 1500;
  public static final int LEFT = 0;
  public static final int RIGHT = 1;
  private static final String TAG = "AutoScrollViewPager";
  public static final int SLIDE_BORDER_MODE_NONE = 0;
  public static final int SLIDE_BORDER_MODE_CYCLE = 1;
  public static final int SLIDE_BORDER_MODE_TO_PARENT = 2;
  private long interval = DEFAULT_INTERVAL;
  private int direction = RIGHT;
  private boolean isCycle = true;
  private boolean stopScrollWhenTouch = true;
  private int slideBorderMode = SLIDE_BORDER_MODE_NONE;
  private boolean isBorderAnimation = true;
  private double autoScrollFactor = 1.0;
  private double swipeScrollFactor = 1.0;
  private Handler handler;
  private boolean enabled;
  private boolean swipeEnabled = true;
  @Nullable
  private DurationScroller scroller;
  public static final int SCROLL_WHAT = 0;
  private boolean isAutoScroll = false;
  private boolean isStopByTouch = false;
  private float downX = 0f;

  private float initialXValue;
  private SwipeDirection newDirection;

  public OneDirectionViewPager(Context paramContext) {
    super(paramContext);
    init();
  }
  public OneDirectionViewPager(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    this.enabled = true;
    init();
  }
  private void init() {
    handler = new MyHandler(this);
    setViewPagerScroller();
  }
  /**
   * start auto scroll, first scroll delay time is {@link #getInterval()}.
   */
  public void startAutoScroll() {
    if (scroller != null) {
      isAutoScroll = true;
      sendScrollMessage(
          (long) (interval + scroller.getDuration() / autoScrollFactor * swipeScrollFactor));
    }
  }
  /**
   * start auto scroll.
   *
   * @param delayTimeInMills first scroll delay time.
   */
  public void startAutoScroll(int delayTimeInMills) {

    isAutoScroll = true;
    sendScrollMessage(delayTimeInMills);
  }
  /**
   * stop auto scroll.
   */
  public void stopAutoScroll() {
    isAutoScroll = false;
    handler.removeMessages(SCROLL_WHAT);
  }
  /**
   * set the factor by which the duration of sliding animation will change while swiping.
   */
  public void setSwipeScrollDurationFactor(double scrollFactor) {
    swipeScrollFactor = scrollFactor;
  }
  /**
   * set the factor by which the duration of sliding animation will change while auto scrolling.
   */
  public void setAutoScrollDurationFactor(double scrollFactor) {
    autoScrollFactor = scrollFactor;
  }
  private void sendScrollMessage(long delayTimeInMills) {
    /** remove messages before, keeps one message is running at most **/
    handler.removeMessages(SCROLL_WHAT);
    handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
  }
  /**
   * set ViewPager scroller to change animation duration when sliding.
   */
  private void setViewPagerScroller() {
    try {
      Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
      scrollerField.setAccessible(true);
      Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
      interpolatorField.setAccessible(true);
      scroller =
          new DurationScroller(getContext(), (Interpolator) interpolatorField.get(null));
      scrollerField.set(this, scroller);
    } catch (IllegalAccessException e) {
      Log.e(TAG, "setViewPagerScroller: ",e );
    //  Timber.e(e);
    } catch (NoSuchFieldException e) {
      Log.e(TAG, "setViewPagerScroller: ",e );
     // Timber.e(e);
    }
  }
  /**
   * scroll only once.
   */
  public void scrollOnce() {
    PagerAdapter adapter = getAdapter();
    int currentItem = getCurrentItem();
    int totalCount = adapter != null ? adapter.getCount() : -100;
    if (adapter == null || totalCount <= 1) {
      return;
    }
    int nextItem = (direction == LEFT) ? - currentItem : ++currentItem;
    if (nextItem < 0) {
      if (isCycle) {
        setCurrentItem(totalCount - 1, isBorderAnimation);
      }
    } else if (nextItem == totalCount) {
      if (isCycle) {
        setCurrentItem(0, isBorderAnimation);
      }
    } else {
      setCurrentItem(nextItem, true);
    }
  }
  private static class MyHandler extends Handler {
    private final WeakReference<OneDirectionViewPager> autoScrollViewPager;
    public MyHandler(OneDirectionViewPager autoScrollViewPager) {
      this.autoScrollViewPager = new WeakReference<OneDirectionViewPager>(autoScrollViewPager);
    }
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == SCROLL_WHAT) {
        OneDirectionViewPager pager = this.autoScrollViewPager.get();
        if (pager != null && pager.scroller != null) {
          pager.scroller.setScrollDurationFactor(pager.autoScrollFactor);
          pager.scrollOnce();
          pager.scroller.setScrollDurationFactor(pager.swipeScrollFactor);
          pager.sendScrollMessage(pager.interval + pager.scroller.getDuration());
        }
      }
    }
  }
  /**
   * get auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}.
   *
   * @return the interval.
   */
  public long getInterval() {
    return interval;
  }
  /**
   * set auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}.
   *
   * @param interval the interval to set.
   */
  public void setInterval(long interval) {
    this.interval = interval;
  }
  /**
   * get auto scroll direction.
   *
   * @return {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
   */
  public int getDirection() {
    return (direction == LEFT) ? LEFT : RIGHT;
  }
  /**
   * set auto scroll direction.
   *
   * @param direction {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
   */
  public void setDirection(int direction) {
    this.direction = direction;
  }
  /**
   * whether automatic cycle when auto scroll reaching the last or first item, default is true.
   *
   * @return the isCycle.
   */
  public boolean isCycleScroll() {
    return isCycle;
  }
  /**
   * set whether automatic cycle when auto scroll reaching the last or first item, default is true.
   *
   * @param isCycle the isCycle to set.
   */
  public void setCycle(boolean isCycle) {
    this.isCycle = isCycle;
  }
  /**
   * whether stop auto scroll when touching, default is true.
   *
   * @return the stopScrollWhenTouch.
   */
  public boolean isStopScrollWhenTouch() {
    return stopScrollWhenTouch;
  }
  /**
   * set whether stop auto scroll when touching, default is true.
   */
  public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
    this.stopScrollWhenTouch = stopScrollWhenTouch;
  }
  /**
   * get how to process when sliding at the last or first item.
   *
   * @return the slideBorderMode {@link #SLIDE_BORDER_MODE_NONE},
   * {@link #SLIDE_BORDER_MODE_TO_PARENT},
   * {@link #SLIDE_BORDER_MODE_CYCLE}, default is {@link #SLIDE_BORDER_MODE_NONE}
   */
  public int getSlideBorderMode() {
    return slideBorderMode;
  }
  /**
   * set how to process when sliding at the last or first item.
   *
   * @param slideBorderMode {@link #SLIDE_BORDER_MODE_NONE}, {@link #SLIDE_BORDER_MODE_TO_PARENT},
   * {@link #SLIDE_BORDER_MODE_CYCLE}, default is {@link #SLIDE_BORDER_MODE_NONE}
   */
  public void setSlideBorderMode(int slideBorderMode) {
    this.slideBorderMode = slideBorderMode;
  }
  /**
   * whether animating when auto scroll at the last or first item, default is true.
   */
  public boolean isBorderAnimationEnabled() {
    return isBorderAnimation;
  }
  /**
   * set whether animating when auto scroll at the last or first item, default is true.
   */
  public void setBorderAnimation(boolean isBorderAnimation) {
    this.isBorderAnimation = isBorderAnimation;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    int action = ev.getActionMasked();

    if (stopScrollWhenTouch) {
      if ((action == MotionEvent.ACTION_DOWN) && isAutoScroll) {
        isStopByTouch = true;
        stopAutoScroll();
      } else if (ev.getAction() == MotionEvent.ACTION_UP && isStopByTouch) {
        startAutoScroll();
      }
    }

    if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT || slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
      float touchX = ev.getX();
      if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        downX = touchX;
      }
      int currentItem = getCurrentItem();
      PagerAdapter adapter = getAdapter();
      int pageCount = adapter == null ? 0 : adapter.getCount();
      /**
       * current index is first one and slide to right or current index is last one and slide to left.<br/>
       * if slide border mode is to parent, then requestDisallowInterceptTouchEvent false.<br/>
       * else scroll to last one when current item is first one, scroll to first one when current item is last
       * one.
       */
      if ((currentItem == 0 && downX <= touchX) || (currentItem == pageCount - 1 && downX >= touchX)) {
        if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
          getParent().requestDisallowInterceptTouchEvent(false);
        } else {
          if (pageCount > 1) {
            setCurrentItem(pageCount - currentItem - 1, isBorderAnimation);
          }
          getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
      }
    }
    getParent().requestDisallowInterceptTouchEvent(true);

    return super.dispatchTouchEvent(ev);
  }

//  @Override
//  public boolean onTouchEvent(MotionEvent event) {
//    if (this.swipeEnabled) {
//      return super.onTouchEvent(event);
//    }
//
//    return false;
//  }
//
//  @Override
//  public boolean onInterceptTouchEvent(MotionEvent event) {
//    if (this.swipeEnabled) {
//      return super.onInterceptTouchEvent(event);
//    }
//
//    return false;
//  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (this.isSwipeAllowed(event)) {
      return super.onTouchEvent(event);
    }

    return false;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    if (this.isSwipeAllowed(event)) {
      return super.onInterceptTouchEvent(event);
    }

    return false;
  }

  public void setPagingEnabled(boolean enabled) {
    this.swipeEnabled = enabled;
  }


  private boolean isSwipeAllowed(MotionEvent event) {
    if(this.newDirection == SwipeDirection.ALL) return true;

    if(newDirection == SwipeDirection.NONE )//disable any swipe
      return false;

    if(event.getAction()==MotionEvent.ACTION_DOWN) {
      initialXValue = event.getX();
      return true;
    }

    if (event.getAction() == MotionEvent.ACTION_MOVE) {
      float diffX = event.getX() - initialXValue;
      if (diffX > 0 && newDirection == SwipeDirection.RIGHT) {
        // swipe from left to right detected
        return false;
      } else if (diffX < 0 && newDirection == SwipeDirection.LEFT) {
        // swipe from right to left detected
        return false;
      }
    }

    return true;
  }

  public void setAllowedSwipeDirection(SwipeDirection direction) {
    this.newDirection = direction;
  }
}