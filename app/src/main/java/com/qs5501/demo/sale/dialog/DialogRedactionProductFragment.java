package com.qs5501.demo.sale.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.utils.CheckComponent;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501.demo.utils.MoneyTextWatcher;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;
import com.satsuware.usefulviews.LabelledSpinner;

public class DialogRedactionProductFragment extends DialogFragment {

    DialogListener dialogListener;
    Product product;
    private TextInputEditText goodDialogPrice, goodDialogQuantity, goodDialogDiscount, goodDialogDiscountPercent;
    private LabelledSpinner goodDialogMethodCalculate;
    private Button buttonAccept, buttonCancel;

    private void initializeComponent(View view){
        TextInputLayout textInputLayout;

        textInputLayout = (TextInputLayout) view.findViewById(R.id.good_dialog_price);
        goodDialogPrice = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) view.findViewById(R.id.good_dialog_quantity);
        goodDialogQuantity = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) view.findViewById(R.id.good_dialog_discount);
        goodDialogDiscount = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) view.findViewById(R.id.good_dialog_discount_percent);
        goodDialogDiscountPercent = (TextInputEditText) textInputLayout.getEditText();

        goodDialogMethodCalculate = (LabelledSpinner) view.findViewById(R.id.good_dialog_method_calculation);

        buttonAccept = (Button) view.findViewById(R.id.btn_dialog_redact_accept);
        buttonCancel = (Button) view.findViewById(R.id.btn_dialog_redact_cancel);
    }

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_redaction_product, null);
        builder.setView(view);

        initializeComponent(view);

        //Получение объекта продукта и его позиции
        product = (Product) getArguments().getSerializable(Product.class.getSimpleName());

        goodDialogPrice.setText(ParseNumber.parseNumber("#0.00", product.getPrice()));
        goodDialogQuantity.setText(ParseNumber.parseNumber("#0.000", product.getQuantity()));

        if (product.isMark()){
            goodDialogQuantity.setEnabled(false);
            TextInputLayout goodDialogQuantityTextInput = (TextInputLayout) view.findViewById(R.id.good_dialog_quantity);
            goodDialogQuantityTextInput.setError("не доступно для маркированного товара");
        }

        goodDialogDiscount.setText(ParseNumber.parseNumber("#0.00", product.getDiscount()));
        goodDialogDiscountPercent.setText(ParseNumber.parseNumber("00.00", product.getDiscountPercent()));
        goodDialogMethodCalculate.setSelection(product.getMethodOfCalculation());

        goodDialogPrice.addTextChangedListener(new MoneyTextWatcher(goodDialogPrice, "##0.00"));
        goodDialogQuantity.addTextChangedListener(new MoneyTextWatcher(goodDialogQuantity, "##0.000"));
        goodDialogDiscount.addTextChangedListener(new MoneyTextWatcher(goodDialogDiscount, "##0.00"));
        goodDialogDiscountPercent.addTextChangedListener(new MoneyTextWatcher(goodDialogDiscountPercent, "00.00"));

        //изменение прооисходит напрямую, обратно возвращать product не обязательно

        btnListen(view);
        return builder.create();
    }

    @Override
    public void onStop() {
        KeyboardHider.hideKeyboard(this.getActivity());
        super.onStop();
    }

    private void calculateDiscountPercent(){
        double price = Double.parseDouble(goodDialogPrice.getText().toString());
        double quantity = Double.parseDouble(goodDialogQuantity.getText().toString());
        double discount = Double.parseDouble(goodDialogDiscount.getText().toString());

        double discountPercent = (discount * 100) / (price * quantity);
        goodDialogDiscountPercent.setText(ParseNumber.parseNumber("00.00", discountPercent));
    }

    private void calculateDiscount(){
        double price = Double.parseDouble(goodDialogPrice.getText().toString());
        double quantity = Double.parseDouble(goodDialogQuantity.getText().toString());
        double discountPercent = Double.parseDouble(goodDialogDiscountPercent.getText().toString());

        double discount = discountPercent * price * quantity / 100;
        goodDialogDiscount.setText(ParseNumber.parseNumber("#0.00", discount));
    }

    private void btnListen(View view) {

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckComponent.setCorrectData(true);
                CheckComponent.checkEdits(goodDialogPrice);
                CheckComponent.checkEdits(goodDialogQuantity);
                CheckComponent.checkEdits(goodDialogDiscount);

                if (CheckComponent.isCorrectData()){
                    product.setPrice(Double.parseDouble(goodDialogPrice.getText().toString()));
                    product.setQuantity(Double.parseDouble(goodDialogQuantity.getText().toString()));
                    product.setDiscount(Double.parseDouble(goodDialogDiscount.getText().toString()));
                    product.setDiscountPercent(Double.parseDouble(goodDialogDiscountPercent.getText().toString()));
                    product.setMethodOfCalculation((byte) goodDialogMethodCalculate.getSpinner().getSelectedItemPosition());
                    dialogListener.updateCheck();
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

        goodDialogPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateDiscount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        goodDialogQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateDiscount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        goodDialogDiscountPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (goodDialogDiscountPercent.hasFocus()){
                    calculateDiscount();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        goodDialogDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (goodDialogDiscount.hasFocus()){
                    calculateDiscountPercent();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
