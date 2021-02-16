package edu.rosehulman.galaspp.roseproject.ui

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import edu.rosehulman.galaspp.roseproject.R
//Resource
//https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
class CustomExpandableListAdapter(
        var context: Context? = null,
        var expandableListTitle: List<String>? = null,
        var expandableListDetail: HashMap<String, List<String>>? = null
): BaseExpandableListAdapter() {

    fun CustomExpandableListAdapter(context: Context?, expandableListTitle: List<String>?,
                                    expandableListDetail: HashMap<String, List<String>>?) {
        this.context = context
        this.expandableListTitle = expandableListTitle
        this.expandableListDetail = expandableListDetail
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any? {
        return expandableListDetail!![expandableListTitle!![listPosition]]!!
                .get(expandedListPosition)
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(listPosition: Int, expandedListPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView: View? = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as String?
        if (convertView == null) {
            val layoutInflater = context!!
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_item, null)
        }
        val expandedListTextView = convertView!!
                .findViewById(R.id.lblListItem) as TextView
        expandedListTextView.text = expandedListText
        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return expandableListDetail!![expandableListTitle!![listPosition]]!!
                .size
    }

    override fun getGroup(listPosition: Int): Any? {
        return expandableListTitle!![listPosition]
    }

    override fun getGroupCount(): Int {
        return expandableListTitle!!.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup?): View? {
        var convertView: View? = convertView
        val listTitle = getGroup(listPosition) as String?
        if (convertView == null) {
            val layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_group, null)
        }
        val listTitleTextView = convertView!!
                .findViewById(R.id.lblListHeader) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}