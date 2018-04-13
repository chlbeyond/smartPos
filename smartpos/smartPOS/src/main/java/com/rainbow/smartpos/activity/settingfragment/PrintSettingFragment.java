package com.rainbow.smartpos.activity.settingfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.tablestatus.ShopPrinterAdapter;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala._TestPrintRequest;
import com.sanyipos.sdk.model.ShopPrinter;
import com.sanyipos.sdk.model.TestPrintResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2016/8/30.
 */
public class PrintSettingFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_shop_printer,container,false);
        ShopPrinterAdapter shopPrinterAdapter = new ShopPrinterAdapter(getActivity());
        ListView listView_dialog_shop_printer_list = (ListView) view.findViewById(R.id.listView_dialog_shop_printer_list);
        Button button_shop_printer_item_test = (Button) view.findViewById(R.id.button_shop_printer_item_test);
        button_shop_printer_item_test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                List<Long> printers = new ArrayList<Long>();
                for (ShopPrinter printer : SanyiSDK.rest.operationData.shopPrinters) {
                    printers.add(printer.id);
                }
                SanyiScalaRequests.testPrinterRequest(printers, new _TestPrintRequest.ITestPrintListener() {
                    @Override
                    public void onSuccess(TestPrintResult result) {

                    }



                    @Override
                    public void onFail(String    error) {

                    }
                });
            }
        });
        listView_dialog_shop_printer_list.setAdapter(shopPrinterAdapter);
        return view;
    }
}
