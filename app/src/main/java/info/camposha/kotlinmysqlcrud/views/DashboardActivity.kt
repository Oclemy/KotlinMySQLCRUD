package info.camposha.kotlinmysqlcrud.views

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import info.camposha.kotlinmysqlcrud.helpers.Utils
import info.camposha.kotlinmysqlcrud.R
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    /**
     * Let's initialize our cards  and listen to their click events
     */
    private fun initializeWidgets() {
        viewScientistsCard.setOnClickListener(View.OnClickListener {
            Utils.openActivity(
                this@DashboardActivity,
                ScientistsActivity::class.java
            )
        })
        addScientistCard.setOnClickListener(View.OnClickListener {
            Utils.openActivity(
                this@DashboardActivity,
                CRUDActivity::class.java
            )
        })
        third.setOnClickListener(View.OnClickListener {
            Utils.showInfoDialog(
                this@DashboardActivity, "YEEES",
                "Hey You can Display another page when this is clicked"
            )
        })
        closeCard.setOnClickListener(View.OnClickListener { finish() })
    }

    /**
     * Let's override the attachBaseContext() method
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    /**
     * When the back button is pressed finish this activity
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /**
     * Let's override the onCreate() and call our initializeWidgets()
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initializeWidgets()
    }
}