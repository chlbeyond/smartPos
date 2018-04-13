package com.rainbow.smartpos.tablestatus;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rainbow.smartpos.MainScreenActivity;
import com.rainbow.smartpos.R;
import com.rainbow.smartpos.dialog.NormalDialog;
import com.sanyipos.sdk.api.SanyiSDK;
import com.sanyipos.sdk.api.SanyiScalaRequests;
import com.sanyipos.sdk.api.services.scala._TestPrintRequest;
import com.sanyipos.sdk.model.ShopPrinter;
import com.sanyipos.sdk.model.TestPrintResult;
import com.sanyipos.sdk.model.rest.TableGroup;
import com.sanyipos.sdk.utils.ConstantsUtil;
import com.sanyipos.sdk.utils.DateHelper;
import com.sanyipos.sdk.utils.NetUtil;
import com.rainbow.smartpos.Restaurant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TableViewPagerFragment extends Fragment implements OnClickListener {
    public static final int UPDATE_DATE = 1;
    public ViewPager mViewPager;
    public TableLocationPagerAdapter mPagerAdapter;
    public TableStatusIndicatorAdapter indicatorAdapter;
    public ListView listView_table_status_indicator;
    public ShopPrinterAdapter shopPrinterAdapter;
    TextView textViewServiceType;
    TextView textViewCurrentDevice;
    //TextView textViewCurrentUser;
    TextView textViewCurrentDay;
    TextView textView_table_status_print;
    ImageView imageView_table_status_print;
    ImageView imageView_table_status_cloud_connect;
    ImageView imageView_table_status_local_network;
    private LinearLayout table_status_group;
    private LinearLayout freeTable;
    private LinearLayout useTable;
    private LinearLayout prePrintTable;
    private LinearLayout bingTables;
    public static MainScreenActivity activity;
    Timer timer;
    private boolean free = true;
    private boolean use = true;
    private boolean preprint = true;
    private boolean bing = true;

    public View rootView;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch ((Integer) msg.obj) {
                case UPDATE_DATE:
                    textViewCurrentDay.setText(DateHelper.currentTimeNoSecond());
                    String serviceType = SanyiSDK.rest.getServiceHourNameByTime(new Date());
                    textViewServiceType.setText(serviceType);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragment_table_status_viewpager, container, false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        activity = (MainScreenActivity) getActivity();
        listView_table_status_indicator = (ListView) rootView.findViewById(R.id.listView_table_status_indicator);

        textViewServiceType = (TextView) rootView.findViewById(R.id.textViewServiceType);
        String serviceType = SanyiSDK.rest.getServiceHourNameByTime(new Date());
        textViewServiceType.setText(serviceType);

        textViewCurrentDevice = (TextView) rootView.findViewById(R.id.textViewCurrentDevice);
        textViewCurrentDevice.setText(Html.fromHtml(String.format("%s&#160;<font color='#FFFFFF'>(</font>%s<font color='#FFFFFF'>)</font>", SanyiSDK.registerData.getPosName(), NetUtil.getLocalIpAddress())));

        textViewCurrentDay = (TextView) rootView.findViewById(R.id.textViewCurrentDay);


        textView_table_status_print = (TextView) rootView.findViewById(R.id.textView_table_status_print);
        textView_table_status_print.setOnClickListener(this);
        imageView_table_status_print = (ImageView) rootView.findViewById(R.id.imageView_table_status_print);
        imageView_table_status_print.setOnClickListener(this);
        imageView_table_status_cloud_connect = (ImageView) rootView.findViewById(R.id.imageView_table_status_cloud_connect);
        imageView_table_status_local_network = (ImageView) rootView.findViewById(R.id.imageView_table_status_local_network);
        if (SanyiSDK.getPrinterStatus()) {
            imageView_table_status_print.setImageResource(R.drawable.print);
        } else {
            imageView_table_status_print.setImageResource(R.drawable.print_error);
        }
        setCloudConnectImage();
        indicatorAdapter = new TableStatusIndicatorAdapter(getActivity());
        listView_table_status_indicator.setAdapter(indicatorAdapter);
        listView_table_status_indicator.setFocusable(false);
        listView_table_status_indicator.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                indicatorAdapter.setSelectIndex(position);
                indicatorAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(position);
            }
        });
        mPagerAdapter = new TableLocationPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (hasFocus) {
                }
            }
        });
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                indicatorAdapter.setSelectIndex(position);
                indicatorAdapter.notifyDataSetChanged();
                listView_table_status_indicator.setSelection(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        timer = new Timer();
        freeTable = (LinearLayout) rootView.findViewById(R.id.freeTable);
        useTable = (LinearLayout) rootView.findViewById(R.id.isUsingTable);
        prePrintTable = (LinearLayout) rootView.findViewById(R.id.prePrintTable);
        bingTables = (LinearLayout) rootView.findViewById(R.id.bingTable);
        table_status_group = (LinearLayout) rootView.findViewById(R.id.table_status_group);
        freeTable.setOnClickListener(this);
        useTable.setOnClickListener(this);
        prePrintTable.setOnClickListener(this);
        bingTables.setOnClickListener(this);

        return rootView;
    }

    public void updateScreen() {
        String serviceType = SanyiSDK.rest.getServiceHourNameByTime(new Date());
        textViewServiceType.setText(serviceType);
    }

    public class TableLocationPagerAdapter extends FragmentPagerAdapter {
        ArrayList<TableGroupItemFragment> _tableGroup = new ArrayList<TableGroupItemFragment>();

        public TableLocationPagerAdapter(FragmentManager fm) {
            super(fm);
            initFragments();
        }

        private void initFragments() {
            TableGroupItemFragment prevF = null;
            for (int i = 0; i <= SanyiSDK.rest.tableGroups.size() + 1; ++i) {
                TableGroupItemFragment f = new TableGroupItemFragment();
                Bundle args = new Bundle();
                args.putInt("number", i);
                f.setArguments(args);
                _tableGroup.add(f);
                if (prevF != null) {
                    prevF.nextFragment = f;
                    f.prevFragment = prevF;
                }
                prevF = f;
            }
        }

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            // need to show a "all" group
            return SanyiSDK.rest.tableGroups.size() + 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            int revisedPos = position - 1;
            if (revisedPos < 0) {
                return getActivity().getResources().getString(R.string.all_table);
            }
            TableGroup obj = SanyiSDK.rest.tableGroups.get(revisedPos);
            return obj.name;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
            // return POSITION_UNCHANGED;
        }

        @Override
        public Fragment getItem(int index) {
            // return TableGroupItemFragment.newInstance(index);
            return _tableGroup.get(index);
        }

        public void refresh() {
            initFragments();
            notifyDataSetChanged();
        }

        // @Override
        // public void destroyItem(ViewGroup container, int position, Object
        // object) {
        // // TODO Auto-generated method stub
        // //super.destroyItem(container, position, object);
        // }
        //
        // @Override
        // public Object instantiateItem(ViewGroup container, int position) {
        // // TODO Auto-generated method stub
        // return super.instantiateItem(container, position);
        // }

    }

    public TableGroupItemFragment getCurrentSubFragment() {
        return (TableGroupItemFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPrinterImageOK() {
        imageView_table_status_print.setImageResource(R.drawable.print);
    }

    public void setPrinterImageError() {
        imageView_table_status_print.setImageResource(R.drawable.print_error);
    }

    public void setCloudConnectImage() {
        if (Restaurant.currentCloudConnectStatus == ConstantsUtil.CLOUD_CONNECT_NORMOL) {
            imageView_table_status_cloud_connect.setImageResource(R.drawable.cloud);
        } else {
            imageView_table_status_cloud_connect.setImageResource(R.drawable.cloud_error);
        }
    }

    public void setLocalNetWorkImageOK() {
        imageView_table_status_local_network.setImageResource(R.drawable.network);
    }

    public void setLocalNetWorkImageError() {
        imageView_table_status_local_network.setImageResource(R.drawable.network_error);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.imageView_table_status_print:
            case R.id.textView_table_status_print:
                NormalDialog normalDialog = new NormalDialog(activity);
                View printView = LayoutInflater.from(activity).inflate(R.layout.dialog_shop_printer, null);
                normalDialog.title("打印机状态");
                normalDialog.content(printView);
                normalDialog.heightScale((float) 0.7);
                normalDialog.widthScale((float) 0.5);
                normalDialog.isHasConfirm(false);
                shopPrinterAdapter = new ShopPrinterAdapter(getActivity());
                ListView listView_dialog_shop_printer_list = (ListView) printView.findViewById(R.id.listView_dialog_shop_printer_list);
                Button button_shop_printer_item_test = (Button) printView.findViewById(R.id.button_shop_printer_item_test);
                button_shop_printer_item_test.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        List<Long> printers = new ArrayList<Long>();
                        for(ShopPrinter printer  : SanyiSDK.rest.operationData.shopPrinters){
                            printers.add(printer.id);
                        }
                        SanyiScalaRequests.testPrinterRequest(printers, new _TestPrintRequest.ITestPrintListener() {
                            @Override
                            public void onSuccess(TestPrintResult result) {

                            }



                            @Override
                            public void onFail(String error) {

                            }
                        });
                    }
                });
                listView_dialog_shop_printer_list.setAdapter(shopPrinterAdapter);
                normalDialog.show();
                break;
            case R.id.freeTable:
                if (free) {
                    setLayoutSelect((LinearLayout) v, true);
                    ((TableGroupItemFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem())).refreshTable(TableAdapter.AVAILABLE);
                    free = false;
                } else {
                    setLayoutSelect((LinearLayout) v, false);
                    free = true;
                    ((TableGroupItemFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem())).refreshTable(TableAdapter.ALL);
                }
                use = true;
                preprint = true;
                bing = true;
                break;
            case R.id.isUsingTable:
                if (use) {
                    setLayoutSelect((LinearLayout) v, true);
                    ((TableGroupItemFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem())).refreshTable(TableAdapter.SERVING);
                    use = false;
                } else {
                    setLayoutSelect((LinearLayout) v, false);
                    use = true;
                    ((TableGroupItemFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem())).refreshTable(TableAdapter.ALL);
                }
                free = true;
                preprint = true;
                bing = true;
                break;
            case R.id.prePrintTable:
                if (preprint) {
                    setLayoutSelect((LinearLayout) v, true);
                    ((TableGroupItemFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem())).refreshTable(TableAdapter.PREPRINT);
                    preprint = false;
                } else {
                    setLayoutSelect((LinearLayout) v, false);
                    preprint = true;
                    ((TableGroupItemFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem())).refreshTable(TableAdapter.ALL);
                }
                free = true;
                use = true;
                bing = true;
                break;
            case R.id.bingTable:
                if (bing) {
                    setLayoutSelect((LinearLayout) v, true);
                    ((TableGroupItemFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem())).refreshTable(TableAdapter.MERGE);
                    bing = false;
                } else {
                    setLayoutSelect((LinearLayout) v, false);
                    bing = true;
                    ((TableGroupItemFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem())).refreshTable(TableAdapter.ALL);
                }
                free = true;
                use = true;
                preprint = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // if (!free) {
        // LoginActivity.activity.settings.edit().putInt("selected_id",
        // R.id.freeTable).commit();
        // } else if (!use) {
        // LoginActivity.activity.settings.edit().putInt("selected_id",
        // R.id.isUsingTable).commit();
        // } else if (!preprint) {
        // LoginActivity.activity.settings.edit().putInt("selected_id",
        // R.id.prePrintTable).commit();
        // }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.obj = UPDATE_DATE;
                handler.sendMessage(message);
            }
        }, 0, 1000 * 60);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
    }

    public void setLayoutSelect(LinearLayout layout, boolean flag) {
        for (int i = 0; i < table_status_group.getChildCount(); i++) {
            LinearLayout view = (LinearLayout) table_status_group.getChildAt(i);
            if (view.getId() == layout.getId()) {
                view.setSelected(flag);
            } else {
                view.setSelected(false);
            }
        }
    }
}
