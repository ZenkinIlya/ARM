package com.qs5501.demo.settings.operators;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qs5501.demo.settings.entity.Operator;
import com.qs5501demo.aidl.R;

import java.util.List;

public class OperatorsAdapter extends ArrayAdapter<Operator> {

    private final List<Operator> operatorList;
    private final int layout;
    private final LayoutInflater inflater;
    private final Context context;

    public OperatorsAdapter(@NonNull Context context, int resource, @NonNull List<Operator> operatorList) {
        super(context, resource, operatorList);
        this.operatorList = operatorList;
        this.inflater =  LayoutInflater.from(context);
        this.layout = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;
        //если нет вьюшки
        if(convertView==null){
            //получаем вью из (list_item_sale, parent, false)
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (OperatorsAdapter.ViewHolder) convertView.getTag();
        }
        final Operator operator = operatorList.get(position);

        String fullNameOperator = getFullNameOperator(operator);
        viewHolder.nameOperatorView.setText(fullNameOperator);
        viewHolder.postOperatorView.setText(operator.getPost());

        //Отображение первого администратора
        if (position == 0){
            viewHolder.deleteBtnOperatorView.setVisibility(View.INVISIBLE);
            viewHolder.operatorView.setBackgroundColor(Color.parseColor("#F0F8FF"));
        }

        viewHolder.redactBtnOperatorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof OperatorsActivity){
                    ((OperatorsActivity)context).updateOperator(operator, position);
                }
            }
        });

        viewHolder.deleteBtnOperatorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof OperatorsActivity){
                    ((OperatorsActivity)context).deleteOperator(operator, position);
                }
            }
        });

        return convertView;
    }

    private String getFullNameOperator(Operator operator) {
        String firstName = operator.getName();
        String secondName = operator.getSecondName();
        String thirdName = operator.getThirdName();

        if (!firstName.isEmpty()){
            firstName = " " + firstName.charAt(0) + ".";
        }

        if (!thirdName.isEmpty()){
            thirdName = thirdName.charAt(0) + ".";
        }

        return secondName + firstName + thirdName;
    }

    private static class ViewHolder {
        final TextView nameOperatorView, postOperatorView;
        final ImageButton redactBtnOperatorView, deleteBtnOperatorView;
        final LinearLayout operatorView;
        ViewHolder(View view){
            nameOperatorView = (TextView) view.findViewById(R.id.operators_name);
            postOperatorView = (TextView) view.findViewById(R.id.operators_post);
            redactBtnOperatorView = (ImageButton) view.findViewById(R.id.operators_btn_redact);
            deleteBtnOperatorView = (ImageButton) view.findViewById(R.id.operators_btn_delete);
            operatorView = (LinearLayout) view.findViewById(R.id.operators_item);
        }
    }

}
