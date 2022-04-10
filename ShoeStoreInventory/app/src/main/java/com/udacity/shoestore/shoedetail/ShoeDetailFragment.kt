package com.udacity.shoestore.shoedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.udacity.shoestore.R
import com.udacity.shoestore.ShoeViewModel
import com.udacity.shoestore.databinding.FragmentShoeDetailBinding
import com.udacity.shoestore.models.Shoe

class ShoeDetailFragment : Fragment() {

    private lateinit var binding: FragmentShoeDetailBinding
    private val sharedViewModel: ShoeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoe_detail, container, false)

        val args = ShoeDetailFragmentArgs.fromBundle(requireArguments())
        binding.shoe = args.shoe

        binding.buttonSave.setOnClickListener {
            updateShoe()
        }

        binding.buttonCancel.setOnClickListener {
            navigateToListShoeScreen()
        }

        sharedViewModel.eventAddShoeFinish.observe(viewLifecycleOwner, { isSuccess ->
            if (isSuccess) {
                showToast(isSuccess)
                navigateToListShoeScreen()
                sharedViewModel.onAddShoeComplete()
            }
        })

        return binding.root
    }

    private fun navigateToListShoeScreen() {
        findNavController().navigateUp()
    }

    private fun showToast(success: Boolean) {
        val message = if (success) {
            R.string.success_add_shoe
        } else {
            R.string.failed_add_shoe
        }

        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
    }

    private fun updateShoe() {
        with (binding) {
            if (isFieldsEmpty(editName, editCompany, editSize, editCompany, editDescription, editImage)) {
                Toast.makeText(requireContext(), getString(R.string.empty_forms), Toast.LENGTH_SHORT).show()
                return
            }

            val shoe = Shoe(
                name = getText(editName),
                size = getText(editSize).toDouble(),
                company = getText(editCompany),
                description = getText(editDescription),
                images = listOf(getText(editImage))
            )

            sharedViewModel.addShoe(shoe)

        }
    }

    private fun getText(editText: EditText) : String {
        return editText.text.toString()
    }

    private fun isFieldsEmpty(vararg editTexts: EditText) :Boolean {
        val result = editTexts.filter { editText -> editText.text.isEmpty() }

        return result.isNotEmpty()
    }


}