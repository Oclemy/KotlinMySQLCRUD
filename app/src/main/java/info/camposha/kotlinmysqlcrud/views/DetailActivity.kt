package info.camposha.kotlinmysqlcrud.views

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import info.camposha.kotlinmysqlcrud.R
import info.camposha.kotlinmysqlcrud.helpers.Utils
import info.camposha.kotlinmysqlcrud.retrofit.Scientist
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.detail_content.*

class DetailActivity : AppCompatActivity() {
    //Let's define our instance fields
    private var receivedScientist: Scientist? = null

    /**
     * We will now receive and show our data to their appropriate views.
     */
    private fun receiveAndShowData() {
        receivedScientist = Utils.receiveScientist(intent, this@DetailActivity)
        if (receivedScientist != null) {
            nameTV.setText(receivedScientist!!.name)
            descriptionTV.setText(receivedScientist!!.description)
            galaxyTV.setText(receivedScientist!!.galaxy)
            starTV.setText(receivedScientist!!.star)
            dobTV.setText(receivedScientist!!.dob)
            diedTV.setText(receivedScientist!!.died)
            mCollapsingToolbarLayout!!.title = receivedScientist!!.name
            mCollapsingToolbarLayout!!.setExpandedTitleColor(resources.getColor(R.color.white))
        }
    }

    /**
     * Let's inflate our menu for the detail page
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_page_menu, menu)
        return true
    }

    /**
     * When a menu item is selected we want to navigate to the appropriate page
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                Utils.sendScientistToActivity(this, receivedScientist, CRUDActivity::class.java)
                finish()
                return true
            }
            R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Let's once again override the attachBaseContext. We do this for our
     * Calligraphy library
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    /**
     * Let's finish the current activity when back button is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /**
     * Our onCreate method
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        receiveAndShowData()
        editFAB.setOnClickListener {
            Utils.sendScientistToActivity(this, receivedScientist, CRUDActivity::class.java)
            finish()
        }
    }
}
//end