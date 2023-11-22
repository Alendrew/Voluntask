package com.example.voluntask.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentTermosBinding

class TermosFragment : Fragment() {

    private lateinit var binding: FragmentTermosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onPause() {
        super.onPause()
        binding.btnAceitar.isEnabled = false
        binding.checkbox.setChecked(false)
        binding.btnAceitar.setBackgroundColor(Color.parseColor("#737373"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTermosBinding.inflate(inflater, container, false)


        binding.Layoutcheckbox.setOnClickListener{
            if (binding.btnAceitar.isEnabled){
                binding.btnAceitar.isEnabled = false
                binding.checkbox.setChecked(false)
                binding.btnAceitar.setBackgroundColor(Color.parseColor("#737373"))
            }else {
                binding.btnAceitar.isEnabled = true
                binding.checkbox.setChecked(true)
                binding.btnAceitar.setBackgroundColor(Color.parseColor("#6F70B5"))
            }
        }

        binding.btnAceitar.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_termosFragment_to_escolherContaFragment)
        }

        binding.btnTermos.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1WLf0wAFy4_4Kr8QmAF9UA-mABQIUUKiopuBKjS7FrQs/edit?usp=sharing"))
            startActivity(browserIntent)
        }


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TermosFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}