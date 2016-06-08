/*
 * Copyright 2016 Farbod Salamat-Zadeh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.satsuware.usefulviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

/**
 * A widget that can be 'flipped', like a card.
 *
 * <p>
 * The Views to display on the front and back of the card are set
 * via the constructors, XML attribute, or methods. Then, at particular
 * times, such as when this widget is clicked, the widget will display
 * a flipping animation to turn to the other side of the card.
 * </p>
 *
 * @attr ref R.styleable#FlippableView_viewFront
 * @attr ref R.styleable#FlippableView_viewBack
 */
public class FlippableView extends FrameLayout {

    /**
     * A tag to display on log/debugging messages for this class
     */
    private static final String LOG_TAG = "FlippableView";

    /**
     * Stores a context object which can be accessed throughout this
     * widget class for tasks such as inflating Views.
     */
    private Context mContext;

    /**
     * The View used as a side of the card/widget.
     */
    private View mFrontView, mBackView;

    /**
     * This is responsible for containing the Views for both sides of
     * the card/widget. It is used to animate the flip when changing
     * from showing one side of the card to another.
     */
    private ViewFlipper mViewFlipper;

    /**
     * Whether or not the reverse side of the card/widget is currently
     * being displayed
     */
    private boolean mIsBackShowing;


    public FlippableView(Context context) {
        this(context, null);
    }

    public FlippableView(Context context, View frontView, View backView) {
        this(context, null, frontView, backView);
    }

    public FlippableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlippableView(Context context, AttributeSet attrs, View frontView, View backView) {
        this(context, attrs, 0, frontView, backView);
    }

    public FlippableView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null, null);
    }

    public FlippableView(Context context, AttributeSet attrs, int defStyleAttr,
                         View frontView, View backView) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        mFrontView = frontView;
        mBackView = backView;
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.FlippableView, defStyleAttr, 0);

        initializeView(a);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlippableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr, defStyleRes, null, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlippableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes,
                         View frontView, View backView) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        mFrontView = frontView;
        mBackView = backView;
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.FlippableView, defStyleAttr, defStyleRes);

        initializeView(a);
    }


    /**
     * Sets up views and widget attributes
     *
     * @param a The {@link android.content.res.TypedArray} passed from the
     *          constructor(s) used to retrieve XML attributes
     */
    private void initializeView(final TypedArray a) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View[] views = new View[] {mFrontView, mBackView};
        int[] styleables = new int[] {R.styleable.FlippableView_viewFront,
                R.styleable.FlippableView_viewBack};
        for (int i = 0; i < 2; i++) {
            if (views[i] != null) {
                continue;
            }

            int viewResId = a.getResourceId(styleables[i], -1);
            if (viewResId == -1) {
                Log.d(LOG_TAG, "Front and/or back view not set yet (via constructor " +
                        "or XML attribute - will be ignored for now)");
                views[i] = null;
            } else {
                views[i] = inflater.inflate(viewResId, null);
            }
        }
        a.recycle();

        inflater.inflate(R.layout.widget_flippable_view, this, true);
        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        mViewFlipper = (ViewFlipper) getChildAt(0);

        if (mFrontView != null && mBackView != null) {
            updateFrontAndBack();
        }
    }


    /**
     * Updates both sides of the card/widget by removing both Views, and
     * re-adding them using the global variables.
     *
     * Listeners for click callbacks are set to these views, and they are
     * made sure to have the same height (as a piece of card could not be
     * longer on one side).
     */
    private void updateFrontAndBack() {
        mViewFlipper.removeAllViews();

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                flip();
            }
        };
        View[] views = new View[] {mFrontView, mBackView};
        for (int i = 0; i < 2; i++) {
            views[i].setOnClickListener(onClickListener);
            mViewFlipper.addView(views[i]);
        }
        mIsBackShowing = false;

        mViewFlipper.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int flipperHeight = mViewFlipper.getHeight();
                mFrontView.setMinimumHeight(flipperHeight);
                mBackView.setMinimumHeight(flipperHeight);
            }
        });
    }


    /**
     * Animates a 'flip' so that the widget displays the View on the
     * opposite side to what is currently being displayed.
     */
    public void flip() {
        if (mFrontView == null || mBackView == null) {
            throw new NullPointerException("You must specify a front and back view for the " +
                    "FlippableView, through either a constructor, XML attribute, or method");
        }

        if (!mIsBackShowing) {
            if (mViewFlipper.getDisplayedChild() == 1) {
                return;  // If there is a child (to the left), stop
            }
            mViewFlipper.setInAnimation(mContext, R.anim.grow_from_middle);
            mViewFlipper.setOutAnimation(mContext, R.anim.shrink_to_middle);
            mViewFlipper.showPrevious();  // Display previous screen
        } else {
            if (mViewFlipper.getDisplayedChild() == 0) {
                return;  // If there aren't any other children, stop
            }
            mViewFlipper.setInAnimation(mContext, R.anim.grow_from_middle);
            mViewFlipper.setOutAnimation(mContext, R.anim.shrink_to_middle);
            mViewFlipper.showNext();  // Display next screen
        }

        mIsBackShowing = !mIsBackShowing;
    }


    /**
     * @return the View used as the front of the card/widget
     */
    public View getFrontView() {
        return mFrontView;
    }

    /**
     * @return the View used as the back of the card/widget
     */
    public View getBackView() {
        return mBackView;
    }

    /**
     * @return whether or not the back side of the card is currently
     * being displayed
     */
    public boolean isBackShowing() {
        return mIsBackShowing;
    }

    /**
     * Changes the View shown on the front side of the card
     *
     * @param frontView The View for the front side of the card
     *
     * @see #getFrontView()
     * @see #setBackView(View)
     * @see #setFrontAndBackViews(View, View)
     * @attr ref R.styleable#FlippableView_viewFront
     */
    public void setFrontView(View frontView) {
        mFrontView = frontView;
        updateFrontAndBack();
    }

    /**
     * Changes the View shown on the back side of the card
     *
     * @param backView The view for the back side of the card
     *
     * @see #getBackView()
     * @see #setFrontView(View)
     * @see #setFrontAndBackViews(View, View)
     * @attr ref R.styleable#FlippableView_viewBack
     */
    public void setBackView(View backView) {
        mBackView = backView;
        updateFrontAndBack();
    }

    /**
     * Changes the View shown on both sides of the card
     *
     * @param frontView The View for the front side of the card
     * @param backView The View for the back side of the card
     *
     * @see #setFrontView(View)
     * @see #setBackView(View)
     * @attr ref R.styleable#FlippableView_viewFront
     * @attr ref R.styleable#FlippableView_viewBack
     */
    public void setFrontAndBackViews(View frontView, View backView) {
        mFrontView = frontView;
        mBackView = backView;
        updateFrontAndBack();
    }

    /**
     * Removes the front and back Views from the card/widget
     *
     * @see #setFrontAndBackViews(View, View)
     */
    public void removeFrontAndBack() {
        mViewFlipper.removeAllViews();
    }
}

