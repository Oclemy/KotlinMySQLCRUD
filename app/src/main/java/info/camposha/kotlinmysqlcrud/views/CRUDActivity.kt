package info.camposha.kotlinmysqlcrud.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import info.camposha.kotlinmysqlcrud.R
import info.camposha.kotlinmysqlcrud.helpers.Utils
import info.camposha.kotlinmysqlcrud.helpers.Utils.showInfoDialog
import info.camposha.kotlinmysqlcrud.retrofit.ResponseModel
import info.camposha.kotlinmysqlcrud.retrofit.RestApi
import info.camposha.kotlinmysqlcrud.retrofit.Scientist
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_crud.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CRUDActivity : AppCompatActivity() {
    private val a: AppCompatActivity = this

    //we'll have several instance fields
    private var id: String? = null
    private var receivedScientist: Scientist? = null
    private val c: Context = this@CRUDActivity

    /**
     * Let's initialize our widgets
     */
    private fun initializeWidgets() {
        mProgressBarSave.isIndeterminate = true
        mProgressBarSave.visibility = View.GONE

        dobTxt.setFormat(Utils.DATE_FORMAT)
        dodTxt.setFormat(Utils.DATE_FORMAT)
    }

    /**
     * The following method will allow us insert data typed in this page into th
     * e database
     */
    private fun insertData() {
        val name: String
        val description: String
        val galaxy: String
        val star: String
        val dob: String
        val died: String
        if (Utils.validate(nameTxt, descriptionTxt, galaxyTxt)) {
            name = nameTxt!!.text.toString()
            description = descriptionTxt!!.text.toString()
            galaxy = galaxyTxt!!.text.toString()
            star = starTxt!!.text.toString()
            if (dobTxt!!.date != null) {

                dob = dobTxt!!.format.format(dobTxt!!.date)
            } else {
                dobTxt!!.error = "Invalid Date"
                dobTxt!!.requestFocus()
                return
            }
            if (dodTxt!!.date != null) {

                died = dobTxt!!.format.format(dodTxt!!.date)
            } else {
                dodTxt!!.error = "Invalid Date"
                dodTxt!!.requestFocus()
                return
            }
            val api: RestApi = Utils.client!!.create(RestApi::class.java)
            val insertData: Call<ResponseModel?>? = api.insertData(
                "INSERT", name,
                description, galaxy,
                star, dob, died
            )
            Utils.showProgressBar(mProgressBarSave)
            insertData!!.enqueue(object : Callback<ResponseModel?> {
                override fun onResponse(
                    call: Call<ResponseModel?>?,
                    response: Response<ResponseModel?>?
                ) {
                    var myResponseCode = ""
                    if (response?.body() != null) {
                        myResponseCode = response.body()!!.code!!
                    }
                    when {
                        myResponseCode == "1" -> {
                            Utils.show(
                                c, """SUCCESS:
                 1. Data Inserted Successfully.
                 2. ResponseCode: $myResponseCode"""
                            )
                            Utils.openActivity(c, ScientistsActivity::class.java)
                        }
                        myResponseCode.equals("2", ignoreCase = true) -> {
                            showInfoDialog(
                                this@CRUDActivity, "UNSUCCESSFUL",
                                """However Good Response.
                 1. CONNECTION TO SERVER WAS SUCCESSFUL
                 2. WE ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: $myResponseCode
                 3. Most probably the problem is with your PHP Code."""
                            )
                        }
                        myResponseCode.equals("3", ignoreCase = true) -> {
                            showInfoDialog(
                                this@CRUDActivity,
                                "NO MYSQL CONNECTION",
                                "Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials."
                            )
                        }
                    }
                    Utils.hideProgressBar(mProgressBarSave)
                }

                override fun onFailure(call: Call<ResponseModel?>?, t: Throwable) {
                    Log.d("RETROFIT", "ERROR: " + t.message)
                    Utils.hideProgressBar(mProgressBarSave)
                    showInfoDialog(
                        this@CRUDActivity, "FAILURE",
                        "FAILURE THROWN DURING INSERT." +
                                " ERROR Message: " + t.message
                    )
                }
            })
        }
    }

    /**
     * The following method will allow us update the current scientist's data in the database
     */
    private fun updateData() {
        val name: String
        val description: String
        val galaxy: String
        val star: String
        val dob: String
        val died: String
        if (Utils.validate(nameTxt, descriptionTxt, galaxyTxt)) {
            name = nameTxt!!.text.toString()
            description = descriptionTxt!!.text.toString()
            galaxy = galaxyTxt!!.text.toString()
            star = starTxt!!.text.toString()
            if (dobTxt!!.date != null) {
                dob = dobTxt!!.format.format(dobTxt!!.date)
            } else {
                dobTxt!!.error = "Invalid Date"
                dobTxt!!.requestFocus()
                return
            }
            if (dodTxt!!.date != null) {
                died = dodTxt!!.format.format(dodTxt!!.date)
            } else {
                dodTxt!!.error = "Invalid Date"
                dodTxt!!.requestFocus()
                return
            }
            Utils.showProgressBar(mProgressBarSave)
            val api: RestApi = Utils.client!!.create(RestApi::class.java)
            val update: Call<ResponseModel?>? = api.updateData(
                "UPDATE", id, name, description, galaxy,
                star,
                dob, died
            )

            update!!.enqueue(object : Callback<ResponseModel?> {
                override fun onResponse(
                    call: Call<ResponseModel?>?,
                    response: Response<ResponseModel?>?
                ) {
                    Utils.hideProgressBar(mProgressBarSave)
                    if (response?.body() == null) {
                        showInfoDialog(a, "ERROR", "Response or ResponseBody is null")
                        return
                    }
                    val myResponseCode: String = response.body()!!.code!!
                    if (myResponseCode.equals("1", ignoreCase = true)) {
                        Utils.show(c, response.body()!!.message)
                        Utils.openActivity(c, ScientistsActivity::class.java)
                        finish()
                    } else if (myResponseCode.equals("2", ignoreCase = true)) {
                        showInfoDialog(
                            this@CRUDActivity, "UNSUCCESSFUL",
                            """Good Response From PHP,WE ATTEMPTED UPDATING DATA BUT ENCOUNTERED ResponseCode: $myResponseCode
 3. Most probably the problem is with your PHP Code."""
                        )
                    } else if (myResponseCode.equals("3", ignoreCase = true)) {
                        showInfoDialog(
                            this@CRUDActivity, "NO MYSQL CONNECTION",
                            " Your PHP Code" +
                                    " is unable to connect to mysql database. Make sure you have supplied correct" +
                                    " database credentials."
                        )
                    }
                }

                override fun onFailure(
                    call: Call<ResponseModel?>?,
                    t: Throwable
                ) {
                    Log.d("RETROFIT", "ERROR THROWN DURING UPDATE: " + t.message)
                    Utils.hideProgressBar(mProgressBarSave)
                    showInfoDialog(
                        this@CRUDActivity, "FAILURE THROWN", "ERROR DURING UPDATE.Here" +
                                " is the Error: " + t.message
                    )
                }
            })
        }
    }

    /**
     * The following method will allow us delete data from database
     */
    private fun deleteData() {
        val api: RestApi = Utils.client!!.create(RestApi::class.java)
        val del: Call<ResponseModel?>? = api.remove("DELETE", id)
        Utils.showProgressBar(mProgressBarSave)
        del!!.enqueue(object : Callback<ResponseModel?> {
            override fun onResponse(
                call: Call<ResponseModel?>?,
                response: Response<ResponseModel?>?
            ) {
                if (response?.body() == null) {
                    showInfoDialog(a, "ERROR", "Response or ResponseBody is null")
                    return
                }
                Utils.hideProgressBar(mProgressBarSave)
                val myResponseCode: String = response.body()!!.code!!
                if (myResponseCode.equals("1", ignoreCase = true)) {
                    Utils.show(c, response.body()!!.message)
                    Utils.openActivity(c, ScientistsActivity::class.java)
                    finish()
                } else if (myResponseCode.equals("2", ignoreCase = true)) {
                    showInfoDialog(
                        this@CRUDActivity, "UNSUCCESSFUL",
                        """However Good Response.
 1. CONNECTION TO SERVER WAS SUCCESSFUL
 2. WE ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: $myResponseCode
 3. Most probably the problem is with your PHP Code."""
                    )
                } else if (myResponseCode.equals("3", ignoreCase = true)) {
                    showInfoDialog(
                        this@CRUDActivity, "NO MYSQL CONNECTION",
                        " Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials."
                    )
                }
            }

            override fun onFailure(call: Call<ResponseModel?>, t: Throwable) {
                Utils.hideProgressBar(mProgressBarSave)
                Log.d("RETROFIT", "ERROR: " + t.message)
                showInfoDialog(
                    this@CRUDActivity,
                    "FAILURE THROWN",
                    "ERROR during DELETE attempt. Message: " + t.message
                )
            }
        })
    }

    /**
     * Show selected star in our edittext
     */
    private fun showSelectedStarInEditText() {
        starTxt!!.setOnClickListener { v: View? ->
            Utils.selectStar(
                c,
                starTxt
            )
        }
    }

    /**
     * When our back button is pressed
     */
    override fun onBackPressed() {
        showInfoDialog(this, "Warning", "Are you sure you want to exit?")
    }

    /**
     * Let's inflate our menu based on the role this page has been opened for.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (receivedScientist == null) {
            menuInflater.inflate(R.menu.new_item_menu, menu)
            headerTxt!!.text = "Add New Scientist"
        } else {
            menuInflater.inflate(R.menu.edit_item_menu, menu)
            headerTxt!!.text = "Edit Existing Scientist"
        }
        return true
    }

    /**
     * Let's listen to menu action events and perform appropriate function
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.insertMenuItem -> {
                insertData()
                return true
            }
            R.id.editMenuItem -> {
                if (receivedScientist != null) {
                    updateData()
                } else {
                    Utils.show(this, "EDIT ONLY WORKS IN EDITING MODE")
                }
                return true
            }
            R.id.deleteMenuItem -> {
                if (receivedScientist != null) {
                    deleteData()
                } else {
                    Utils.show(this, "DELETE ONLY WORKS IN EDITING MODE")
                }
                return true
            }
            R.id.viewAllMenuItem -> {
                Utils.openActivity(this, ScientistsActivity::class.java)
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
     * Attach Base Context
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    /**
     * When our activity is resumed we will receive our data and set them to their editing
     * widgets.
     */
    override fun onResume() {
        super.onResume()
        val s: Scientist? = Utils.receiveScientist(intent, c)
        if (s != null) {
            receivedScientist = s
            id = receivedScientist!!.id
            nameTxt.setText(receivedScientist!!.name)
            descriptionTxt.setText(receivedScientist!!.description)
            galaxyTxt.setText(receivedScientist!!.galaxy)
            starTxt.setText(receivedScientist!!.star)

            if (receivedScientist!!.dob != null) {
                dobTxt!!.date = Utils.giveMeDate(receivedScientist!!.dob.toString())
            }
            if (receivedScientist!!.died != null) {
                dodTxt!!.date = Utils.giveMeDate(receivedScientist!!.died.toString())
            }
        }
    }

    /**
     * Let's override our onCreate() method
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)
        initializeWidgets()
        showSelectedStarInEditText()
    }
}
//end