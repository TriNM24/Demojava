package fpt.isc.nshreport.activities.Fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.ShiftCloseOtherAdapter;
import fpt.isc.nshreport.models.ShiftCloseOtherDetail;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class ShiftCloseTab2Activity extends BaseFragment {
    //Declares Variables
    @BindView(R.id.lvAdd)
    RecyclerView lvOther;

    private ShiftCloseOtherAdapter adapter;

    public List<ShiftCloseOtherDetail> listData;
    User user;

    public static ShiftCloseTab2Activity newInstance(List<ShiftCloseOtherDetail> data) {

        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) data);
        ShiftCloseTab2Activity fragment = new ShiftCloseTab2Activity();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_shift_close_tab2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        user  = NSHSharedPreferences.getInstance(getContext()).getUserLogin();
        initView();
        getData();
    }

    public void initView()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        lvOther.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lvOther.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.cus_divide));
        lvOther.addItemDecoration(dividerItemDecoration);

    }

    public void getData()
    {
        listData = (List<ShiftCloseOtherDetail>) getArguments().getSerializable("data");
        if(listData != null) {
            adapter = new ShiftCloseOtherAdapter(getActivity(),listData);
            lvOther.setAdapter(adapter);
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
