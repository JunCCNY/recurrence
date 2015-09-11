package com.bleyl.recurrence.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bleyl.recurrence.adapters.ColoursAdapter;
import com.bleyl.recurrence.adapters.IconsAdapter;
import com.bleyl.recurrence.models.Notification;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.database.Database;
import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.utils.AnimationUtil;
import com.bleyl.recurrence.utils.DateAndTimeUtil;

import java.util.Calendar;

public class CreateEditActivity extends AppCompatActivity {

    private CoordinatorLayout mCoordinatorLayout;
    private AlertDialog mIconSelectorDialog;
    private AlertDialog mColourSelectorDialog;
    private TypedArray mIconsArray;
    private String[] mColoursArray;
    private String[] mColourNames;
    private EditText mTitleEditText;
    private EditText mContentEditText;
    private TextView mTimeText;
    private TextView mDateText;
    private TextView mRepeatText;
    private SwitchCompat mForeverSwitch;
    private EditText mTimesEditText;
    private TableRow mForeverRow;
    private TableRow mBottomRow;
    private View mBottomView;
    private TextView mShowText;
    private TextView mTimesText;
    private TextView mIconText;
    private TextView mColourText;
    private ImageView mImageIconSelect;
    private ImageView mImageColourSelect;
    private ImageView mImageWarningTime;
    private ImageView mImageWarningDate;
    private ImageView mImageWarningShow;
    private Calendar mCalendar;
    private int mTimesShown;
    private int mRepeatType;
    private int mIconNumber;
    private int mColourNumber;
    private boolean newNotification;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.create_coordinator);
        mTitleEditText = (EditText) findViewById(R.id.notificationTitle);
        mContentEditText = (EditText) findViewById(R.id.notificationContent);
        mTimeText = (TextView) findViewById(R.id.time);
        mDateText = (TextView) findViewById(R.id.date);
        mRepeatText = (TextView) findViewById(R.id.repeatDay);
        mForeverSwitch = (SwitchCompat) findViewById(R.id.switchToggle);
        mTimesEditText = (EditText) findViewById(R.id.showTimesNumber);
        mForeverRow = (TableRow) findViewById(R.id.foreverRow);
        mBottomRow = (TableRow) findViewById(R.id.bottomRow);
        mBottomView = findViewById(R.id.bottomView);
        mShowText = (TextView) findViewById(R.id.show);
        mTimesText = (TextView) findViewById(R.id.times);
        mIconText = (TextView) findViewById(R.id.selectIconText);
        mColourText = (TextView) findViewById(R.id.selectColourText);
        mImageColourSelect = (ImageView) findViewById(R.id.colourIcon);
        mImageIconSelect = (ImageView) findViewById(R.id.selectedIcon);
        mImageWarningTime = (ImageView) findViewById(R.id.errorTime);
        mImageWarningDate = (ImageView) findViewById(R.id.errorDate);
        mImageWarningShow = (ImageView) findViewById(R.id.errorShow);

        mIconsArray = getResources().obtainTypedArray(R.array.icons_array);
        mColoursArray = getResources().getStringArray(R.array.colours_array);
        mColourNames = getResources().getStringArray(R.array.colour_names_array);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        mCalendar = Calendar.getInstance();
        mId = getIntent().getIntExtra("NOTIFICATION_ID", 0);

        // Check whether to edit or create a new notification
        if (mId == 0) {
            assignDefaultValues();
        } else {
            assignNotificationValues();
        }
    }

    public void assignDefaultValues() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(getResources().getString(R.string.create_notification));
        Database database = new Database(this.getApplicationContext());
        mId = database.getLastId() + 1;
        database.close();
        mCalendar.set(Calendar.SECOND, 0);
        mTimesEditText.setText("1");
        newNotification = true;
        mRepeatType = 0;
        mTimesShown = 0;
        mIconNumber = 0;
        mColourNumber = 0;
    }

    public void assignNotificationValues() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(getResources().getString(R.string.edit_notification));
        Database database = new Database(this.getApplicationContext());
        Notification notification = database.getNotification(mId);
        database.close();

        mShowText.setText(String.format(getResources().getString(R.string.times_shown_edit), notification.getNumberShown()));
        mTimesEditText.setText(Integer.toString(notification.getNumberToShow()));
        mCalendar = DateAndTimeUtil.parseDateAndTime(notification.getDateAndTime());
        mTitleEditText.setText(notification.getTitle());
        mContentEditText.setText(notification.getContent());
        mTimeText.setText(DateAndTimeUtil.toStringReadableTime(mCalendar));
        mDateText.setText(DateAndTimeUtil.toStringReadableDate(mCalendar));
        mTimesEditText.setText(Integer.toString(notification.getNumberToShow()));
        mTimesShown = notification.getNumberShown();
        mRepeatType = notification.getRepeatType();
        mIconNumber = notification.getIconNumber();
        mColourNumber = notification.getColourNumber();
        mTimesText.setVisibility(View.VISIBLE);
        mCalendar.set(Calendar.SECOND, 0);
        newNotification = false;

        if (mIconNumber != 0) {
            mImageIconSelect.setImageDrawable(mIconsArray.getDrawable(mIconNumber));
            mIconText.setText(getResources().getString(R.string.custom_icon));
        }

        if (mColourNumber != 0) {
            mImageColourSelect.setColorFilter(Color.parseColor(mColoursArray[mColourNumber]));
            mColourText.setText(mColourNames[mColourNumber]);
        }

        if (notification.getRepeatType() != 0) {
            mForeverRow.setVisibility(View.VISIBLE);
            mBottomRow.setVisibility(View.VISIBLE);
            mBottomView.setVisibility(View.VISIBLE);
            String[] mRepeatTexts = getResources().getStringArray(R.array.repeat_array);
            mRepeatText.setText(mRepeatTexts[notification.getRepeatType()]);
        }

        if (Boolean.parseBoolean(notification.getForeverState())) {
            mForeverSwitch.setChecked(true);
            toggleTextColour();
        }
    }

    public void timePicker(View view) {
        TimePickerDialog TimePicker = new TimePickerDialog(CreateEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                mCalendar.set(Calendar.MINUTE, minute);
                mTimeText.setText(DateAndTimeUtil.toStringReadableTime(mCalendar));
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        TimePicker.show();
    }

    public void datePicker(View view) {
        DatePickerDialog DatePicker = new DatePickerDialog(CreateEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker DatePicker, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDateText.setText(DateAndTimeUtil.toStringReadableDate(mCalendar));
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        DatePicker.show();
    }

    public void iconSelector(View view) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.create_coordinator);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_dialog_icons, coordinatorLayout, false);

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.icons_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(new IconsAdapter(this, R.layout.item_icon_grid, mIconsArray));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.select_icon));
        builder.setView(dialogView);
        mIconSelectorDialog = builder.show();
    }

    public void iconSelected(Drawable icon, int position) {
        mIconNumber = position;
        if (mIconNumber != 0) {
            mIconText.setText(getResources().getString(R.string.custom_icon));
        } else {
            mIconText.setText(getResources().getString(R.string.default_icon));
        }
        mImageIconSelect.setImageDrawable(icon);
        mIconSelectorDialog.cancel();
    }

    public void colourSelector(View view) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.create_coordinator);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_dialog_colour, coordinatorLayout, false);

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.colours_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new ColoursAdapter(this, R.layout.item_colour_list, mColoursArray, mColourNames));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.select_colour));
        builder.setView(dialogView);
        mColourSelectorDialog = builder.show();
    }

    public void colourSelected(String name, int colour, int position) {
        mColourNumber = position;
        mImageColourSelect.setColorFilter(colour);
        mColourText.setText(name);
        mColourSelectorDialog.cancel();
    }

    public void repeatSelector(View view) {
        final String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(repeatArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mForeverSwitch.setChecked(false);
                    mForeverRow.setVisibility(View.GONE);
                    mBottomRow.setVisibility(View.GONE);
                    mBottomView.setVisibility(View.GONE);
                } else {
                    mForeverRow.setVisibility(View.VISIBLE);
                    mBottomRow.setVisibility(View.VISIBLE);
                    mBottomView.setVisibility(View.VISIBLE);
                }
                mRepeatType = which;
                mRepeatText.setText(repeatArray[which]);
            }
        });
        builder.create();
        builder.show();
    }

    public void saveNotification() {
        Calendar nowCalendar = Calendar.getInstance();
        String title = mTitleEditText.getText().toString();
        String content = mContentEditText.getText().toString();
        String forever = Boolean.toString(mForeverSwitch.isChecked());
        int timesToShow = Integer.parseInt(mTimesEditText.getText().toString());

        // Assign time depending on whether a value was selected
        String time;
        if (mTimeText.getText().equals(getResources().getString(R.string.time_now))) {
            time = DateAndTimeUtil.toStringTime(nowCalendar);
        } else {
            time = DateAndTimeUtil.toStringTime(mCalendar);
        }

        // Assign date depending on whether a value was selected
        String date;
        if (mDateText.getText().equals(getResources().getString(R.string.date_today))) {
            date = DateAndTimeUtil.toStringDate(nowCalendar);
        } else {
            date = DateAndTimeUtil.toStringDate(mCalendar);
        }

        // Show notification once if set to not repeat
        if (mRepeatType == 0) {
            timesToShow = mTimesShown + 1;
        }

        Database database = new Database(this.getApplicationContext());
        Notification notification = new Notification(mId, title, content, date + time, mRepeatType, forever, timesToShow, mTimesShown, mIconNumber, mColourNumber);
        if (newNotification) {
            database.add(notification);
        } else {
            database.update(notification);
        }
        database.close();
        mIconsArray.recycle();
        AlarmUtil.setAlarm(getApplicationContext(), notification.getId(), mCalendar);
        finish();
    }

    public void toggleSwitch(View view) {
        mForeverSwitch.toggle();
        toggleTextColour();
    }

    public void switchClicked(View view) {
        toggleTextColour();
    }

    public void toggleTextColour() {
        if (mForeverSwitch.isChecked()) {
            mShowText.setTextColor(getResources().getColor(R.color.textLightGray));
            mTimesEditText.setTextColor(getResources().getColor(R.color.textLightGray));
            mTimesText.setTextColor(getResources().getColor(R.color.textLightGray));
        } else {
            mShowText.setTextColor(Color.BLACK);
            mTimesEditText.setTextColor(Color.BLACK);
            mTimesText.setTextColor(Color.BLACK);
        }
    }

    public void validateInput() {
        Calendar nowCalendar = Calendar.getInstance();
        Boolean isTimeNow = mTimeText.getText().equals(getResources().getString(R.string.time_now));
        Boolean isToday = mDateText.getText().equals(getResources().getString(R.string.date_today));

        if (isTimeNow) {
            mCalendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY));
            mCalendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE));
        }

        if (isToday) {
            mCalendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
            mCalendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
            mCalendar.set(Calendar.DAY_OF_MONTH, nowCalendar.get(Calendar.DAY_OF_MONTH));
        }

        // Check if the number of times to show notification is empty
        String times;
        if (mTimesEditText.getText().toString().isEmpty()) {
            times = "0";
            mTimesEditText.append(times);
        } else {
            times = mTimesEditText.getText().toString();
        }

        // Check if selected date is before today's date
        if (DateAndTimeUtil.toLongDateAndTime(mCalendar) < DateAndTimeUtil.toLongDateAndTime(nowCalendar)) {
            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.toast_past_date), Snackbar.LENGTH_SHORT).show();
            mImageWarningTime.setVisibility(View.VISIBLE);
            mImageWarningDate.setVisibility(View.VISIBLE);

            // Check if title is empty
        } else if (mTitleEditText.getText().toString().trim().isEmpty()) {
            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.toast_title_empty), Snackbar.LENGTH_SHORT).show();
            AnimationUtil.shakeView(mTitleEditText, getApplicationContext());

            // Check if times to show notification is too low
        } else if (Integer.parseInt(times) <= mTimesShown && !mForeverSwitch.isChecked() && mRepeatType != 0) {
            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.toast_higher_number), Snackbar.LENGTH_SHORT).show();
            mImageWarningShow.setVisibility(View.VISIBLE);
        } else {
            saveNotification();
        }
    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                validateInput();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}