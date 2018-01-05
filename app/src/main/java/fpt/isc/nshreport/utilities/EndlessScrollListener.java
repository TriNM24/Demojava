package fpt.isc.nshreport.utilities;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * Created by Chick on 8/2/2017.
 */

public class EndlessScrollListener implements OnScrollListener {
    private int currentPage = 0;
    private int lastPage = 0;
    private boolean loading = false;

    private onActionListViewScroll mActionListViewScroll;

    public interface onActionListViewScroll {
        void onApiLoadMoreTask(int page);
    }

    public EndlessScrollListener(int currentPage_,int lastPage_,onActionListViewScroll actionListViewScroll) {

            this.currentPage = currentPage_;
            this.lastPage = lastPage_;
            //this.nextPage = nextPage_;
            this.mActionListViewScroll = actionListViewScroll;
            if(lastPage > currentPage)
            {
                loading = true;
            }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && loading && (lastPage > currentPage)) {
            if (loading == true) {
                loading = false;
                mActionListViewScroll.onApiLoadMoreTask(currentPage + 1);
            }
        }
    }
}
