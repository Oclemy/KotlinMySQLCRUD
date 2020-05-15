package info.camposha.kotlinmysqlcrud.helpers

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.ivbaranov.mli.MaterialLetterIcon
import info.camposha.kotlinmysqlcrud.R
import info.camposha.kotlinmysqlcrud.retrofit.Scientist
import info.camposha.kotlinmysqlcrud.views.DetailActivity
import java.util.*

/**
 * This is our adapter class. It has the following roles;
 * 1. Inflate our model layout into a view then subsequently recycle that view.
 * 2. Bind data to that view for all rows, making our recyclerview.
 * 3. Show name initials in icons with random icon bg color applied.
 * 4. Listen to click events of recyclerview item and pass the clicked item to recyclerview
 */
class MyAdapter(private val c: Context, scientists: ArrayList<Scientist>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    private val mMaterialColors: IntArray
    private val scientists: List<Scientist>
    var searchString = ""

    /**
     * Our ViewHolder class. It's responsibilities include:
     * 1. Hold all the widgets which will be recycled and reference them.
     * 2. Implement click event.
     */
    class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val nameTxt: TextView
        val mDescriptionTxt: TextView
        val galaxyTxt: TextView
        val mIcon: MaterialLetterIcon
        private var itemClickListener: ItemClickListener? = null
        override fun onClick(view: View) {
            itemClickListener!!.onItemClick(this.layoutPosition)
        }

        fun setItemClickListener(itemClickListener: ItemClickListener?) {
            this.itemClickListener = itemClickListener
        }

        /**
         * We reference our widgets
         */
        init {
            mIcon = itemView.findViewById(R.id.mMaterialLetterIcon)
            nameTxt = itemView.findViewById(R.id.mNameTxt)
            mDescriptionTxt = itemView.findViewById(R.id.mDescriptionTxt)
            galaxyTxt = itemView.findViewById(R.id.mGalaxyTxt)
            itemView.setOnClickListener(this)
        }
    }

    /**
     * We override the onCreateViewHolder. Here is where we inflate our model.xml
     * layout into a view object and set it's background color
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(c).inflate(R.layout.model, parent, false)
        return ViewHolder(view)
    }

    /**
     * Our onBindViewHolder method
     */
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        //get current scientist
        val s: Scientist = scientists[position]

        //bind data to widgets
        holder.nameTxt.setText(s.name)
        holder.mDescriptionTxt.setText(s.description)
        holder.galaxyTxt.setText(s.galaxy)
        holder.mIcon.isInitials = true
        holder.mIcon.initialsNumber = 2
        holder.mIcon.letterSize = 25
        holder.mIcon.shapeColor = mMaterialColors[Random().nextInt(
            mMaterialColors.size
        )]
        holder.mIcon.letter = s.name

        //get name and galaxy
        val name: String = s.name!!.toLowerCase(Locale.getDefault())
        val galaxy: String = s.galaxy!!.toLowerCase(Locale.getDefault())

        //highlight name text while searching
        if (name.contains(searchString) && !searchString.isEmpty()) {
            val startPos = name.indexOf(searchString)
            val endPos = startPos + searchString.length
            val spanString =
                Spannable.Factory.getInstance().newSpannable(holder.nameTxt.text)
            spanString.setSpan(
                ForegroundColorSpan(Color.RED), startPos, endPos,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.nameTxt.text = spanString
        } else {
            //Utils.show(ctx, "Search string empty");
        }

        //highligh galaxy text while searching
        if (galaxy.contains(searchString) && !searchString.isEmpty()) {
            val startPos = galaxy.indexOf(searchString)
            val endPos = startPos + searchString.length
            val spanString =
                Spannable.Factory.getInstance().newSpannable(holder.galaxyTxt.text)
            spanString.setSpan(
                ForegroundColorSpan(Color.BLUE), startPos, endPos,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.galaxyTxt.text = spanString
        }
        //open detail activity when clicked
        holder.setItemClickListener(object : ItemClickListener {
            override fun onItemClick(pos: Int) {
                Utils.sendScientistToActivity(
                    c, s,
                    DetailActivity::class.java
                )
            }
        })
    }

    override fun getItemCount(): Int {
        return scientists.size
    }

    interface ItemClickListener {
        fun onItemClick(pos: Int)
    }

    /**
     * Our MyAdapter's costructor
     */
    init {
        this.scientists = scientists
        mMaterialColors = c.resources.getIntArray(R.array.colors)
    }
}