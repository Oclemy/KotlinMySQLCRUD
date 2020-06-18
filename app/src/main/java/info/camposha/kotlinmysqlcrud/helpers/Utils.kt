package info.camposha.kotlinmysqlcrud.helpers

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import info.camposha.kotlinmysqlcrud.R
import info.camposha.kotlinmysqlcrud.retrofit.Scientist
import info.camposha.kotlinmysqlcrud.views.DashboardActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A Utility class. Contains reusable utility methods we will use throughout our project.This
 * class will save us from typing lots of repetitive code.
 */
object Utils {
    /**
     * Let's define some Constants
     */
    //supply your ip address. Type ipconfig while connected to internet to get your
    //ip address in cmd. Watch video for more details.
    //private  static  final String base_url = "http://192.168.43.91/PHP/scientists/";
    private const val base_url = "https://camposha.info/PHP/scientists/"

    //private  static  final String base_url = "http://10.0.2.2/PHP/scientists/";
    //private  static  final String base_url = "http://10.0.3.2/PHP/scientists/";
    private var retrofit: Retrofit? = null
    const val DATE_FORMAT = "yyyy-MM-dd"

    /**
     * This method will return us our Retrofit instance which we can use to initiate HTTP calls.
     */
    val client: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

    /**
     * THis method will allow us show Toast messages throughout all activities
     */
    fun show(c: Context?, message: String?) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * This method will allow us validate edittexts
     */
    fun validate(vararg editTexts: EditText): Boolean {
        val nameTxt = editTexts[0]
        val descriptionTxt = editTexts[1]
        val galaxyTxt = editTexts[2]
        if (nameTxt.text == null || nameTxt.text.toString().isEmpty()) {
            nameTxt.error = "Name is Required Please!"
            return false
        }
        if (descriptionTxt.text == null || descriptionTxt.text.toString().isEmpty()) {
            descriptionTxt.error = "Description is Required Please!"
            return false
        }
        if (galaxyTxt.text == null || galaxyTxt.text.toString().isEmpty()) {
            galaxyTxt.error = "Galaxy is Required Please!"
            return false
        }
        return true
    }

    /**
     * This utility method will allow us clear arbitrary number of edittexts
     */
    fun clearEditTexts(vararg editTexts: EditText) {
        for (editText in editTexts) {
            editText.setText("")
        }
    }

    /**
     * This utility method will allow us open any activity.
     */
    fun openActivity(c: Context, clazz: Class<*>?) {
        val intent = Intent(c, clazz)
        c.startActivity(intent)
    }

    /**
     * This method will allow us show an Info dialog anywhere in our app.
     */
    fun showInfoDialog(
        activity: AppCompatActivity, title: String?,
        message: String?
    ) {
        LovelyStandardDialog(activity, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
            .setTopColorRes(R.color.indigo)
            .setButtonsColorRes(R.color.darkDeepOrange)
            .setIcon(R.drawable.home_love)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "Relax"
            ) { }
            .setNeutralButton(
                "Go Home"
            ) {
                openActivity(
                    activity,
                    DashboardActivity::class.java
                )
            }
            .setNegativeButton(
                "Go Back"
            ) { v: View? -> activity.finish() }
            .show()
    }

    /**
     * This method will allow us show a single select dialog where we can select and return a
     * star to an edittext.
     */
    fun selectStar(c: Context?, starTxt: EditText) {
        val stars = arrayOf(
            "Rigel", "Aldebaran", "Arcturus", "Betelgeuse", "Antares", "Deneb",
            "Wezen", "VY Canis Majoris", "Sirius", "Alpha Pegasi", "Vega", "Saiph", "Polaris",
            "Canopus", "KY Cygni", "VV Cephei", "Uy Scuti", "Bellatrix", "Naos", "Pollux",
            "Achernar", "Other"
        )
        val adapter = ArrayAdapter(
            c!!,
            android.R.layout.simple_list_item_1,
            stars
        )
        LovelyChoiceDialog(c)
            .setTopColorRes(R.color.darkGreen)
            .setTitle("Stars Picker")
            .setTitleGravity(Gravity.CENTER_HORIZONTAL)
            .setIcon(R.drawable.m_list)
            .setMessage("Select the Star where the Scientist was born.")
            .setMessageGravity(Gravity.CENTER_HORIZONTAL)
            .setItems(
                adapter
            ) { _: Int, item: String? ->
                starTxt.setText(item)
            }
            .show()
    }

    /**
     * This method will allow us convert a string into a java.util.Date object and
     * return it.
     */
    fun giveMeDate(stringDate: String?): Date? {
        return try {
            val sdf =
                SimpleDateFormat(DATE_FORMAT)
            sdf.parse(stringDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * This method will allow us show a progressbar
     */
    fun showProgressBar(pb: ProgressBar) {
        pb.visibility = View.VISIBLE
    }

    /**
     * This method will allow us hide a progressbar
     */
    fun hideProgressBar(pb: ProgressBar) {
        pb.visibility = View.GONE
    }

    /**
     * This method will allow us send a serialized scientist objec  to a specified
     * activity
     */
    fun sendScientistToActivity(
        c: Context, scientist: Scientist?,
        clazz: Class<*>?
    ) {
        val i = Intent(c, clazz)
        i.putExtra("SCIENTIST_KEY", scientist)
        c.startActivity(i)
    }

    /**
     * This method will allow us receive a serialized scientist, deserialize it and return it,.
     */
    fun receiveScientist(intent: Intent, c: Context?): Scientist? {
        try {
            return intent.getSerializableExtra("SCIENTIST_KEY") as Scientist
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
//end