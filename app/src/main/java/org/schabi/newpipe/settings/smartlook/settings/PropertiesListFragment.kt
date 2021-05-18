package org.schabi.newpipe.settings.smartlook.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.schabi.newpipe.R
import org.schabi.newpipe.databinding.FragmentSmartlookPropertiesBinding
import org.schabi.newpipe.databinding.ViewPropertyItemBinding
import org.schabi.newpipe.settings.smartlook.extensions.viewBinding
import org.schabi.newpipe.settings.smartlook.prefs.Property
import org.schabi.newpipe.settings.smartlook.prefs.PropertyType
import org.schabi.newpipe.settings.smartlook.prefs.SmartlookSettingsRepository

class PropertiesListFragment : Fragment(R.layout.fragment_smartlook_properties), ItemsAdapter.AdapterListener {

    private var adapter: ItemsAdapter? = null

    private val binding by viewBinding(FragmentSmartlookPropertiesBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_delete).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindViews()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun FragmentSmartlookPropertiesBinding.bindViews() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = when(type()) {
            PropertyType.Session -> "Session Properties"
            PropertyType.Global -> "Global Properties"
        }

        adapter = ItemsAdapter(this@PropertiesListFragment, requireContext())

        propertiesList.layoutManager = LinearLayoutManager(requireContext())
        propertiesList.adapter = adapter

        addPropertyButton.setOnClickListener {
            (activity as SmartlookNavigator).goTo(PropertyDetailFragment.newInstance(null, type()))
        }
    }

    private fun loadData() {
        adapter?.data = when (type()) {
            PropertyType.Session -> SmartlookSettingsRepository.sessionProperties
                    ?: listOf()
            PropertyType.Global -> SmartlookSettingsRepository.globalProperties ?: listOf()
        }.toMutableList()
    }

    override fun onItemClicked(key: String) {
        (activity as SmartlookNavigator).goTo(PropertyDetailFragment.newInstance(adapter?.data?.firstOrNull { it.key == key }, type()))
    }

    override fun onItemDeleted(property: Property) {

    }

    private fun type() = arguments?.getSerializable(TYPE) as PropertyType

    companion object {
        private const val TYPE = "type"

        fun newInstance(type: PropertyType): PropertiesListFragment {
            return PropertiesListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TYPE, type)
                }
            }
        }
    }
}

class ItemsAdapter(private val adapterListener: AdapterListener, val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: MutableList<Property>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ItemViewHolder(
                    ViewPropertyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        data?.get(position)?.let { item ->
            if (holder is ItemViewHolder) {
                holder.bind(item)
            }
        }
    }

    fun deleteItem(position: Int) {
        data?.get(position)?.let {
            data?.remove(it)
            notifyItemRemoved(position)
            adapterListener.onItemDeleted(it)
        }
    }

    interface AdapterListener {
        fun onItemClicked(key: String)
        fun onItemDeleted(property: Property)
    }

    // View Holders ****************************************************************************************************

    inner class ItemViewHolder(private val binding: ViewPropertyItemBinding) : BaseViewHolder<Property>(binding.root) {

        override fun bind(item: Property) {
            binding.valueText.text = item.value
            binding.keyText.text = item.key

            binding.root.setOnClickListener {
                adapterListener.onItemClicked(item.key)
            }
        }
    }

    /**
     * Base Class for all view holders in NotesAdapter.
     */
    abstract inner class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Set layout according to passed data model.
         */
        abstract fun bind(item: T)
    }
}
