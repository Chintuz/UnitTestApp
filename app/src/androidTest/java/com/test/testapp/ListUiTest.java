package com.test.testapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

public class ListUiTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(
            MainActivity.class, true, true
    );

    @Test
    public void testRecycleVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.my_recycler_view))
                .inRoot(RootMatchers.withDecorView(Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testForRecyclerClick() {
        Espresso.onView(ViewMatchers.withId(R.id.my_recycler_view))
                .inRoot(RootMatchers.withDecorView(Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
    }

    @Test
    public void testForRecyclerScroll() {

        RecyclerView view = mainActivityActivityTestRule.getActivity().findViewById(R.id.my_recycler_view);
        int itemCount = view.getChildCount();

        Espresso.onView(ViewMatchers.withId(R.id.my_recycler_view))
                .inRoot(RootMatchers.withDecorView(Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .perform(RecyclerViewActions.scrollToPosition(itemCount - 1));
    }

    @Test
    public void testForRecyclerItemView() {

        Espresso.onView(ViewMatchers.withId(R.id.my_recycler_view))
                .inRoot(RootMatchers.withDecorView(Matchers.is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(ViewAssertions.matches(withViewAtPosition(0, 0, ViewMatchers.withId(R.id.image))));

    }

    public Matcher<View> withViewAtPosition(int position, int itemPosition, Matcher<View> item) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                item.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                return viewHolder != null && item.matches(((RelativeLayout) ((RelativeLayout) ((CardView) viewHolder.itemView).getChildAt(0))
                        .getChildAt(1)).getChildAt(itemPosition));
            }
        };
    }
}
