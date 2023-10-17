package com.example.voluntask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.voluntask.R
import com.example.voluntask.databinding.FragmentEscolherContaBinding


class EscolherContaFragment : Fragment() {

    private lateinit var binding: FragmentEscolherContaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEscolherContaBinding.inflate(inflater, container, false)

        binding.layoutRbVoluntario.setOnClickListener{
            Navigation.findNavController(binding.root).navigate(R.id.action_escolherContaFragment_to_registerVoluntarioFragment)
        }

        binding.layoutRbInstituicao.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_escolherContaFragment_to_registerInstituicaoFragment)
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EscolherContaFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}