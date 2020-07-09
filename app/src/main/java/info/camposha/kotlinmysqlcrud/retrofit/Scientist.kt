package info.camposha.kotlinmysqlcrud.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Let's Create our Scientist class to represent a single Scientist.
 * It will implement java.io.Serializable interface, a marker interface that will allow
 * our
 * class to support serialization and deserialization.
 */
class Scientist : Serializable {
    /**
     * Let' now come define instance fields for this class. We decorate them with
     * @SerializedName
     * attribute. Through this we are specifying the keys in our json data.
     */
    @SerializedName("id")
    var id: String? = ""

    @SerializedName("name")
    var name: String? = ""

    @SerializedName("description")
    var description: String? = ""

    @SerializedName("galaxy")
    var galaxy: String? = ""

    @SerializedName("star")
    var star: String? = ""

    @SerializedName("dob")
    var dob: String? = ""

    @SerializedName("died")
    var died: String? = ""

}
//end


