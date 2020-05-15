package info.camposha.kotlinmysqlcrud.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.camposha.kotlinmysqlcrud.helpers.MyAdapter
import info.camposha.kotlinmysqlcrud.helpers.Utils
import info.camposha.kotlinmysqlcrud.helpers.Utils.showInfoDialog
import info.camposha.kotlinmysqlcrud.R
import info.camposha.kotlinmysqlcrud.retrofit.ResponseModel
import info.camposha.kotlinmysqlcrud.retrofit.RestApi
import info.camposha.kotlinmysqlcrud.retrofit.Scientist
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_scientists.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ScientistsActivity : AppCompatActivity(),
    SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    //We define our instance fields
    private var mAdapter: MyAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    var allPagesScientists: ArrayList<Scientist> = ArrayList<Scientist>()
    private var currentPageScientists: ArrayList<Scientist>? = ArrayList()
    private var isScrolling = false
    private var currentScientists = 0
    private var totalScientists = 0
    private var scrolledOutScientists = 0
    private val a: ScientistsActivity=this

    /**
     * We initialize our widgets
     */
    private fun initializeViews() {
        pb.isIndeterminate = true
        pb.visibility= View.VISIBLE
    }

    /**
     * This method will setup oir RecyclerView
     */
    private fun setupRecyclerView() {
        layoutManager = LinearLayoutManager(this)
        mAdapter = MyAdapter(this, allPagesScientists)
        rv.adapter = mAdapter
        rv.layoutManager = layoutManager
    }

    /**
     * This method will download for us data from php mysql based on supplied query string
     * as well as pagination parameters. We are basiclally searching or selecting data
     * without seaching. However all the arriving data is paginated at the server level.
     */
    private fun retrieveAndFillRecyclerView(action: String, queryString: String, start: String, limit: String) {
        mAdapter!!.searchString = queryString
        val api: RestApi = Utils.client!!.create(RestApi::class.java)
        val retrievedData: Call<ResponseModel?>?
        when {
            action.isNotEmpty() -> {
                retrievedData = api.search(action, queryString, start, limit)
                pb.visibility= View.VISIBLE
            }
            else -> {
                pb.visibility= View.VISIBLE
                retrievedData = api.retrieve()
            }
        }
        retrievedData!!.enqueue(object : Callback<ResponseModel?> {
            override fun onResponse(call: Call<ResponseModel?>?, response: Response<ResponseModel?>?) {
                pb.visibility= View.GONE

                if (response?.body() == null) {
                    Utils.showInfoDialog(a, "ERROR", "Response or Response boyd is null")
                    return
                }
                currentPageScientists = response.body()?.result!!
                if (currentPageScientists != null && currentPageScientists!!.size > 0) {
                    if (action.equals("GET_PAGINATED_SEARCH", ignoreCase = true)) {
                        allPagesScientists.clear()
                    }
                    for (i in currentPageScientists!!.indices) {
                        allPagesScientists.add(currentPageScientists!![i])
                    }
                } else {
                    if (action.equals("GET_PAGINATED_SEARCH", ignoreCase = true)) {
                        allPagesScientists.clear()
                    }
                }
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<ResponseModel?>?, t: Throwable) {
                pb.visibility= View.VISIBLE
                Log.d("RETROFIT", "ERROR: " + t.message)
                showInfoDialog(a, "ERROR", t.message)
            }
        })
    }

    /**
     * We will listen to scroll events. This is important as we are implementing scroll to
     * load more data pagination technique
     */
    private fun listenToRecyclerViewScroll() {
        rv!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(mRecyclerView: RecyclerView, newState: Int) {
                //when scrolling starts
                super.onScrollStateChanged(mRecyclerView, newState)
                //check for scroll state
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(mRecyclerView: RecyclerView, dx: Int, dy: Int) {
                // When the scrolling has stopped
                super.onScrolled(mRecyclerView, dx, dy)
                currentScientists = layoutManager!!.childCount
                totalScientists = layoutManager!!.itemCount
                scrolledOutScientists =
                    (mRecyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                if (isScrolling && currentScientists + scrolledOutScientists == totalScientists
                ) {
                    isScrolling = false
                    if (dy > 0) {
                        // Scrolling up
                        retrieveAndFillRecyclerView(
                            "GET_PAGINATED",
                            mAdapter!!.searchString, totalScientists.toString(), "7"
                        )
                    } else {
                        // Scrolling down
                    }
                }
            }
        })
    }

    /**
     * We inflate our menu. We show SearchView inside the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.scientists_page_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView =
            searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.isIconified = true
        searchView.queryHint = "Search"
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new -> {
                Utils.openActivity(this, CRUDActivity::class.java)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        retrieveAndFillRecyclerView("GET_PAGINATED_SEARCH", query, "0", "5")
        return false
    }

    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
        return false
    }

    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
        return false
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scientists)
        initializeViews()
        listenToRecyclerViewScroll()
        setupRecyclerView()
        retrieveAndFillRecyclerView("GET_PAGINATED", "", "0", "7")
    }
}