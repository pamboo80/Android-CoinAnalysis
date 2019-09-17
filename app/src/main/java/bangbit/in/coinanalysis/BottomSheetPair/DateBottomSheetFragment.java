package bangbit.in.coinanalysis.BottomSheetPair;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import bangbit.in.coinanalysis.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDateInteractionListener} interface
 * to handle interaction events.
 */
public class DateBottomSheetFragment extends BottomSheetDialogFragment {

    private static OnDateInteractionListener mListener;
    private final String[] months;
    private final int todaysYear;
    private NumberPicker dateNumberPicker;
    private boolean isLeapYear;
    int date, month, year;
    Calendar todaysCalander = Calendar.getInstance();

    private DateBottomSheetFragment bottomSheetFragment;
    private static Calendar calendar;
    private final int todaysDate;
    private final int todaysMonth;
    private String TAG = DateBottomSheetFragment.class.getSimpleName();


    public DateBottomSheetFragment() {
        // Required empty public constructor
        todaysMonth = todaysCalander.get(Calendar.MONTH) + 1;
        todaysDate = todaysCalander.get(Calendar.DATE);
        todaysYear = todaysCalander.get(Calendar.YEAR);
        Log.d("date", "DateBottomSheetFragment: " + todaysCalander.get(Calendar.YEAR));
        months = new String[]
                {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                        "Sep", "Oct", "Nov", "Dec"};
    }

    public static DateBottomSheetFragment newInstance(OnDateInteractionListener onFragmentInteractionListener,
                                                      Calendar selectedDateCalander) {
        DateBottomSheetFragment dateBottomSheetFragment = new DateBottomSheetFragment();
        mListener = onFragmentInteractionListener;
        if (selectedDateCalander == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = selectedDateCalander;
            Log.d("DateBottomSheetFragment", "newInstance: " + calendar.get(Calendar.YEAR));
        }
        return dateBottomSheetFragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_date_bottom_sheet, container, false);
        bottomSheetFragment = this;
        TextView doneTextView = view.findViewById(R.id.doneButton);
        TextView cancelTextView = view.findViewById(R.id.cancelButton);

        date = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        isLeapYear(calendar.get(Calendar.YEAR));

        dateNumberPicker = view.findViewById(R.id.date_number_picker);
        dateNumberPicker.setMinValue(1);
        dateNumberPicker.setValue(calendar.get(Calendar.DATE));
        dateNumberPicker.setFormatter("%02d");

        final NumberPicker monthNumberPicker = view.findViewById(R.id.month_number_picker);
        monthNumberPicker.setMinValue(1);
        monthNumberPicker.setMaxValue(months.length);
        monthNumberPicker.setDisplayedValues(months);
        monthNumberPicker.setValue(calendar.get(Calendar.MONTH) + 1);


        final NumberPicker yearNumberPicker = view.findViewById(R.id.year_number_picker);
        yearNumberPicker.setMinValue(2008);
        yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR));
        yearNumberPicker.setValue(calendar.get(Calendar.YEAR));
        if (todaysYear==yearNumberPicker.getMaxValue()) {
            if (month > todaysMonth) {
                yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR) - 1);

            } else {
                if (date > todaysDate && month >= todaysMonth) {
                    yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR) - 1);
                } else {
                    yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR));
                }
            }
        }
        dateNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                month = monthNumberPicker.getValue();
                year = yearNumberPicker.getValue();
                date = newVal;
                if (month > todaysMonth) {
                    yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR) - 1);

                } else {
                    if (date > todaysDate && month >= todaysMonth) {
                        yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR) - 1);
                    } else {
                        yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR));
                    }
                }
            }
        });

        yearNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                year = newVal;
                isLeapYear(newVal);
                if (monthNumberPicker.getValue() == 2) {
                    if (isLeapYear) {
                        updateDateNumberPicker(29);
                    } else {
                        updateDateNumberPicker(28);
                    }
                }
            }
        });

        monthNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                month = newVal;
                year = yearNumberPicker.getValue();
                date = dateNumberPicker.getValue();
                if (month > todaysMonth) {
                    yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR) - 1);

                } else {
                    if (date > todaysDate && month >= todaysMonth) {
                        yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR) - 1);
                    } else {
                        yearNumberPicker.setMaxValue(todaysCalander.get(Calendar.YEAR));
                    }
                }

                switch (newVal) {
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        updateDateNumberPicker(30);
                        break;
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        updateDateNumberPicker(31);
                        break;
                    case 2:
                        if (isLeapYear) {
                            updateDateNumberPicker(29);
                        } else {
                            updateDateNumberPicker(28);
                        }
                        break;
                }
            }
        });
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = yearNumberPicker.getValue();
                date = dateNumberPicker.getValue();
                month=monthNumberPicker.getValue();
                mListener.onDateInteraction(date, month, year, months[month - 1]);
                bottomSheetFragment.dismiss();
            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetFragment.dismiss();
            }
        });
        return view;
    }


    private void isLeapYear(int newVal) {
        isLeapYear = ((newVal % 4 == 0) && (newVal % 100 != 0)) || (newVal % 400 == 0);
    }

    private void updateDateNumberPicker(int endValue) {
        dateNumberPicker.setMaxValue(endValue);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnDateInteractionListener {
        void onDateInteraction(int date, int month, int year, String monthString);
    }
}
