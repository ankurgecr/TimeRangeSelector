package info.ankurpandya.timerangeselector;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.apptik.widget.MultiSlider;

public class PickTimeRange {

    private static final String DEFAULT_TIME_FORMAT = "hh:mm a";

    private Builder builder;
    private SimpleDateFormat timeFormat;

    TextView txtTitle;
    MultiSlider slider;
    View btnSelect;
    View btnCancel;
    TextView txtSubTitle;
    TextView txtSelection;
    TextView txtMin;
    TextView txtMax;

    public PickTimeRange(Builder builder) {
        this.builder = builder;
        this.timeFormat = new SimpleDateFormat(builder.visibleTimeFormat, Locale.getDefault());
    }

    private Dialog getDialog() {
        final Dialog dialog = new Dialog(builder.context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_pick_time);
        dialog.setCancelable(true);

        final Window window = dialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(0.5f);
        window.setAttributes(wlp);

        txtTitle = dialog.findViewById(R.id.txt_title);
        slider = dialog.findViewById(R.id.slider);
        btnSelect = dialog.findViewById(R.id.btn_select);
        btnCancel = dialog.findViewById(R.id.btn_cancel);
        txtSubTitle = dialog.findViewById(R.id.txt_sub_title);
        txtSelection = dialog.findViewById(R.id.txt_selection);
        txtMin = dialog.findViewById(R.id.txt_min);
        txtMax = dialog.findViewById(R.id.txt_max);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (builder.listener != null) {
                    builder.listener.onTimeRangeSelected(
                            toDate(slider.getThumb(0).getValue()),
                            toDate(slider.getThumb(1).getValue())
                    );
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (builder.title != null && builder.title.trim().length() > 0) {
            txtTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(builder.title);
        } else {
            txtTitle.setVisibility(View.GONE);
        }

        if (builder.subTitle != null) {
            txtSubTitle.setText(builder.subTitle);
        }

        //Final - static
        slider.setMax(toInt(builder.maxTime));
        slider.setMin(toInt(builder.minTime));
        txtMax.setText(format(builder.maxTime));
        txtMin.setText(format(builder.minTime));

        if (builder.minTime.equals(builder.maxTime)) {
            slider.setEnabled(false);
            slider.setOnThumbValueChangeListener(null);
            slider.repositionThumbs();
            updateSelectedTime(true);
        } else {
            slider.setEnabled(true);
            slider.setOnThumbValueChangeListener(null);
            updateSelectedTime(true);
            slider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
                @Override
                public void onValueChanged(
                        MultiSlider multiSlider,
                        MultiSlider.Thumb thumb,
                        int thumbIndex,
                        int value
                ) {
                    if (thumbIndex == 0) {
                        builder.fromTime = toDate(value);
                    } else {
                        builder.toTime = toDate(value);
                    }
                    updateSelectedTime(false);
                }
            });
        }


        return dialog;
    }

    private void updateSelectedTime(boolean autoPositionThumbs) {
        if (txtSelection != null) {
            txtSelection.setText(
                    format(builder.fromTime) +
                            " - " +
                            format(builder.toTime)
            );
            if (autoPositionThumbs) {
                slider.getThumb(1).setValue(toInt(builder.toTime));
                slider.getThumb(0).setValue(toInt(builder.fromTime));
            }
        }
    }

    private void updatePriceRangeTexts(boolean autoPositionThumbs) {
//        if (builder.newUserFilter == null) {
//            return;
//        }
//        String newMinPrice = builder.newUserFilter.getMinPrice();
//        String newMaxPrice = builder.newUserFilter.getMaxPrice();
//        if (newMinPrice == null) {
//            newMinPrice = (int) builder.systemFilters.getMinPrice() + "";
//        }
//        if (newMaxPrice == null) {
//            newMaxPrice = (int) builder.systemFilters.getMaxPrice() + "";
//        }
//        if (autoPositionThumbs) {
//            // WARNING - Here you have to set MAX value first and then set MIN value, due to library bugs
//            select_price_range.getThumb(1).setValue(Integer.parseInt(newMaxPrice));
//            select_price_range.getThumb(0).setValue(Integer.parseInt(newMinPrice));
//        }
//        txtSelection.setText(getString(
//                R.string.price_range_value,
//                StringUtils.getAmount(newMinPrice),
//                StringUtils.getAmount(newMaxPrice)
//        ));
    }

    private String format(Date date) {
        try {
            return timeFormat.format(date);
        } catch (Exception ignored) {
            return "";
        }
    }

    private static int toInt(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        return hours * 10000 + min * 100 + sec;
    }

    private static Date toDate(int time) {
        int hours = time / 10000;
        int min = time / 100 - hours * 100;
        int sec = time - hours * 10000 - min * 100;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        return calendar.getTime();
    }

    public interface Listener {
        void onTimeRangeSelected(Date fromTime, Date toTime);
    }

    public static class Builder {
        private Listener listener;
        private String title;
        private String subTitle;
        private Date minTime = toDate(0);
        private Date maxTime = toDate(235959);
        private Date fromTime;
        private Date toTime;
        private String visibleTimeFormat = DEFAULT_TIME_FORMAT;
        private Context context;

        public Builder(Context context) {
            this.context = context;
            updateFromTime();
            updateToTime();
        }

        public Builder setTitle(@Nullable String title) {
            this.title = title;
            return this;
        }

        public Builder setSubTitle(@Nullable String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public Builder setMinTime(@NonNull Date minTime) {
            this.minTime = minTime;
            updateFromTime();
            return this;
        }

        public Builder setMaxTime(@NonNull Date maxTime) {
            this.maxTime = maxTime;
            updateToTime();
            return this;
        }

        public Builder setFromTime(@Nullable Date fromTime) {
            if (fromTime != null && fromTime.after(minTime)) {
                this.fromTime = fromTime;
            } else {
                this.fromTime = minTime;
            }
            return this;
        }

        public Builder setToTime(@Nullable Date toTime) {
            if (toTime != null && toTime.before(maxTime)) {
                this.toTime = toTime;
            } else {
                this.toTime = maxTime;
            }
            return this;
        }

        public Builder setListener(Listener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setVisibleTimeFormat(@NonNull String timeFormat) {
            this.visibleTimeFormat = timeFormat;
            return this;
        }

        public Dialog build() {
            return new PickTimeRange(this).getDialog();
        }

        private void updateFromTime() {
            if (fromTime == null || fromTime.before(minTime)) {
                fromTime = minTime;
            }
        }

        private void updateToTime() {
            if (toTime == null || toTime.after(maxTime)) {
                toTime = maxTime;
            }
        }
    }
}
