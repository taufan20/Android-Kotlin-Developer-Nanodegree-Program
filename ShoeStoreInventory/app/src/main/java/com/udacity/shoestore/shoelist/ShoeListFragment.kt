package com.udacity.shoestore.shoelist

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.udacity.shoestore.R
import com.udacity.shoestore.ShoeViewModel
import com.udacity.shoestore.databinding.FragmentShoeListBinding
import com.udacity.shoestore.databinding.ShoeItemBinding
import com.udacity.shoestore.models.Shoe

class ShoeListFragment : Fragment() {

    private lateinit var binding: FragmentShoeListBinding
    private val sharedViewModel: ShoeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoe_list, container, false)
        binding.lifecycleOwner = this

        binding.fabAdd.setOnClickListener { navigateToShoeDetailPage() }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        sharedViewModel.shoeList.observe(viewLifecycleOwner, { shoeList ->
            displayShoeList(shoeList)
        })

    }

    private fun displayShoeList(shoeList: List<Shoe>) {

        for (shoe in shoeList) {

            val itemBinding = DataBindingUtil.inflate<ShoeItemBinding>(
                layoutInflater, R.layout.shoe_item, binding.linearContainer, false)

            itemBinding.shoe = shoe

            itemBinding.root.setOnClickListener { navigateToShoeDetailPage(shoe) }

            binding.linearContainer.addView(itemBinding.root)
        }
    }

    private fun navigateToShoeDetailPage(shoe: Shoe? = null) {
        findNavController().navigate(
            ShoeListFragmentDirections.actionShoeListFragmentToShoeDetailFragment(shoe))
    }
}


@BindingAdapter("imageUrl")
fun AppCompatImageView.loadImage(url: String) {
    if (url.isNotEmpty()){

        Glide.with(this.context).load(url).centerCrop()
            .into(this)
    }
    else{
        this.setBackgroundColor(Color.parseColor("E0E0E0"))
    }
}