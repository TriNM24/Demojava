package fpt.isc.nshreport.utilities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

/**
 * Created by Chick on 8/2/2017.
 */

public class EndlessScrollListenerRecyclerView extends RecyclerView.OnScrollListener {
    private int currentPage = 0;
    private int lastPage = 0;
    private boolean loading = false;

    private onActionListViewScroll mActionListViewScroll;

    //new code
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mlinearLayoutManager;

    public interface onActionListViewScroll {
        void onApiLoadMoreTask(int page);
    }

    public EndlessScrollListenerRecyclerView(int currentPage_, int lastPage_, onActionListViewScroll actionListViewScroll,LinearLayoutManager linearLayoutManager) {

            this.currentPage = currentPage_;
            this.lastPage = lastPage_;
            //this.nextPage = nextPage_;
            this.mActionListViewScroll = actionListViewScroll;
            this.mlinearLayoutManager = linearLayoutManager;
            if(lastPage > currentPage)
            {
                loading = true;
            }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if(dy > 0) //check for scroll down
        {
            visibleItemCount = mlinearLayoutManager.getChildCount();
            totalItemCount = mlinearLayoutManager.getItemCount();
            pastVisiblesItems = mlinearLayoutManager.findFirstVisibleItemPosition();

            if (loading)
            {
                if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                {
                    loading = false;
                    mActionListViewScroll.onApiLoadMoreTask(currentPage + 1);
                }
            }
        }
    }
}
