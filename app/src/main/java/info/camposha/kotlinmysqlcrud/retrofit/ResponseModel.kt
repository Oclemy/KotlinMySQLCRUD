package info.camposha.kotlinmysqlcrud.retrofit

import com.google.gson.annotations.SerializedName

/**
 * Our json response will be mapped to this class.
 */
class ResponseModel {
    /**
     * Our ResponseModel attributes
     */
    @SerializedName("result")
    var result: ArrayList<Scientist>? = ArrayList()

    @SerializedName("code")
    var code: String? = "-1"

    @SerializedName("message")
    var message: String? = "UNKNOWN MESSAGE"

}