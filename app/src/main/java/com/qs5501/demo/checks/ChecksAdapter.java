package com.qs5501.demo.checks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qs5501.demo.sale.entity.Check;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

import java.util.ArrayList;

public class ChecksAdapter extends ArrayAdapter<Check> {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Check> checkArrayList;
    private Context context;

    public ChecksAdapter(Context context, int layout, ArrayList<Check> checkArrayList) {
        super(context, layout, checkArrayList);
        this.context = context;
        this.layout = layout;
        this.checkArrayList = checkArrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ChecksAdapter.ViewHolder viewHolder;
        if(convertView==null){
            //получаем вью из (list_item_check, parent, false)
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ChecksAdapter.ViewHolder) convertView.getTag();
        }
        final Check check = checkArrayList.get(position);

        viewHolder.checkType.setText(check.getTypeCheck());
        viewHolder.checkTotal.setText(ParseNumber.parseNumber("0.00", check.getTotal()));
        viewHolder.checkSignCalculation.setText(check.getSignCalculation());

        String timeDate = check.getTime() +"  "+ check.getDate();
        viewHolder.checkTimeDate.setText(timeDate);

        String numShift = "Номер смены " + check.getNumShift();
        viewHolder.checkNumberShift.setText(numShift);

        String numCheck = "Номер чека №" + check.getNumCheck();
        viewHolder.checkNumberCheck.setText(numCheck);

        return convertView;
    }

    private static class ViewHolder {
        final TextView checkType, checkTotal, checkSignCalculation, checkTimeDate,
                checkNumberShift, checkNumberCheck;
        ViewHolder(View view){
            checkType = (TextView) view.findViewById(R.id.list_item_check_type);
            checkTotal = (TextView) view.findViewById(R.id.list_item_check_total);
            checkSignCalculation = (TextView) view.findViewById(R.id.list_item_check_sign_calculation);
            checkTimeDate = (TextView) view.findViewById(R.id.list_item_check_time_date);
            checkNumberShift = (TextView) view.findViewById(R.id.list_item_check_number_shift);
            checkNumberCheck = (TextView) view.findViewById(R.id.list_item_check_number_check);
        }
    }
}
