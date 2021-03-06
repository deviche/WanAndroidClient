package cn.onlyloveyd.wanandroidclient.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder
import cn.bingoogolapple.refreshlayout.BGARefreshLayout
import cn.onlyloveyd.wanandroidclient.R
import cn.onlyloveyd.wanandroidclient.adapter.KnowledgeTreeAdapter
import cn.onlyloveyd.wanandroidclient.bean.HttpResult
import cn.onlyloveyd.wanandroidclient.bean.KnowledgeTreeBody
import cn.onlyloveyd.wanandroidclient.http.Retrofitance
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_article.*
import me.yokeyword.fragmentation.SupportFragment

/**
 * 文 件 名: KnowledgeTreeFragment
 * 创建日期: 2018/2/7 10:50
 * 邮   箱: yidong@gz.csg.cn
 * 描   述：
 * @author Mraz
 */
class KnowledgeTreeFragment : SupportFragment(), BGARefreshLayout.BGARefreshLayoutDelegate {

    private val datas = mutableListOf<KnowledgeTreeBody>()

    private val knowledgeTreeAdapter: KnowledgeTreeAdapter by lazy {
        KnowledgeTreeAdapter(context, datas)
    }
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_article, null, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initBGALayout()
        initRvContent()
        initBGAData()
    }

    private fun initBGALayout() {
        // 为BGARefreshLayout 设置代理
        bgarefreshlayout.setDelegate(this)
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能

        val refreshViewHolder = BGANormalRefreshViewHolder(context, true)
        refreshViewHolder.setLoadingMoreText(getString(R.string.load_more))
        refreshViewHolder.setLoadMoreBackgroundColorRes(R.color.white)
        refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.white)
        bgarefreshlayout.setRefreshViewHolder(refreshViewHolder)
    }

    private fun initRvContent() {
        rv_content.layoutManager = linearLayoutManager
        rv_content.adapter = knowledgeTreeAdapter
    }

    private fun initBGAData() {
        bgarefreshlayout.beginRefreshing()
    }

    override fun onBGARefreshLayoutBeginLoadingMore(refreshLayout: BGARefreshLayout?): Boolean {
        //getKnowledgeTree()
        return false
    }

    override fun onBGARefreshLayoutBeginRefreshing(refreshLayout: BGARefreshLayout?) {
        getKnowledgeTree()
    }

    private fun getKnowledgeTree() {
        Retrofitance.wanAndroidAPI.getKnowledgeTrees()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t: HttpResult<List<KnowledgeTreeBody>> ->
                    t.data.let {
                        knowledgeTreeAdapter.run {
                            replaceData(it)
                            loadMoreComplete()
                            loadMoreEnd()
                            setEnableLoadMore(false)
                        }
                    }
                    bgarefreshlayout.endRefreshing()
                }, { error ->
                    error.printStackTrace()
                }, {
                    Logger.d("onComplete")
                }, {
                    Logger.d("onStart")
                })

    }
}