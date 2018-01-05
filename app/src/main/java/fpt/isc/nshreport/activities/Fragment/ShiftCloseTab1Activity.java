package fpt.isc.nshreport.activities.Fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.BaseActivity;
import fpt.isc.nshreport.adapters.SalesReportCloseAdapter;
import fpt.isc.nshreport.adapters.ShiftCloseDetailAdapter;
import fpt.isc.nshreport.models.GasPumps;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.models.ShiftCloseOtherDetail;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.objectParse.ShiftOpen;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class ShiftCloseTab1Activity extends BaseFragment {
    //Declares Variables
    @BindView(R.id.lvSalesReportAdd)
    public ExpandableListView lvAdd = null;
    @BindView(R.id.lvDetail)
    ListView lvDetail;
    private int lastExpandedPosition = -1;
    @BindView(R.id.layout_add_detail)
    public LinearLayout layoutAddDetail;

    private List<ShiftCloseOtherDetail> listDetail;

    private ShiftCloseDetailAdapter adapterDetail;

    private ShiftOpen shiftOpen;
    public ArrayList<GasPumps> headerDataList = null;
    public HashMap<GasPumps, ArrayList<PumpLinesAdd>> childDataList = null;
    public SalesReportCloseAdapter adapter = null;

    User user;

    public static ShiftCloseTab1Activity newInstance(ShiftOpen shiftOpen_) {

        Bundle args = new Bundle();
        args.putSerializable("shift_data", shiftOpen_);
        ShiftCloseTab1Activity fragment = new ShiftCloseTab1Activity();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_shift_add_tab1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        user  = NSHSharedPreferences.getInstance(getContext()).getUserLogin();
        initViews();
        getData();
    }
    public void initViews()
    {
        ColorDrawable sage = new ColorDrawable(ContextCompat.getColor(getContext(), android.R.color.transparent));
        lvDetail.setDivider(sage);
        lvDetail.setDividerHeight(10);

        lvAdd.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    lvAdd.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
                ((BaseActivity)getActivity()).btnDetail.setVisibility(View.GONE);
            }
        });
        lvAdd.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                ((BaseActivity)getActivity()).btnDetail.setVisibility(View.VISIBLE);
            }
        });
    }

    public void getDataDetail() {
        listDetail = new ArrayList<>();

        for (int i = 0; i < headerDataList.size(); i++) {
            ArrayList<PumpLinesAdd> resultList = childDataList.get(headerDataList.get(i));
            for (int j = 0; j < resultList.size(); j++) {
                ShiftCloseOtherDetail tmp = new ShiftCloseOtherDetail();
                tmp.setTitle(headerDataList.get(i).getName());
                tmp.setContent(resultList.get(j).getLineName());
                tmp.setOldLitre(String.valueOf(resultList.get(j).getReportNumPre()));
                tmp.setNewLitre(String.valueOf(resultList.get(j).getSalesLitre()));
                tmp.setNum(String.valueOf(resultList.get(j).getSalesAmount()));
                tmp.setImage(resultList.get(j).getPhotos());
                listDetail.add(tmp);
            }
        }
        adapterDetail = new ShiftCloseDetailAdapter(getContext(), listDetail);
        lvDetail.setAdapter(adapterDetail);
    }

    private void getData() {

        shiftOpen = (ShiftOpen)getArguments().getSerializable("shift_data");
        headerDataList = new ArrayList<GasPumps>();
        childDataList = new HashMap<GasPumps, ArrayList<PumpLinesAdd>>();
        try {
            //set headerDataList
            for (int i = 0; i < shiftOpen.data.daily_report_details.fuel_filling_columns.size(); i++) {
                GasPumps tmp = new GasPumps();
                tmp.setId(shiftOpen.data.daily_report_details.fuel_filling_columns.get(i).fuel_filling_column_id);
                tmp.setName(shiftOpen.data.daily_report_details.fuel_filling_columns.get(i).fuel_filling_column_code);
                headerDataList.add(tmp);
                List<ShiftOpen.data.daily_report_details.fuel_filling_columns.pumps> pump = shiftOpen.data.daily_report_details.fuel_filling_columns.get(i).pumps;

                ArrayList<PumpLinesAdd> pumplist = new ArrayList<>();
                for (int j = 0; j < pump.size(); j++) {
                    PumpLinesAdd a = new PumpLinesAdd();
                    a.setId(pump.get(j).daily_report_detail_id);
                    a.setPumpId(pump.get(j).pump_id);
                    a.setLineName(pump.get(j).pump_name + " " + pump.get(j).location);

                    long numLitre = pump.get(j).number_shift_open;

                    a.setSalesLitre(numLitre);
                    a.setReportNumPre(numLitre);
                    a.setSalesPrice(pump.get(j).current_price);

                    //get image in type = shift_close
                    String photo = "";
                    for (int k = 0; k < pump.get(j).images.size(); k++) {
                        if (pump.get(j).images.get(k).type.equals("shift_open")) {
                            photo = pump.get(j).images.get(k).daily_report_name;
                        }
                    }
                    a.setPhotos(photo);
                    //khong bat buoc nhap hinh moi
                    a.setImageID(1);
                    pumplist.add(a);
                }
                childDataList.put(tmp, pumplist);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        showDataList();
        ((BaseActivity)getActivity()).swipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Show data list for App
     */
    private void showDataList() {
        if (headerDataList != null && childDataList != null) {
            adapter = new SalesReportCloseAdapter(getActivity(), headerDataList, childDataList);
            lvAdd.setAdapter(adapter);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
