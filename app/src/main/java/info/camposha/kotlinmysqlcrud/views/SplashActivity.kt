package info.camposha.kotlinmysqlcrud.views

import android.content.Context
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import info.camposha.kotlinmysqlcrud.helpers.Utils
import info.camposha.kotlinmysqlcrud.R
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    /**
     * Let's show our Splash animation using Animation class. We fade in our widgets.
     */
    private fun showSplashAnimation() {
        val animation =
            AnimationUtils.loadAnimation(this, R.anim.top_to_bottom)
        mLogo!!.startAnimation(animation)
        val fadeIn =
            AnimationUtils.loadAnimation(this, R.anim.fade_in)
        mainTitle!!.startAnimation(fadeIn)
        subTitle!!.startAnimation(fadeIn)
    }

    /**
     * Let's go to our DashBoard after 2 seconds
     */
    private fun goToDashboard() {
        val t: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(2000)
                    Utils.openActivity(this@SplashActivity, DashboardActivity::class.java)
                    finish()
                    super.run()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        t.start()
    }

    /**
     * Let's Override attachBaseContext method
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    /**
     * Let's create our onCreate method
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showSplashAnimation()
        goToDashboard()
    }
}