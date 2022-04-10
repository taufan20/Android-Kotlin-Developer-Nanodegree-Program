package com.udacity.shoestore.shoelist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.udacity.shoestore.R
import com.udacity.shoestore.ShoeViewModel
import com.udacity.shoestore.databinding.FragmentShoeListBinding
import com.udacity.shoestore.models.Shoe
import kotlinx.android.synthetic.main.shoe_item.view.*

class ShoeListFragment : Fragment() {

    private lateinit var binding: FragmentShoeListBinding
    private val sharedViewModel: ShoeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoe_list, container, false)

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

            val shoeItemLayout = layoutInflater.inflate(R.layout.shoe_item, binding.linearContainer, false)

            with (shoeItemLayout) {
                text_name.text = shoe.name
                text_company.text = shoe.company
                Glide.with(this).load(shoe.images[0]).into(image_shoe)
                setOnClickListener {
                    navigateToShoeDetailPage(shoe)
                }
            }
            binding.linearContainer.addView(shoeItemLayout)
        }
    }

    private fun navigateToShoeDetailPage(shoe: Shoe? = null) {
        findNavController().navigate(
            ShoeListFragmentDirections.actionShoeListFragmentToShoeDetailFragment(shoe))
    }

}