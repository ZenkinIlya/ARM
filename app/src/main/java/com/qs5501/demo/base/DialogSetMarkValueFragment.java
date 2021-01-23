package com.qs5501.demo.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Selection;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.qs.wiget.App;
import com.qs5501.demo.sale.dialog.DialogListener;
import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.utils.CheckComponent;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501.demo.utils.MoneyTextWatcher;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;
import com.satsuware.usefulviews.LabelledSpinner;

import java.util.Objects;

public class DialogSetMarkValueFragment extends DialogFragment {

    DialogListener dialogListener;
    private Button buttonAccept, buttonCancel;
    private TextInputEditText  productMarkValue;
    private ScanBroadcastReceiver scanBroadcastReceiver;

    private Product product, productMark;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dialogListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_set_mark_value, null);
        builder.setView(view);

        initComponents(view);
        listenBtn(view);

        //Получение объекта продукта
        product = (Product) getArguments().getSerializable(Product.class.getSimpleName());
        try {
            productMark = (Product) product.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        scanBroadcastReceiver = new ScanBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.qs.scancode");
        getContext().registerReceiver(scanBroadcastReceiver, intentFilter);

        //Поток обработки закрытия диалогФрагмента

        return builder.create();
    }

    class ScanBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Получить данные сканирования
            String barcode = Objects.requireNonNull(intent.getExtras()).getString("code");

            if (barcode != null) {
                productMarkValue.setText(barcode.trim());
            }
            Spannable text = productMarkValue.getText();
            Selection.setSelection(text, text.length());
        }
    }

    @Override
    public void onStop() {
        KeyboardHider.hideKeyboard(this.getActivity());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(scanBroadcastReceiver);
        super.onDestroy();
    }

    private void listenBtn(View view) {
        view.findViewById(R.id.dialog_set_mark_value_btn_qr_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productMarkValue.requestFocus();
                App.openScan();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckComponent.setCorrectData(true);
                CheckComponent.checkEdits(productMarkValue);

                if (CheckComponent.isCorrectData()){
                    productMark.setMarkValue(productMarkValue.getText().toString());
                    dialogListener.updateProduct(productMark);
                    dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initComponents(View view){
        TextInputLayout textInputLayout;

        textInputLayout = (TextInputLayout) view.findViewById(R.id.dialog_set_mark_value_mark);
        productMarkValue = (TextInputEditText) textInputLayout.getEditText();

        buttonAccept = (Button) view.findViewById(R.id.dialog_set_mark_value_btn_accept);
        buttonCancel = (Button) view.findViewById(R.id.dialog_set_mark_value_btn_cancel);
    }
}