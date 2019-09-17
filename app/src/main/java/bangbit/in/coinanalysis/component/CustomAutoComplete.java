package bangbit.in.coinanalysis.component;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;


/**
 * Created by Nagarajan on 2/19/2018.
 */

public class CustomAutoComplete extends AppCompatAutoCompleteTextView {

    public CustomAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }
}
